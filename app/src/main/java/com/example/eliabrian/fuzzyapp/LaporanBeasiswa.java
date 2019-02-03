package com.example.eliabrian.fuzzyapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class LaporanBeasiswa extends AppCompatActivity {
    //FloatingActionButton mInsert;
    JSONParser jsonParser = new JSONParser();
    ListView listView;
    TextView tvnim;
    ArrayList<HashMap<String, String>> laporanList;
    ProgressDialog progressDialog;
    JSONArray laporan = null;
    Object objLaporan;
    String idBeasiswa;

    private static String url_delete_laporan = "http://10.0.2.2/PHP%20Beasiswa/delete_beasiswa.php";
    private static String url_laporan = "http://10.0.2.2/PHP%20Beasiswa/read_beasiswa.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_NETWORK = "NETWORK";
    private static final String TAG_DEBUG = "DEBUG";
    private static final String TAG_NO = "no";
    private static final String TAG_ID = "id_beasiswa";
    private static final String TAG_NIM = "nim";
    private static final String TAG_NAMA = "nama_mahasiswa";
    private static final String TAG_JURUSAN = "nama_jurusan";
    private static final String TAG_PENGHASILAN = "penghasilan_orangtua";
    private static final String TAG_TANGGUNGAN = "jumlah_tanggungan";
    private static final String TAG_IPK = "ipk";
    private static final String TAG_SKOR = "skor";
    private static final String TAG_STATUS = "status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_beasiswa);
        listView = (ListView)findViewById(R.id.listLaporan);
        HashMap<String, String> map = new HashMap<String, String>();
        laporanList = new ArrayList<>();
        new loadLaporan().execute();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG_DEBUG, "On Click");
                String kode ="a";

                //Intent i = new Intent(getApplicationContext(), EditJurusan.class);
                //i.putExtra(TAG_ID, kode);
                //startActivityForResult(i, 100);
                objLaporan = parent.getItemAtPosition(position);
                String  y =objLaporan.toString();
                String no = y.split("id_beasiswa=")[1];
                idBeasiswa = no.split(",")[0];
                alertDilog(position,idBeasiswa);

            }
        });
    }

    public void alertDilog(final int position,String idBeasiswa) {

        // TODO Auto-generated method stub

        // Creating alert Dialog with two Buttons

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Confirm Delete");

        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want delete this ?");

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.ic_delete_black_24dp);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                new deleteLaporan().execute();
                // Write your code here to execute after dialog

            }
        });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog

                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();



    }

    private class loadLaporan extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(LaporanBeasiswa.this);
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
                jsonObject = jsonParser.getJsonObject(url_laporan, "POST", args);
                Log.d("jaa", jsonObject.toString());
            }catch(IOException e){
                Log.d(TAG_NETWORK, e.getLocalizedMessage());
            }
            try{
                int success = jsonObject.getInt(TAG_SUCCESS);
                if(success == 1){
                    laporan = jsonObject.getJSONArray("beasiswa");
                    int no = 1;
                    for (int i = 0; i<laporan.length(); i++){
                        try{
                            JSONObject c = laporan.getJSONObject(i);
                            String id = c.getString(TAG_ID);
                            String nim = c.getString(TAG_NIM);
                            String nama = c.getString(TAG_NAMA);
                            String jurusan = c.getString(TAG_JURUSAN);
                            String penghasilan = c.getString(TAG_PENGHASILAN);
                            String tanggungan = c.getString(TAG_TANGGUNGAN);
                            String ipk = c.getString(TAG_IPK);
                            String skor = c.getString(TAG_SKOR);
                            String status = c.getString(TAG_STATUS);
                            HashMap<String, String>map = new HashMap<String, String>();
                            map.put(TAG_NO, String.valueOf(no));
                            map.put(TAG_ID, id);
                            map.put(TAG_NIM, nim);
                            map.put(TAG_NAMA, nama);
                            map.put(TAG_JURUSAN, jurusan);
                            map.put(TAG_PENGHASILAN, penghasilan);
                            map.put(TAG_TANGGUNGAN, tanggungan);
                            map.put(TAG_IPK, ipk);
                            map.put(TAG_SKOR, skor);
                            map.put(TAG_STATUS, status);
                            laporanList.add(map);
                            no++;
                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                }
                Log.d(TAG_DEBUG , laporanList.toString());
            }catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s){
            progressDialog.dismiss();
            ListAdapter adapter = new SimpleAdapter(LaporanBeasiswa.this, laporanList, R.layout.laporan_menu,
                    new String[]{TAG_ID, TAG_NIM, TAG_NAMA, TAG_JURUSAN, TAG_IPK, TAG_STATUS},
                    new int[]{R.id.tvKode, R.id.tvNim, R.id.tvNama, R.id.tvJurusan, R.id.tvIpk, R.id.tvStatus });
            listView.setAdapter(adapter);
        }
    }

    // delete
    private class deleteLaporan extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(LaporanBeasiswa.this);
            progressDialog.setMessage("Updating Jurusan...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG_DEBUG, "Do In Background - delete Jurusan");
            List<Pair<String, String>> args = new ArrayList<Pair<String, String>>();
            args.add(new Pair<String, String>(TAG_ID, idBeasiswa));
            JSONObject jsonObject = null;
            try{
                jsonObject = jsonParser.getJsonObject(url_delete_laporan, "POST", args);
            }catch(IOException e){
                Log.d(TAG_NETWORK, e.getLocalizedMessage());
            }
            try{
                int success = jsonObject.getInt(TAG_SUCCESS);
                if (success == 1){
                    Intent i = new Intent(getApplicationContext(), LaporanBeasiswa.class);
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
