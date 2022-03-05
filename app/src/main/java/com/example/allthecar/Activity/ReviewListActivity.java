package com.example.allthecar.Activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
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
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.allthecar.Model.ReviewStoreModel;
import com.example.allthecar.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReviewListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ReviewStoreModel reviewStoreModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);  //뒤로가기
        actionBar.setDisplayShowTitleEnabled(false); //원래 타이틀 지우기

        recyclerView = (RecyclerView) findViewById(R.id.review_list_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getLayoutInflater().getContext()));
        recyclerView.setAdapter(new ReviewRecyclerViewAdapter());


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
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class ReviewRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList <ReviewStoreModel> reviewStoreModelArrayList = new ArrayList<>();
        private String uid;

        public ReviewRecyclerViewAdapter() {  //리뷰 목록

            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseDatabase.getInstance().getReference().child("Review").child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    reviewStoreModelArrayList.clear();
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        if (dataSnapshot.exists()) {
                            reviewStoreModelArrayList.add(item.getValue(ReviewStoreModel.class));
                        }
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

//            FirebaseDatabase.getInstance().getReference().child("Review").child(uid).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    reviewStoreModel = dataSnapshot.getValue(ReviewStoreModel.class);

                    ((CustomViewHolder)holder).textView_title.setText(reviewStoreModelArrayList.get(position).review_kind_name);
                    ((CustomViewHolder)holder).textView_review.setText(reviewStoreModelArrayList.get(position).review_text);
                    ((CustomViewHolder)holder).textView_time.setText(reviewStoreModelArrayList.get(position).review_time);
                    if(reviewStoreModelArrayList.get(position).review_rating_score ==null){
                        ((CustomViewHolder)holder).ratingBar.setRating(2);
                    }else {
                        ((CustomViewHolder)holder).ratingBar.setRating(reviewStoreModelArrayList.get(position).review_rating_score);
                    }

                    switch (reviewStoreModelArrayList.get(position).review_kind){
                        case 0:
                            ((CustomViewHolder)holder).textView_kind.setText("주차장");
                            ((CustomViewHolder)holder).imageView_kind.setImageResource(R.drawable.car_icon_parking);
                            break;
                        case 1:
                            ((CustomViewHolder)holder).textView_kind.setText("세차장");
                            ((CustomViewHolder)holder).imageView_kind.setImageResource(R.drawable.car_icon_wash);
                            break;
                        case 2:
                            ((CustomViewHolder)holder).textView_kind.setText("휴게소");
                            ((CustomViewHolder)holder).imageView_kind.setImageResource(R.drawable.car_icon_rest_area);
                            break;
                        case 3:
                            ((CustomViewHolder)holder).textView_kind.setText("정비소");
                            ((CustomViewHolder)holder).imageView_kind.setImageResource(R.drawable.car_icon_repair);
                            break;
                        case 4:
                            ((CustomViewHolder)holder).textView_kind.setText("전기차 충전");
                            ((CustomViewHolder)holder).imageView_kind.setImageResource(R.drawable.car_icon_charging);
                            break;
                        case 5:
                            ((CustomViewHolder)holder).textView_kind.setText("렌터카");
                            ((CustomViewHolder)holder).imageView_kind.setImageResource(R.drawable.car_icon_rental);
                            break;
                    }

//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });

            ((CustomViewHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { //클릭 후 화면 이동
                    Intent intent = new Intent(v.getContext(), DetailActivity.class);
                    ActivityOptions activityOptions = null; //화면 애니메이션 지정
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        activityOptions = ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.fromright, R.anim.toleft);
//                        intent.putExtra("kind", itemview);
//                        intent.putExtra("kindNum", kindNum);
                        startActivity(intent, activityOptions.toBundle());
                    }
                }
            });


        }

        @Override
        public int getItemCount() {
            return reviewStoreModelArrayList.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder { //inner class
            public ImageView imageView_kind;
            public TextView textView_kind;
            public TextView textView_title;
            public RatingBar ratingBar;
            public TextView textView_review;
            public TextView textView_time;

            public CustomViewHolder(View view) {
                super(view);

                imageView_kind = (ImageView) view.findViewById(R.id.reviewItem_imageview_location_kind);
                textView_kind = (TextView) view.findViewById(R.id.reviewItem_kind);
                textView_title = (TextView) view.findViewById(R.id.reviewItem_location);
                ratingBar = (RatingBar) view.findViewById(R.id.reviewItem_ratingbar);
                textView_review = (TextView) view.findViewById(R.id.reviewItem_review);
                textView_time = (TextView) view.findViewById(R.id.reviewItem_review_write_time);
            }
        }

    }
}


