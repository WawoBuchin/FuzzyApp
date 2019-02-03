package com.example.eliabrian.fuzzyapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditMahasiswa extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    ProgressDialog progressDialog;
    TextInputEditText mNim, mNama, mSms;
    AppCompatButton mSave, mDelete;
    Spinner mSpinner;
    String sNim, sNama, sSms, sJur;
    int posisi;

    JSONParser jsonParser = new JSONParser();
    JSONArray jurusan = null;
    private ArrayList<String> listJurusan;

    private static String url_all_jurusan = "http://10.0.2.2/PHP%20Beasiswa/read_jurusan.php";
    private static String url_all_mahasiswa = "http://10.0.2.2/PHP%20Beasiswa/read_mahasiswa.php";
    private static String url_edit_mahasiswa = "http://10.0.2.2/PHP%20Beasiswa/edit_mahasiswa.php";
    private static String url_delete_mahasiswa = "http://10.0.2.2/PHP%20Beasiswa/delete_mahasiswa.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_DEBUG = "DEBUG";
    private static final String TAG_NETWORK = "NETWORK";
    private static final String TAG_POSISI = "POSISI";

    private static final String TAG_MAHASISWA = "mahasiswa";
    private static final String TAG_NIM_MAHASISWA = "nim";
    private static final String TAG_NAMA_MAHASISWA = "nama_mahasiswa";
    private static final String TAG_SMS_MAHASISWA = "semester";

    private static final String TAG_JURUSAN = "jurusan";
    private static final String TAG_KODE_JURUSAN = "kode_jurusan";
    private static final String TAG_NAMA_JURUSAN = "nama_jurusan";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mahasiswa);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mNim = (TextInputEditText)findViewById(R.id.itNim);
        mNama = (TextInputEditText)findViewById(R.id.itNama);
        mSms = (TextInputEditText)findViewById(R.id.itSms);
        mSpinner = (Spinner)findViewById(R.id.spinner);
        mSave = (AppCompatButton)findViewById(R.id.save);
        mDelete = (AppCompatButton)findViewById(R.id.delete);
        mSave.setOnClickListener(this);
        mDelete.setOnClickListener(this);
        mSpinner.setOnItemSelectedListener(this);
        listJurusan = new ArrayList<>();

        Intent i = getIntent();
        sNim = i.getStringExtra(TAG_NIM_MAHASISWA);
        posisi = i.getIntExtra(TAG_POSISI, 0);


        new getMahasiswa().execute();
        new loadJurusan().execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save:
                sNama = mNama.getText().toString();
                sSms = mSms.getText().toString();
                new updateMahasiswa().execute();
                break;
            case R.id.delete:
                new deleteMahasiswa().execute();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.spinner){
            sJur = parent.getItemAtPosition(position).toString();
            Log.d(TAG_DEBUG, "You selected : "+ sJur);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class loadJurusan extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(EditMahasiswa.this);
            progressDialog.setMessage("Loading all jurusan, please wait...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            List<Pair<String, String>> args = new ArrayList<Pair<String, String>>();
            JSONObject jsonObject = null;
            try{
                jsonObject = jsonParser.getJsonObject(url_all_jurusan, "POST", args);
                Log.d(TAG_JURUSAN, jsonObject.toString());
            }catch(IOException e){
                Log.d(TAG_NETWORK, e.getLocalizedMessage());
            }
            Log.d(TAG_JURUSAN, jsonObject.toString());
            try{
                int success = jsonObject.getInt(TAG_SUCCESS);
                if (success == 1){
                    jurusan = jsonObject.getJSONArray(TAG_JURUSAN);
                    Log.d(TAG_DEBUG, jurusan.toString());
                    for (int i = 0; i<jurusan.length(); i++){
                        JSONObject j = jurusan.getJSONObject(i);
                        String kodeJur = j.getString(TAG_KODE_JURUSAN);
                        listJurusan.add(kodeJur);
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
            mSpinner.setAdapter(new ArrayAdapter<String>(EditMahasiswa.this, android.R.layout.simple_spinner_dropdown_item, listJurusan));

            return null;
        }

        @Override
        protected void onPostExecute(String s){
            progressDialog.dismiss();
        }
    }
    //TODO: Mengubah data di spinner.
    private void setSpinnerText(Spinner spin, String text){
        for (int i = 0; i< spin.getAdapter().getCount(); i++){
            if (spin.getAdapter().getItem(i).toString().contains(text)){
                spin.setSelection(i);
            }
        }
    }

    private class getMahasiswa extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(EditMahasiswa.this);
            progressDialog.setMessage("Loading all mahasiswa, please wait...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG_DEBUG, "Do In Background - Get Mahasiswa");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG_DEBUG, "Do In Background - Run On UI Thread");
                    int success;
                    try{
                        List<Pair<String, String>> args = new ArrayList<Pair<String, String>>();
                        args.add(new Pair<String, String>(TAG_NIM_MAHASISWA, sNim));
                        JSONObject jsonObject = null;
                        try{
                            jsonObject = jsonParser.getJsonObject(url_all_mahasiswa, "POST", args);
                        } catch (IOException e) {
                            Log.d(TAG_DEBUG, e.getLocalizedMessage());
                        }
                        Log.d("Mahasiswa = ", jsonObject.toString());
                        success = jsonObject.getInt(TAG_SUCCESS);
                        if(success == 1){
                            Log.d(TAG_DEBUG, "Mahasiswa ditemukan");
                            JSONArray j = jsonObject.getJSONArray(TAG_MAHASISWA);
                            JSONObject mahasiswa = j.getJSONObject(posisi);
                            mNim.setText(mahasiswa.getString(TAG_NIM_MAHASISWA));
                            mNama.setText(mahasiswa.getString(TAG_NAMA_MAHASISWA));
                            mSms.setText(mahasiswa.getString(TAG_SMS_MAHASISWA));
                            setSpinnerText(mSpinner, mahasiswa.getString(TAG_KODE_JURUSAN));
                        }else{
                            Log.d(TAG_DEBUG, "Mahasiswa tidak ditemukan");
                        }
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            });
            progressDialog.dismiss();
            return null;
        }

        @Override
        protected void onPostExecute(String s){
            progressDialog.dismiss();
        }
    }

    private class updateMahasiswa extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(EditMahasiswa.this);
            progressDialog.setMessage("Updating mahasiswa, please wait...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            List<Pair<String, String>> args = new ArrayList<Pair<String, String>>();
            args.add(new Pair<String, String>(TAG_NIM_MAHASISWA, sNim));
            args.add(new Pair<String, String>(TAG_KODE_JURUSAN, sJur));
            args.add(new Pair<String, String>(TAG_NAMA_MAHASISWA, sNama));
            args.add(new Pair<String, String>(TAG_SMS_MAHASISWA, sSms));
            JSONObject jsonObject = null;
            try{
                jsonObject = jsonParser.getJsonObject(url_edit_mahasiswa, "POST", args);
            }catch(IOException e){
                Log.d(TAG_NETWORK, e.getLocalizedMessage());
            }
            Log.d(TAG_DEBUG, jsonObject.toString());
            try{
                int success = jsonObject.getInt(TAG_SUCCESS);
                if (success == 1){
                    Intent i = new Intent(getApplicationContext(), MahasiswaActivity.class);
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

    private class deleteMahasiswa extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(EditMahasiswa.this);
            progressDialog.setMessage("Updating mahasiswa, please wait...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            List<Pair<String, String>> args = new ArrayList<Pair<String, String>>();
            args.add(new Pair<String, String>(TAG_NIM_MAHASISWA, sNim));
            JSONObject jsonObject = null;
            try{
                jsonObject = jsonParser.getJsonObject(url_delete_mahasiswa, "POST", args);
            }catch(IOException e){
                Log.d(TAG_NETWORK, e.getLocalizedMessage());
            }
            try{
                int success = jsonObject.getInt(TAG_SUCCESS);
                if (success == 1){
                    Intent i = new Intent(getApplicationContext(), MahasiswaActivity.class);
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
