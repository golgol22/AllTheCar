package com.example.allthecar.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allthecar.CommonApplication;
import com.example.allthecar.Model.ReviewStoreModel;
import com.example.allthecar.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReviewWriteActivity extends AppCompatActivity {

    private CommonApplication application;
    ImageView imageView_location_kind;
    TextView textView_location_kind, textView_location_name;
    RatingBar ratingBar;
    EditText editText_review;
    Button button_reg;
    String storeName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_write);

        application = (CommonApplication) CommonApplication.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);  //뒤로가기
        actionBar.setDisplayShowTitleEnabled(false); //원래 타이틀 지우기

        final Intent intent = getIntent();
        storeName = intent.getStringExtra("storeName");

        imageView_location_kind = (ImageView) findViewById(R.id.review_write_imageview_type);
        textView_location_kind = (TextView) findViewById(R.id.review_write_textview_location_kind);
        textView_location_name = (TextView) findViewById(R.id.review_write_textview_location_name);
        ratingBar = (RatingBar) findViewById(R.id.review_write_ratingbar);
        editText_review = (EditText) findViewById(R.id.review_write_edittext_review_write);
        button_reg = (Button) findViewById(R.id.review_write_button_reg);

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

        switch (application.tabPositionNum) {
            case CommonApplication.PARKING:
                imageView_location_kind.setImageResource(R.drawable.car_icon_parking);
                textView_location_kind.setText("주차장");
                break;

            case CommonApplication.WASHCAR:
                imageView_location_kind.setImageResource(R.drawable.car_icon_wash);
                textView_location_kind.setText("세차장");
                break;

            case CommonApplication.RESTAREA:
                imageView_location_kind.setImageResource(R.drawable.car_icon_rest_area);
                textView_location_kind.setText("휴게소");
                break;

            case CommonApplication.REPAIRSHOP:
                imageView_location_kind.setImageResource(R.drawable.car_icon_repair);
                textView_location_kind.setText("정비소");
                break;

            case CommonApplication.CHARGINGCAR:
                imageView_location_kind.setImageResource(R.drawable.car_icon_charging);
                textView_location_kind.setText("전기차 충전소");
                break;

            case CommonApplication.RENTALCAR:
                imageView_location_kind.setImageResource(R.drawable.car_icon_rental);
                textView_location_kind.setText("렌터카카");
               break;
        }

        textView_location_name.setText(storeName);

        button_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ratingBar.getNumStars() == 0) {
                    Toast.makeText(ReviewWriteActivity.this, "별점을 내주세요.", Toast.LENGTH_SHORT).show();
                } else if (editText_review.getText().toString().trim().equals("")) {
                    Toast.makeText(ReviewWriteActivity.this, "리뷰를 작성해주세요.", Toast.LENGTH_SHORT).show();
                } else if(editText_review.getText().toString().length() <20) {
                    Toast.makeText(ReviewWriteActivity.this, "리뷰를 20자이상 작성해주세요.", Toast.LENGTH_SHORT).show();
                } else {

                    final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    final ReviewStoreModel reviewStoreModel = new ReviewStoreModel();

                    long now = System.currentTimeMillis(); //현재시간을 구함
                    Date date = new Date(now); //현재시간을(now) 변수에 저장
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                    final String time = simpleDateFormat.format(date); //현재 시간을 형식에 맞게 만들고 변수에 저장

                    reviewStoreModel.review_kind = application.tabPositionNum; //포지션값
                    reviewStoreModel.review_kind_name =storeName ; //장소 이름
//                    reviewStoreModel.review_kind_num =  ; //장소 식별값
                    ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                            if(String.valueOf(rating).equals("0")){
                                Toast.makeText(ReviewWriteActivity.this, "평점을 매겨주세요.", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(ReviewWriteActivity.this, String.valueOf(rating), Toast.LENGTH_SHORT).show();
                                reviewStoreModel.review_rating_score = Float.parseFloat(String.valueOf(rating));
                            }
                        }
                    });
                    reviewStoreModel.review_text = editText_review.getText().toString();
                    reviewStoreModel.review_time = time;

                    FirebaseDatabase.getInstance().getReference().child("Review").child(uid).push().setValue(reviewStoreModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(ReviewWriteActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                ReviewWriteActivity.this.finish();
                                Toast.makeText(ReviewWriteActivity.this, "리뷰가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });

                }
            }
        });



    }//onCreate
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}



