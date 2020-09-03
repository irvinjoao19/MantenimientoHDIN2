package com.example.mantenimientohdin2.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mantenimientohdin2.R;
import com.example.mantenimientohdin2.data.TrabajosVO;
import com.example.mantenimientohdin2.helper.VolleySingleton;
import com.example.mantenimientohdin2.ui.adapters.Adaptadortrabajos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TrabajosActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    RecyclerView recyclerUsuarios;
    ArrayList<TrabajosVO> listaUsuarios;
    String crq, manto;
    ProgressDialog progress;
    // RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trabajos);
        listaUsuarios = new ArrayList<>();
        recyclerUsuarios = TrabajosActivity.this.findViewById(R.id.listarecycler);
        recyclerUsuarios.setLayoutManager(new LinearLayoutManager(this));
        recyclerUsuarios.setHasFixedSize(true);

        // request= Volley.newRequestQueue(getContext());

        cargarWebService();

    }

    private void cargarWebService() {

        progress = new ProgressDialog(TrabajosActivity.this);
        progress.setMessage("Consultando...");
        progress.show();

        SharedPreferences preferences2 = getSharedPreferences("preferenciacrq", Context.MODE_PRIVATE);
        crq = preferences2.getString("crq", "micorreo@gmail.com");

        String url = "http://apphdin.mnperu.com/apphdin/lista_trabajos.php?codigo=" + crq;

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        // request.add(jsonObjectRequest);
        VolleySingleton.getIntanciaVolley(TrabajosActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(TrabajosActivity.this, "No se puede conectar " + error.toString(), Toast.LENGTH_LONG).show();
        System.out.println();
        Log.d("ERROR: ", error.toString());
        progress.hide();
    }

    @Override
    public void onResponse(JSONObject response) {

        TrabajosVO usuario;

        JSONArray json = response.optJSONArray("trabajo");

        try {

            for (int i = 0; i < json.length(); i++) {
                usuario = new TrabajosVO();
                JSONObject jsonObject;
                jsonObject = json.getJSONObject(i);

                usuario.setDocumento(jsonObject.optString("idmanto"));
                usuario.setNombre(jsonObject.optString("sistemas") + " " + jsonObject.optString("sala"));
                usuario.setProfesion(jsonObject.optString("codigo_siten") + " " + jsonObject.optString("nombre_site"));

                listaUsuarios.add(usuario);
            }
            progress.hide();
            Adaptadortrabajos adapter = new Adaptadortrabajos(listaUsuarios);

            adapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    manto = listaUsuarios.get
                            (recyclerUsuarios.getChildAdapterPosition(view))
                            .getDocumento();
                    SharedPreferences preferences2 = getSharedPreferences("preferenciamanto", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor2 = preferences2.edit();
                    editor2.putString("manto", manto);

                    editor2.putBoolean("sesion5", true);
                    editor2.commit();
                    Intent intent = new Intent(getApplicationContext(), FotoActivity.class);
                    startActivity(intent);
                }
            });
            recyclerUsuarios.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(TrabajosActivity.this, "No se ha podido establecer conexión con el servidor" +
                    " " + response, Toast.LENGTH_LONG).show();
            progress.hide();
        }
    }
}