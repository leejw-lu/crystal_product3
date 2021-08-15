package com.example.crystalProduct;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class NicknameResetActivity extends AppCompatActivity {

    private EditText NickName;
    private Button checkNickname;
    private DatabaseReference DataBaseRef;
    private String strName;

    private ValueEventListener checkRegister = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

            if (snapshot.getChildren() != null) {
                if (NickName.getText().toString().length() > 7) {
                    Toast.makeText(getApplicationContext(), "닉네임은 7자리까지 가능합니다.", Toast.LENGTH_LONG).show();
                    return;
                }

                Iterator<DataSnapshot> child = snapshot.getChildren().iterator();  //userAccount의 모든 자식들의 key값과 value 값들을 iterator
                long idSize = snapshot.getChildrenCount();    //userAccount에 있는 id 개수
                for (long i = 0; i < idSize; i++) {
                    //닉네임 중복 검사
                    if (child.next().child("nickname").getValue().equals(NickName.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "이미 등록된 닉네임입니다.", Toast.LENGTH_LONG).show();
                        DataBaseRef.removeEventListener(this);
                        return;
                    }
                }
            }

            //닉넴 중복이 없다면
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // 로그인한 유저의 정보 가져오기
            String uid = user != null ? user.getUid() : null; // 로그인한 유저의 고유 uid 가져오기

            DatabaseReference nickname = DataBaseRef.child("UserAccount").child(uid).child("nickname");

            nickname.setValue(strName);
            Toast.makeText(NicknameResetActivity.this, "닉네임 변경이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(NicknameResetActivity.this, MainActivity.class));
        }

        public void onCancelled(@NonNull @NotNull DatabaseError error) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname_reset);

        DataBaseRef= FirebaseDatabase.getInstance().getReference("login");
        NickName=findViewById(R.id.EditTextNickname);
        checkNickname=findViewById(R.id.checkNickname);

        checkNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strName=NickName.getText().toString();
                if(strName.length() > 0) {
                    DataBaseRef.child("UserAccount").addListenerForSingleValueEvent(checkRegister); //닉네임 중복 검사
                }
                else{   //빈칸입력
                    Toast.makeText(NicknameResetActivity.this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
