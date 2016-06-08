<?php
require_once 'include/DB_Functions.php';
$db = new DB_Functions();

$response = array("error" => FALSE);

if (isset($_POST['nome']) && isset($_POST['id_ristorante'])){
    $nome = $_POST['nome'];
    $id_ristorante = $_POST['id_ristorante'];
    
    if ($db->isMenuExisted($nome, $id_ristorante)){
        
        $response["error"] = TRUE;
        $response["error_msg"] = "Menu already existed with " . $nome;
        echo json_encode($response);
    } else{
        
        $menu = $db->storeMenu($nome, $id_ristorante);
        if ($menu){
            
            $response["error"] = FALSE;
            $response["uid"] = $menu["unique_id"];
            $response["menu"]["id"] = $menu["id"];
            $response["menu"]["nome"] = $menu["nome"];
            $response["menu"]["created_at"] = $menu["created_at"];
            $response["menu"]["id_ristorante"] = $menu["id_ristorante"];
            echo json_encode($response);
        } else{
            
            $response["error"] = TRUE;
            $response["error_msg"] = "Errore sconosciuto si è verificato in inserimento del menu!";
            echo json_encode($response);
        }
    }
} else{
    $response["error"] = TRUE;
            $response["error_msg"] = "Paramentri richiesti sono mancanti!";
            echo json_encode($response); 
}
?>