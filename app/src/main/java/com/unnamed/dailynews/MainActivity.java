package com.unnamed.dailynews;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.unnamed.dailynews.GetData.DownloadImage;
import com.unnamed.dailynews.GetData.Parse;
import com.unnamed.dailynews.GetData.SendVote;
import com.unnamed.dailynews.GetData.Server;
import com.unnamed.dailynews.Ui.NewsAdapter;
import com.unnamed.dailynews.WebScraper.XMLHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity {
    final String rssFeed = "https://news.google.com/news/rss/?ned=it&gl=IT&hl=it";

    private Intent searchIntent;
    private String result, news;
    private Context context;
    ArrayList<Data> dataObjs;
    NewsAdapter newsAdapter;
    BottomNavigationView navigation;


    @Override
    public FileInputStream openFileInput(String name) throws FileNotFoundException {
        return super.openFileInput(name);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loadData(final String rssFeed){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL url = new URL(rssFeed);
                    news = "";
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    BufferedReader rssReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line = "";
                    while((line=rssReader.readLine())!=null){
                        news+=line;
                    }
                    while(news.contains("<item>")){
                        news = news.substring(news.indexOf("<item>") + 6);
                        String item = news.substring(0, news.indexOf("</item>") + 7);
                        try {
                            String newData = new Parse().execute(item).get();
                            System.out.println(newData);
                            Data dataObj = new Gson().fromJson(newData, Data.class);
                            try {
                                dataObj.setBitmap(new DownloadImage().execute(dataObj.getImage()).get());
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            dataObjs.add(dataObj);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    newsAdapter.notifyDataSetChanged();
                                }
                            });
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_check:
                    startActivity(searchIntent);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().findItem(R.id.navigation_rss).setChecked(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = MainActivity.this;

        searchIntent = new Intent(this, Verify.class);
        searchIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        final ListView list = (ListView) findViewById(R.id.news_list);
        list.setDivider(null);

        final SwipeRefreshLayout swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Looper.prepare();
                            HttpURLConnection connection = (HttpURLConnection) new URL(Server.REST_SERVER_URL).openConnection();
                            connection.setConnectTimeout(1000);
                            connection.connect();
                            loadData(rssFeed);
                        }catch(IOException e){
                            if(e instanceof SocketTimeoutException)
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Can't connect to server, try refreshing.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            e.printStackTrace();
                        }
                        swipe.setRefreshing(false);
                    }
                }).start();
            }
        });

        dataObjs = new ArrayList<>();
        newsAdapter = new NewsAdapter(context, dataObjs);
        list.setAdapter(newsAdapter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Looper.prepare();
                    HttpURLConnection connection = (HttpURLConnection) new URL(Server.REST_SERVER_URL).openConnection();
                    connection.setConnectTimeout(1000);
                    connection.connect();
                    loadData(rssFeed);
                }catch(IOException IO){
                    if(IO.getCause() instanceof SocketTimeoutException)
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Can't connect to server, try refreshing.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    IO.printStackTrace();
                }
            }
        }).start();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Data entity = (Data) adapterView.getItemAtPosition(i);
                String link = entity.getUrl();
                Intent custom = new Intent(MainActivity.this, CustomTabs.class);
                custom.putExtra("URL", link);
                startActivity(custom);
            }
        });
        registerForContextMenu(list);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId()==R.id.news_list){
            ListView lv = (ListView) v;
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Data obj = (Data) lv.getItemAtPosition(acmi.position);
            MenuInflater inflater = new MenuInflater(this);
            inflater.inflate(R.menu.context_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        System.out.println(dataObjs.get(info.position).getTitle());
        switch(item.getItemId()){
            case R.id.menu_fake:
                sendVote(0);
                return true;
            case R.id.menu_true:
                sendVote(1);
                return true;
            default:
                return false;
        }
    }
    private void sendVote(final int vote){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    result = new SendVote().execute(vote).get(2000, java.util.concurrent.TimeUnit.MILLISECONDS);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }catch(TimeoutException e){
                    e.printStackTrace();
                }catch (ExecutionException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
