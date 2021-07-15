package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth nFirebaseAuth;     //firebase인증
    private DatabaseReference nDataBaseRef; //실시간데이터베이스
    private EditText nEtEmail,nEtPwd;       //회원가입 입력필드
    private Button nBtnRegister;            //회원가입 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nFirebaseAuth=FirebaseAuth.getInstance();
        nDataBaseRef= FirebaseDatabase.getInstance().getReference("login");

        nEtEmail=findViewById(R.id.et_email);
        nEtPwd=findViewById(R.id.et_pwd);
        nBtnRegister=findViewById(R.id.btn_register);

        nBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //회원가입 처리 시작
                String strEmail=nEtEmail.getText().toString();
                String strPwd=nEtPwd.getText().toString();

                //
                //Firebase Auth 진행
                nFirebaseAuth.createUserWithEmailAndPassword(strEmail,strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser=nFirebaseAuth.getCurrentUser();
                            UserAccount account=new UserAccount();
                            account.setIdToken(firebaseUser.getUid());
                            account.setEmailId(firebaseUser.getEmail());
                            account.setPassword(strPwd);


                            //setValue : database에 insert(삽입) 행위
                            nDataBaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);

                            Toast.makeText(RegisterActivity.this,"회원가입에 성공하셨습니다.",Toast.LENGTH_SHORT).show();
                        } else{
                            Toast.makeText(RegisterActivity.this,"회원가입에 실패하셨습니다.",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });



    }
}