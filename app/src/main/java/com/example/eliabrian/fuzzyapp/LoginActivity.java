package com.example.eliabrian.fuzzyapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity  implements View.OnClickListener{

    private ProgressDialog progressDialog;
    JSONParser jsonParser = new JSONParser();
    TextInputEditText mUsername, mPassword;
    AppCompatButton mLogin;
    String sUsername, sPassword;

    private static String url_login = "http://10.0.2.2/PHP%20Beasiswa/login.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_DEBUG = "DEBUG";
    private static final String TAG_NETWORK = "NETWORK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsername = (TextInputEditText)findViewById(R.id.textInputEditTextUsername);
        mPassword = (TextInputEditText)findViewById(R.id.textInputEditTextPassword);
        mLogin = (AppCompatButton)findViewById(R.id.appCompatButtonLogin);
        mLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.appCompatButtonLogin :
                sUsername = mUsername.getText().toString();
                sPassword = mPassword.getText().toString();
                Log.d(TAG_DEBUG, "Button Login Clicked");
                new LoginProcess().execute();
        }
    }

    private class LoginProcess extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            Log.d(TAG_DEBUG, "Pre Execute");
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Logging in...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG_DEBUG, "Do In Background");
            List<Pair<String, String>> args = new ArrayList<Pair<String, String>>();
            args.add(new Pair<>("username", sUsername));
            args.add(new Pair<>("password", sPassword));
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
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    Log.d(TAG_DEBUG, "Failed to Log In");
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
