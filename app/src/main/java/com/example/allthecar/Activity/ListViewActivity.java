package com.example.allthecar.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.allthecar.Adapter.ListViewAdapter;
import com.example.allthecar.CommonApplication;
import com.example.allthecar.Item.ChargingCarItem;
import com.example.allthecar.Item.ParkingItem;
import com.example.allthecar.Item.RentalCarItem;
import com.example.allthecar.Item.RepairShopItem;
import com.example.allthecar.Item.RestAreaItem;
import com.example.allthecar.Item.WashCarItem;
import com.example.allthecar.R;

public class ListViewActivity extends AppCompatActivity {

    ListView listView = null;
    ListViewAdapter adapter;
//    public int tabPosition;

    private CommonApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        application = (CommonApplication) CommonApplication.getInstance();

        createCustomToolbar();

        listView = (ListView) findViewById(R.id.listView);

        adapter = new ListViewAdapter(ListViewActivity.this, application.tabPositionNum);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                parent.getAdapter().getItem(position);

//                ImageView typeIcon = (ImageView) view.findViewById(R.id.listItem_icon);
//                Drawable drawable = typeIcon.getDrawable();
//                Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
//
//                TextView store = view.findViewById(R.id.listItem_store_name);
//                String storeName = store.getText().toString();
//
//                TextView address = view.findViewById(R.id.listItem_address);
//                String addrStr = address.getText().toString();
//
//                TextView tel = view.findViewById(R.id.listItem_tel);
//                String phoneNum = tel.getText().toString();

//                final Movie item = (Movie) lv.getItemAtPosition(position);
//                final String title = item.title;
                String storeNm ="", storeName="", addrStr="", phoneNum="";

                switch (application.tabPositionNum) {
                    case CommonApplication.PARKING:
                        ParkingItem parkingItem = (ParkingItem) listView.getItemAtPosition(position);
                        storeName = parkingItem.getStoreNm();
                        addrStr = parkingItem.getRdnmadr();
                        phoneNum = parkingItem.getPhoneNumber();
                        break;
                    case CommonApplication.WASHCAR:
                        WashCarItem washCarItem = (WashCarItem) listView.getItemAtPosition(position);
                        storeName = washCarItem.getStoreNm();
                        addrStr = washCarItem.getRdnmadr();
                        phoneNum = washCarItem.getPhoneNumber();
                        break;

                    case CommonApplication.RESTAREA:
                        RestAreaItem restAreaItem = (RestAreaItem) listView.getItemAtPosition(position);
                        storeName = restAreaItem.getStoreNm();
//                        addrStr = restAreaItem.getRdnmadr();
                        phoneNum = restAreaItem.getPhoneNumber();
                        break;

                    case CommonApplication.REPAIRSHOP:
                        RepairShopItem repairShopItem = (RepairShopItem) listView.getItemAtPosition(position);
                        storeName = repairShopItem.getStoreNm();
                        addrStr = repairShopItem.getRdnmadr();
                        phoneNum = repairShopItem.getPhoneNumber();
                        break;

                    case CommonApplication.CHARGINGCAR:
                        ChargingCarItem chargingCarItem = (ChargingCarItem) listView.getItemAtPosition(position);
                        storeName = chargingCarItem.getStoreNm();
                        addrStr = chargingCarItem.getRdnmadr();
                        phoneNum = chargingCarItem.getPhoneNumber();
                        break;

                    case CommonApplication.RENTALCAR:
                        RentalCarItem rentalCarItem = (RentalCarItem) listView.getItemAtPosition(position);
                        storeName =rentalCarItem.getStoreNm();
                        addrStr = rentalCarItem.getRdnmadr();
                        phoneNum = rentalCarItem.getPhoneNumber();
                        break;
                }

                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);

                intent.putExtra("storeName", storeName);
                intent.putExtra("address", addrStr);
                intent.putExtra("tel", phoneNum);
                startActivity(intent);
            }
        });


    }



    private void createCustomToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        String titleStr = application.getTitleWithPosition();
        title.setText(titleStr);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
