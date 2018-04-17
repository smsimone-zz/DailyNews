package com.unnamed.dailynews.Ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.unnamed.dailynews.Data;
import com.unnamed.dailynews.GetData.SendVote;
import com.unnamed.dailynews.MainActivity;
import com.unnamed.dailynews.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class NewsAdapter extends ArrayAdapter<Data> implements Serializable{
    Context context;
    ArrayList<Data> data;
    private SendVote vote;
    private DataSetObserver observer;

    public NewsAdapter(Context context, ArrayList<Data> data){
        super(context, 0);
        this.context = context;
        this.data = data;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
        observer = dataSetObserver;
        super.registerDataSetObserver(dataSetObserver);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        super.unregisterDataSetObserver(dataSetObserver);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    public void add(Data obj){
        data.add(obj);
        this.notifyDataSetChanged();
    }

    @Override
    public Data getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View vi = view;
        if(vi==null) vi = LayoutInflater.from(context).inflate(R.layout.list_element, null, false);
        TextView title = (TextView) vi.findViewById(R.id.list_title);
        TextView url = (TextView) vi.findViewById(R.id.list_url);
        ImageView image = (ImageView) vi.findViewById(R.id.imageView);
        try{
            title.setText(data.get(i).getTitle());
            url.setText(data.get(i).getUrl());
            image.setImageBitmap(data.get(i).getBitmap());
        }catch(NullPointerException e){
            e.printStackTrace();
        }
        return vi;
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }



    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int i) {
        return true;
    }
}
