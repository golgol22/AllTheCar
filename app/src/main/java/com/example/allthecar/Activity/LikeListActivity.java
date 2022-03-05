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
import android.widget.TextView;

import com.example.allthecar.Model.LikeAddModel;
import com.example.allthecar.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class LikeListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
//    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
    LikeAddModel likeAddModel;
    public ArrayList<String> kind = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.like_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);  //뒤로가기
        actionBar.setDisplayShowTitleEnabled(false); //원래 타이틀 지우기

        recyclerView = (RecyclerView) findViewById(R.id.like_list_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getLayoutInflater().getContext()));
        recyclerView.setAdapter(new LikeRecyclerViewAdapter());

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

    class LikeRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList <LikeAddModel> likeAddModelList = new ArrayList<>();
        private String uid;

        public LikeRecyclerViewAdapter() {  //좋아요 목록
            //어댑터 생성시 컨텍스트와 데이터 배열을 가져옴

            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseDatabase.getInstance().getReference().child("Like").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    likeAddModelList.clear();
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        if (dataSnapshot.exists()) {
                            likeAddModelList.add(item.getValue(LikeAddModel.class));
                        }
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_like, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

            FirebaseDatabase.getInstance().getReference().child("Like").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    likeAddModel = dataSnapshot.getValue(LikeAddModel.class);

                    ((CustomViewHolder)holder).textView_title.setText(likeAddModelList.get(position).Like_kind_name);
                    switch (likeAddModelList.get(position).Like_kind){
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
                    ((CustomViewHolder)holder).textView_address.setText(likeAddModelList.get(position).Like_kind_address);
                    ((CustomViewHolder)holder).textView_tel.setText(likeAddModelList.get(position).Like_Kind_tel);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



            ((CustomViewHolder)holder).imageView_heart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //하트를 눌렀을 때

                }
            });


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

//            ((CustomViewHolder)holder).imageView_heart.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //즐겨찾기 DB에서 삭제
//                    ((CustomViewHolder)holder).imageView_heart.setImageResource(R.drawable.whiteheart);
//                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
//                    Query query = ref.child("Review").child(uid).orderByChild("Like_kind_num");
//
//                    query.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            for (DataSnapshot Snapshot: dataSnapshot.getChildren()) {
//                                Snapshot.getRef().removeValue();
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
////                            Log.e(TAG, "onCancelled", databaseError.toException());
//                        }
//                    });
//                }
//            });


        }

        @Override
        public int getItemCount() {
            return likeAddModelList.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder { //inner class
            public ImageView imageView_kind;
            public TextView textView_kind;
            public TextView textView_title;
            public TextView textView_address;
            public TextView textView_tel;
            public ImageView imageView_heart;

            public CustomViewHolder(View view) {
                super(view);

                imageView_kind = (ImageView) view.findViewById(R.id.likeItem_imageview_location_kind);
                textView_kind = (TextView) view.findViewById(R.id.likeItem_kind);
                textView_title = (TextView) view.findViewById(R.id.likeItem_textview_location_name);
                textView_address = (TextView) view.findViewById(R.id.likeItem_textview_location_address);
                textView_tel = (TextView) view.findViewById(R.id.likeItem_textview_location_tel);
                imageView_heart = (ImageView) view.findViewById(R.id.likeItem_blackheart);
            }
        }

    }
}


