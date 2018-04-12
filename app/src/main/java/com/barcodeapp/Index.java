package com.barcodeapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class Index extends AppCompatActivity implements View.OnClickListener {

    LinearLayout mScan, mGenerate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        mScan = (LinearLayout) findViewById(R.id.scan);
        mGenerate =(LinearLayout) findViewById(R.id.generate);

        mScan.setOnClickListener(this);
        mGenerate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.generate){
            startActivity(new Intent(this,MainActivity.class));
        }else if(v.getId()==R.id.scan){
            startActivity(new Intent(this,ScanActivity.class));

        }
    }
}
