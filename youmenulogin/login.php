
<?php
require_once 'include/DB_Functions.php';
$db = new DB_Functions();
 
// json response array
$response = array("error" => FALSE);

 
if (isset($_POST['email']) && isset($_POST['password'])) {
 
    // receiving the post params
    $email = $_POST['email'];
    $password = $_POST['password'];
    
 
    // get the user by email and password
    $user = $db->getUserByEmailAndPassword($email, $password);
	$userrist = $db->getRistoranteByEmailAndPassword($email, $password);
    
    if ($user != false) {
        // use is found
        $response["error"] = FALSE;
        $response["uid"] = $user["unique_id"];
        $response["user"]["id"] = $user["id"];        
        $response["user"]["name"] = $user["name"];
        $response["user"]["address"] = $user["address"];
        $response["user"]["partitaIva"] = $user["partitaIva"];
        $response["user"]["email"] = $user["email"];
		$response["user"]["telefono"] = $user["telefono"];
		$response["user"]["foto"] = $user["foto"];
        $response["user"]["created_at"] = $user["created_at"];
        $response["user"]["updated_at"] = $user["updated_at"];
        echo json_encode($response);
	} elseif ($userrist != false){
		$response["error"] = FALSE;
		$response["uid"] = $userrist["unique_id"];
        $response["user"]["id"] = $userrist["id"];
        $response["user"]["name"] = $userrist["name"];
        $response["user"]["address"] = $userrist["address"];
        $response["user"]["partitaIva"] = $userrist["partitaIva"];
        $response["user"]["email"] = $userrist["email"];
		$response["user"]["telefono"] = $userrist["telefono"];
		$response["user"]["foto"] = $user["foto"];
        $response["user"]["created_at"] = $userrist["created_at"];
        $response["user"]["updated_at"] = $userrist["updated_at"];
		echo json_encode($response);
    } else {
        // user is not found with the credentials
        $response["error"] = TRUE;
        $response["error_msg"] = "Login credentials are wrong. Please try again!";
        echo json_encode($response);
    }
	
} else {
    // required post params is missing
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters email or password is missing!";
    echo json_encode($response);
}
    
?>