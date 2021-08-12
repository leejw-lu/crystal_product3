package com.example.crystalProduct;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth FirebaseAuth;     //firebase인증
    private DatabaseReference DataBaseRef; //실시간데이터베이스
    private EditText EtEmail,EtPwd,NickName;       //회원가입 입력필드
    private Button nBtnRegister;            //회원가입 버튼

    //회원가입 처리 시작
    private String strEmail;
    private String strPwd;
    private String strName;

    private ValueEventListener checkRegister = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
            FirebaseUser firebaseUser=FirebaseAuth.getCurrentUser();

            if (snapshot.getChildren() != null){
                Iterator<DataSnapshot> child = snapshot.getChildren().iterator();  //userAccount의 모든 자식들의 key값과 value 값들을 iterator
                long idSize = snapshot.getChildrenCount();    //userAccount에 있는 id 개수
                for (long i = 0; i < idSize ; i++) {
                    //닉네임 중복 검사
                    if (child.next().child("nickname").getValue().equals(NickName.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "이미 등록된 닉네임입니다.", Toast.LENGTH_LONG).show();
                        DataBaseRef.removeEventListener(this);
                        return;
                    }
                }
            }

            //닉네임이 중복이 아닐 경우 : 데이터베이스 추가
            FirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        UserAccount account=new UserAccount();
                        account.setIdToken(firebaseUser.getUid());
                        account.setEmailId(firebaseUser.getEmail());
                        account.setPassword(strPwd);
                        account.setNickname(strName);

                        //setValue : database에 insert(삽입) 행위
                        DataBaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);

                        Toast.makeText(RegisterActivity.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                        sendVerificationEmail();    //이메일 인증 보내기 함수

                    } else {
                        Toast.makeText(RegisterActivity.this, "이미 등록된 이메일입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public void onCancelled(@NonNull @NotNull DatabaseError error) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirebaseAuth=FirebaseAuth.getInstance();
        DataBaseRef= FirebaseDatabase.getInstance().getReference("login");

        EtEmail=findViewById(R.id.et_email);
        EtPwd=findViewById(R.id.et_pwd);
        NickName=findViewById(R.id.et_nickname);

        nBtnRegister=findViewById(R.id.btn_register);
        nBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strEmail=EtEmail.getText().toString();
                strPwd=EtPwd.getText().toString();
                strName=NickName.getText().toString();

                //
                if(TextUtils.isEmpty(strEmail) || TextUtils.isEmpty(strPwd) || TextUtils.isEmpty(strName)) {
                    Toast.makeText(RegisterActivity.this,"모두 입력하십시오.",Toast.LENGTH_SHORT).show();
                } else if (strPwd.length() <6){
                    Toast.makeText(RegisterActivity.this,"비밀번호는 6자리 이상입니다.",Toast.LENGTH_SHORT).show();
                } else if (strName.length() > 0) {
                    DataBaseRef.child("UserAccount").addListenerForSingleValueEvent(checkRegister);   //닉네임 중복 검사
                }

            }
        });

        //회원가입 끝낸 후, login버튼
        Button login_go=findViewById(R.id.login_go);
        login_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //회원가입 화면으로 이동.
                Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    //인증메일 보내는 함수
    private void sendVerificationEmail() {
        final FirebaseUser user = FirebaseAuth.getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "인증메일이 전송되었습니다.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(RegisterActivity.this, "인증메일 전송에 실패했습니다.", Toast.LENGTH_LONG).show();
                }
            }

        });

    }
}
