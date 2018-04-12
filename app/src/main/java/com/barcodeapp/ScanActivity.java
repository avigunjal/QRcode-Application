package com.barcodeapp;
        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.content.ContextCompat;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.LinearLayout;
        import android.widget.ProgressBar;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.barcodeapp.ApiClient.ApiClient;
        import com.barcodeapp.ApiClient.ApiInterface;
        import com.barcodeapp.ApiClient.Connection;
        import com.barcodeapp.Model.CustomerData;
        import com.barcodeapp.Model.CustomerDetails;
        import com.barcodeapp.Model.CustomerInfo;
        import com.google.zxing.integration.android.IntentIntegrator;
        import com.google.zxing.integration.android.IntentResult;

        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.List;

        import retrofit2.Call;
        import retrofit2.Callback;
        import retrofit2.Response;

//implementing onclicklistener
public class ScanActivity extends AppCompatActivity implements View.OnClickListener,ActivityCompat.OnRequestPermissionsResultCallback {

    //View Objects
    private ApiInterface apiInterface;
    private Button buttonScan;
    private TextView mNameTextView, mDobTextView, mEmailTextView, mMobileTextView;
    public static final int MULTIPLE_PERMISSIONS = 10;
    LinearLayout ll_info,ll_progressbar;
    //qr code scanner object
    private IntentIntegrator qrScan;
    int PERMISSION_ALL = 1;
    String[] permissions = new String[]{
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scan);
        //View objects
        buttonScan = (Button) findViewById(R.id.buttonScan);
        mNameTextView = (TextView) findViewById(R.id.tv_name);
        mDobTextView = (TextView) findViewById(R.id.tv_dob);
        mEmailTextView = (TextView) findViewById(R.id.tv_email);
        mMobileTextView = (TextView) findViewById(R.id.tv_mobile);
        ll_progressbar = (LinearLayout) findViewById(R.id.progressbar);
        ll_info = (LinearLayout) findViewById(R.id.ll_info);


        //intializing create object
        if (checkPermissions()) {

        }
        qrScan = new IntentIntegrator(this);
        qrScan.initiateScan();
        //attaching onclick listener
        buttonScan.setOnClickListener(this);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {

                }
                return;
            }
        }
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    //Getting the create results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                Log.e("JSONDATA",result.toString());
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());


                    String email = obj.getString("email");
                    String dob = obj.getString("dob");

                    if (Connection.isNetworkAvailable(ScanActivity.this)) {
                        getData(email,dob);
                    }
                    else {
                        Toast.makeText(ScanActivity.this, "Please check internet connection..", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void getData(String email, String dob){

            apiInterface =  ApiClient.getApiClient().create(ApiInterface.class);
            Call<CustomerInfo> getDetails = apiInterface.get_details(email);

            getDetails.enqueue(new Callback<CustomerInfo>() {
                @Override
                public void onResponse(Call<CustomerInfo> call, Response<CustomerInfo> response) {
                    if (response.isSuccessful()) {
                        ll_progressbar.setVisibility(View.GONE);
                        ll_info.setVisibility(View.VISIBLE);
                        if (response.body().getResult() == 200) {
                            Toast.makeText(ScanActivity.this, "Information is available", Toast.LENGTH_SHORT).show();
                            List<CustomerData> customerDataResponse = response.body().getData();
                            String name = customerDataResponse.get(0).getName();
                            String email = customerDataResponse.get(0).getEmail();
                            String dob = customerDataResponse.get(0).getDob();
                            String mobile = customerDataResponse.get(0).getMobile();

                            mNameTextView.setText(name);
                            mEmailTextView.setText(email);
                            mDobTextView.setText(dob);
                            mMobileTextView.setText(mobile);


                        } else if(response.body().getResult() == 400)
                        {
                            Toast.makeText(ScanActivity.this, "Information not available", Toast.LENGTH_SHORT).show();

                        }
                    }
                    else {
                        Toast.makeText(ScanActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        ll_progressbar.setVisibility(View.GONE);
                    }


                }

                @Override
                public void onFailure(Call<CustomerInfo> call, Throwable t) {
                    ll_progressbar.setVisibility(View.GONE);
                    Toast.makeText(ScanActivity.this, "Something went wrong..", Toast.LENGTH_SHORT).show();
                    System.out.println("Error: " + t.getMessage());
                }
            });

    }

    @Override
    public void onClick(View view) {
        //initiating the qr code create
        qrScan.initiateScan();
    }
}