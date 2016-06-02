<?php
require_once 'include/DB_Functions.php';
$db = new DB_Functions();

$response = array("error" => FALSE);

if (isset($_POST['name']) && isset($_POST['categoria']) && isset($_POST['descrizione']) && isset($_POST['prezzo']) && isset($_POST['opzioni']) && isset($_POST['disponibile']) && isset($_POST['foto'])){
    $name =$_POST['name'];
	$categoria =$_POST['categoria'];
	$descrizione =$_POST['descrizione'];
	$prezzo =$_POST['prezzo'];
	$opzioni =$_POST['opzioni'];
	$disponibile =$_POST['disponibile'];
	$foto =$_POST['foto'];
    
    if ($db->isPortataExisted($nome)){
        
        $response["error"] = TRUE;
        $response["error_msg"] = "Portata already existed with " . $nome;
        echo json_encode($response);
    } else{
        
        $portata = $db->storePortata($name, $categoria, $descrizione, $prezzo, $opzioni, $disponibile, $foto);
        if ($portata){
            
            $response["error"] = FALSE;
            $response["uid"] = $portata["unique_id"];
            $response["portata"]["nome"] = $portata["nome"];
			$response["portata"]["categoria"] = $portata["categoria"];
			$response["portata"]["descrizione"] = $portata["descrizione"];
			$response["portata"]["prezzo"] = $portata["prezzo"];
			$response["portata"]["opzioni"] = $portata["opzioni"];
			$response["portata"]["disponibile"] = $portata["disponibile"];
			$response["portata"]["foto"] = $portata["foto"];
            $response["portata"]["created_at"] = $portata["created_at"];
            echo json_encode($response);
        } else{
            
            $response["error"] = TRUE;
            $response["error_msg"] = "Errore sconosciuto si è verificato in inserimento della portata!";
            echo json_encode($response);
        }
    }
} else{
    $response["error"] = TRUE;
            $response["error_msg"] = "Paramentri richiesti sono mancanti!";
            echo json_encode($response); 
}
?>