<?php

mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

function conexion_bd(): mysqli
{
    $conexion = new mysqli('localhost', 'root', '', 'crudphpjson');
    $conexion->set_charset('utf8mb4');

    return $conexion;
}