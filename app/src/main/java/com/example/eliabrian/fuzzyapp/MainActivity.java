package com.example.eliabrian.fuzzyapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button btnjur, btnmhs, btnhit, btnlap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnjur = (Button) findViewById(R.id.buttonjur);
        btnmhs = (Button)findViewById(R.id.buttonmhs);
        btnhit = (Button)findViewById(R.id.buttonhit);
        btnlap = (Button)findViewById(R.id.buttonlap);
        btnjur.setOnClickListener(this);
        btnmhs.setOnClickListener(this);
        btnhit.setOnClickListener(this);
        btnlap.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonjur:
                Intent intent = new Intent(this, JurusanActivity.class);
                startActivity(intent);
                break;
            case R.id.buttonmhs:
                intent = new Intent(this, MahasiswaActivity.class);
                startActivity(intent);
                break;
            case R.id.buttonhit:
                intent = new Intent(this, Beasiswa.class);
                startActivity(intent);
                break;
            case R.id.buttonlap:
                intent = new Intent(this, LaporanBeasiswa.class);
                startActivity(intent);
                break;
        }
    }
}
