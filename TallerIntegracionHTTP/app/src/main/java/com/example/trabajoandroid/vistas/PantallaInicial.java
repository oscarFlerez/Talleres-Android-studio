package com.example.trabajoandroid.vistas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.trabajoandroid.R;
import com.example.trabajoandroid.controladores.ConexionHttpPostServer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

public class PantallaInicial extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_inicial);

        Button registerButton = findViewById(R.id.btnCancelar);
        registerButton.setOnClickListener(view ->
            startActivity(new Intent(this, PantallaCrudUsuario.class)));

        EditText emailField = findViewById(R.id.campoEmail);
        EditText passwordField = findViewById(R.id.campoClave);
        Button loginButton = findViewById(R.id.btnIniciarSesion);
        loginButton.setOnClickListener(view -> iniciarSesion(
                emailField.getText().toString().trim(),
                passwordField.getText().toString(),
                loginButton));
    }

    private void iniciarSesion(String email, String password, Button loginButton) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Ingresa el email y la clave", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, String> parametros = new HashMap<>();
        parametros.put("accion", "login");
        parametros.put("email", email);
        parametros.put("psw", password);
        loginButton.setEnabled(false);

        ConexionHttpPostServer.post(parametros, new ConexionHttpPostServer.Callback() {
            @Override
            public void onSuccess(String respuesta) {
                loginButton.setEnabled(true);
                try {
                    JsonObject usuario = JsonParser.parseString(respuesta).getAsJsonObject();
                    if (usuario.has("mensaje")) {
                        Toast.makeText(PantallaInicial.this,
                        usuario.get("mensaje").getAsString(),
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    Intent intento = new Intent(PantallaInicial.this, PantallaListado.class);
                    intento.putExtra("email", usuario.has("email")
                        ? usuario.get("email").getAsString() : email);
                    intento.putExtra("nombre", usuario.has("nombre")
                        ? usuario.get("nombre").getAsString() : "");
                    startActivity(intento);
                } catch (RuntimeException error) {
                    Toast.makeText(PantallaInicial.this,
                            "Respuesta invalida del servidor", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String mensaje) {
                loginButton.setEnabled(true);
                Toast.makeText(PantallaInicial.this, mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }
}