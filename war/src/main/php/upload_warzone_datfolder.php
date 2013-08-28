<?php
if(isset($_POST["warzone"])) {
    $name = $_POST["warzone"];
} else {
    $name = "warzone";
}
if(isset($_POST["server"])) {
    if(strpbrk($_POST["server"], "/")) {
	    die("Client attempted to modify filesystem maliciously");
	}
    $server = $_POST["server"];
} else {
    $server = "minecraftserver";
}
if(isset($_POST["data"])) {
    $strDat = $_POST["data"];
} else {
    $strDat = "";
}

$file = dio_open($server . "/" . $name . "/" . "warzonedat-" . $name . "zip", O_WRONLY | O_CREAT | O_NONBLOCK);
dio_write($file, $strData);
dio_close($file);
