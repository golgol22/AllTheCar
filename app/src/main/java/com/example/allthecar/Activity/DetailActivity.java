package com.example.allthecar.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allthecar.CommonApplication;
import com.example.allthecar.Model.ReviewStoreModel;
import com.example.allthecar.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class DetailActivity extends AppCompatActivity {

    private CommonApplication application;
    ReviewStoreModel reviewStoreModel;
    ArrayList <ReviewStoreModel> arrayList;
    Boolean written=false;
    ArrayList <ReviewStoreModel> detailreviewStoreModelArrayList = new ArrayList<>();
    ArrayList<String> userID_list = new ArrayList<>();

    ImageView detail_type_image;
    Button button_review;

    RecyclerView recyclerView;
    LinearLayout detail_parking, detail_wash_car, detail_rest_area, detail_repair_shop, detail_charging, detail_rental_car;

    FirebaseUser user;
    String storeName="", addrStr="", phoneNum="";
    String uid="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);  //뒤로가기
        actionBar.setDisplayShowTitleEnabled(false); //원래 타이틀 지우기
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

        application = (CommonApplication) CommonApplication.getInstance();

        detail_type_image = (ImageView) findViewById(R.id.detail_type_image);

        detail_parking = (LinearLayout) findViewById(R.id.detail_parking);
        detail_wash_car = (LinearLayout) findViewById(R.id.detail_wash_car);
        detail_rest_area = (LinearLayout) findViewById(R.id.detail_rest_area);
        detail_repair_shop= (LinearLayout) findViewById(R.id.detail_repair_shop);
        detail_charging = (LinearLayout) findViewById(R.id.detail_charging);
        detail_rental_car = (LinearLayout) findViewById(R.id.detail_rental_car);

        button_review = (Button) findViewById(R.id.detail_button_review_write);

        recyclerView = (RecyclerView) findViewById(R.id.detail_recyclerView_review);

        final Intent intent = getIntent();

//        Bitmap bitmap = (Bitmap)intent.getParcelableExtra("icon");
          storeName = intent.getStringExtra("storeName");
//        int position = intent.getIntExtra("position", 0);
          addrStr = intent.getStringExtra("address");
          phoneNum = intent.getStringExtra("tel");

        arrayList = new ArrayList<>(); //내가 작성한 리뷰 목록을 받아옴
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        written_OK();
        reviewReloading();

        //이미 작성한 데이터가 있는 항목인데 또 작성되는 것을 방지해서 서버에 데이터를 조회해서 있으면 VISIBLE처리
        if(written.equals(true)){
            button_review.setVisibility(View.INVISIBLE);
        }else{
            button_review.setVisibility(View.VISIBLE);
        }

        button_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user == null) {
                    Toast.makeText(getApplicationContext(), "로그인 후 이용할 수 있는 기능입니다.", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent1 = new Intent(getApplicationContext(), ReviewWriteActivity.class);
                    intent1.putExtra("storeName", storeName);
                    startActivity(intent1);
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getLayoutInflater().getContext()));
        recyclerView.setAdapter(new DetailReviewRecyclerViewAdapter());

        switch (application.tabPositionNum) {
            case CommonApplication.PARKING:
                detail_type_image.setImageResource(R.drawable.car_icon_parking);
                detail_parking.setVisibility(View.VISIBLE);
                detail_wash_car.setVisibility(View.INVISIBLE);
                detail_rest_area.setVisibility(View.INVISIBLE);
                detail_repair_shop.setVisibility(View.INVISIBLE);
                detail_charging.setVisibility(View.INVISIBLE);
                detail_rental_car.setVisibility(View.INVISIBLE);
                break;
            case CommonApplication.WASHCAR:
                detail_type_image.setImageResource(R.drawable.car_icon_wash);
                detail_parking.setVisibility(View.INVISIBLE);
                detail_wash_car.setVisibility(View.VISIBLE);
                detail_rest_area.setVisibility(View.INVISIBLE);
                detail_repair_shop.setVisibility(View.INVISIBLE);
                detail_charging.setVisibility(View.INVISIBLE);
                detail_rental_car.setVisibility(View.INVISIBLE);
                break;

            case CommonApplication.RESTAREA:
                detail_type_image.setImageResource(R.drawable.car_icon_rest_area);
                detail_parking.setVisibility(View.INVISIBLE);
                detail_wash_car.setVisibility(View.INVISIBLE);
                detail_rest_area.setVisibility(View.VISIBLE);
                detail_repair_shop.setVisibility(View.INVISIBLE);
                detail_charging.setVisibility(View.INVISIBLE);
                detail_rental_car.setVisibility(View.INVISIBLE);
                break;

            case CommonApplication.REPAIRSHOP:
                detail_type_image.setImageResource(R.drawable.car_icon_repair);
                detail_parking.setVisibility(View.INVISIBLE);
                detail_wash_car.setVisibility(View.INVISIBLE);
                detail_rest_area.setVisibility(View.INVISIBLE);
                detail_repair_shop.setVisibility(View.VISIBLE);
                detail_charging.setVisibility(View.INVISIBLE);
                detail_rental_car.setVisibility(View.INVISIBLE);
                break;

            case CommonApplication.CHARGINGCAR:
                detail_type_image.setImageResource(R.drawable.car_icon_charging);
                detail_parking.setVisibility(View.INVISIBLE);
                detail_wash_car.setVisibility(View.INVISIBLE);
                detail_rest_area.setVisibility(View.INVISIBLE);
                detail_repair_shop.setVisibility(View.INVISIBLE);
                detail_charging.setVisibility(View.VISIBLE);
                detail_rental_car.setVisibility(View.INVISIBLE);
                break;

            case CommonApplication.RENTALCAR:
                detail_type_image.setImageResource(R.drawable.car_icon_rental);
                detail_parking.setVisibility(View.INVISIBLE);
                detail_wash_car.setVisibility(View.INVISIBLE);
                detail_rest_area.setVisibility(View.INVISIBLE);
                detail_repair_shop.setVisibility(View.INVISIBLE);
                detail_charging.setVisibility(View.INVISIBLE);
                detail_rental_car.setVisibility(View.VISIBLE);
                break;
        }


        TextView storeTitle = (TextView) findViewById(R.id.detail_store_name);
        storeTitle.setText(storeName);

        final TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tab1 = tabHost.newTabSpec("TAB1").setIndicator("상세정보");
        tab1.setContent(R.id.linear1);
        tabHost.addTab(tab1);

        TabHost.TabSpec tab2 = tabHost.newTabSpec("TAB2").setIndicator("리뷰");
        tab2.setContent(R.id.linear2);
        tabHost.addTab(tab2);


    }//onCreate


    public void written_OK() {
        FirebaseDatabase.getInstance().getReference().child("Review").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    if (dataSnapshot.exists()) {
                        arrayList.add(item.getValue(ReviewStoreModel.class));
                    }
                }

                for (int i = 0; i < arrayList.size(); i++) {
                    if (storeName.equals(arrayList.get(i).review_kind_name)) {
                        written = true;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void reviewReloading() {  //리뷰 데이터 리로딩시킴

        userID_list.clear();
        detailreviewStoreModelArrayList.clear();

        FirebaseDatabase.getInstance().getReference().child("Review").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    if (dataSnapshot.exists()) {
                        userID_list.add(item.getKey());
                    }
                }
                Toast.makeText(getApplicationContext(), userID_list.get(0), Toast.LENGTH_SHORT).show(); //찍힘
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        for (int i=0; i<userID_list.size(); i++) {
            final int pos = i;
            FirebaseDatabase.getInstance().getReference().child("Review").child(userID_list.get(pos)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    reviewStoreModel = dataSnapshot.getValue(ReviewStoreModel.class);

                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        if (dataSnapshot.exists()) {
                            if(item.getValue(ReviewStoreModel.class).review_kind_name.equals(storeName)) {
                                detailreviewStoreModelArrayList.add(item.getValue(ReviewStoreModel.class));
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    class DetailReviewRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

//        private String uid;
        public DetailReviewRecyclerViewAdapter() {  //디테일 리뷰 목록
//            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            reviewReloading();
            notifyDataSetChanged();

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_review, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

            ((CustomViewHolder)holder).textView_review.setText(detailreviewStoreModelArrayList.get(position).review_text);
            if(detailreviewStoreModelArrayList.get(position).review_rating_score == null){
                ((CustomViewHolder)holder).ratingBar.setRating(1);
            }else {
                ((CustomViewHolder)holder).ratingBar.setRating(detailreviewStoreModelArrayList.get(position).review_rating_score);
            }

        }

        @Override
        public int getItemCount() {
            return detailreviewStoreModelArrayList.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder { //inner class
//            public ImageView imageView_kind;
//            public TextView textView_kind;
//            public TextView textView_title;
            public RatingBar ratingBar;
            public TextView textView_review;
//            public TextView textView_time;

            public CustomViewHolder(View view) {
                super(view);

//                imageView_kind = (ImageView) view.findViewById(R.id.reviewItem_imageview_location_kind);
//                textView_kind = (TextView) view.findViewById(R.id.reviewItem_kind);
//                textView_title = (TextView) view.findViewById(R.id.reviewItem_location);
                ratingBar = (RatingBar) view.findViewById(R.id.detail_review_ratingbar);
                textView_review = (TextView) view.findViewById(R.id.detail_review_text);
//                textView_time = (TextView) view.findViewById(R.id.reviewItem_review_write_time);
            }
        }

    }

    @Override
    protected void onResume() {  //리뷰를 작성하고 디테일 페이지에 왔을때 firebase에서 리뷰 목록 다시 불러옴
        super.onResume();
        //데이터 리로드
        //리뷰버튼 gone
        reviewReloading();
        button_review.setVisibility(View.GONE);
    }

}
