<?php

header('Content-Type: application/json; charset=utf-8');
require_once __DIR__ . '/../bd/conexion_bd.php';

function responder_json($datos, int $estado = 200): void
{
    http_response_code($estado);
    echo json_encode($datos, JSON_UNESCAPED_UNICODE);
    exit;
}

function parametro(string $nombre, bool $obligatorio = true): string
{
    $valor = trim((string) ($_POST[$nombre] ?? $_GET[$nombre] ?? ''));
    if ($obligatorio && $valor === '') {
        responder_json(['mensaje' => "Falta el parametro $nombre"], 400);
    }

    return $valor;
}

try {
    $conexion = conexion_bd();
    $accion = parametro('accion');

    switch ($accion) {
        case 'login':
            $email = parametro('email');
            $clave = parametro('psw');
            $consulta = $conexion->prepare('SELECT password, nombre FROM Usuarios WHERE email = ?');
            $consulta->bind_param('s', $email);
            $consulta->execute();
            $consulta->bind_result($claveGuardada, $nombre);

            if (!$consulta->fetch()) {
                $consulta->close();
                responder_json(['mensaje' => 'Acceso denegado'], 401);
            }
            $consulta->close();

            $valida = password_verify($clave, $claveGuardada);
            if (!$valida && hash_equals($claveGuardada, $clave)) {
                $valida = true;
                $hash = password_hash($clave, PASSWORD_DEFAULT);
                $actualizar = $conexion->prepare('UPDATE Usuarios SET password = ? WHERE email = ?');
                $actualizar->bind_param('ss', $hash, $email);
                $actualizar->execute();
                $actualizar->close();
            }

            if (!$valida) {
                responder_json(['mensaje' => 'Acceso denegado'], 401);
            }

            responder_json(['email' => $email, 'nombre' => $nombre]);

        case 'Agregar':
        case 'editar':
            $email = parametro('email');
            $nombre = parametro('nombre');
            $clave = parametro('psw', $accion === 'Agregar');

            $buscar = $conexion->prepare('SELECT email FROM Usuarios WHERE email = ?');
            $buscar->bind_param('s', $email);
            $buscar->execute();
            $buscar->store_result();
            $existe = $buscar->num_rows > 0;
            $buscar->close();

            if ($existe) {
                if ($clave !== '') {
                    $hash = password_hash($clave, PASSWORD_DEFAULT);
                    $actualizar = $conexion->prepare('UPDATE Usuarios SET password = ?, nombre = ? WHERE email = ?');
                    $actualizar->bind_param('sss', $hash, $nombre, $email);
                } else {
                    $actualizar = $conexion->prepare('UPDATE Usuarios SET nombre = ? WHERE email = ?');
                    $actualizar->bind_param('ss', $nombre, $email);
                }
                $actualizar->execute();
                $actualizar->close();
            } else {
                if ($clave === '') {
                    responder_json(['mensaje' => 'La clave es obligatoria para registrar un usuario'], 400);
                }
                $hash = password_hash($clave, PASSWORD_DEFAULT);
                $insertar = $conexion->prepare('INSERT INTO Usuarios (email, password, nombre) VALUES (?, ?, ?)');
                $insertar->bind_param('sss', $email, $hash, $nombre);
                $insertar->execute();
                $insertar->close();
            }

            responder_json(['mensaje' => 'OK']);

        case 'listar':
            $resultado = $conexion->query('SELECT email, nombre FROM Usuarios ORDER BY nombre, email');
            $usuarios = $resultado->fetch_all(MYSQLI_ASSOC);
            responder_json($usuarios);

        case 'eliminar':
            $email = parametro('email');
            $eliminar = $conexion->prepare('DELETE FROM Usuarios WHERE email = ?');
            $eliminar->bind_param('s', $email);
            $eliminar->execute();
            $eliminado = $eliminar->affected_rows > 0;
            $eliminar->close();
            responder_json(['mensaje' => $eliminado ? 'OK' : 'Usuario no existe']);

        default:
            responder_json(['mensaje' => 'Accion no valida'], 400);
    }
} catch (Throwable $error) {
    error_log($error->getMessage());
    responder_json(['mensaje' => 'Error del servidor. Revisa la conexion a MySQL.'], 500);
}