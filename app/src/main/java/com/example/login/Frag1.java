package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Frag1 extends Fragment {

    private View view;

    //지우 변수추가
    private RecyclerView recyclerView;
    private List<ImageDTO> imageDTOList = new ArrayList<>();
    private List<String> uidList = new ArrayList<>();
    private FirebaseDatabase firebaseDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        view = inflater.inflate(R.layout.frag1_home, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.main_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(new MyRecyclerViewAdapter());

        final MyRecyclerViewAdapter uploadedImageAdapter = new MyRecyclerViewAdapter(imageDTOList, uidList, new MyRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(ImageDTO details) {
                //showToast(details.getTitle() + " Clicked");

                Intent intent = new Intent(getActivity(), ProductDetailPage.class); //fragment는 this못쓰기 때문에 get쓰기.
                //intent했을때, productdetailpage.java 액티비티로 해당 post의 값 보내기
                intent.putExtra("image",details.getImageUrl());
                intent.putExtra("title",details.getTitle()); //jw추가
                intent.putExtra("price",details.getPrice());
                intent.putExtra("deadline",details.getDeadline());
                intent.putExtra("form",details.getPurchaseLink());
                intent.putExtra("description",details.getDescription());

                //댓글기능할떄 추가함
                intent.putExtra("postid",details.getPostid());
                intent.putExtra("publisherid",details.getUid());

                startActivity(intent);
            }
        });


        recyclerView.setAdapter(uploadedImageAdapter);//데이터 넣기기

        //옵저버 패턴 --> 변화가 있으면 클라이언트에 알려준다.
        firebaseDatabase.getReference().child("Post").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {  //변화된 값이 DataSnapshot 으로 넘어온다.
                //데이터가 쌓이기 때문에  clear()
                imageDTOList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren())           //여러 값을 불러와 하나씩
                {
                    ImageDTO imageDTO = ds.getValue(ImageDTO.class);
                    imageDTOList.add(imageDTO);
                }
                uploadedImageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    private void showToast(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

}


