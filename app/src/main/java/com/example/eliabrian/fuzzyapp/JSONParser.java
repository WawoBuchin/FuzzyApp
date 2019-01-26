package com.example.eliabrian.fuzzyapp;

import android.util.Log;
import android.util.Pair;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

public class JSONParser {
    static InputStream inputStream = null;
    static JSONObject jsonObject = null;
    static String json = "";
    static int response = -1;

    public JSONParser(){
        //Empty Constructor
    }

    public static JSONObject getJsonObject(String url, String method, List<Pair<String, String>> params) throws IOException {
        URL urls = new URL(url);
        URLConnection conn = urls.openConnection();

        if(!(conn instanceof HttpURLConnection)){
            throw new IOException("Not a HTTP Connection");
        }

        try{
            HttpURLConnection httpURLConnection = (HttpURLConnection)conn;
            httpURLConnection.setAllowUserInteraction(false);
            httpURLConnection.setInstanceFollowRedirects(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(getQuery(params));
            Log.d("DEBUG", getQuery(params));
            writer.flush();
            writer.close();
            outputStream.close();
            httpURLConnection.connect();
            response = httpURLConnection.getResponseCode();

            if (response == HttpURLConnection.HTTP_OK){
                inputStream = httpURLConnection.getInputStream();
            }

        }catch (Exception e){
            Log.e("Networking", e.getLocalizedMessage());
            Log.e("Networking", "Error Boi " + e.toString());
            throw new IOException("Error Connecting");
        }

        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null){
                stringBuilder.append(line+"\n");
            }

            inputStream.close();
            json = stringBuilder.toString();
        }catch (Exception e){
            Log.e("Buffered error", "Error converting result" + e.toString());
        }

        try{
            Log.e("Buffer Errori", json);
            jsonObject = new JSONObject(json);
        }catch (Exception e){
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        return jsonObject;
    }

    private static String getQuery(List<Pair<String,String>> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for(Pair<String, String>pair : params){
            result.append("&");
            result.append(URLEncoder.encode(pair.first, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.second, "UTF-8"));
        }
        return result.toString();
    }
}
