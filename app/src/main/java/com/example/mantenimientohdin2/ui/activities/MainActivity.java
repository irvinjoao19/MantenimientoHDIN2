package com.example.mantenimientohdin2.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mantenimientohdin2.R;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    EditText lblusuario, lblcontraseña;
    Button btningresar;
    String usuario, contraseña;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lblusuario = findViewById(R.id.txtusuario);
        lblcontraseña = findViewById(R.id.txtcontraseña);
        btningresar = findViewById(R.id.btningresar);

        recuperarpreferencias();
        btningresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                usuario = lblusuario.getText().toString();
                contraseña = lblcontraseña.getText().toString();
                if (!usuario.isEmpty() && !contraseña.isEmpty()) {
                    validar_usuario("http://apphdin.mnperu.com/apphdin/validar_usuario.php");
                } else {
                    Toast.makeText(MainActivity.this, "Ingrese Usuario y Contraseña", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    private void validar_usuario(String URL) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.isEmpty()) {

                    guardarpreferencias();
                    Toast.makeText(MainActivity.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Usuario y Contraseña incorrectas", Toast.LENGTH_SHORT).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("usuario", usuario);
                parametros.put("contrasena", contraseña);
                return parametros;
            }
        };

        RequestQueue requeestQueve = Volley.newRequestQueue(this);
        requeestQueve.add(stringRequest);


    }

    private void guardarpreferencias() {
        SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("usuario", usuario);
        editor.putString("contraseña", contraseña);
        editor.putBoolean("sesion", true);
        editor.commit();
    }

    private void recuperarpreferencias() {
        SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        lblusuario.setText(preferences.getString("usuario", "micorreo@gmail.com"));
        lblcontraseña.setText(preferences.getString("contraseña", "123456"));
    }

}