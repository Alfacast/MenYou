<?php
require_once 'include/DB_Functions.php';
$db = new DB_Functions();

$response = array("error" => FALSE);

if (isset($_POST['name']) && isset($_POST['address']) && isset($_POST['partitaIva']) && isset($_POST['email']) && isset($_POST['telefono']) && isset($_POST['password'])){
    $name =$_POST['name'];
    $address = $_POST['address'];
    $partitaIva = $_POST['partitaIva'];
    $email = $_POST['email'];
    $telefono = $_POST['telefono'];
    $password = $_POST['password'];
    
    if ($db->isRistoranteExisted($partitaIva)){
        
        $response["error"] = TRUE;
        $response["error_msg"] = "User already existed with " . $partitaIva;
        echo json_encode($response);
    } else{
        
        $user = $db->storeRistorante($name, $address, $partitaIva, $email, $telefono, $password);
        if ($user){
            
            $response["error"] = FALSE;
            $response["uid"] = $user["unique_id"];
            $response["user"]["name"] = $user["name"];
            $response["user"]["address"] = $user["address"];
            $response["user"]["partitaIva"] = $user["partitaIva"];
            $response["user"]["email"] = $user["email"];
            $response["user"]["telefono"] = $user["telefono"];
            $response["user"]["created_at"] = $user["created_at"];
            $response["user"]["updated_at"] = $user["updated_at"];
            echo json_encode($response);
        } else{
            
            $response["error"] = TRUE;
            $response["error_msg"] = "Errore sconosciuto si è verificato in registrazione!";
            echo json_encode($response);
        }
    }
} else{
    $response["error"] = TRUE;
            $response["error_msg"] = "Paramentri richiesti sono mancanti!";
            echo json_encode($response); 
}
?>