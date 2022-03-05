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
import android.widget.Toast;

import com.example.allthecar.Model.UserModel;
import com.example.allthecar.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class JoinActivity extends AppCompatActivity {

    private EditText editText_id, editText_name, editText_password, editText_password_check, editText_phone;

    private Button button_join;
//    private Uri imageUri;

    private FirebaseAuth firebaseAuth;  // 파이어베이스 인증 객체 생성
    private StorageReference storageReference;
//    private static final int PICK_FROM_ALBUM = 10;

    private String name, id, password, password_check, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);  //뒤로가기
        actionBar.setDisplayShowTitleEnabled(false); //원래 타이틀 지우기


        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                // 23 버전 이상일 때 상태바 하얀 색상에 회색 아이콘 색상을 설정
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(Color.parseColor("#ffffff"));
            }
        } else if (Build.VERSION.SDK_INT >= 21) {
            // 21 버전 이상일 때
            getWindow().setStatusBarColor(Color.BLACK);
        }


        editText_name = (EditText) findViewById(R.id.join_edittext_name);
        editText_id = (EditText) findViewById(R.id.join_edittext_id);
        editText_password = (EditText) findViewById(R.id.join_edittext_pw);
        editText_password_check = (EditText) findViewById(R.id.join_edittext_pw_check);
        editText_phone= (EditText) findViewById(R.id.join_edittext_phone);
        button_join = (Button) findViewById(R.id.join_button_join);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();



        button_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  //회원가입
                name = editText_name.getText().toString().trim();
                id = editText_id.getText().toString().trim();
                password = editText_password.getText().toString().trim();
                password_check = editText_password_check.getText().toString().trim();
                phone = editText_phone.getText().toString().trim();

                if(name.isEmpty() ) {
                    editText_name.requestFocus();
                    Toast.makeText(getApplicationContext(), "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if(id.isEmpty()){
                    editText_id.requestFocus();
                    Toast.makeText(getApplicationContext(), "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if(password.isEmpty()){
                    editText_id.requestFocus();
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if(password_check.isEmpty()){
                    editText_id.requestFocus();
                    Toast.makeText(getApplicationContext(), "비밀번호 확인란을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if(phone.isEmpty()){
                    editText_id.requestFocus();
                    Toast.makeText(getApplicationContext(), "핸드폰 번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
//                else if(imageUri ==null){
//                    Toast.makeText(getApplicationContext(), "이미지를 선택해주세요.", Toast.LENGTH_SHORT).show();
//                }
                else if(!password.equals(password_check)){
                    editText_password.requestFocus();
                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }else {
                    //회원가입
                    firebaseAuth
                        .createUserWithEmailAndPassword(id, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull final Task<AuthResult> task) {

                                if (!task.isSuccessful()) { //회원가입 실패한 부분
                                    Toast.makeText(JoinActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show(); //화원가입 에러메시지
                                } else {
                                    final String uid = task.getResult().getUser().getUid();
                                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(editText_name.getText().toString()).build();
                                    task.getResult().getUser().updateProfile(userProfileChangeRequest); //회원가입을 할 때 이름이 여기에 담김

//                                    storageReference.child("userImages").child(uid).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                                        //이미지 파일이름을 uid로 줌
//                                        @Override
//                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

//                                            String imageUrl = storageReference.getDownloadUrl().toString(); //이미지를 스토리지에 넣고, 그 url주소를 DB에 저장
                                            UserModel userModel = new UserModel();
                                            userModel.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            userModel.userName = name; //editText_name.getText().toString();
                                            userModel.userEmail = id; //editText_id.getText().toString();
                                            userModel.phoneNumber = phone; //
//                                            userModel.profileImageUrl = imageUrl;

                                            //userModel에 회원가입 항목을 넣고 userModel을 DB에 저장
                                            FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (!task.isSuccessful()) {  //회원가입 실패한 부분
                                                        Toast.makeText(JoinActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        JoinActivity.this.finish();
                                                        Toast.makeText(JoinActivity.this, "회원가입이 되었습니다.", Toast.LENGTH_SHORT).show();
                                                        Intent i5 = new Intent(getApplicationContext(), LoginActivity.class);
                                                        i5.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                                        startActivity(i5);
                                                    }
                                                }
                                            });
                                        }
//                                    });
//                                }
                            }
                        });
                }
            }
        });

    } //onCreate

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode==PICK_FROM_ALBUM &&resultCode==RESULT_OK) {
//            profile.setImageURI(data.getData());//가운데 뷰를 바꿈
//            imageUri=data.getData(); //이미지 경로 원본
//        }
//    }

}