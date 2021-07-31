package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class Frag5 extends Fragment {
    private View view;
    private Button btn_logout;

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseDatabase database;

    ImageView profile_imageview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.frag5_profile, container,false);

        mAuth = FirebaseAuth.getInstance();
        btn_logout = (Button) view.findViewById(R.id.btn_logout);

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
