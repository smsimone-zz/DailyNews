package com.unnamed.dailynews.WebScraper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.unnamed.dailynews.Data;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class XMLHandler {

    private final String START_TITLE = "<title>";
    private final String END_TITLE = "</title>";
    private final String START_LINK = "<link>";
    private final String END_LINK = "</link>";

    public ArrayList<Data> ParseFull(String xml){
        ArrayList<Data> dataObjs = new ArrayList<>();
        while(xml.contains("<item>")) {
            xml = xml.substring(xml.indexOf("<item>") + 6);
            String item = xml.substring(0, xml.indexOf("</item>") + 7);
            dataObjs.add(fromJson(item.substring(item.indexOf(START_LINK)+6, item.indexOf(END_LINK))));
        }
        return dataObjs;
    }
    public Data ParseSingle(String item){
        return fromJson(item.substring(item.indexOf(START_LINK)+6, item.indexOf(END_LINK)));
    }

    private Data fromJson(String link){
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("http://10.17.1.112:5000/"+link).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            connection.setRequestMethod(connection.getRequestMethod());
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.connect();

            JsonReader reader = new JsonReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            Gson gson = new GsonBuilder().create();

            reader.setLenient(true);
            Data data = gson.fromJson(reader, Data.class);

            reader.close();
            connection.disconnect();
            return data;
        }catch(MalformedURLException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
}