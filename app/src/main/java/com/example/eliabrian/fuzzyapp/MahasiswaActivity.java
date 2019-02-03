package com.example.eliabrian.fuzzyapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MahasiswaActivity extends AppCompatActivity implements View.OnClickListener {
    FloatingActionButton mInsert;
    JSONParser jsonParser = new JSONParser();
    ListView listView;
    ArrayList<HashMap<String, String>> mahasiswaList;
    ProgressDialog progressDialog;
    JSONArray mahasiswa = null;

    private static String url_all_mahasiswa = "http://10.0.2.2/PHP%20Beasiswa/read_mahasiswa.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MAHASISWA = "mahasiswa";
    private static final String TAG_NIM_MAHASISWA = "nim";
    private static final String TAG_NAMA_MAHASISWA = "nama_mahasiswa";
    private static final String TAG_KODE_JURUSAN = "kode_jurusan";
    private static final String TAG_SEMESTER = "semester";

    private static final String TAG_NETWORK = "NETWORK";
    private static final String TAG_DEBUG = "DEBUG";
    private static final String TAG_POSISI = "POSISI";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mahasiswa);
        listView = (ListView)findViewById(R.id.listMahasiswa);
        mahasiswaList = new ArrayList<>();
        mInsert = (FloatingActionButton)findViewById(R.id.fab);
        mInsert.setOnClickListener(this);
        new loadMahasiswa().execute();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG_DEBUG, "On Click");
                int pos = listView.getPositionForView(view);
                Log.d(TAG_POSISI, String.valueOf(pos));
                String nim = ((TextView)view.findViewById(R.id.tvNim)).getText().toString();
                Intent i = new Intent(getApplicationContext(), EditMahasiswa.class);
                i.putExtra(TAG_POSISI, pos);
                i.putExtra(TAG_NIM_MAHASISWA, nim);
                startActivityForResult(i, 100);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                Intent i = new Intent(this, InsertMahasiswa.class);
                startActivity(i);
                break;
        }
    }

    private class loadMahasiswa extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(MahasiswaActivity.this);
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
                jsonObject = jsonParser.getJsonObject(url_all_mahasiswa, "POST", args);
            }catch(IOException e){
                Log.d(TAG_NETWORK, e.getLocalizedMessage());
            }
            Log.d(TAG_MAHASISWA, jsonObject.toString());
            try{
                int success = jsonObject.getInt(TAG_SUCCESS);
                if(success == 1){
                    mahasiswa = jsonObject.getJSONArray(TAG_MAHASISWA);
                    for (int i = 0; i<mahasiswa.length(); i++){
                        try{
                            JSONObject c = mahasiswa.getJSONObject(i);
                            String nim = c.getString(TAG_NIM_MAHASISWA);
                            String nama = c.getString(TAG_NAMA_MAHASISWA);
                            String kode = c.getString(TAG_KODE_JURUSAN);
                            String sms = c.getString(TAG_SEMESTER);
                            HashMap<String, String>map = new HashMap<String, String>();
                            map.put(TAG_NIM_MAHASISWA, nim);
                            map.put(TAG_NAMA_MAHASISWA, nama);
                            map.put(TAG_KODE_JURUSAN, kode);
                            map.put(TAG_SEMESTER, sms);
                            mahasiswaList.add(map);
                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                }
                Log.d(TAG_DEBUG , mahasiswaList.toString());
            }catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s){
            progressDialog.dismiss();
            ListAdapter adapter = new SimpleAdapter(MahasiswaActivity.this, mahasiswaList, R.layout.mahasiswa_menu, new String[]{TAG_NIM_MAHASISWA, TAG_NAMA_MAHASISWA, TAG_KODE_JURUSAN}, new int[]{R.id.tvNim, R.id.tvNama, R.id.tvKode,});
            listView.setAdapter(adapter);

        }
    }
}
