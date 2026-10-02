package com.example.trabajoandroid.vistas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.trabajoandroid.R;
import com.example.trabajoandroid.controladores.ConexionHttpPostServer;
import com.example.trabajoandroid.datos.Usuario;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PantallaListado extends Activity {
    private String emailUsuario;
    private String nombreUsuario;
    private ArrayAdapter<String> adaptadorUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_listado);

        emailUsuario = getIntent().getStringExtra("email");
        nombreUsuario = getIntent().getStringExtra("nombre");
        ListView usersList = findViewById(R.id.listaUsuarios);
        adaptadorUsuarios = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                new ArrayList<>());
        usersList.setAdapter(adaptadorUsuarios);

        Button editButton = findViewById(R.id.btnGuardar);
        editButton.setOnClickListener(view -> abrirEdicion());
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarUsuarios();
    }

    private void abrirEdicion() {
        if (emailUsuario == null || emailUsuario.isEmpty()) {
            Toast.makeText(this, "Inicia sesion para editar tus datos", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intento = new Intent(this, PantallaCrudUsuario.class);
        intento.putExtra("modoEdicion", true);
        intento.putExtra("email", emailUsuario);
        intento.putExtra("nombre", nombreUsuario);
        startActivity(intento);
    }

    private void cargarUsuarios() {
        Map<String, String> parametros = new HashMap<>();
        parametros.put("accion", "listar");

        ConexionHttpPostServer.post(parametros, new ConexionHttpPostServer.Callback() {
            @Override
            public void onSuccess(String respuesta) {
                try {
                    Type tipoLista = new TypeToken<List<Usuario>>() { }.getType();
                    List<Usuario> usuarios = new Gson().fromJson(respuesta, tipoLista);
                    adaptadorUsuarios.clear();
                    if (usuarios == null) {
                        usuarios = new ArrayList<>();
                    }
                    for (Usuario usuario : usuarios) {
                        adaptadorUsuarios.add(usuario.getEmail() + " - " + usuario.getNombre());
                    }
                    if (usuarios.isEmpty()) {
                        Toast.makeText(PantallaListado.this,
                                "No hay usuarios registrados", Toast.LENGTH_LONG).show();
                    }
                } catch (RuntimeException error) {
                    Toast.makeText(PantallaListado.this,
                            "Respuesta invalida del servidor", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String mensaje) {
                Toast.makeText(PantallaListado.this, mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }
}