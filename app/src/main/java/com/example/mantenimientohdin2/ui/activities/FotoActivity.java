package com.example.mantenimientohdin2.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.mantenimientohdin2.R;
import com.example.mantenimientohdin2.helper.Util;
import com.example.mantenimientohdin2.helper.VolleySingleton;


import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FotoActivity extends AppCompatActivity {

    private static final String CARPETA_PRINCIPAL = "misImagenesApp/";//directorio principal
    private static final String CARPETA_IMAGEN = "imagenes";//carpeta donde se guardan las fotos
    private static final String DIRECTORIO_IMAGEN = CARPETA_PRINCIPAL + CARPETA_IMAGEN;//ruta carpeta de directorios
    private String path;//almacena la ruta de la imagen
    File fileImagen;
    Bitmap bitmap;

    private final int MIS_PERMISOS = 100;
    private static final int COD_SELECCIONA = 10;
    private static final int COD_FOTO = 20;

    EditText campoNombre, campoDocumento, campoProfesion;
    Button botonRegistro, btnFoto, btnguarda;
    ImageView imgFoto;
    ProgressDialog progreso;
    String crq, manto;
    RelativeLayout layoutRegistrar;//permisos

    // RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    StringRequest stringRequest;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MIS_PERMISOS) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {//el dos representa los 2 permisos
                Toast.makeText(FotoActivity.this, "Permisos aceptados", Toast.LENGTH_SHORT);
                btnFoto.setEnabled(true);
            }
        } else {
            solicitarPermisosManual();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case COD_SELECCIONA:
                if (data != null) {
                    Uri miPath = data.getData();
                    imgFoto.setImageURI(miPath);

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(FotoActivity.this.getContentResolver(), miPath);
                        imgFoto.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case COD_FOTO:
                MediaScannerConnection.scanFile(FotoActivity.this, new String[]{fileImagen.getAbsolutePath()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i("Path", "" + path);
                            }
                        });

                bitmap = BitmapFactory.decodeFile(fileImagen.getAbsolutePath());
                imgFoto.setImageBitmap(bitmap);

                break;
        }
        bitmap = redimensionarImagen(bitmap, 600, 800);
    }


    private Bitmap redimensionarImagen(Bitmap bitmap, float anchoNuevo, float altoNuevo) {

        int ancho = bitmap.getWidth();
        int alto = bitmap.getHeight();

        if (ancho > anchoNuevo || alto > altoNuevo) {
            float escalaAncho = anchoNuevo / ancho;
            float escalaAlto = altoNuevo / alto;

            Matrix matrix = new Matrix();
            matrix.postScale(escalaAncho, escalaAlto);

            return Bitmap.createBitmap(bitmap, 0, 0, ancho, alto, matrix, false);

        } else {
            return bitmap;
        }
    }

    private void solicitarPermisosManual() {

        final CharSequence[] opciones = {"si", "no"};
        final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(FotoActivity.this);//estamos en fragment
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("si")) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", FotoActivity.this.getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                } else {
                    Toast.makeText(FotoActivity.this, "Los permisos no fueron aceptados", Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto);
        btnFoto = (Button) FotoActivity.this.findViewById(R.id.btnFoto);
        btnguarda = (Button) FotoActivity.this.findViewById(R.id.btnguardar);
        imgFoto = (ImageView) FotoActivity.this.findViewById(R.id.imgFoto);
        SharedPreferences preferences2 = getSharedPreferences("preferenciamanto", Context.MODE_PRIVATE);
        manto = preferences2.getString("manto", "micorreo@gmail.com");
        Toast.makeText(FotoActivity.this, "Codigo Manto:" + manto, Toast.LENGTH_SHORT).show();
        if (solicitaPermisosVersionesSuperiores()) {
            btnFoto.setEnabled(true);
        } else {
            btnFoto.setEnabled(false);
        }

        btnguarda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarWebService();
            }
        });
        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogOpciones();
            }


            private void mostrarDialogOpciones() {

                final CharSequence[] opciones = {"Tomar Foto", "Elegir de Galeria", "Cancelar"};
                final AlertDialog.Builder builder = new AlertDialog.Builder(FotoActivity.this);
                builder.setTitle("Elige una Opción");
                builder.setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (opciones[i].equals("Tomar Foto")) {
                            createImage();
                        } else {
                            if (opciones[i].equals("Elegir de Galeria")) {
                                Intent intent = new Intent(Intent.ACTION_PICK,
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                intent.setType("image/");
                                startActivityForResult(intent.createChooser(intent, "Seleccione"), COD_SELECCIONA);
                            } else {
                                dialogInterface.dismiss();
                            }
                        }
                    }
                });
                builder.show();
            }

            private void createImage() {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(Objects.requireNonNull(getApplicationContext()).getPackageManager()) != null) {
                    long consecutivo = System.currentTimeMillis() / 1000;
                    String nombre = consecutivo + ".jpg";
                    fileImagen = new File(Util.getFolder(getApplicationContext()), nombre);

                    Uri uriSavedImage = Uri.fromFile(fileImagen);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                    if (Build.VERSION.SDK_INT >= 24) {
                        try {
                            String valor = "disableDeathOnFileUriExposure";
                            Method m = StrictMode.class.getMethod(valor);
                            m.invoke(null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    startActivityForResult(takePictureIntent, COD_FOTO);
                }
            }

//            private void abriCamara() {
//                File miFile = new File(Environment.getExternalStorageDirectory(), DIRECTORIO_IMAGEN);
//                boolean isCreada = miFile.exists();
//
//                if (isCreada == false) {
//                    isCreada = miFile.mkdirs();
//                }
//
//                if (isCreada == true) {
//
//                    //Toast.makeText(Foto.this, "SE CREO EL DIRECTORIO", Toast.LENGTH_SHORT).show();
//                    Long consecutivo = System.currentTimeMillis() / 1000;
//                    String nombre = consecutivo.toString() + ".jpg";
//
//                    path = Environment.getExternalStorageDirectory() + File.separator + DIRECTORIO_IMAGEN
//                            + File.separator + nombre;//indicamos la ruta de almacenamiento
//
//                    fileImagen = new File(path);
//
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImagen));
//
//                    ////
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        String authorities = FotoActivity.this.getPackageName() + ".provider";
//                        Uri imageUri = FileProvider.getUriForFile(FotoActivity.this, authorities, fileImagen);
//                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                    } else {
//                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImagen));
//                    }
//                    startActivityForResult(intent, COD_FOTO);
//
//                    ////
//
//                }
//
//            }


        });


    }

    public boolean solicitaPermisosVersionesSuperiores() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {//validamos si estamos en android menor a 6 para no buscar los permisos
            return true;
        }

        //validamos si los permisos ya fueron aceptados
        if ((FotoActivity.this.checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) && FotoActivity.this.checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }


        if ((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE) || (shouldShowRequestPermissionRationale(CAMERA)))) {
            cargarDialogoRecomendacion();
        } else {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MIS_PERMISOS);
        }

        return false;
    }


    private void cargarWebService() {

        progreso = new ProgressDialog(FotoActivity.this);
        progreso.setMessage("Cargando...");
        progreso.show();


        String url = "http://apphdin.mnperu.com/apphdin/guardaimagen.php?";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                progreso.hide();

                if (response.trim().equalsIgnoreCase("registra")) {

                    Toast.makeText(FotoActivity.this, "Se ha registrado con exito", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FotoActivity.this, "No se ha registrado ", Toast.LENGTH_SHORT).show();
                    Log.i("RESPUESTA: ", "" + response);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FotoActivity.this, "No se ha podido conectar", Toast.LENGTH_SHORT).show();
                progreso.hide();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                SharedPreferences preferences2 = getSharedPreferences("preferenciacrq", Context.MODE_PRIVATE);
                crq = preferences2.getString("crq", "micorreo@gmail.com");

                String imagen = convertirImgString(bitmap);

                Map<String, String> parametros = new HashMap<>();

                parametros.put("crq", crq);
                parametros.put("imagen", imagen);

                return parametros;
            }
        };
        //request.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(FotoActivity.this).addToRequestQueue(stringRequest);
    }


    private String convertirImgString(Bitmap bitmap) {

        ByteArrayOutputStream array = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, array);
        byte[] imagenByte = array.toByteArray();
        String imagenString = Base64.encodeToString(imagenByte, Base64.DEFAULT);

        return imagenString;
    }

    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(FotoActivity.this);
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, 100);
            }
        });
        dialogo.show();
    }
}