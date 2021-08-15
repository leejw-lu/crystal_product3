package com.example.crystalProduct;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.crystalProduct.Adapter.CommentAdapter;
import com.example.crystalProduct.DTO.CommentsDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductDetailPage extends AppCompatActivity {

    //세부항목(글) 불러오기
    ImageView imageView_image, delete_post;
    TextView textView_title, textView_price, textView_deadline, textView_form, textView_description;

    //댓글
    RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<CommentsDTO> commentList;

    EditText addcomment;
    ImageView heart;
    ImageView post;

    String postid, publisherid;
    String postuid, postToken;

    FirebaseUser firebaseUser;
    private FirebaseStorage storage;

    //수요조사
    CheckBox demands_btn;
    TextView demands_t;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();

        //글 세팅(보여주기)
        imageView_image = findViewById(R.id.detailImage);
        textView_title = findViewById(R.id.detailTitle);
        textView_price = findViewById(R.id.detailPrice);
        textView_deadline = findViewById(R.id.detailDeadline);
        textView_form = findViewById(R.id.detailForm);
        textView_description = findViewById(R.id.detailDescription);

        String image_url = intent.getStringExtra("image");
        String title = intent.getStringExtra("title");
        String price = intent.getStringExtra("price");
        String deadline = intent.getStringExtra("deadline");
        String form = intent.getStringExtra("form");
        String description = intent.getStringExtra("description");

        //댓글
        postid = intent.getStringExtra("postid");
        publisherid = intent.getStringExtra("publisherid");

        //글 삭제
        postuid = intent.getStringExtra("postuid");
        postToken = intent.getStringExtra("postToken");

        Glide.with(ProductDetailPage.this).load(image_url).into(imageView_image);

        textView_title.setText(title);
        textView_price.setText(price);
        textView_deadline.setText(deadline);
        textView_form.setText(form);
        textView_description.setText(description);

        //댓글기능
        recyclerView = findViewById(R.id.comment_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList, postid,postuid); //postuid도 추가.
        recyclerView.setAdapter(commentAdapter);

        addcomment = findViewById(R.id.add_comment);
        post = findViewById(R.id.comment_post);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addcomment.getText().toString().equals("")) {
                    Toast.makeText(ProductDetailPage.this, "댓글을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    addcomment();
                }
            }
        });

        readComments();     //댓글보여주는함수 부름

        //하트버튼 추가
        heart = findViewById(R.id.detail_heart);
        isLiked(postid, heart); //하트함수

        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (heart.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(firebaseUser.getUid())
                            .child(postid).setValue(true);
                    Toast.makeText(ProductDetailPage.this, "관심상품에 등록되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(firebaseUser.getUid())
                            .child(postid).removeValue();
                    Toast.makeText(ProductDetailPage.this, "관심상품에서 취소되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //수요조사
        demands_btn = findViewById(R.id.btn_buy);
        demands_t = findViewById(R.id.buy_count);

        isDemands(postid, demands_btn);
        nrDemands(demands_t, postid);

        //수요조사
        demands_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (demands_btn.getTag().equals("not_demand")) {
                    FirebaseDatabase.getInstance().getReference().child("Demands").child(postid).child(firebaseUser.getUid()).setValue(true);
                    Toast.makeText(ProductDetailPage.this, "수요조사 찬성", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Demands").child(postid).child(firebaseUser.getUid()).removeValue();
                    Toast.makeText(ProductDetailPage.this, "수요조사 찬성 취소", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //포스트 글 삭제
        storage=FirebaseStorage.getInstance();
        delete_post = findViewById(R.id.delete_post);

        if (postuid.equals(firebaseUser.getUid())) {     //글쓴사람만 글 삭제할 수 있다.
            delete_post.setVisibility(View.VISIBLE);
            delete_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog alertDialog = new AlertDialog.Builder(ProductDetailPage.this).create();
                    alertDialog.setTitle("작성하신 글을 삭제하시겠습니까?");

                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteContent(postToken, postid);
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();

                }
            });

        }
    }

    //1
    //댓글 firebase에 등록 함수
    private void addcomment() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);

        String commentid = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", addcomment.getText().toString());
        hashMap.put("publisher", firebaseUser.getUid());
        hashMap.put("commentid", commentid);

        reference.child(commentid).setValue(hashMap);
        addcomment.setText("");
    }

    //2
    //댓글 불러와서 읽기 함수
    private void readComments() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CommentsDTO comment = dataSnapshot.getValue(CommentsDTO.class);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //3
    //하트버튼 메소드 다시 만들기
    private void isLiked(final String postid, final ImageView imageView) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postid).exists()) {
                    imageView.setImageResource(R.drawable.heart_on);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.heart_off);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //4
    //수요조사 클릭
    private void isDemands(final String postid, final CheckBox checkBox) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Demands").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()) {
                    checkBox.setChecked(true);
                    checkBox.setTag("demand");
                } else {
                    checkBox.setChecked(false);
                    checkBox.setTag("not_demand");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //5
    //수요조사 수
    private void nrDemands(final TextView demands_p, String postId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Demands").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                demands_p.setText(dataSnapshot.getChildrenCount() + " people");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //6
    //삭제함수 (Post, Comments, Demands,Likes) 연관된 글 다 삭제.
    private void deleteContent(String postToken, String postid) {

        FirebaseDatabase.getInstance().getReference("Post").child(postToken).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ProductDetailPage.this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        FirebaseDatabase.getInstance().getReference("Comments").child(postid).removeValue();
        FirebaseDatabase.getInstance().getReference("Demands").child(postid).removeValue();
        FirebaseDatabase.getInstance().getReference("Likes").child(firebaseUser.getUid()).child(postid).setValue(null);

        startActivity(new Intent(this, MainActivity.class));
    }

}