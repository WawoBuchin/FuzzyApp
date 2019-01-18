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
        btnjur.setOnClickListener(this);
        btnmhs.setOnClickListener(this);
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
        }
    }
}
