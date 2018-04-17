package com.unnamed.dailynews.GetData;

import android.os.AsyncTask;

import java.net.URL;

public class CheckPage extends AsyncTask<URL, Integer, String>{


    @Override
    protected String doInBackground(URL... urls) {
        //TODO connessione api per la pagina
        try{
            Thread.sleep(1000);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        String url = urls[0].toString();
        return url;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String aLong) {

    }
}