package com.example.recognition;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by victor on 28.11.2018.
 */


interface IDataConvert
{
    void setData(final String str);
    String toString();
}

class ProgressTask extends AsyncTask<String, Void, String> {

    IDataConvert data = null;
    private final Context context;

    public ProgressTask(IDataConvert data, Context context)
    {
        this.data = data;
        this.context = context;
    }

    @Override
    protected String doInBackground(String... path) {

        String content;
        try{
            content = getContent(path[0]);
        }
        catch (IOException ex){
            content = ex.getMessage();
            return null;
        }
        return content;
    }

    boolean postExecute(String content){
        if (data == null) return false;
        if(content == null){
            Toast.makeText(context, "Запрос не выполнен", Toast.LENGTH_LONG).show();
            return false;
        }
        data.setData(content);
        return true;
    }

    @Override
    protected void onPostExecute(String content) {

        postExecute(content);

    }

    private String getContent(String path) throws IOException {
        BufferedReader reader=null;
        try {
            URL url=new URL(path);
            HttpURLConnection c=(HttpURLConnection)url.openConnection();
            c.setRequestMethod("GET");
            c.setReadTimeout(10000);
            c.connect();
            reader= new BufferedReader(new InputStreamReader(c.getInputStream()));
            StringBuilder buf=new StringBuilder();
            String line=null;
            while ((line=reader.readLine()) != null) {
                buf.append(line).append("\n");
            }
            return(buf.toString());
        }
        finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}

