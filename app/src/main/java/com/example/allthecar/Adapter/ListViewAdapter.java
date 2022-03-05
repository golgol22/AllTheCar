package com.example.allthecar.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.example.allthecar.CommonApplication;
import com.example.allthecar.Item.ChargingCarItem;
import com.example.allthecar.Item.ParkingItem;
import com.example.allthecar.Item.RentalCarItem;
import com.example.allthecar.Item.RepairShopItem;
import com.example.allthecar.Item.RestAreaItem;
import com.example.allthecar.Item.WashCarItem;
import com.example.allthecar.Model.LikeAddModel;
import com.example.allthecar.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    private Context context;
    private AQuery aQuery;
    public int tabPosition;
    CommonApplication application;
    private  ArrayList arrayList;
    private int image;
    int count=0;

    String addr = "";
    String phone = "";
    String name="";




    public ListViewAdapter(Context context, int position) {

        this.context = context;
        aQuery = new AQuery(context);
        tabPosition = position;
        application = (CommonApplication) CommonApplication.getInstance();
        getArrayListWithPosition(position);
    }

    public void getArrayListWithPosition(int position) {

        switch (position) {
            case CommonApplication.PARKING:
                arrayList = application.parkingItemArrayList;
                image = R.drawable.car_icon_parking;
                break;

            case CommonApplication.WASHCAR:
                arrayList = application.washCarItemArrayList;
                image = R.drawable.car_icon_wash;
                break;

            case CommonApplication.RESTAREA:
                arrayList = application.restAreaItemArrayList;
                image = R.drawable.car_icon_rest_area;
                break;

            case CommonApplication.REPAIRSHOP:
                arrayList = application.repairShopItemArrayList;
                image = R.drawable.car_icon_repair;
                break;

            case CommonApplication.CHARGINGCAR:
                arrayList = application.chargingCarItemArrayList;
                image = R.drawable.car_icon_charging;
                break;

            case CommonApplication.RENTALCAR:
                arrayList = application.rentalCarItemArrayList;
                image = R.drawable.car_icon_rental;
                break;
        }
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final int pos = position;
        final  Context context = parent.getContext();

        final LikeAddModel likeAddModel = new LikeAddModel();


        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_row, parent, false);
        }

        ImageView typeIcon = (ImageView) convertView.findViewById(R.id.listItem_icon);
        typeIcon.setImageResource(image);

        final TextView storeName = (TextView) convertView.findViewById(R.id.listItem_store_name);
        TextView address = (TextView) convertView.findViewById(R.id.listItem_address);
        TextView tel = (TextView) convertView.findViewById(R.id.listItem_tel);
        final ImageView heart = (ImageView) convertView.findViewById(R.id.listItem_blackheart);

        LinearLayout addrContainer = (LinearLayout) convertView.findViewById(R.id.listItem_address_view);


        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference().child("Review").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        switch (tabPosition) {
            case CommonApplication.PARKING:
                ParkingItem parkingItem = (ParkingItem) getItem(pos);
                name = parkingItem.getStoreNm();
                addr = parkingItem.getRdnmadr().equals("null") ? "" : parkingItem.getRdnmadr();
                phone = parkingItem.getPhoneNumber().equals("null") ? "" : parkingItem.getPhoneNumber();
                break;

            case CommonApplication.WASHCAR:
                WashCarItem washCarItem = (WashCarItem) getItem(pos);
                name = washCarItem.getStoreNm();
                addr = washCarItem.getRdnmadr().equals("") ? "" : washCarItem.getRdnmadr();
                phone = washCarItem.getPhoneNumber().equals("") ? "" : washCarItem.getPhoneNumber();
                break;

            case CommonApplication.RESTAREA:
                RestAreaItem restAreaItem = (RestAreaItem) getItem(pos);
                name = restAreaItem.getStoreNm();
                addrContainer.setVisibility(View.GONE);
                phone = restAreaItem.getPhoneNumber().equals("null") ? "" : restAreaItem.getPhoneNumber();
                break;

            case CommonApplication.REPAIRSHOP:
                RepairShopItem repairShopItem = (RepairShopItem) getItem(pos);
                name = repairShopItem.getStoreNm();
                addr = repairShopItem.getRdnmadr().equals("") ? "" : repairShopItem.getRdnmadr();
                phone = repairShopItem.getPhoneNumber().equals("") ? "" : repairShopItem.getPhoneNumber();
                break;

            case CommonApplication.CHARGINGCAR:
                ChargingCarItem chargingCarItem = (ChargingCarItem) getItem(pos);
                name = chargingCarItem.getStoreNm();
                addr = chargingCarItem.getRdnmadr().equals("") ? "" : chargingCarItem.getRdnmadr();
                phone = chargingCarItem.getPhoneNumber().equals("") ? "" : chargingCarItem.getPhoneNumber();
                break;

            case CommonApplication.RENTALCAR:
                RentalCarItem rentalCarItem = (RentalCarItem) getItem(pos);
                name = rentalCarItem.getStoreNm();
                addr = rentalCarItem.getRdnmadr().equals("null") ? "" : rentalCarItem.getRdnmadr();
                phone = rentalCarItem.getPhoneNumber().equals("null") ? "" : rentalCarItem.getPhoneNumber();
                break;
        }

        storeName.setText(name);
        address.setText(addr);
        tel.setText(phone);

        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = count + 1;
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user == null) {
                    Toast.makeText(context, "로그인 후 이용할 수 있는 기능입니다.", Toast.LENGTH_SHORT).show();
                } else {
//                    getArrayListWithPosition(pos);

                    switch (tabPosition) {
                        case CommonApplication.PARKING:
//                        RentalCarItem rentalCarItem = (RentalCarItem) getItem(pos);
                            likeAddModel.Like_kind = 0;
                            likeAddModel.Like_kind_name = name;
//                            likeAddModel.Like_kind_num = "1223-w45445";
                            likeAddModel.Like_kind_address = addr;
                            likeAddModel.Like_Kind_tel = phone;

                            break;

                        case CommonApplication.WASHCAR:
                            likeAddModel.Like_kind = 1;
                            break;

                        case CommonApplication.RESTAREA:
                            likeAddModel.Like_kind = 2;
                            break;

                        case CommonApplication.REPAIRSHOP:
                            likeAddModel.Like_kind = 3;
                            break;

                        case CommonApplication.CHARGINGCAR:
                            likeAddModel.Like_kind = 4;
                            break;

                        case CommonApplication.RENTALCAR:
                            likeAddModel.Like_kind = 5;
                            break;
                    }

                    String uid = user.getUid();
                    if (count % 2 == 1) {  //즐겨찾기 삭제
                        heart.setImageResource(R.drawable.whiteheart);
//                        ParkingItem parkingItem = (ParkingItem) getView().getItemAtPosition(position);
//                        storeName = parkingItem.getStoreNm();
//                        addrStr = parkingItem.getRdnmadr();
//                        phoneNum = parkingItem.getPhoneNumber();

                        //키값으로 정렬한 후 키에 해당해는 값을 지정하여 삭제 (리스트의 값들중 하트를 누른 값에 해당하는 num을 지정하지 못함)
                        FirebaseDatabase.getInstance().getReference().child("Like").child(uid).orderByChild("Like_kine_num").equalTo(likeAddModel.Like_kind_num).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    snapshot.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("delete", "onCanceled", databaseError.toException());
                            }
                        });

                    } else {  //즐겨찾기 추가

                        heart.setImageResource(R.drawable.blackheart);
                        FirebaseDatabase.getInstance().getReference().child("Like").child(uid).push().setValue(likeAddModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });
        return convertView;
    }
}
