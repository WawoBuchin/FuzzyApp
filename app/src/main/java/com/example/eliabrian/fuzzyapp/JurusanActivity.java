package com.example.eliabrian.fuzzyapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
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

public class JurusanActivity extends AppCompatActivity implements View.OnClickListener{
    FloatingActionButton mInsert;
    JSONParser jsonParser = new JSONParser();
    ListView listView;
    ArrayList<HashMap<String, String>>jurusanList;
    ProgressDialog progressDialog;
    JSONArray jurusan = null;

    private static String url_all_jurusan = "http://192.168.43.116:8888/PHP%20Beasiswa/read_jurusan.php";
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
        setContentView(R.layout.activity_jurusan);
        listView = (ListView)findViewById(R.id.listJurusan);
        jurusanList = new ArrayList<>();
        mInsert = (FloatingActionButton)findViewById(R.id.fab);
        mInsert.setOnClickListener(this);
        new loadJurusan().execute();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG_DEBUG, "On Click");
                int pos = listView.getPositionForView(view);
                Log.d(TAG_POSISI, String.valueOf(pos));
                String kode = ((TextView)view.findViewById(R.id.tvKode)).getText().toString();
                Intent i = new Intent(getApplicationContext(), EditJurusan.class);
                i.putExtra(TAG_POSISI, pos);
                i.putExtra(TAG_KODE_JURUSAN, kode);
                startActivityForResult(i, 100);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                Intent i = new Intent(this, InsertJurusan.class);
                startActivity(i);
                break;
        }
    }

    private class loadJurusan extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(JurusanActivity.this);
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
            }catch(IOException e){
                Log.d(TAG_NETWORK, e.getLocalizedMessage());
            }
            Log.d(TAG_JURUSAN, jsonObject.toString());
            try{
                int success = jsonObject.getInt(TAG_SUCCESS);
                if(success == 1){
                    jurusan = jsonObject.getJSONArray(TAG_JURUSAN);
                    for (int i = 0; i<jurusan.length(); i++){
                        try{
                            JSONObject c = jurusan.getJSONObject(i);
                            String kode = c.getString(TAG_KODE_JURUSAN);
                            String nama = c.getString(TAG_NAMA_JURUSAN);
                            HashMap<String, String>map = new HashMap<String, String>();
                            map.put(TAG_KODE_JURUSAN, kode);
                            map.put(TAG_NAMA_JURUSAN, nama);
                            jurusanList.add(map);
                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                }
                Log.d(TAG_DEBUG , jurusanList.toString());
            }catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s){
            progressDialog.dismiss();
            ListAdapter adapter = new SimpleAdapter(JurusanActivity.this, jurusanList, R.layout.jurusan_menu, new String[]{TAG_KODE_JURUSAN, TAG_NAMA_JURUSAN}, new int[]{R.id.tvKode, R.id.tvNama});
            listView.setAdapter(adapter);

        }
    }


}
