package com.example.allthecar.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.allthecar.CommonApplication;
import com.example.allthecar.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener {

    public static final String restKey = "f3f4eb56f3d6c99bc32575c2769fd834";
//    private ViewPager viewPager;
    private CommonApplication application;

    private FirebaseAuth firebaseAuth;
//    private FirebaseAuth.AuthStateListener mAuthListener;

    TabLayout tabLayout;

    public MapView mapView;

    ProgressDialog progressDialog;

    private MapPOIItem currentMarker = null;

    private static final int LOAD_MESSAGE_SUCCESS = 101;
    Handler handler;

    public static Context context;

    interface addPOIItemInMapInterface {
        void addPOIItemInMap();
    }
    private addPOIItemInMapInterface addPOIItemInMapCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;

        application = (CommonApplication) getApplication();

        mapView = (MapView) findViewById(R.id.map);

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        mapView.setCurrentLocationEventListener(this);

        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);


        firebaseAuth = FirebaseAuth.getInstance();
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

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

//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    // User is signed in
//                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//                } else {
//                    // User is signed out
//                    Log.d(TAG, "onAuthStateChanged:signed_out");
//                }
//            }
//        };

        createCustomToolbar();

        createFloatingActionButton();

//        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

//        MapFragment mapFragment = new MapFragment();
//        Bundle bundle = new Bundle(position);
//        bundle.putInt("tabPosition", position);
//        mapFragment.setArguments(bundle);

//        viewPager = (ViewPager) findViewById(R.id.container);
//


//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
//        tabLayout.addTab(tabLayout.newTab().setText("주차장"));

//        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
//        final PagerAdapter adapter = new com.example.allthecar.Adapter.PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
//        viewPager.setAdapter(adapter);
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

    }

    private void createCustomToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_person_white_24dp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
//                Log.i(">>menu: ", "mypage"); //TODO: insert mypage

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Intent i1 = new Intent(this, MypageActivity.class);
                    startActivity(i1);
                    Log.d("loginOkay", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Intent i1 = new Intent(this, LoginActivity.class);
                    startActivity(i1);
                    Log.d("loginNo", "onAuthStateChanged:signed_out");
                }

                break;

            case R.id.menu_right_listview:
                Intent intent = new Intent(this, ListViewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createFloatingActionButton() {

        FloatingActionButton detailSearchBtn = (FloatingActionButton) findViewById(R.id.fab);
        detailSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchDetailActivity.class);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onStart() {
        super.onStart();

//        RelativeLayout contentsLayout = (RelativeLayout) findViewById(R.id.contents_layout);






        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                application.tabPositionNum = tab.getPosition();

                if (!application.address1.isEmpty()) {

                    try {
                        application.getOpenData();

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    progressDialog.setMessage("Please wait...");
                    progressDialog.show();

                }

//                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

//                application.setInitMapView(mapView, tab.getPosition());

//                viewPager.setCurrentItem(tab.getPosition());


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.i(">> unselect: ", String.valueOf(tab.getPosition()));
                removeMapPOIItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

//        while(!application.address1.isEmpty()) {
//            if (application.address1.isEmpty()) {
//                break;
//            }

//            try {
//                application.getOpenData();
//                Log.i(">>poitem: ", "poitem firmst");
//                if (application.tabPositionNum == 0) {
//                    if (application.parkingItemArrayList.size() > 0) {
//                        Log.i(">>poitem: ", "poitem second");
//                        for (int i = 0; i < application.parkingItemArrayList.size(); i++) {
//                            mapView.addPOIItem(application.parkingItemArrayList.get(i).getMapPOIItem());
//                        }
//                    }
//                }
//
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }

//        }

//        MapPOIItem.ImageOffset trackingImageAnchorPointOffset = new MapPOIItem.ImageOffset(0, 0);
//        mapView.setCustomCurrentLocationMarkerTrackingImage(R.drawable.current_marker, trackingImageAnchorPointOffset);



//        mapView.setMapCenterPoint(mapView.getMapCenterPoint(), false);


//        application.setInitMapView(mapView, (Integer) 0);



//        final WeakReference<MainActivity> weakReference;
//        weakReference = new WeakReference<MainActivity>(this);
//
//        handler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//
//
//
////                MainActivity mainActivity = weakReference.get();
//
////                if (mainActivity != null) {
//                    switch (msg.what) {
//                        case LOAD_MESSAGE_SUCCESS:
//
////                            progressDialog.dismiss();
//
//                            String jsonString = (String) msg.obj;
//
//                            Log.i(">>>jsonString: ", jsonString);
//                            break;
//                    }
////                }
//            }
//        };

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mapView.setShowCurrentLocationMarker(false);
    }



    public void searchMapView() {

        String baseUrl = "http://dapi.kakao.com/v2/local/search/keyword.json?";
//        final java.util.logging.Handler handler = null;

        String query = "주차장";

        MapPoint.GeoCoordinate geoCoordinate = mapView.getMapCenterPoint().getMapPointGeoCoord();
        double latitude = geoCoordinate.latitude;
        double longitude = geoCoordinate.longitude;
        int radius = 1000;

        final String searchUrl = baseUrl + "query=" + query + "&x=" + longitude + "&y=" + latitude + "&radius=" + radius;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                String result;

                try {

                    Log.d(">> searchUrl: ", searchUrl);

                    URL url = new URL(searchUrl);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

                    httpURLConnection.setRequestProperty("Authorization", "KakaoAK " + restKey);

                    httpURLConnection.setReadTimeout(3000);
                    httpURLConnection.setConnectTimeout(3000);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.connect();

                    int responseStatusCode = httpURLConnection.getResponseCode();

                    InputStream inputStream;
                    if (responseStatusCode == HttpURLConnection.HTTP_OK) {

                        inputStream = httpURLConnection.getInputStream();
                    } else {
                        inputStream = httpURLConnection.getErrorStream();

                    }

                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    StringBuilder sb = new StringBuilder();
                    String line;


                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }

                    bufferedReader.close();
                    httpURLConnection.disconnect();

                    result = sb.toString().trim();

                    Message msg = handler.obtainMessage(LOAD_MESSAGE_SUCCESS, result);
                    handler.sendMessage(msg);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }



//    private  final MapSearchHandler searchHandler = new MapSearchHandler(this);


//    private static class MapSearchHandler extends Handler {
//
//        public static final int LOAD_SUCCESS = 101;
//
//        private final WeakReference<MainActivity> weakReference;
//
//        public MapSearchHandler(MainActivity mainActivity) {
//            weakReference = new WeakReference<MainActivity>(mainActivity);
//        }
//
//        public void handleMessage(Message msg) {
//            MainActivity mainActivity = weakReference.get();
//
//            if (mainActivity != null) {
//                switch (msg.what) {
//                    case LOAD_SUCCESS:
//                        mainActivity.progressDialog.dismiss();
//
//                        String jsonString = (String)msg.obj;
//
//                        Toast.makeText(mainActivity, jsonString, Toast.LENGTH_LONG).show();
//                        break;
//                }
//            }
//        }
//    }

    public void setAddPOIItemInMapCallback() {

        if (application.tabPositionNum == 0) {
            if (application.parkingItemArrayList.size() > 0) {
                Log.i(">>poitem: ", "poitem second");
                for (int i = 0; i < application.parkingItemArrayList.size(); i++) {
                    Log.i(">>poItem2: ", application.parkingItemArrayList.get(i).getMapPOIItem().toString());
                    mapView.addPOIItem(application.parkingItemArrayList.get(i).getMapPOIItem());

                }
            }
        }
    }




    public void addMapPOIItem() {

//        application.tabPosition.value = application.tabPositionNum;
        Log.i(">>addMapPo: ", String.valueOf(application.tabPositionNum));

        switch (application.tabPositionNum) {

            case CommonApplication.PARKING:
                Log.i(">>addMapPo: ", "PARKING");
                if (application.parkingItemArrayList.size() > 0) {

                    for (int i = 0; i < application.parkingItemArrayList.size(); i++) {

                        mapView.addPOIItem(application.parkingItemArrayList.get(i).getMapPOIItem());
                    }
                }
                break;

            case CommonApplication.WASHCAR:
                Log.i(">>addMapPo: ", "WASHCAR");
                if (application.washCarItemArrayList.size() > 0) {

                    for (int i = 0; i < application.washCarItemArrayList.size(); i++) {

                        mapView.addPOIItem(application.washCarItemArrayList.get(i).getMapPOIItem());
                    }
                }
                break;

            case CommonApplication.RESTAREA:
                Log.i(">>addMapPo: ", "RESTAREA");
                if (application.restAreaItemArrayList.size() > 0) {

                    for (int i = 0; i < application.restAreaItemArrayList.size(); i++) {

                        mapView.addPOIItem(application.restAreaItemArrayList.get(i).getMapPOIItem());
                    }
                }
                break;

            case CommonApplication.REPAIRSHOP:
                Log.i(">>addMapPo: ", "REPAIRSHOP");
                if (application.repairShopItemArrayList.size() > 0) {

                    for (int i = 0; i < application.repairShopItemArrayList.size(); i++) {

                        mapView.addPOIItem(application.repairShopItemArrayList.get(i).getMapPOIItem());
                    }
                }
                break;

            case CommonApplication.CHARGINGCAR:
                Log.i(">>addMapPo: ", "CHARGINGCAR");
                if (application.chargingCarItemArrayList.size() > 0) {

                    for (int i = 0; i < application.chargingCarItemArrayList.size(); i++) {

                        mapView.addPOIItem(application.chargingCarItemArrayList.get(i).getMapPOIItem());
                    }
                }
                break;

            case CommonApplication.RENTALCAR:
                Log.i(">>addMapPo: ", "RENTALCAR");
                if (application.rentalCarItemArrayList.size() > 0) {

                    for (int i = 0; i < application.rentalCarItemArrayList.size(); i++) {

                        mapView.addPOIItem(application.rentalCarItemArrayList.get(i).getMapPOIItem());
                    }
                }
                break;
        }

        progressDialog.dismiss();

//        if (application.tabPositionNum == 0) {
//            if (application.parkingItemArrayList.size() > 0) {
//                Log.i(">>poitem: ", "poitem second");
//                for (int i = 0; i < application.parkingItemArrayList.size(); i++) {
//                    Log.i(">>poitem2: ", application.parkingItemArrayList.get(i).toString());
//                    mapView.addPOIItem(application.parkingItemArrayList.get(i).getMapPOIItem());
//                }
//            }
//        }
    }

    public void removeMapPOIItem(int position) {


        switch (application.tabPositionNum) {

            case CommonApplication.PARKING:
                Log.i(">>removeMapPo: ", "PARKING");
                if (application.parkingItemArrayList.size() > 0) {

                    for (int i = 0; i < application.parkingItemArrayList.size(); i++) {

                        mapView.removePOIItem(application.parkingItemArrayList.get(i).getMapPOIItem());
                    }
                }
                break;

            case CommonApplication.WASHCAR:
                Log.i(">>removeMapPo: ", "WASHCAR");
                if (application.washCarItemArrayList.size() > 0) {

                    for (int i = 0; i < application.washCarItemArrayList.size(); i++) {

                        mapView.removePOIItem(application.washCarItemArrayList.get(i).getMapPOIItem());
                    }
                }
                break;

            case CommonApplication.RESTAREA:
                Log.i(">>removeMapPo: ", "RESTAREA");
                if (application.restAreaItemArrayList.size() > 0) {

                    for (int i = 0; i < application.restAreaItemArrayList.size(); i++) {

                        mapView.removePOIItem(application.restAreaItemArrayList.get(i).getMapPOIItem());
                    }
                }
                break;

            case CommonApplication.REPAIRSHOP:
                Log.i(">>removeMapPo: ", "REPAIRSHOP");
                if (application.repairShopItemArrayList.size() > 0) {

                    for (int i = 0; i < application.repairShopItemArrayList.size(); i++) {

                        mapView.removePOIItem(application.repairShopItemArrayList.get(i).getMapPOIItem());
                    }
                }
                break;

            case CommonApplication.CHARGINGCAR:
                Log.i(">>removeMapPo: ", "CHARGINGCAR");
                if (application.chargingCarItemArrayList.size() > 0) {

                    for (int i = 0; i < application.chargingCarItemArrayList.size(); i++) {

                        mapView.removePOIItem(application.chargingCarItemArrayList.get(i).getMapPOIItem());
                    }
                }
                break;

            case CommonApplication.RENTALCAR:
                Log.i(">>removeMapPo: ", "RENTALCAR");
                if (application.rentalCarItemArrayList.size() > 0) {

                    for (int i = 0; i < application.rentalCarItemArrayList.size(); i++) {

                        mapView.removePOIItem(application.rentalCarItemArrayList.get(i).getMapPOIItem());
                    }
                }
                break;
        }
    }

    private void showAll() {
        int padding = 20;
        float minZoomLevel = 7;
        float maxZoomLevel = 10;
//        MapPointBounds bounds = new MapPointBounds(CUSTOM_MARKER_POINT, DEFAULT_MARKER_POINT);
//        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(bounds, padding, minZoomLevel, maxZoomLevel));
    }

    private void createCurrentMarker (MapPoint currentPoint) {

        if (currentMarker != null) {
            mapView.removePOIItem(currentMarker);
        }

        currentMarker = new MapPOIItem();
        currentMarker.setItemName("현재 위치"); //TODO: change name
        currentMarker.setTag(0);
        currentMarker.setMapPoint(currentPoint);

        currentMarker.setShowAnimationType(MapPOIItem.ShowAnimationType.NoAnimation);
        currentMarker.setShowCalloutBalloonOnTouch(true);

        currentMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        currentMarker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
        currentMarker.setCustomImageResourceId(R.drawable.current_marker);
        currentMarker.setCustomImageAutoscale(false);
        currentMarker.setCustomImageAnchor(0.5f, 1.0f);
        currentMarker.setCustomImageAnchorPointOffset(new MapPOIItem.ImageOffset(0,0));

        mapView.addPOIItem(currentMarker);
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {

        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        MapPoint currentPoint = MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude);

        MapReverseGeoCoder mapReverseGeoCoder = new MapReverseGeoCoder(restKey, currentPoint, this, this);
        mapReverseGeoCoder.startFindingAddress();

        createCurrentMarker(currentPoint);

        progressDialog.dismiss();
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {
        Log.i(">> updatecancelled", "currentLoc");
    }

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        Log.i(">> address: ", s);

        String[] arr = s.split(" ");

        arr[0] = arr[0].equals("서울") ? "서울특별시" : arr[0];

        application.address1 = arr[0];
        application.address2 = arr[1];


        try {
            application.getOpenData();

            application.address1 = "서울특별시";
            application.address2 = "구로구";
            application.getOpenData();


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        progressDialog.setMessage("Please wait...");
        progressDialog.show();

    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
        Log.i(">> address fail", mapReverseGeoCoder.toString());
    }

}

