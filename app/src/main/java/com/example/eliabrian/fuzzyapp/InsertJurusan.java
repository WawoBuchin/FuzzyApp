package com.example.eliabrian.fuzzyapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InsertJurusan extends AppCompatActivity implements View.OnClickListener{
    private ProgressDialog progressDialog;
    JSONParser jsonParser = new JSONParser();
    TextInputEditText mKodeJurusan, mNamaJurusan;
    FloatingActionButton mSave;
    String sKodeJurusan, sNamaJurusan;

    private static String url_login = "http://192.168.0.100/PHP%20Beasiswa/create_jurusan.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_DEBUG = "DEBUG";
    private static final String TAG_NETWORK = "NETWORK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_jurusan);
        mKodeJurusan = (TextInputEditText)findViewById(R.id.itTitle);
        mNamaJurusan = (TextInputEditText)findViewById(R.id.itDesc);
        mSave = (FloatingActionButton)findViewById(R.id.fab);
        mSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                sKodeJurusan = mKodeJurusan.getText().toString();
                sNamaJurusan = mNamaJurusan.getText().toString();
                new insertProcess().execute();
                break;
        }
    }

    private class insertProcess extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            Log.d(TAG_DEBUG, "Pre Execute");
            progressDialog = new ProgressDialog(InsertJurusan.this);
            progressDialog.setMessage("Inserting...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG_DEBUG, "Do In Background");
            List<Pair<String, String>> args = new ArrayList<Pair<String, String>>();
            args.add(new Pair<>("kode_jurusan", sKodeJurusan));
            args.add(new Pair<>("nama_jurusan", sNamaJurusan));
            JSONObject jsonObject = null;
            try{
                jsonObject = jsonParser.getJsonObject(url_login, "POST", args);
            }catch (IOException e){
                Log.d(TAG_NETWORK, e.getLocalizedMessage());
            }
            Log.d(TAG_DEBUG, jsonObject.toString());
            try{
                int success = jsonObject.getInt(TAG_SUCCESS);
                if(success == 1){
                    Intent i = new Intent(getApplicationContext(), JurusanActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    Log.d(TAG_DEBUG, "Failed to Insert Jurusan");
                }
            }catch(JSONException e){
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
