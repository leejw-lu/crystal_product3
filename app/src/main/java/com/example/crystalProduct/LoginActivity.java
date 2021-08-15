package com.example.crystalProduct;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth FirebaseAuth;     //firebase인증
    private DatabaseReference DataBaseRef; //실시간데이터베이스
    private EditText EtEmail,EtPwd;        //회원가입 입력필드


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseAuth = FirebaseAuth.getInstance();
        DataBaseRef = FirebaseDatabase.getInstance().getReference("login");

        EtEmail = findViewById(R.id.et_email);
        EtPwd = findViewById(R.id.et_pwd);

        Button btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //로그인 요청
                String strEmail = EtEmail.getText().toString();
                String strPwd = EtPwd.getText().toString();

                if (TextUtils.isEmpty(strEmail) || TextUtils.isEmpty(strPwd)) {
                    Toast.makeText(LoginActivity.this, "모두 입력하십시오.", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                if (FirebaseAuth.getCurrentUser().isEmailVerified() == true) {     //이메일 인증 성공해야 로그인성공.
                                    Intent intent = new Intent(LoginActivity.this, com.example.crystalProduct.MainActivity.class);
                                    startActivity(intent);
                                    finish();   //현재 액티비티 파괴
                                } else { //이메일 인증안했을 시,
                                    Toast.makeText(LoginActivity.this, "이메일 인증 후 로그인이 가능합니다.", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(LoginActivity.this, "계정 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                }
            }
        });

        Button btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //회원가입 화면으로 이동.
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        //비밀번호 찾기 화면으로 이동.
        TextView findPassword = findViewById(R.id.findPassword);
        findPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, PasswordResetActivity.class);
                startActivity(intent);
            }
        });
    }
}