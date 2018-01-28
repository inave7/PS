package com.bellaku.naveenprakash.musicmaterial;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;


public class SplashActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST = 1;
    private static final float ROTATE_FROM = 0.0f;
    private static final float ROTATE_TO = -10.0f * 360.0f;
    private TextView Tx;
    private ImageView Imgv;
    private RotateAnimation mRotateAnimation;
//    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mRotateAnimation = new RotateAnimation(ROTATE_FROM, ROTATE_TO, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);//0, 0, 40, 0);
        mRotateAnimation.setDuration((long) 2 * 500);
        mRotateAnimation.setRepeatCount(5);


        Tx = (TextView) findViewById(R.id.s_tx);
        Imgv = (ImageView) findViewById(R.id.s_imgv);
        Imgv.setAnimation(mRotateAnimation);
        //     spinner = (ProgressBar) findViewById(R.id.progressBar);


    }

    @Override
    protected void onResume() {
        super.onResume();

        PermissionsCheck();

    }

    private void PermissionsCheck() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            GotoMain();
        else
            ActivityCompat.requestPermissions(SplashActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSION_REQUEST)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeToast("Storage permission granted now");
                GotoMain();
            }
            else Tx.setText("App can't work without access to Storage");

        if (Tx.getText().toString().startsWith("App"))
            Tx.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PermissionsCheck();
                }
            });
    }

    private void makeToast(String s) {
        Snackbar.make(getWindow().getDecorView().getRootView(), s, Snackbar.LENGTH_SHORT).show();
    }

    public void GotoMain() {
        new Handler().postDelayed(new Runnable() {

            // Using handler with postDelayed called runnable run method

            @Override
            public void run() {


                {
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                }

            }


        }, 500);
    }


}



