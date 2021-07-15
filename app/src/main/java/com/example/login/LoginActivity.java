package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//2
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth nFirebaseAuth;     //firebase인증
    private DatabaseReference nDataBaseRef; //실시간데이터베이스
    private EditText nEtEmail,nEtPwd;       //회원가입 입력필드



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nFirebaseAuth=FirebaseAuth.getInstance();
        nDataBaseRef= FirebaseDatabase.getInstance().getReference("login");

        nEtEmail=findViewById(R.id.et_email);
        nEtPwd=findViewById(R.id.et_pwd);

        Button btn_login=findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로그인 요청
                String strEmail=nEtEmail.getText().toString();
                String strPwd=nEtPwd.getText().toString();

                nFirebaseAuth.signInWithEmailAndPassword(strEmail,strPwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //로그인 성공!!
                            Intent intent= new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish(); //현재 액티비티 파괴
                        } else{
                            Toast.makeText(LoginActivity.this,"로그인 실패",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });


        Button btn_register=findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //회원가입 화면으로 이동.
                Intent intent=new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}