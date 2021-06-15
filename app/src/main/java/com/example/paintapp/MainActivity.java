package com.example.paintapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.kyanogen.signatureview.SignatureView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {


    private static String filename;
    int colorDefault;
    SignatureView signatureView;
    ImageButton imgEraser, imgColor, imgSave;
    SeekBar seekBar;
    TextView textpensize;
    File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/mypaintings");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setTitle("Painting App");


        signatureView = findViewById(R.id.signature_view);
        seekBar = findViewById(R.id.pensize);
        textpensize = findViewById(R.id.Txtpensize);
        imgColor = findViewById(R.id.btnColor);
        imgEraser = findViewById(R.id.btnEraser);
        imgSave = findViewById(R.id.btnSave);

        askPermission();


        colorDefault = ContextCompat.getColor(this, R.color.black);


        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());

        String date = format.format(new Date());

        filename = path + "/" + date + ".png" ;

        if (!path.exists()) {
            path.mkdirs();
        }


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


                textpensize.setText(progress + "dp");
                signatureView.setPenSize(progress);
                seekBar.setMax(50);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        imgColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                opencolorPicker();
            }
        });

        imgEraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signatureView.clearCanvas();

            }
        });

        imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!signatureView.isBitmapEmpty()) {
                    try {
                        saveimage();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Couldn't Save", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        int id = item.getItemId();


        if (id == R.id.about) {

            Toast.makeText(this, "Devloped by Siddhesh Vast", Toast.LENGTH_SHORT).show();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);


        return super.onCreateOptionsMenu(menu);


    }


    private void saveimage() throws IOException {

        File file = new File(filename);

        Bitmap bitmap = signatureView.getSignatureBitmap();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);

        byte[] bitmapData = byteArrayOutputStream.toByteArray();

        FileOutputStream fos = new FileOutputStream(file);

        fos.write(bitmapData);
        fos.flush();
        fos.close();

        Toast.makeText(this, "painting Saved !!", Toast.LENGTH_SHORT).show();


    }

    private void opencolorPicker() {

        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(this, colorDefault, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {

                colorDefault = color;
                signatureView.setPenColor(color);


            }
        });
        ambilWarnaDialog.show();


    }

    private void askPermission() {


        Dexter.withContext(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                        Toast.makeText(MainActivity.this, "Granted !!!", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                        permissionToken.continuePermissionRequest();


                    }
                }).check();


    }
}