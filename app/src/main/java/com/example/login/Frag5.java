package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

public class Frag5 extends Fragment {
    private View view;
    private Button btn_logout;

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseDatabase database;

    //database 이메일 가져오기
    DatabaseReference mDatabase;
    TextView email_info,nickname_info;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.frag5_profile, container,false);

        mAuth = FirebaseAuth.getInstance();
        btn_logout = (Button) view.findViewById(R.id.btn_logout);   //로그아웃 버튼

        // 닉네임, 이메일 정보 가져오기.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // 로그인한 유저의 정보 가져오기
        String uid = user != null ? user.getUid() : null; // 로그인한 유저의 고유 uid 가져오기

        mAuth = FirebaseAuth.getInstance(); // 유저 계정 정보 가져오기
        mDatabase = FirebaseDatabase.getInstance().getReference("login"); // 파이어베이스 realtime database 에서 정보 가져오기

        DatabaseReference email = mDatabase.child("UserAccount").child(uid).child("emailId");
        DatabaseReference nickname = mDatabase.child("UserAccount").child(uid).child("nickname");

        //DatabaseReference 원하는 변수 = mDatabase.child(uid).child("nickname");
        // uid = 파이어베이스 유저 고유 uid , nickname = 데이터 베이스 child 명

        //닉네임 띄워주기
        nickname_info=view.findViewById(R.id.nickname_info);
        nickname.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.getValue(String.class);
                nickname_info.setText(name);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        //이메일 띄워주기
        email_info=view.findViewById(R.id.email_address);
        email.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            String name = snapshot.getValue(String.class);
            email_info.setText(name);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) { }
    });

        //로그아웃 버튼
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_logout) {
                    mAuth.signOut();

                    Intent intent = new Intent(getActivity(), LoginActivity.class); //fragment는 this못쓰기 때문에 get쓰기.
                    startActivity(intent);
                    }
                }
            });
        return view;
    }


}


