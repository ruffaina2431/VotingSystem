<?php
date_default_timezone_set('Asia/Taipei'); // Change to your timezone
// Composer Autoload (make sure you ran `composer require phpmailer/phpmailer`)
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

switch ($action) {
    // ========== CANDIDATE CRUD ==========
    case "create_candidate":
        $name = $_POST['name'];
        $position = $_POST['position'];
        $party = $_POST['party'];
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
        echo json_encode(["success" => true, "candidates" => $candidates]);
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

    // ========== RESULTS ==========
    case "get_results":
        $sql = "SELECT 
                    c.id,
                    c.name AS name,
                    c.position,
                    c.party,
                    COUNT(v.id) AS vote_count
                FROM candidates c
                LEFT JOIN votes v ON c.id = v.candidate_id
                GROUP BY c.id
                ORDER BY c.position, vote_count DESC";
        $result = $conn->query($sql);
        $results = [];
        while ($row = $result->fetch_assoc()) {
            $results[] = $row;
        }
        echo json_encode(["success" => true, "results" => $results]);
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
        $stmt = $conn->prepare("SELECT id FROM users WHERE email = ?");
        $stmt->bind_param("s", $email);
        $stmt->execute();
        $result = $stmt->get_result();
        echo json_encode(["exists" => $result->num_rows > 0]);
        break;

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
?>
