package com.uth.myapplication;

public class ImageData {
    private int id; //el ide del lista
    private String imageBase64; // string donde pasara cadena de base64
    private String description; // descripcioon tipo txt

    private String imagePath;  // ruta de la imagen

    public ImageData(int id, String imageBase64, String description, String imagePath) {
        this.id = id;
        this.imageBase64 = imageBase64;
        this.description = description;
        this.imagePath = imagePath;
    }

    public int getId() {
        return id;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


}
