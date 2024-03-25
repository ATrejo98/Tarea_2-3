package com.uth.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class ActivityListaFotos extends AppCompatActivity {

    ListView listadofotos;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_fotos);

        listadofotos = (ListView) findViewById(R.id.ListaFotos);

        dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Imagenes", null);

        ArrayList<ImageData> dataList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String imageBase64 = cursor.getString(cursor.getColumnIndexOrThrow("imagen"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("ruta"));


                dataList.add(new ImageData(id, imageBase64, description, imagePath));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // Configurar el adaptador personalizado
        CustomAdapter adapter = new CustomAdapter(this, dataList);

        // Configurar el ListView
        ListView listView = findViewById(R.id.ListaFotos);
        listView.setAdapter(adapter);

        listadofotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Obtener los datos del elemento seleccionado
                ImageData imageData = dataList.get(position);

                // Crear un intent para ir a MainActivity
                Intent intent = new Intent(ActivityListaFotos.this, MainActivity.class);

                // Pasar la ruta de la imagen y la descripción a través del intent
                intent.putExtra("imagePath", imageData.getImagePath());
                intent.putExtra("description", imageData.getDescription());
                intent.putExtra("elementSelected", true);

                startActivity(intent);
            }
        });



    }
}