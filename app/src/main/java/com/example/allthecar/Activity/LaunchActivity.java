package com.example.allthecar.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.allthecar.R;

import java.security.MessageDigest;

//import android.content.pm.PackageManager;

public class LaunchActivity extends AppCompatActivity {

    private final int PERMISSIONS_REQUEST_LOCATION = 1;
    private ProgressBar progressBar;

    Handler handler = new Handler();
    int value = 0;
    int add = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        progressBar = new ProgressBar(LaunchActivity.this);

        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                // 23 버전 이상일 때 상태바 하얀 색상에 회색 아이콘 색상을 설정
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(Color.parseColor("#ffffff"));
            }
        }
        else if (Build.VERSION.SDK_INT >= 21) {
            // 21 버전 이상일 때
            getWindow().setStatusBarColor(Color.BLACK);
        }


        // kakao map hashkey
        getAppKeyHash();

        // 권한 요청
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        String permissionString = "All the Car 에서 현재 위치 정보를 사용하고자 합니다.";
        int checkPermission = ContextCompat.checkSelfPermission(this, permission);

        if (checkPermission != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {

                // 권한 재요청
                Toast.makeText(getApplicationContext(), permissionString, Toast.LENGTH_SHORT).show();

                requestPermissions(new String[]{permission}, PERMISSIONS_REQUEST_LOCATION);

            } else {

                //권한 최초 요청
                Toast.makeText(getApplicationContext(), permissionString, Toast.LENGTH_SHORT).show();

                requestPermissions(new String[]{permission}, PERMISSIONS_REQUEST_LOCATION);

            }
        } else { // 권한 있음

            goToMainActivity();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // 권한 승인
                    goToMainActivity();

                } else {

                    // 권한 승인 거절

                }
            }
        }
    }

    public static Boolean getInternetState (Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }

    private void goToMainActivity() {

        Boolean isConnectNetwork = getInternetState(getApplicationContext());

//        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!isConnectNetwork) {

            Toast.makeText(getApplicationContext(), "please, connect internet", Toast.LENGTH_LONG).show();

            // check GPS
//            if (!locationManager.isProviderEnabled((LocationManager.GPS_PROVIDER))) {
//                //GPS 설정화면 이동
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                intent.addCategory(Intent.CATEGORY_DEFAULT);
//                startActivity(intent);
//                finish();
//            }
        }

//        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = getLayoutInflater().from(this).inflate(R.layout.activity_main, null);
//        setContentView(view);


//        View view = inflater.inflate(R.layout.activity_main, mapView, true);
//        Context context = null;
//        final MapView mapView = (MapView) main.findViewById(R.id.map);
//        MapView mapView = (MapView) findViewById(R.id.map);
//        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);




        Thread thread = new Thread() {

            public void run() {

                for (int progress=0; progress<100; progress+=10) {
                    try {
                        Thread.sleep(100);
                        progressBar.setProgress(progress);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();




//                for(value = 0; value <= 100; value++) {
//
//                    progressBar.setProgress(value);
////                    handler.post(new Runnable() {
////                        @Override
////                        public void run() { // 화면에 변경하는 작업을 구현
////                            progressBar.setProgress(value);
////                        }
////                    });
//
//                    try {
//
//                        Thread.sleep(100); // 시간지연
//
//                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                        startActivity(intent);
//
//                        finish();
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }



//                while(true) {
//                    value = value + add;
//                    if (value>=100 || value<=0) {
//                        add = -add;
//                    }
//
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() { // 화면에 변경하는 작업을 구현
//                            progressBar.setProgress(value);
//                        }
//                    });
//
//                    try {
//
//                        Thread.sleep(100); // 시간지연
//
//                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                        startActivity(intent);
//
//                        finish();
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
            }
        };

        thread.start();
    }

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }

}
