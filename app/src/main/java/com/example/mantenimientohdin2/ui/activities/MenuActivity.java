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

public class MenuActivity extends AppCompatActivity {
    Button btncerrar, btnbuscar, btnasignados;
    EditText crq;
    String crq2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        crq = findViewById(R.id.lblcrq);
        btncerrar = findViewById(R.id.btncerrar);
        btnasignados = findViewById(R.id.btnasignados);
        btnbuscar = findViewById(R.id.btnbuscar);
        btncerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
                preferences.edit().clear().commit();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnasignados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(getApplicationContext(), AsignacionesActivity.class);
                startActivity(intent);
                finish();
            }
        });


        btnbuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                crq2 = crq.getText().toString();

                if (!crq2.isEmpty()) {
                    validar_usuario("http://apphdin.mnperu.com/apphdin/validar_crq.php");
                } else {
                    Toast.makeText(MenuActivity.this, "Ingrese Numero de CRQ", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void validar_usuario(String URL) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                if (response.equals("existe")) {

                    guardarpreferencias();
                    Toast.makeText(MenuActivity.this, "Trabajos disponibles", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), TrabajosActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MenuActivity.this, "No existe la CRQ o no hay Trbajos Activos", Toast.LENGTH_SHORT).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MenuActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("crq", crq2);

                return parametros;
            }
        };

        RequestQueue requeestQueve = Volley.newRequestQueue(this);
        requeestQueve.add(stringRequest);


    }

    private void guardarpreferencias() {
        SharedPreferences preferences2 = getSharedPreferences("preferenciacrq", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = preferences2.edit();
        editor2.putString("crq", crq2);

        editor2.putBoolean("sesion", true);
        editor2.commit();
    }
}