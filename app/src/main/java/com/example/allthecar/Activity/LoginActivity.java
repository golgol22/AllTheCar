package com.example.allthecar.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allthecar.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText editText_id, editText_password;
    private CheckBox checkBox_autologin;
    private Button button_login;
    private TextView textView_join;

    private FirebaseAuth firebaseAuth;

    SharedPreferences prof;
    SharedPreferences.Editor editor;

    private String user_id, user_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

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
        }
        else if (Build.VERSION.SDK_INT >= 21) {
            // 21 버전 이상일 때
            getWindow().setStatusBarColor(Color.BLACK);
        }

        firebaseAuth = FirebaseAuth.getInstance();

        prof =  getSharedPreferences("auto_login", 0);
        editor = prof.edit(); //id, password 저장

//        key=getSharedPreferences("loginkey", 0);
//        editorkey= key.edit(); //로그인 여부 확인 boolean값 저장

        editText_id = (EditText) findViewById(R.id.login_edittext_id);
        editText_password = (EditText) findViewById(R.id.login_edittext_pw);
        checkBox_autologin = (CheckBox)findViewById(R.id.login_checkbox_auto);
        button_login = (Button) findViewById(R.id.login_button_login);
        textView_join =(TextView)findViewById(R.id.login_textview_join);


        if(checkBox_autologin.isChecked()) {
            if (prof.getBoolean("auto_save", true) == true) {  //불러올때
                editText_id.setText(prof.getString("id", ""));
                editText_password.setText(prof.getString("pw", ""));
                checkBox_autologin.setChecked(true);
            }
        }else{
            checkBox_autologin.setChecked(false);
        }


        checkBox_autologin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBox_autologin.isChecked() == true) {
                    editor.putString("id", editText_id.getText().toString().trim());
                    editor.putString("pw", editText_password.getText().toString().trim());
                    editor.putBoolean("auto_save", true);
                    editor.commit();  //저장(안해주면 변경값 저장 안됨)
                } else {
                    editor.clear(); //모두 없애줄때
                    editor.commit();
                   // checkBox_autologin.setChecked(false); //체크박스 선택 해제
                }
            }
        });


        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginEvent(); //다음화면으로 넘겨주는 기능 담당
            }
        });
        textView_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  //회원가입
                Intent i = new Intent(getApplicationContext(), JoinActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
            }
        });

    }//onCreate

    void loginEvent() {
        user_id = editText_id.getText().toString().trim();
        user_password = editText_password.getText().toString().trim();

        if (user_id.isEmpty()) {
            editText_id.requestFocus();
            Toast.makeText(LoginActivity.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else if (user_password.isEmpty()) {
            editText_password.requestFocus();
            Toast.makeText(LoginActivity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            firebaseAuth.signInWithEmailAndPassword(user_id, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) { //로그인이 맞는지만 확인 (다음화면으로 넘겨주는 기능 담당하지 않음)
                    if (!task.isSuccessful()) {
                        //로그인 실패한 부분
                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        //로그인 성공
                        editor.putString("uid", firebaseAuth.getUid());
                        editor.commit();
                        Toast.makeText(LoginActivity.this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            });
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

}

