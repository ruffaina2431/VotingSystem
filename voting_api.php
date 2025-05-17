<?php
$host = "localhost";
$user = "admin";
$pass = "24(r]ipg)aGlXhwd";
$db = "db_votingSystem";

$conn = new mysqli($host, $user, $pass, $db);
if ($conn->connect_error) {
    die(json_encode(["success" => false, "message" => "Connection failed."]));
}

header("Content-Type: application/json");

$action = $_POST['action'] ?? '';

switch ($action) {

    // ========== CANDIDATE CRUD ==========
    //case action
    // block of code
    //break;
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
        $response = ["success" => false, "message" => ""];
        
        // Validate required parameters
        if (empty($_POST['id']) || empty($_POST['name']) || empty($_POST['position']) || empty($_POST['party'])) {
            $response["message"] = "Missing required fields";
            echo json_encode($response);
            break;
        }

        $id = (int)$_POST['id'];
        $name = trim($_POST['name']);
        $position = trim($_POST['position']);
        $party = trim($_POST['party']);

        try {
            $stmt = $conn->prepare("UPDATE candidates SET name=?, position=?, party=? WHERE id=?");
            $stmt->bind_param("sssi", $name, $position, $party, $id);
            $stmt->execute();
            
            // Check if any rows were affected
            if ($stmt->affected_rows > 0) {
                $response["success"] = true;
                $response["message"] = "Candidate updated successfully";
            } else {
                $response["message"] = "No candidate found with ID $id or no changes made";
            }
        } catch (Exception $e) {
            $response["message"] = "Error: " . $e->getMessage();
        }
        
        echo json_encode($response);
        break;

    case "delete_candidate":
        $id = $_POST['id'];
        $stmt = $conn->prepare("DELETE FROM candidates WHERE id=?");
        $stmt->bind_param("i", $id);
        $success = $stmt->execute();
        echo json_encode(["success" => $success]);
        break;

    // ========== CAST A VOTE ==========
    case "cast_vote":
        $student_id = $_POST['student_id'];
        $candidate_id = $_POST['candidate_id'];
        $position = $_POST['position'];

        // Optional: Prevent duplicate votes per position
        $check = $conn->prepare("SELECT * FROM votes WHERE student_id=? AND position=?");
        $check->bind_param("is", $student_id, $position);
        $check->execute();
        $checkResult = $check->get_result();
        if ($checkResult->num_rows > 0) {
            echo json_encode(["success" => false, "message" => "Already voted for this position."]);
        } else {
            $stmt = $conn->prepare("INSERT INTO votes (student_id, candidate_id, position) VALUES (?, ?, ?)");
            $stmt->bind_param("iis", $student_id, $candidate_id, $position);
            $success = $stmt->execute();
            echo json_encode(["success" => $success]);
        }
        break;

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
        
    // ========== USER REGISTRATION AND LOGIN ==========
    case "register_user":
        $name = trim($_POST['name'] ?? '');
        $email = trim($_POST['email'] ?? '');
        $password = $_POST['password'] ?? '';
        $role = $_POST['role'] ?? 'student'; // default to 'student' if not provided

        // Basic validation
        if (empty($name) || empty($email) || empty($password) || empty($role)) {
            echo json_encode(["success" => false, "message" => "All fields are required."]);
            break;
        }

        // Check if email already exists
        $check = $conn->prepare("SELECT id FROM users WHERE email = ?");
        $check->bind_param("s", $email);
        $check->execute();
        $checkResult = $check->get_result();
        if ($checkResult->num_rows > 0) {
            echo json_encode(["success" => false, "message" => "Email already registered."]);
            break;
        }

        // Hash the password
        $hashedPassword = password_hash($password, PASSWORD_DEFAULT);

        // Insert user
        $stmt = $conn->prepare("INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)");
        $stmt->bind_param("ssss", $name, $email, $hashedPassword, $role);
        $success = $stmt->execute();

        if ($success) {
            echo json_encode(["success" => true, "message" => "Registration successful."]);
        } else {
            echo json_encode(["success" => false, "message" => "Registration failed."]);
        }
        break;

    case "login_user":

        $email = trim($_POST['email'] ?? '');
        $password = $_POST['password'] ?? '';

        // Basic validation
        if (empty($email) || empty($password)) {
            echo json_encode(["success" => false, "message" => "Email and password are required."]);
            break;
        }

        $stmt = $conn->prepare("SELECT id, name, email, password, role FROM users WHERE email = ?");
        $stmt->bind_param("s", $email);
        $stmt->execute();
        $result = $stmt->get_result();

        $response = [];

        if ($row = $result->fetch_assoc()) {
            if (password_verify($password, $row['password'])) {
                $response['success'] = true;
                $response['user'] = [
                    'id' => $row['id'],
                    'name' => $row['name'],
                    'email' => $row['email'],
                    'role' => $row['role']
                ];
                $response['message'] = "Login successful.";
            } else {
                $response['success'] = false;
                $response['message'] = "Invalid password.";
            }
        } else {
            $response['success'] = false;
            $response['message'] = "User not found.";
        }

        echo json_encode($response);
        break;

    default:
        echo json_encode(["success" => false, "message" => "Invalid or missing action."]);


}

$conn->close();
?>
