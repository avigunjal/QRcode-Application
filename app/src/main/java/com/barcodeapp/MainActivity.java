package com.barcodeapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.constraint.solver.SolverVariable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.barcodeapp.ApiClient.ApiClient;
import com.barcodeapp.ApiClient.ApiInterface;
import com.barcodeapp.ApiClient.Connection;
import com.barcodeapp.Model.CustomerDetails;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
   EditText mCustNameEdittext, mCustEmailEdittext, mCustMobileEditText, mCustDobEditText;
   Button mSaveButton;
   TextView mProgress;

    String name = "", email="",dob="",mobile="";
    private ApiInterface apiInterface;
    public final static int QRcodeWidth = 500 ;
    public final static int QRcodeHeight = 500 ;
    Bitmap bitmap ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Connection.isNetworkAvailable(MainActivity.this)) {
                    sendToServer();
                }
                else {
                    Toast.makeText(MainActivity.this, "Please check internet connection..", Toast.LENGTH_SHORT).show();
                }
            }
        });
        isStoragePermissionGranted();

    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){

        }
    }

    public void init(){
        mCustNameEdittext = (EditText)findViewById(R.id.et_name);
        mCustEmailEdittext = (EditText)findViewById(R.id.et_email);
        mCustDobEditText = (EditText)findViewById(R.id.et_dob);
        mCustMobileEditText = (EditText)findViewById(R.id.et_mobno);
        mSaveButton = (Button) findViewById(R.id.btn_save);


        mCustEmailEdittext.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }


        public  void sendToServer(){

            name = mCustNameEdittext.getText().toString();
            email = mCustEmailEdittext.getText().toString();
            dob = mCustDobEditText.getText().toString();
            mobile = mCustMobileEditText.getText().toString();

            if(!name.isEmpty()){
               if(!email.isEmpty()){
                   if(!dob.isEmpty())
                   {
                       insert_record();

                   }else {
                       Toast.makeText(this, "Enter DOB", Toast.LENGTH_SHORT).show();
                   }
               }else {
                   Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
               }
            }else{
                Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show();
            }


        }

        public void insert_record(){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("breath in..breath out..");
            progressDialog.show();
            apiInterface =  ApiClient.getApiClient().create(ApiInterface.class);
            Call<CustomerDetails> insert_details = apiInterface.CUSTOMER_DETAILS_CALL(name,email,dob,mobile);

            insert_details.enqueue(new Callback<CustomerDetails>() {
                @Override
                public void onResponse(Call<CustomerDetails> call, Response<CustomerDetails> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getCode() == 200) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Successfully Submitted", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, ConfirmationActivity.class);
                            intent.putExtra("name", name);
                            intent.putExtra("email", email);
                            intent.putExtra("dob", dob);
                            intent.putExtra("mobile", mobile);
                            startActivity(intent);

                        } else if(response.body().getCode() == 400)
                        {
                            Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }


                }

                @Override
                public void onFailure(Call<CustomerDetails> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Something went wrong..", Toast.LENGTH_SHORT).show();
                    System.out.println("Error: " + t.getMessage());
                }
            });
        }






}
