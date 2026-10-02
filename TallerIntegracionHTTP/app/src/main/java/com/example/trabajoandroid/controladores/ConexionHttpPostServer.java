package com.example.trabajoandroid.controladores;

import android.os.Handler;
import android.os.Looper;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ConexionHttpPostServer {
    public static final String ENDPOINT =
            "http://127.0.0.1:8080/crudphpjson/crud/operacion.php";

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private static final Handler MAIN_THREAD = new Handler(Looper.getMainLooper());

    private ConexionHttpPostServer() {
    }

    public interface Callback {
        void onSuccess(String respuesta);

        void onError(String mensaje);
    }

    public static void post(Map<String, String> parametros, Callback callback) {
        EXECUTOR.execute(() -> {
            try {
                String respuesta = enviar(parametros);
                MAIN_THREAD.post(() -> callback.onSuccess(respuesta));
            } catch (Exception error) {
                String mensaje = error.getMessage() == null
                        ? "No se pudo conectar con el servidor"
                        : error.getMessage();
                MAIN_THREAD.post(() -> callback.onError(mensaje));
            }
        });
    }

    private static String enviar(Map<String, String> parametros) throws Exception {
        HttpClient cliente = new HttpClient();
        HttpConnectionManagerParams configuracion =
                cliente.getHttpConnectionManager().getParams();
        configuracion.setConnectionTimeout(10000);
        configuracion.setSoTimeout(15000);

        PostMethod peticion = new PostMethod(ENDPOINT);
        peticion.getParams().setContentCharset("UTF-8");
        for (Map.Entry<String, String> entrada : parametros.entrySet()) {
            peticion.addParameter(new NameValuePair(entrada.getKey(), entrada.getValue()));
        }

        try {
            cliente.executeMethod(peticion);
            String respuesta = peticion.getResponseBodyAsString();
            if (respuesta == null) {
                throw new IllegalStateException("El servidor no envio una respuesta");
            }
            return respuesta;
        } finally {
            peticion.releaseConnection();
        }
    }
}