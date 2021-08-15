package com.example.crystalProduct.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crystalProduct.DTO.CommentsDTO;
import com.example.crystalProduct.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private String postid;
    private String postuid; //댓글에 글쓴이 표시

    FirebaseUser firebaseUser;
    DatabaseReference mDatabase;

    public CommentAdapter(Context mContext, List<CommentsDTO> mComments,String postid,String postuid) {
        this.mContext = mContext;
        this.mComments = mComments;
        this.postid=postid;
        this.postuid=postuid;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CommentAdapter.ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        CommentsDTO comment = mComments.get(position);

        //리사이클러뷰에서 댓글보이기setText하기. -> 댓글쓴 사람의 닉네임이랑 댓글내용이 보여진다.
        holder.comment.setText(comment.getComment());
        getUserInfo(holder.username,comment.getPublisher(),postuid, holder.writer);

        //댓글 삭제하기 -> 사용자가 해당댓글 longclick하면 알림창뜨게.
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (comment.getPublisher().equals(firebaseUser.getUid())){         // 댓글쓴 사람이 본인인지 확인.
                    AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle("댓글을 삭제하시겠습니까?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseDatabase.getInstance().getReference("Comments")
                                            .child(postid).child(comment.getCommentid())
                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(mContext, "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                return true;
            }
        });


    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username, comment , writer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
            writer= itemView.findViewById(R.id.writer);     //댓글에 글쓴이 표시
        }
    }

    //댓글단 유저정보 가져오기
    private void getUserInfo(TextView username,String publisherid,String postuid,TextView writer) {

        mDatabase = FirebaseDatabase.getInstance().getReference("login"); // 파이어베이스 realtime database 에서 정보 가져오기
        DatabaseReference nickname = mDatabase.child("UserAccount").child(publisherid).child("nickname");

        //댓글에서 댓글단 사용자의 닉네임 띄워주기.
        nickname.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.getValue(String.class);
                username.setText(name);

                if(postuid.equals(publisherid)) {    //댓글쓴 사람이 글쓴이이면 (글쓴이) 표시해주기.
                        writer.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });

    }

}