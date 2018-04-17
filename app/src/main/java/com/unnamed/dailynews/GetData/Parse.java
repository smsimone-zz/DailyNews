package com.unnamed.dailynews.GetData;

import android.os.AsyncTask;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Parse extends AsyncTask<String, String, String> {
    private final String START_TITLE = "<title>";
    private final String END_TITLE = "</title>";
    private final String START_LINK = "<link>";
    private final String END_LINK = "</link>";

    @Override
    protected String doInBackground(String... strings) {
        String item = strings[0];
        return fromJson(item.substring(item.indexOf(START_LINK)+6, item.indexOf(END_LINK)));
    }

    private String fromJson(String link){
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(Server.REST_SERVER_URL+link).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            connection.setRequestMethod(connection.getRequestMethod());
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
            String line="";
            String message="";
            while((line=reader.readLine())!=null) message+=line;

            reader.close();
            connection.disconnect();
            return message;
        }catch(MalformedURLException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

}
