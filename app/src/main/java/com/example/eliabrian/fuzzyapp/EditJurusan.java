package com.example.eliabrian.fuzzyapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditJurusan extends AppCompatActivity implements View.OnClickListener {
    private ProgressDialog progressDialog;
    JSONParser jsonParser = new JSONParser();
    TextInputEditText mKodeJurusan, mNamaJurusan;
    AppCompatButton mSave, mDelete;
    String sKodeJurusan, sNamaJurusan;
    int posisi;

    private static String url_all_jurusan = "http://192.168.43.116:8888/PHP%20Beasiswa/read_jurusan.php";
    private static String url_delete_jurusan = "http://192.168.43.116:8888/PHP%20Beasiswa/delete_jurusan.php";
    private static String url_update_jurusan = "http://192.168.43.116:8888/PHP%20Beasiswa/edit_jurusan.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_JURUSAN = "jurusan";
    private static final String TAG_KODE_JURUSAN = "kode_jurusan";
    private static final String TAG_NAMA_JURUSAN = "nama_jurusan";
    private static final String TAG_NETWORK = "NETWORK";
    private static final String TAG_DEBUG = "DEBUG";
    private static final String TAG_POSISI = "POSISI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_jurusan);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mKodeJurusan = (TextInputEditText)findViewById(R.id.itTitle);
        mNamaJurusan = (TextInputEditText)findViewById(R.id.itDesc);
        mSave = (AppCompatButton) findViewById(R.id.save);
        mDelete = (AppCompatButton)findViewById(R.id.delete);
        mSave.setOnClickListener(this);
        mDelete.setOnClickListener(this);
        Intent i = getIntent();
        sKodeJurusan = i.getStringExtra(TAG_KODE_JURUSAN);
        posisi = i.getIntExtra(TAG_POSISI, 0);
        new getJurusan().execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save:
                sNamaJurusan = mNamaJurusan.getText().toString();
                new updateJurusan().execute();
                break;
            case R.id.delete:
                new deleteJurusan().execute();
                break;
        }
    }
    //Ini untuk mengambil data jurusan yang dimasukkan ke dalam Edit Text
    private class getJurusan extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(EditJurusan.this);
            progressDialog.setMessage("Loading all jurusan, please wait...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG_DEBUG, "Do In Background - Get Jurusan");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG_DEBUG, "Do In Background - Run On UI Thread");
                    int success;
                    try{
                        List<Pair<String, String>> args = new ArrayList<Pair<String, String>>();
                        args.add(new Pair<String, String>(TAG_KODE_JURUSAN, sKodeJurusan));
                        JSONObject jsonObject = null;
                        try{
                            jsonObject = jsonParser.getJsonObject(url_all_jurusan, "POST", args);
                        } catch (IOException e) {
                            Log.d(TAG_DEBUG, e.getLocalizedMessage());
                        }

                        Log.d("Jurusan = ", jsonObject.toString());
                        success = jsonObject.getInt(TAG_SUCCESS);
                        if(success == 1){
                            Log.d(TAG_DEBUG, "Jurusan ditemukan");
                            JSONArray j = jsonObject.getJSONArray(TAG_JURUSAN);
                            JSONObject jurusan = j.getJSONObject(posisi);
                            mKodeJurusan.setText(jurusan.getString(TAG_KODE_JURUSAN));
                            mNamaJurusan.setText(jurusan.getString(TAG_NAMA_JURUSAN));
                        }else{
                            Log.d(TAG_DEBUG, "Jurusan tidak ditemukan");
                        }
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String s){
            progressDialog.dismiss();
        }
    }

    //Ini untuk menyimpan perubahan jurusan
    private class updateJurusan extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(EditJurusan.this);
            progressDialog.setMessage("Updating Jurusan...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG_DEBUG, "Do In Background - Update Jurusan");
            List<Pair<String, String>> args = new ArrayList<Pair<String, String>>();
            args.add(new Pair<String, String>(TAG_KODE_JURUSAN, sKodeJurusan));
            args.add(new Pair<>(TAG_NAMA_JURUSAN, sNamaJurusan));
            JSONObject jsonObject = null;
            try{
                jsonObject = jsonParser.getJsonObject(url_update_jurusan, "POST", args);
            }catch(IOException e){
                Log.d(TAG_NETWORK, e.getLocalizedMessage());
            }
            try{
                int success = jsonObject.getInt(TAG_SUCCESS);
                if (success == 1){
                    Intent i = new Intent(getApplicationContext(), JurusanActivity.class);
                    startActivity(i);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s){
            progressDialog.dismiss();
        }
    }

    private class deleteJurusan extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(EditJurusan.this);
            progressDialog.setMessage("Updating Jurusan...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG_DEBUG, "Do In Background - Update Jurusan");
            List<Pair<String, String>> args = new ArrayList<Pair<String, String>>();
            args.add(new Pair<String, String>(TAG_KODE_JURUSAN, sKodeJurusan));
            JSONObject jsonObject = null;
            try{
                jsonObject = jsonParser.getJsonObject(url_delete_jurusan, "POST", args);
            }catch(IOException e){
                Log.d(TAG_NETWORK, e.getLocalizedMessage());
            }
            try{
                int success = jsonObject.getInt(TAG_SUCCESS);
                if (success == 1){
                    Intent i = new Intent(getApplicationContext(), JurusanActivity.class);
                    startActivity(i);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s){
            progressDialog.dismiss();
        }
    }
}
