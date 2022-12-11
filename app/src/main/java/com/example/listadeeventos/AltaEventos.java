package com.example.listadeeventos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;


import java.text.ParseException;

public class AltaEventos extends Activity implements View.OnClickListener {

    private static final int FOTO_EVENTO = 1;
    private String accion;
    private long idEvento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_eventos);

        Button btAlta = findViewById(R.id.btAlta);
        btAlta.setOnClickListener(this);
        Button btCancelar = findViewById(R.id.btCancelar);
        btCancelar.setOnClickListener(this);
        ImageButton ibImagen = findViewById(R.id.ibImagen);
        ibImagen.setOnClickListener(this);

        accion = getIntent().getStringExtra("accion");
        if (accion.equals("modificar")) {
            Evento evento = (Evento) getIntent().getSerializableExtra("evento");
            Bitmap imagenEvento = Util.getBitmap(getIntent().getByteArrayExtra("imagen"));
            rellenarDatos(evento, imagenEvento);
            btAlta.setText(R.string.guardar);
        }
    }

    private void rellenarDatos(Evento evento, Bitmap imagenEvento) {
        EditText etNombre = findViewById(R.id.etNombre);
        EditText etDescripcion = findViewById(R.id.etDescripcion);
        EditText etDireccion = findViewById(R.id.etDireccion);
        EditText etFecha = findViewById(R.id.etFecha);
        EditText etPrecio = findViewById(R.id.etPrecio);
        EditText etAforo = findViewById(R.id.etAforo);
        ImageButton ibImagen = findViewById(R.id.ibImagen);

        etNombre.setText(evento.getNombre());
        etDescripcion.setText(evento.getDescripcion());
        etDireccion.setText(evento.getDireccion());
        etFecha.setText(Util.formatearFecha(evento.getFecha()));
        etPrecio.setText(String.valueOf(evento.getPrecio()));
        etAforo.setText(String.valueOf(evento.getAforo()));
        ibImagen.setImageBitmap(imagenEvento);

        idEvento = evento.getId();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btAlta:
                EditText etNombre = findViewById(R.id.etNombre);
                EditText etDescripcion = findViewById(R.id.etDescripcion);
                EditText etDireccion = findViewById(R.id.etDireccion);
                EditText etFecha = findViewById(R.id.etFecha);
                EditText etPrecio = findViewById(R.id.etPrecio);
                EditText etAforo = findViewById(R.id.etAforo);
                ImageButton ibImagen = findViewById(R.id.ibImagen);

                try {
                    if (etPrecio.getText().toString().equals("")) {
                        Toast.makeText(this, R.string.mensaje_precio,
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (etAforo.getText().toString().equals(""))
                        etAforo.setText("0");

                    Evento evento = new Evento();
                    evento.setNombre(etNombre.getText().toString());
                    evento.setDescripcion(etDescripcion.getText().toString());
                    evento.setDireccion(etDireccion.getText().toString());
                    evento.setFecha(Util.parsearFecha(etFecha.getText().toString()));
                    evento.setPrecio(Float.parseFloat(etPrecio.getText().toString()));
                    evento.setAforo(Integer.parseInt(etAforo.getText().toString()));
                    try{
                        evento.setImagen(((BitmapDrawable) ibImagen.getDrawable()).getBitmap());
                    } catch (Exception e) {
                        Log.e("Error", "Error al cargar la imagen");
                        //evento.setImagen(getBitmap(R.drawable.ic_launcher_background));
                        evento.setImagen(getBitmapFromVectorDrawable(this, R.drawable.ic_launcher_background));
                    }


                    Database db = new Database(this);
                    switch (accion) {
                        case "nuevo":
                            db.nuevoEvento(evento);
                            break;
                        case "modificar":
                            evento.setId(idEvento);
                            db.modificarEvento(evento);
                            break;
                        default:
                            break;
                    }

                    Toast.makeText(this, "El evento " + evento.getNombre() +
                            " ha sido guardado", Toast.LENGTH_LONG).show();

                    etNombre.setText("");
                    etNombre.requestFocus();
                    etDescripcion.setText("");
                    etDireccion.setText("");
                    etPrecio.setText("");
                    etAforo.setText("");
                    etFecha.setText("");
                } catch (ParseException pe) {
                    Toast.makeText(this, "Formato de fecha no válido, formato esperado: dd.MM.yyyy", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btCancelar:
                onBackPressed();
                break;
            case R.id.ibImagen:
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, FOTO_EVENTO);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if ((resultCode == RESULT_OK) && (data != null)) {

            switch (requestCode) {
                case FOTO_EVENTO:
                    // Obtiene el Uri de la imagen seleccionada por el usuario
                    Uri imagenSeleccionada = data.getData();
                    String[] ruta = {MediaStore.Images.Media.DATA };

                    // Realiza una consulta a la galería de imágenes solicitando la imagen seleccionada
                    Cursor cursor = getContentResolver().query(imagenSeleccionada, ruta, null, null, null);
                    cursor.moveToFirst();

                    // Obtiene la ruta a la imagen
                    int indice = cursor.getColumnIndex(ruta[0]);
                    String picturePath = cursor.getString(indice);
                    cursor.close();

                    // Carga la imagen en una vista ImageView que se encuentra en
                    // en layout de la Activity actual
                    ImageButton ibImagen = findViewById(R.id.ibImagen);
                    ibImagen.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                    break;
                default:
                    break;
            }
        }
    }
    private Bitmap getBitmap(int drawableRes){
        Drawable drawable = ContextCompat.getDrawable(this, drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());

        drawable.draw(canvas);
        return bitmap;
    }
    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}