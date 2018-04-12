package com.barcodeapp;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ConfirmationActivity extends AppCompatActivity {

    String name, email, dob, mobile;
    public final static int QRcodeWidth = 500 ;
    public final static int QRcodeHeight = 500 ;
    Bitmap bitmap ;
    String sQRValue;
    ImageView mQRImageView;
    TextView mNameTextView, mEmailTextView, mDobTextView, mMobileTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        email = intent.getStringExtra("email");
        dob = intent.getStringExtra("dob");
        mobile = intent.getStringExtra("mobile");


        mQRImageView = (ImageView)findViewById(R.id.iv_qrcode);
        mNameTextView = (TextView)findViewById(R.id.name);
        mEmailTextView = (TextView)findViewById(R.id.email);
        mDobTextView = (TextView)findViewById(R.id.dob);
        mMobileTextView = (TextView)findViewById(R.id.mobile);

        mNameTextView.setText(name);
        mEmailTextView.setText(email);
        mDobTextView.setText(dob);
        mMobileTextView.setText(mobile);

        generateQR();

    }

    private void createExcelSheet()
    {
        String Fnamexls=name+"-excelSheet"+System.currentTimeMillis()+ ".xls";
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File (sdCard.getAbsolutePath() + "/barcode");
        directory.mkdirs();
        File file = new File(directory, Fnamexls);

        WorkbookSettings wbSettings = new WorkbookSettings();

        wbSettings.setLocale(new Locale("en", "EN"));

        WritableWorkbook workbook;
        try {
            int a = 1;
            workbook = Workbook.createWorkbook(file, wbSettings);
            //workbook.createSheet("Report", 0);
            WritableSheet sheet = workbook.createSheet("First Sheet", 0);
            Label label0 = new Label(0,0,"Name");
            Label label1 = new Label(1,0,name);
            Label label2 = new Label(0,1,"Email");
            Label label3 = new Label(1,1,email);
            Label label4 = new Label(0,2, "Dob");
            Label label5 = new Label(1,2, dob);
            Label label6 = new Label(0,3, "Mobile");
            Label label7 = new Label(1,3, mobile);
            try {
                sheet.addCell(label0);
                sheet.addCell(label1);
                sheet.addCell(label2);
                sheet.addCell(label3);
                sheet.addCell(label4);
                sheet.addCell(label5);
                sheet.addCell(label6);
                sheet.addCell(label7);
            } catch (RowsExceededException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (WriteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(this, "Problem while creating excel", Toast.LENGTH_SHORT).show();

            }


            workbook.write();

            Toast.makeText(this, "Excel sheet created and saved!", Toast.LENGTH_SHORT).show();
            try {
                workbook.close();
            } catch (WriteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(this, "Problem while creating excel", Toast.LENGTH_SHORT).show();

            }
            //createExcel(excelSheet);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, "Problem while creating excel", Toast.LENGTH_SHORT).show();
        }
    }



    void generateQR(){

        if(email.equals("") && dob.equals("") )
        {
            Toast.makeText(getApplicationContext(),"Empty fields not allowed", Toast.LENGTH_SHORT).show();
        }
        else {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email",email);
                jsonObject.put("dob",dob);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            sQRValue = jsonObject.toString();

            Log.i("DATA:",sQRValue);

            try {
                bitmap = TextToImageEncode(sQRValue);
                mQRImageView.setImageBitmap(bitmap);
                saveImage(bitmap,name);

            } catch (WriterException e) {
                e.printStackTrace();
            }
        }


    }

    private void saveImage(Bitmap finalBitmap, String image_name) {

        File root = Environment.getExternalStorageDirectory();
        File myDir = new File(root.getAbsolutePath() + "/barcode");
        myDir.mkdirs();
        String fname = "QR-" + image_name+ ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        Log.i("LOAD", root + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.QR_CODE,
                    QRcodeWidth, QRcodeHeight, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.QRCodeBlackColor):getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
            case R.id.excel:
                createExcelSheet();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;

    }
}

