<?php
date_default_timezone_set('Asia/Taipei'); // Change to your timezone
// Composer Autoload (make sure you ran composer require phpmailer/phpmailer)
require 'vendor/autoload.php';

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

// ========== DB CONNECTION ==========
$host = "localhost";
$user = "admin";
$pass = "24(r]ipg)aGlXhwd";
$db   = "db_votingSystem";

$conn = new mysqli($host, $user, $pass, $db);
if ($conn->connect_error) {
    die(json_encode(["success" => false, "message" => "Connection failed."]));
}

header("Content-Type: application/json");

$action = $_POST['action'] ?? '';

// Turn off error display and log instead
ini_set('display_errors', 0);
ini_set('log_errors', 1);
error_reporting(E_ALL);


switch ($action) {
    // ========== CANDIDATE CRUD ==========
    case "create_candidate":
        $name = trim($_POST['name']);
        $position = trim($_POST['position']);
        $party = trim($_POST['party']);

        // Check for duplicate name
        $check = $conn->prepare("SELECT id FROM candidates WHERE name = ?");
        $check->bind_param("s", $name);
        $check->execute();
        $result = $check->get_result();

        if ($result->num_rows > 0) {
            echo json_encode(["success" => false, "message" => "Candidate name already exists."]);
            break;
        }

        $stmt = $conn->prepare("INSERT INTO candidates (name, position, party) VALUES (?, ?, ?)");
        $stmt->bind_param("sss", $name, $position, $party);
        $success = $stmt->execute();
        echo json_encode(["success" => $success]);
        break;

    case "read_candidates":
        $result = $conn->query("SELECT * FROM candidates");
        $candidates = [];
        while ($row = $result->fetch_assoc()) {
            $candidates[] = $row;
        }

        // Include election state in response
        $state = $conn->query("SELECT is_started, official_end_time FROM election_state WHERE id = 1")->fetch_assoc();

        echo json_encode([
            "success" => true,
            "candidates" => $candidates,
            "election_started" => (bool)$state['is_started'],
            "official_end_time" => $state['official_end_time']
        ]);
        break;


   case "update_candidate":
        if (empty($_POST['id']) || empty($_POST['name']) || empty($_POST['position']) || empty($_POST['party'])) {
            echo json_encode(["success" => false, "message" => "Missing required fields"]);
            break;
        }

        $id = (int)$_POST['id'];
        $name = trim($_POST['name']);
        $position = trim($_POST['position']);
        $party = trim($_POST['party']);

        // Check for duplicate name (excluding current candidate)
        $check = $conn->prepare("SELECT id FROM candidates WHERE name = ? AND id != ?");
        $check->bind_param("si", $name, $id);
        $check->execute();
        $result = $check->get_result();
        if ($result->num_rows > 0) {
            echo json_encode(["success" => false, "message" => "Candidate name already exists."]);
            break;
        }

        $stmt = $conn->prepare("UPDATE candidates SET name=?, position=?, party=? WHERE id=?");
        $stmt->bind_param("sssi", $name, $position, $party, $id);
        $stmt->execute();
        $affected = $stmt->affected_rows;

        echo json_encode([
            "success" => $affected > 0,
            "message" => $affected > 0 ? "Candidate updated." : "No changes made or invalid ID."
        ]);
        break;

    case "delete_candidate":
        $id = $_POST['id'];
        $stmt = $conn->prepare("DELETE FROM candidates WHERE id=?");
        $stmt->bind_param("i", $id);
        $success = $stmt->execute();
        echo json_encode(["success" => $success]);
        break;
        
    case 'start_election':
        $durationHours = 12;
        $endTime = strtotime("+$durationHours hours") * 1000; // ms timestamp


        $stmt = $conn->prepare("UPDATE election_state SET is_started = 1, official_end_time = ?");
        $stmt->bind_param("s", $endTime);
        $stmt->execute();

        echo json_encode(["success" => true, "official_end_time" => $endTime]);
        break;

    case 'reset_election':
        $stmt = $conn->prepare("SELECT is_started, official_end_time FROM election_state WHERE id = 1");
        $stmt->execute();
        $result = $stmt->get_result()->fetch_assoc();

        $now = round(microtime(true) * 1000); // current time in ms
        $endTime = $result['official_end_time'];

        if ($now < $endTime) {
            echo json_encode(["success" => false, "message" => "Election has not ended yet."]);
            break;
        }

        // Clear candidates and votes
        $conn->query("DELETE FROM candidates");
        $conn->query("DELETE FROM votes");

        // Reset state
        $conn->query("UPDATE election_state SET is_started = 0, official_end_time = NULL");

        echo json_encode(["success" => true]);
        break;

    // ========== CAST VOTE ==========
    case "cast_vote":
        $student_id = $_POST['student_id'];
        $candidate_id = $_POST['candidate_id'];
        $position = $_POST['position'];

        $check = $conn->prepare("SELECT * FROM votes WHERE student_id = ? AND position = ?");
        $check->bind_param("is", $student_id, $position);
        $check->execute();
        $result = $check->get_result();

        if ($result->num_rows > 0) {
            echo json_encode(["status" => "error", "message" => "You already voted for $position."]);
        } else {
            $stmt = $conn->prepare("INSERT INTO votes (student_id, candidate_id, position) VALUES (?, ?, ?)");
            $stmt->bind_param("iis", $student_id, $candidate_id, $position);
            $success = $stmt->execute();

            echo json_encode([
                "status" => $success ? "success" : "error",
                "message" => $success ? "Vote cast successfully." : "Failed to cast vote."
            ]);
        }
        break;

    case "get_results":
        // Step 1: Check if result viewing is allowed based on schedule
        $scheduleSql = "SELECT scheduled_at FROM result_schedule ORDER BY id DESC LIMIT 1";
        $scheduleResult = $conn->query($scheduleSql);

        if ($scheduleResult && $scheduleResult->num_rows > 0) {
            $scheduleRow = $scheduleResult->fetch_assoc();
            $releaseDatetime = $scheduleRow['scheduled_at'];

            $currentDatetime = date("Y-m-d H:i:s");
            if ($currentDatetime < $releaseDatetime) {
                echo json_encode([
                    "success" => false,
                    "message" => "Results will be available on: " . $releaseDatetime
                ]);
                exit;
            }
        } else {
            // No schedule set
            echo json_encode([
                "success" => false,
                "message" => "Result schedule not set by admin."
            ]);
            exit;
        }

        // Step 2: Fetch and return vote results
        $sql = "SELECT 
                    c.id,
                    c.name AS candidate_name,
                    c.position,
                    c.party,
                    COUNT(v.id) AS vote_count
                FROM candidates c
                LEFT JOIN votes v ON c.id = v.candidate_id
                GROUP BY c.id
                ORDER BY c.position, vote_count DESC";

        $result = $conn->query($sql);
        $results = [];

        if ($result) {
            while ($row = $result->fetch_assoc()) {
                $results[] = $row;
            }
            echo json_encode([
                "success" => true,
                "results" => $results
            ]);
        } else {
            echo json_encode([
                "success" => false,
                "message" => "Failed to fetch results"
            ]);
        }
        exit;

    break;

     case 'set_result_schedule':
        $datetime = $_POST['scheduled_at'];
        $query = "INSERT INTO result_schedule (scheduled_at) VALUES (?)";
        $stmt = $conn->prepare($query);
        $stmt->bind_param("s", $datetime);
        if ($stmt->execute()) {
            echo json_encode(["success" => true]);
        } else {
            echo json_encode(["success" => false]);
        }
        break;

    case 'get_result_schedule':
        $query = "SELECT scheduled_at FROM result_schedule ORDER BY id DESC LIMIT 1";
        $result = $conn->query($query);
        if ($row = $result->fetch_assoc()) {
            echo json_encode(["scheduled_at" => $row['scheduled_at']]);
        } else {
            echo json_encode(["scheduled_at" => null]);
        }
        break;
        
    case 'clear_vote_results':
        $conn->query("DELETE FROM votes");
        if (isset($_POST['clear_schedule']) && $_POST['clear_schedule'] == '1') {
            $conn->query("DELETE FROM result_schedule");
        }
        echo json_encode(["success" => true]);
        break;


    // ========== USER ==========
    case "register_user":
        $name = trim($_POST['name'] ?? '');
        $email = trim($_POST['email'] ?? '');
        $password = $_POST['password'] ?? '';
        $role = $_POST['role'] ?? 'student';

        if (empty($name) || empty($email) || empty($password)) {
            echo json_encode(["success" => false, "message" => "All fields are required."]);
            break;
        }

        $check = $conn->prepare("SELECT id FROM users WHERE email = ?");
        $check->bind_param("s", $email);
        $check->execute();
        if ($check->get_result()->num_rows > 0) {
            echo json_encode(["success" => false, "message" => "Email already exists."]);
            break;
        }

        $hashedPassword = password_hash($password, PASSWORD_DEFAULT);
        $stmt = $conn->prepare("INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)");
        $stmt->bind_param("ssss", $name, $email, $hashedPassword, $role);
        $success = $stmt->execute();
        echo json_encode(["success" => $success, "message" => $success ? "Registered." : "Failed to register."]);
        break;

    case "login_user":
        $email = trim($_POST['email'] ?? '');
        $password = $_POST['password'] ?? '';

        $stmt = $conn->prepare("SELECT id, name, email, password, role FROM users WHERE email = ?");
        $stmt->bind_param("s", $email);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($row = $result->fetch_assoc()) {
            if (password_verify($password, $row['password'])) {
                echo json_encode([
                    "success" => true,
                    "message" => "Login successful.",
                    "user" => [
                        "id" => $row['id'],
                        "name" => $row['name'],
                        "email" => $row['email'],
                        "role" => $row['role']
                    ]
                ]);
            } else {
                echo json_encode(["success" => false, "message" => "Wrong password."]);
            }
        } else {
            echo json_encode(["success" => false, "message" => "User not found."]);
        }
        break;

    case "check_email":
        $email = trim($_POST['email'] ?? '');
    
        if ($email === '') {
            echo json_encode(["error" => "Email is required"]);
            exit;
        }

        $stmt = $conn->prepare("SELECT id FROM users WHERE email = ?");
        $stmt->bind_param("s", $email);
        $stmt->execute();
        $result = $stmt->get_result();

        echo json_encode(["exists" => $result->num_rows > 0]);
        break;

    /*case 'get_voting_period':
        $sql = "SELECT start_date, end_date FROM voting_period LIMIT 1";
        $result = $conn->query($sql);

        if ($result && $row = $result->fetch_assoc()) {
            echo json_encode([
                "status" => "success",
                "start_date" => $row["start_date"],
                "end_date" => $row["end_date"]
            ]);
        } else {
            echo json_encode(["status" => "error", "message" => "No data found."]);
        }
        break;

    case 'update_voting_period':
        $start_date = $_POST['start_date'] ?? '';
        $end_date = $_POST['end_date'] ?? '';

        if (!$start_date || !$end_date) {
            echo json_encode(["status" => "error", "message" => "Missing dates."]);
            break;
        }

        $check = $conn->query("SELECT id FROM voting_period LIMIT 1");

        if ($check->num_rows > 0) {
            $sql = "UPDATE voting_period SET start_date=?, end_date=? WHERE id=1";
        } else {
            $sql = "INSERT INTO voting_period (start_date, end_date) VALUES (?, ?)";
        }

        $stmt = $conn->prepare($sql);
        $stmt->bind_param("ss", $start_date, $end_date);

        if ($stmt->execute()) {
            echo json_encode(["status" => "success", "message" => "Voting period saved."]);
        } else {
            echo json_encode(["status" => "error", "message" => "Database update failed."]);
        }
        break;
*/
    // ========== OTP ==========
    case "send_otp":
        sendOtp();
        break;

    case "verify_otp":
        verifyOtp();
        break;

    default:
        echo json_encode(["success" => false, "message" => "Invalid action."]);
        break;
}

// ========== FUNCTIONS ==========
function sendOtp() {
    global $conn;

    $email = $_POST['email'] ?? '';
    $otp = rand(100000, 999999);
    $expiry = date('Y-m-d H:i:s', strtotime('+5 minutes'));

    // Store OTP with expiry
    $stmt2 = $conn->prepare("INSERT INTO otps (email, otp, expiry, created_at) VALUES (?, ?, ?, NOW())");
    $stmt2->bind_param("sss", $email, $otp, $expiry);
    $stmt2->execute();
    $stmt2->close();

    // Send OTP using PHPMailer
    $mail = new PHPMailer(true);
    try {
        $mail->isSMTP();
        $mail->Host       = 'smtp.gmail.com';
        $mail->SMTPAuth   = true;
        $mail->Username   = 'tapvote2025@gmail.com';
        $mail->Password   = 'fqgmcdqthxiijskz'; // App password
        $mail->SMTPSecure = 'tls';
        $mail->Port       = 587;

        $mail->setFrom('tapvote2025@gmail.com', 'TapVote');
        $mail->addAddress($email);
        $mail->isHTML(true);
        $mail->Subject = 'Your TapVote OTP';
        $mail->Body    = "<p>Your OTP is: <strong>$otp</strong></p>";

        $mail->send();
        echo json_encode(["success" => true, "message" => "OTP sent to $email"]);
    } catch (Exception $e) {
        echo json_encode(["success" => false, "message" => "PHPMailer Error: " . $mail->ErrorInfo]);
    }
}
// Verify OTP
function verifyOtp() {
    global $conn;
    $email = $_POST['email'] ?? '';
    $otp = $_POST['otp'] ?? '';

    $stmt = $conn->prepare("SELECT otp, expiry FROM otps WHERE email=? ORDER BY created_at DESC LIMIT 1");
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $result = $stmt->get_result();
    $row = $result->fetch_assoc();

    if ($row && strval($row['otp']) === strval($otp) && strtotime($row['expiry']) >= time()) {
        echo json_encode(["status" => "verified"]);
    } else {
        echo json_encode(["status" => "invalid", "message" => "Invalid or expired OTP"]);
    }

    $stmt->close();
}

$conn->close();
