package com.unnamed.dailynews;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.JsonTreeReader;
import com.jaunt.HttpRequest;
import com.jaunt.HttpResponse;
import com.unnamed.dailynews.GetData.CheckPage;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Verify extends AppCompatActivity{

    private Intent rssIntent;
    private Context context;
    private Button checkBtn;
    private ProgressBar bar;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_rss:
                    startActivity(rssIntent);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        context = this;
        rssIntent = new Intent(this, MainActivity.class);
        rssIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().findItem(R.id.navigation_check).setChecked(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        checkBtn = (Button) findViewById(R.id.check_btn);
        final EditText newsLink = (EditText) findViewById(R.id.news_link);
        bar = (ProgressBar) findViewById(R.id.progressBar);

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    final String link = newsLink.getText().toString();
                    final URL newsURL = new URL(link);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        checkBtn.setVisibility(Button.INVISIBLE);
                                        bar.setVisibility(ProgressBar.VISIBLE);
                                    }
                                });
                                Looper.prepare();
                                String result = new CheckPage().execute(newsURL).get(3000, TimeUnit.MILLISECONDS);
                                //TODO Intent per esito del test
                                try {
                                    HttpURLConnection connection = (HttpURLConnection) new URL("http://10.17.1.112:5000/"+link).openConnection();
                                    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
                                    connection.setRequestMethod(connection.getRequestMethod());
                                    connection.setDoInput(true);
                                    connection.setDoOutput(true);
                                    connection.setReadTimeout(10000);
                                    connection.setConnectTimeout(15000);
                                    connection.connect();

                                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                                    String message ="";
                                    String line="";
                                    while((line=reader.readLine())!=null)
                                        System.out.print(line);
                                    int res = connection.getResponseCode();
                                    connection.disconnect();
                                }catch(MalformedURLException e){
                                    e.printStackTrace();
                                }catch(IOException e){
                                    e.printStackTrace();
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        checkBtn.setVisibility(Button.VISIBLE);
                                        bar.setVisibility(ProgressBar.INVISIBLE);
                                    }
                                });
                            }catch(TimeoutException e){
                                Toast.makeText(context, "Request timed out", Toast.LENGTH_SHORT).show();
                            }catch(ExecutionException e){
                                Toast.makeText(context, "ExecutionException incurred", Toast.LENGTH_SHORT).show();
                            }catch(InterruptedException e){
                                Toast.makeText(context, "InterruptedException incurred", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).start();
                }catch(Exception e){
                    Toast.makeText(context, "Malformed url", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
