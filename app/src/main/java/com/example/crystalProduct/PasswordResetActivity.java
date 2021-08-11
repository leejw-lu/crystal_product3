package com.example.crystalProduct;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class PasswordResetActivity extends AppCompatActivity {

    //변수선언
    EditText editTextUserEmail;
    Button buttonFind;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        editTextUserEmail=findViewById(R.id.editTextUserEmail);

        buttonFind=findViewById(R.id.buttonFind);

        buttonFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailadress=editTextUserEmail.getText().toString();

                if(emailadress.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){     //가입한 이메일이 맞는지 확인.

                    FirebaseAuth.getInstance().sendPasswordResetEmail(emailadress).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(PasswordResetActivity.this, "비밀번호 변경 메일을 전송했습니다.", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(PasswordResetActivity.this, "비밀번호 변경에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
                //가입한 이메일 정보와 EdiText의 이메일주소가 다르면.
                else{
                    Toast.makeText(PasswordResetActivity.this, "가입한 이메일 주소가 아닙니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //비밀번호재설정 후, login버튼
        Button login_go2=findViewById(R.id.login_go2);
        login_go2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로그인 화면으로 이동.
                Intent intent=new Intent(PasswordResetActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


    }   //Oncreate함수

}
