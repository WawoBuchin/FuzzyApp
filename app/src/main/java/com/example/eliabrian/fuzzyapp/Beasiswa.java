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
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Beasiswa extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{
    private ProgressDialog progressDialog;
    Button btnHitung;
    JSONParser jsonParser = new JSONParser();
    JSONParser jsonParser1 = new JSONParser();
    TextInputEditText mPenghasilan, mTanggungan, mIPK;
    TextView tvSkor,tvStatus;
    JSONArray mahasiswa = null;
    FloatingActionButton mSave;
    Spinner spinner;
    String  sNim, sNimNama, sPenghasilan, sTanggungan, sIPK, sStatus, sSkor;
    JSONObject jsonObject = null;
    ArrayList<String> listMahasiswa;


    int i;
    float kemampuanEkonomi;
    float penghasilan1, tanggungan1, ipk;
    float[] derajatEkonomi = new float[3];
    float[] derajatIpk = new float[2];
    float[] p = new float[6];
    float[] z = new float[6];

    private static String url_beasiswa = "http://192.168.43.116:8888/PHP%20Beasiswa/create_beasiswa.php";
    private static String url_mahasiswa = "http://192.168.43.116:8888/PHP%20Beasiswa/read_mahasiswa.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_DEBUG = "DEBUG";
    private static final String TAG_NETWORK = "NETWORK";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beasiswa);

        mPenghasilan = (TextInputEditText)findViewById(R.id.itUang);
        mTanggungan = (TextInputEditText)findViewById(R.id.itTang);
        mIPK = (TextInputEditText)findViewById(R.id.itipk);
        mSave = (FloatingActionButton)findViewById(R.id.fab);
        tvStatus = findViewById(R.id.tvStatus);
        tvSkor = findViewById(R.id.tvSkor);
        btnHitung = (Button)findViewById(R.id.btnHitung);
        listMahasiswa=new ArrayList<>();

        mSave.setOnClickListener(this);
        btnHitung.setOnClickListener(this);
        // Spinner element
        spinner = (Spinner)findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        new loadMahasiswa().execute();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                sNim = sNimNama.split("-")[0];
                Log.d("ini nim", sNim);
                sPenghasilan = mPenghasilan.getText().toString();
                sTanggungan = mTanggungan.getText().toString();
                sIPK = mIPK.getText().toString();
                sSkor = tvSkor.getText().toString();
                sStatus = tvStatus.getText().toString();
                new insertProcess().execute();

                onBackPressed();
                break;
            case R.id.btnHitung:
                sPenghasilan = mPenghasilan.getText().toString();
                sTanggungan = mTanggungan.getText().toString();
                sIPK = mIPK.getText().toString();

                ipk = Float.parseFloat(sIPK);
                penghasilan1 = Float.parseFloat(sPenghasilan);
                tanggungan1 = Float.parseFloat(sTanggungan);

                //Hitung Kemampuan Ekonomi
                kemampuanEkonomi = hitKemampuanEkonomi(penghasilan1, tanggungan1);
                //printf("\nKemampuan Ekonomi = %.3f\n", kemampuanEkonomi);

                //Ekonomi Rendah
                derajatEkonomi[0] = hitEkonomiRendah(kemampuanEkonomi);
                //Ekonomi Menengah
                derajatEkonomi[1] = hitEkonomiMenengah(kemampuanEkonomi);
                //Ekonomi Tinggi
                derajatEkonomi[2] = hitEkonomiTinggi(kemampuanEkonomi);
                //IPK Rendah
                derajatIpk[0] = hitIpkRendah(ipk);
                //IPK Tinggi
                derajatIpk[1] = hitIpkTinggi(ipk);

                //Predikat
                p[0] = alphaPredikat(derajatEkonomi[0], derajatIpk[0]);
                p[1] = alphaPredikat(derajatEkonomi[0], derajatIpk[1]);
                p[2] = alphaPredikat(derajatEkonomi[1], derajatIpk[0]);
                p[3] = alphaPredikat(derajatEkonomi[1], derajatIpk[1]);
                p[4] = alphaPredikat(derajatEkonomi[2], derajatIpk[0]);
                p[5] = alphaPredikat(derajatEkonomi[2], derajatIpk[1]);


                //z
                z[0] = himpunanPertimbangan(p[0]);
                z[1] = himpunanSetuju(p[1]);
                z[2] = himpunanPertimbangan(p[2]);
                z[3] = himpunanSetuju(p[3]);
                z[4] = himpunanPertimbangan(p[4]);
                z[5] = himpunanSetuju(p[5]);


                float a = (p[0]*z[0]) + (p[1]*z[1]) + (p[2]*z[2]) + (p[3]*z[3]) + (p[4]*z[4]) + (p[5]*z[5]);
                float b = (p[0]+p[1]+p[2]+p[3]+p[4]+p[5]);
                float akhir = a/b;
                if(akhir <=2 ){
                    sStatus = "Tidak Dapat Beasiswa";
                }else if(akhir > 2 && akhir < 3){
                    sStatus = "Dipertimbangkan";
                }else{
                    sStatus = "Dapat Beasiswa";
                }
                for(i=0;i<=5;i++){
                    Log.d("hasil","z["+i+"] =" +z[i]);
                }
                Log.d("status", sStatus);
                tvStatus.setText(sStatus);
                tvSkor.setText(String.valueOf(akhir));
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getId() == R.id.spinner){
            sNimNama = adapterView.getItemAtPosition(i).toString();
            //Toast.makeText(adapterView.getContext(),"You selected : " + sNimNama, Toast.LENGTH_LONG).show();
        }
    }

    private class loadMahasiswa extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(Beasiswa.this);
            progressDialog.setMessage("Loading all mahasiswa, please wait...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            List<Pair<String, String>> args = new ArrayList<Pair<String, String>>();
            try{
                jsonObject = jsonParser.getJsonObject(url_mahasiswa, "POST", args);
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
                mahasiswa = jsonObject.getJSONArray("mahasiswa");
                Log.d("jsonarrya", mahasiswa.toString());
                for (int i = 0; i < mahasiswa.length(); i++) {

                    JSONObject a = mahasiswa.getJSONObject(i);
                    String nim = a.getString("nim");
                    String nama = a.getString("nama_mahasiswa");
                    //Log.d("country", kdjur + mahasiswa.length());
                    listMahasiswa.add(nim + " - " + nama);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            spinner.setAdapter(new ArrayAdapter<String>(Beasiswa.this, android.R.layout.simple_spinner_dropdown_item, listMahasiswa));
        }
    }

    private class insertProcess extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            Log.d(TAG_DEBUG, "Pre Execute insert");
            progressDialog = new ProgressDialog(Beasiswa.this);
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
            args.add(new Pair<>("penghasilan_orangtua", sPenghasilan));
            args.add(new Pair<>("jumlah_tanggungan", sTanggungan));
            args.add(new Pair<>("ipk", sIPK));
            args.add(new Pair<>("skor", sSkor));
            args.add(new Pair<>("status", sStatus));
            Log.d("YAKIN", args.toString());
            JSONObject jsonObject1 = null;
            try{
                Log.d("YAKIN", "Sampe sini");
                jsonObject1 = jsonParser1.getJsonObject(url_beasiswa, "POST", args);

                Log.d("JSONINSERTBeasiswa", jsonObject1.toString());
            }catch (IOException e){
                Log.d(TAG_NETWORK, e.getLocalizedMessage()+"yaaa");
            }
            Log.d(TAG_DEBUG, jsonObject1.toString());
            try{
                int success = jsonObject1.getInt(TAG_SUCCESS);
                if(success == 1){
                    Intent i = new Intent(getApplicationContext(), LaporanBeasiswa.class);
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

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public float hitKemampuanEkonomi(float penghasilan, float tanggungan){
        return penghasilan/tanggungan;
    }

    public float hitEkonomiRendah(float kemampuanEkonomi){
        float x=0;
        if(kemampuanEkonomi >= 1000000){
            x = 0;
        }else if(kemampuanEkonomi>=750000 && kemampuanEkonomi<=1000000){
            x = (1000000 - kemampuanEkonomi)/250000;
        }else if(kemampuanEkonomi<=750000){
            x= 1;
        }
        return x;
    }

    public float hitEkonomiMenengah(float kemampuanEkonomi){
        float x=0;
        if(kemampuanEkonomi<=750000 || kemampuanEkonomi >=2000000){
            x = 0;
        }else if(kemampuanEkonomi >= 750000 && kemampuanEkonomi <=1000000){
            x = (kemampuanEkonomi - 750000) / 250000;
        }else if(kemampuanEkonomi >= 1750000 && kemampuanEkonomi <= 2000000){
            x = (2000000 - kemampuanEkonomi) / 250000;
        }else if(kemampuanEkonomi >= 1000000 && kemampuanEkonomi <= 1750000){
            x = 1;
        }
        return x;
    }

    public float hitEkonomiTinggi(float kemampuanEkonomi){
        float x =0;
        if(kemampuanEkonomi<=1750000){
            x = 0;
        }else if(kemampuanEkonomi >= 1750000 && kemampuanEkonomi <=2000000){
            x = (kemampuanEkonomi - 1750000) / 250000;
        }else if(kemampuanEkonomi >= 2000000){
            x = 1;
        }
        return x;
    }

    public float hitIpkRendah(float ipk){
        float w = 0;
        if(ipk>=3.5){
            w = 0;
        }else if(ipk>=3 && ipk<=3.5){
            w = (float) ((3.5 - ipk) / 0.5);
        }else if(ipk<=3){
            w = 1;
        }
        return w;
    }

    public float hitIpkTinggi(float ipk){
        float w = 0;
        if(ipk<=3){
            w = 0;
        }else if(ipk>=3 && ipk<=3.5){
            w = (float) ((ipk - 3) / 0.5);
        }else if(ipk >= 3.5){
            w = 1;
        }
        return w;
    }

    public float alphaPredikat(float derajatEkonomi, float derajatIpk){
        if(derajatEkonomi > derajatIpk){
            return derajatIpk;
        }else{
            return derajatEkonomi;
        }
    }
    /*
    public float himpunanPertimbangan(float predikat){
        float z = 3 - predikat;
        return z;
    }

    public float himpunanSetuju(float predikat){
        float z = 2 - predikat;
        return z;
    }*/

    public float himpunanPertimbangan(float predikat){
        float z = ((predikat*(3-2))-3)*-1;
        return z;
    }

    public float himpunanSetuju(float predikat){
        float z = ((predikat*(3-2))+2);
        return z;
    }
}
