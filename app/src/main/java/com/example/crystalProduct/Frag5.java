package com.example.crystalProduct;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Frag5 extends Fragment {

    private View view;
    private Button btn_logout,btn_reNickname,btn_rePassword;

    //database 이메일 가져오기
    private DatabaseReference mDatabase;
    private TextView email_info,nickname_info;

    private TextView emptyLiked;

    private RecyclerView recyclerView;
    private MyRecyclerViewAdapter uploadedImageAdapter;
    private List<ImageDTO> imageDTOList = new ArrayList<>();
    private List<String> postList = new ArrayList<>();
    private List<String> uidList = new ArrayList<>();
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;


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

        emptyLiked = (TextView) view.findViewById(R.id.emptyLiked);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = (RecyclerView) view.findViewById(R.id.heart_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(new MyRecyclerViewAdapter());

        uploadedImageAdapter = new MyRecyclerViewAdapter(imageDTOList, uidList, new MyRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(ImageDTO details,String pos) {
                Intent intent = new Intent(getActivity(), ProductDetailPage.class); //fragment는 this못쓰기 때문에 get쓰기.
                //intent했을때, productdetailpage.java 액티비티로 해당 post의 값 보내기
                intent.putExtra("image",details.getImageUrl());
                intent.putExtra("title",details.getTitle()); //jw추가
                intent.putExtra("price",details.getPrice());
                intent.putExtra("deadline",details.getDeadline());
                intent.putExtra("form",details.getPurchaseLink());
                intent.putExtra("description",details.getDescription());

                //댓글기능할떄 추가함 + 하트
                intent.putExtra("postid",details.getPostid());
                intent.putExtra("publisherid",details.getUserEmail());

                //글 삭제할때 글올린 사람의 uid 보내기
                intent.putExtra("postuid",details.getUid());
                //글 삭제 (Post밑의 토큰 삭제.)
                intent.putExtra("postToken",pos);
                intent.putExtra("imageName",details.getImageName());

                startActivity(intent);
            }
        });
        recyclerView.setAdapter(uploadedImageAdapter);

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

        //닉네임변경 버튼
        btn_reNickname=view.findViewById(R.id.btn_reNickname);
        btn_reNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(),NicknameResetActivity.class);
                startActivity(intent);

            }
        });

        //비밀번호 재설정 창으로 이동.
        btn_rePassword=view.findViewById(R.id.btn_rePassword);
        btn_rePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),PasswordResetActivity.class);
                startActivity(intent);
            }
        });


        getHeart(); //관심상품목록 가져오기


        /////이지현추가

        ImageView heartIcon = (ImageView) view.findViewById(R.id.SecondIcon);
        int color = ContextCompat.getColor(getActivity(), R.color.mainPurple);
        heartIcon.setColorFilter(color);

        if(postList.size()==0)
            emptyLiked.setVisibility(View.VISIBLE);


        return view;
    }


    //양성원 추가


    private void getHeart() {
        //바로 아랫줄이 오류 코드임
        // Kod dev 코드 >>
        //DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Likes").child(id);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Likes").child(firebaseUser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    postList.add(snapshot.getKey());
                }
                showHeartProduct();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showHeartProduct() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imageDTOList.clear();
                uidList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ImageDTO imageDTO = snapshot.getValue(ImageDTO.class);
                    String uidKey=snapshot.getKey();
                    for (String postid : postList){
                        if (imageDTO.getPostid().equals(postid)){
                            imageDTOList.add(imageDTO);
                            uidList.add(uidKey);
                        }
                        if (imageDTOList.size()!=0)
                            emptyLiked.setVisibility(View.GONE);
                    }
                }
                uploadedImageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}


