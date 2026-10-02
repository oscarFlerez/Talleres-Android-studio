# Backend WAMP

El servicio usa PHP, Apache y MySQL. Copia la carpeta `crudphpjson` a la carpeta
`www` de Laragon y ejecuta `database.sql` desde HeidiSQL o el cliente MySQL. Si tu usuario MySQL no
es `root` sin clave, actualiza `bd/conexion_bd.php`.

Para conectar el moto g22 por USB con Apache en el puerto 80, ejecuta:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" reverse tcp:8080 tcp:80
```

La app apunta a `http://127.0.0.1:8080/crudphpjson/crud/operacion.php` porque el
puerto local 80 es privilegiado para `adb reverse`. Si Apache usa otro puerto,
cambia el puerto de destino del reverse y el puerto local de la URL de la app.

El endpoint recibe por POST `accion`, `email`, `psw` y `nombre`; admite las
acciones `login`, `Agregar`, `editar`, `listar` y `eliminar`. Las claves nuevas
se guardan con hash. Este proyecto es didactico; HTTP sin TLS no debe usarse
para credenciales en una red publica.