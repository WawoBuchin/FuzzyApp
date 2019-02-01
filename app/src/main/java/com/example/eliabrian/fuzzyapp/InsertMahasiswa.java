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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class InsertMahasiswa extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private ProgressDialog progressDialog;
    JSONParser jsonParser = new JSONParser();
    JSONParser jsonParser1 = new JSONParser();
    TextInputEditText mNim, mNama, mSms;
    JSONArray jurusan = null;
    FloatingActionButton mSave;
    Spinner spinner;
    String  sNim, sNama, sSms, sjur;
    JSONObject jsonObject = null;
    ArrayList<String> listJurusan;

    private static String url_login = "http://192.168.0.101/PHP%20Beasiswa/create_mahasiswa.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_DEBUG = "DEBUG";
    private static final String TAG_NETWORK = "NETWORK";

    private static String url_all_jurusan = "http://192.168.0.101/PHP%20Beasiswa/read_jurusan.php";
    private static final String TAG_JURUSAN = "jurusan";
    private static final String TAG_KODE_JURUSAN = "kode_jurusan";
    private static final String TAG_NAMA_JURUSAN = "nama_jurusan";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_mahasiswa);
        mNim = (TextInputEditText)findViewById(R.id.itNim);
        mNama = (TextInputEditText)findViewById(R.id.itNama);
        mSms = (TextInputEditText)findViewById(R.id.itSms);
        mSave = (FloatingActionButton)findViewById(R.id.fab);
        listJurusan=new ArrayList<>();
        mSave.setOnClickListener(this);

        // Spinner element
        spinner = (Spinner)findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        new loadJurusan().execute();
        Log.d("SPinner", "SUKSES SPINER");
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab:
                sNim = mNim.getText().toString();
                sNama = mNama.getText().toString();
                sSms = mSms.getText().toString();
                new insertProcess().execute();

                onBackPressed();

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getId() == R.id.spinner){
            sjur = adapterView.getItemAtPosition(i).toString();
            Toast.makeText(adapterView.getContext(),"You selected : " + sjur, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private class loadJurusan extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(InsertMahasiswa.this);
            progressDialog.setMessage("Loading all jurusan, please wait...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            List<Pair<String, String>> args = new ArrayList<Pair<String, String>>();
            try{
                jsonObject = jsonParser.getJsonObject(url_all_jurusan, "POST", args);
                Log.d("inijson", jsonObject.toString());
                 }catch(IOException e){
                Log.d(TAG_NETWORK, e.getLocalizedMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s){
            progressDialog.dismiss();
            try {
                Log.d("lala", jsonObject.toString());
                jurusan = jsonObject.getJSONArray("jurusan");
                Log.d("jsonarrya", jurusan.toString());
                for (int i = 0; i < jurusan.length(); i++) {

                    JSONObject a = jurusan.getJSONObject(i);
                    String kdjur = a.getString("kode_jurusan");
                    Log.d("country", kdjur + jurusan.length());
                    listJurusan.add(kdjur);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            spinner.setAdapter(new ArrayAdapter<String>(InsertMahasiswa.this, android.R.layout.simple_spinner_dropdown_item, listJurusan));
        }
    }

    private class insertProcess extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            Log.d(TAG_DEBUG, "Pre Execute insert");
            progressDialog = new ProgressDialog(InsertMahasiswa.this);
            progressDialog.setMessage("Inserting...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG_DEBUG, "Do In Background insert");
            List<Pair<String, String>> args = new ArrayList<Pair<String, String>>();
            args.add(new Pair<>("nim", sNim));
            args.add(new Pair<>("nama_mahasiswa", sNama));
            args.add(new Pair<>("kode_jurusan", sjur));
            args.add(new Pair<>("semester", sSms));
            Log.d("YAKIN", args.toString());
            JSONObject jsonObject1 = null;
            try{
                Log.d("YAKIN", "Sampe sini");
                jsonObject1 = jsonParser1.getJsonObject(url_login, "POST", args);

                Log.d("JSONINSERT", jsonObject1.toString());
            }catch (IOException e){
                Log.d(TAG_NETWORK, e.getLocalizedMessage()+"yaaa");
            }
            Log.d(TAG_DEBUG, jsonObject1.toString());
            try{
                int success = jsonObject1.getInt(TAG_SUCCESS);
                if(success == 1){
                    //adapter.notifyDataSetChanged();
                    //Intent i = new Intent(getApplicationContext(), MahasiswaActivity.class);
                    //startActivity(i);
                    //finish();
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



    @Override
    public void onBackPressed() {
        //Intent i = new Intent(getBaseContext(), MahasiswaActivity.class);
        //startActivity(i);
        super.onBackPressed();
    }

    @Override
    public void onRestart()
    {  // After a pause OR at startup
        super.onRestart();
        Intent i = new Intent(getBaseContext(), MahasiswaActivity.class);
        startActivity(i);
    }
}

