package com.example.trabajoandroid.vistas;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trabajoandroid.R;
import com.example.trabajoandroid.controladores.ConexionHttpPostServer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

public class PantallaCrudUsuario extends Activity {
    private EditText emailField;
    private EditText passwordField;
    private EditText nameField;
    private Button saveButton;
    private boolean editMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_crud_usuario);

        emailField = findViewById(R.id.campoCodigo2);
        passwordField = findViewById(R.id.campoPassword2);
        nameField = findViewById(R.id.campoNombre);
        saveButton = findViewById(R.id.botonModificar);
        editMode = getIntent().getBooleanExtra("modoEdicion", false);

        if (editMode) {
            emailField.setText(getIntent().getStringExtra("email"));
            emailField.setEnabled(false);
            nameField.setText(getIntent().getStringExtra("nombre"));
            passwordField.setHint("Deja vacio para conservar la clave actual");
            ((TextView) findViewById(R.id.tituloUsuario)).setText("EDITAR DATOS DEL USUARIO");
            saveButton.setText("Guardar cambios");
        }

        Button cancelButton = findViewById(R.id.botonCancelar2);
        cancelButton.setOnClickListener(view -> finish());
        saveButton.setOnClickListener(view -> guardarUsuario());
    }

    private void guardarUsuario() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString();
        String nombre = nameField.getText().toString().trim();

        if (email.isEmpty() || nombre.isEmpty() || (!editMode && password.isEmpty())) {
            Toast.makeText(this, "Completa el email, la clave y el nombre", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, String> parametros = new HashMap<>();
        parametros.put("accion", editMode ? "editar" : "Agregar");
        parametros.put("email", email);
        parametros.put("psw", password);
        parametros.put("nombre", nombre);
        saveButton.setEnabled(false);

        ConexionHttpPostServer.post(parametros, new ConexionHttpPostServer.Callback() {
            @Override
            public void onSuccess(String respuesta) {
                saveButton.setEnabled(true);
                try {
                    JsonObject resultado = JsonParser.parseString(respuesta).getAsJsonObject();
                    String mensaje = resultado.has("mensaje")
                            ? resultado.get("mensaje").getAsString() : "Error al guardar";
                    if ("OK".equals(mensaje)) {
                        Toast.makeText(PantallaCrudUsuario.this,
                                "Usuario guardado", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(PantallaCrudUsuario.this, mensaje, Toast.LENGTH_LONG).show();
                    }
                } catch (RuntimeException error) {
                    Toast.makeText(PantallaCrudUsuario.this,
                            "Respuesta invalida del servidor", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String mensaje) {
                saveButton.setEnabled(true);
                Toast.makeText(PantallaCrudUsuario.this, mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }
}