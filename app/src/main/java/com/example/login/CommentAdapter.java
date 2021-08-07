package com.example.login;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>  {

    private Context mContext;
    private List<CommentsDTO> mComments;

    FirebaseUser firebaseUser;
    DatabaseReference mDatabase;

    //생성자
    public CommentAdapter(Context mContext, List<CommentsDTO> mComments) {
        this.mContext = mContext;
        this.mComments = mComments;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        /*
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.uploaded_image_item,parent,false);
         */

        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);
        return new CommentAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CommentAdapter.ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        CommentsDTO comment = mComments.get(position);

        //리사이클러뷰에서 댓글보이기setText하기. -> 댓글쓴 사람의 닉네임이랑 댓글내용이 보여진다.
        holder.comment.setText(comment.getComment());
        getUserInfo(holder.username,comment.getPublisher());

    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username, comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
        }
    }

    //댓글단 유저정보 가져오기
    private void getUserInfo(TextView username,String publisherid) {

        mDatabase = FirebaseDatabase.getInstance().getReference("login"); // 파이어베이스 realtime database 에서 정보 가져오기
        DatabaseReference nickname = mDatabase.child("UserAccount").child(publisherid).child("nickname");

        //댓글에서 댓글단 사용자의 닉네임 띄워주기.
        nickname.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.getValue(String.class);
                username.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });

    }

}