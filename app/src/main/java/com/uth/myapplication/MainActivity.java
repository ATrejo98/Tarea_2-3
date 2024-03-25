package com.uth.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    static  final int REQUEST_IMAGE = 101;
    static final int ACCESS_CAMERA = 201;
    ImageView sVerImagenVariableLocal;
    Button btnListaFotos, btnTomarFoto, btnGuardarFoto, btnEliminarBase;
    String currentPhotoPath, sImagen;
    TextView txtDescrip;
    DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sVerImagenVariableLocal = (ImageView) findViewById(R.id.sVerImagen);
        txtDescrip = (TextView) findViewById(R.id.txtDescripcion) ;
        btnTomarFoto = (Button) findViewById(R.id.btnTomarFoto);
        btnGuardarFoto = (Button) findViewById(R.id.btnGuardarFoto);
        btnListaFotos = (Button) findViewById(R.id.btnListaFotos);

        Intent intent = getIntent();
        if (intent != null) {
            String imagePath = intent.getStringExtra("imagePath");
            String description = intent.getStringExtra("description");
            boolean elementSelected = intent.getBooleanExtra("elementSelected", false);

            // Mostrar la imagen y la descripción
            if (imagePath != null && !imagePath.isEmpty()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                sVerImagenVariableLocal.setImageBitmap(bitmap);
            }
            if (description != null && !description.isEmpty()) {
                txtDescrip.setText(description);
            }

            // Deshabilitar el botón de guardar si se ha seleccionado un elemento
            if (elementSelected) {
                txtDescrip.setEnabled(false);
                btnGuardarFoto.setEnabled(false);
            }
        }


        ///////////////////////////// INICIO DE BOTONES /////////////////////////////

        btnTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermisosCamara();
            }
        });

        btnGuardarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String descripcion = txtDescrip.getText().toString();
                String imagenBase64 = ConvertImageBase64(currentPhotoPath);
                String imagenRuta = currentPhotoPath;
                dbHelper = new DBHelper(MainActivity.this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("imagen", imagenBase64);
                values.put("descripcion", descripcion);
                values.put("ruta", imagenRuta);
                db.insert("Imagenes", null, values);
                db.close();

                txtDescrip.setText("");
                sVerImagenVariableLocal.setImageBitmap(null);

                Toast.makeText(MainActivity.this, "Perfil guardado exitosamente en SQLite", Toast.LENGTH_SHORT).show();

            }
        });

        btnListaFotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentActivityListaFotos = new Intent(MainActivity.this, ActivityListaFotos.class);
                startActivity(intentActivityListaFotos);

            }
        });
    }

    ///////////////////////////// FIN DE BOTONES /////////////////////////////


    ///////////////////////////// INICIO DE METODOS /////////////////////////////
    private void PermisosCamara() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, ACCESS_CAMERA);
        } else {
            dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.toString();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.uth.myapplication.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE);
            }
        }

        btnGuardarFoto.setEnabled(true);
        txtDescrip.setEnabled(true);
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE) {
            try {
                File foto = new File(currentPhotoPath);
                sVerImagenVariableLocal.setImageURI(Uri.fromFile(foto));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    private String ConvertImageBase64(String Path) {
        try {

            // Leer la orientación de la imagen original
            ExifInterface exif = new ExifInterface(Path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            // Rotar la imagen según la orientación
            Bitmap bitmap = BitmapFactory.decodeFile(Path);
            bitmap = rotateBitmap(bitmap, orientation);

            // Convertir la imagen rotada a Base64
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            byte[] imageArray = byteArrayOutputStream.toByteArray();

            sImagen = Base64.encodeToString(imageArray, Base64.DEFAULT);
            return sImagen;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            default:
                return bitmap;
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }



}
