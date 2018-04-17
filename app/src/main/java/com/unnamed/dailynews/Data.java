package com.unnamed.dailynews;

import android.graphics.Bitmap;

import com.unnamed.dailynews.GetData.DownloadImage;

public class Data {

    private String title, url, image;
    private Bitmap bitmap;

    public Data(String url, String title, String image){
        this.url = url;
        this.title = title;
        this.image = image;
        try {
            this.bitmap = new DownloadImage().execute(image).get();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getImage(){
        return image;
    }

    public void setBitmap(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    public String toString(){
        return "Title: "+ title+", Url: "+ url+", Image: "+image;
    }

}
