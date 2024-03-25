package com.uth.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<ImageData> {

    private ArrayList<ImageData> dataList;
    private Context mContext;

    public CustomAdapter(Context context, ArrayList<ImageData> dataList) {
        super(context, 0, dataList);
        this.dataList = dataList;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        }

        ImageData currentItem = dataList.get(position);

        ImageView imageView = convertView.findViewById(R.id.imageView);
        TextView idTextView = convertView.findViewById(R.id.idTextView);
        TextView descriptionTextView = convertView.findViewById(R.id.descriptionTextView);
        TextView rutaTextView = convertView.findViewById(R.id.rutaTextView);
        TextView base64TextView = convertView.findViewById(R.id.base64TextView);

        // Decodificar la imagen Base64 y establecerla en ImageView
        byte[] decodedString = Base64.decode(currentItem.getImageBase64(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);


        imageView.setImageBitmap(decodedByte);

        idTextView.setText("ID: " + currentItem.getId());
        descriptionTextView.setText("Descripción: " + currentItem.getDescription());
        rutaTextView.setText("Ruta: " + currentItem.getImagePath());
        base64TextView.setText("Base64: " + currentItem.getImageBase64().substring(0, 10) + "...");


        return convertView;
    }
}
