package com.unnamed.dailynews.GetData;

import android.os.AsyncTask;

public class SendVote extends AsyncTask<Integer, Integer, String> {

    @Override
    protected String doInBackground(Integer... integers) {
        //TODO manda voto a server REST
        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
