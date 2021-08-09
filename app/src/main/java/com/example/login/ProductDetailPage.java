package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductDetailPage extends AppCompatActivity {

    //세부항목(글) 불러오기
    ImageView imageView_image;
    TextView textView_title,textView_price,textView_deadline,textView_form,textView_description;

    //댓글
    RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<CommentsDTO> commentList;

    EditText addcomment;
    ImageView heart;
    TextView post;

    String postid;
    String publisherid;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);

        Intent intent = getIntent();
        postid=intent.getStringExtra("postid"); //댓글
        publisherid = intent.getStringExtra("publisherid"); //댓글

        imageView_image=findViewById(R.id.detailImage);
        textView_title=findViewById(R.id.detailTitle);
        textView_price=findViewById(R.id.detailPrice);
        textView_deadline=findViewById(R.id.detailDeadline);
        textView_form=findViewById(R.id.detailForm);
        textView_description=findViewById(R.id.detailDescription);

        String image_url=intent.getStringExtra("image");
        String title = intent.getStringExtra("title");
        String price = intent.getStringExtra("price");
        String deadline=intent.getStringExtra("deadline");
        String form=intent.getStringExtra("form");
        String description=intent.getStringExtra("description");

        Glide.with(ProductDetailPage.this).load(image_url).into(imageView_image);

        textView_title.setText(title);
        textView_price.setText(price);
        textView_deadline.setText(deadline);
        textView_form.setText(form);
        textView_description.setText(description);

        //-----------------------------------------댓글기능 여기서부터
        recyclerView = findViewById(R.id.comment_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList,postid);
        recyclerView.setAdapter(commentAdapter);

        addcomment=findViewById(R.id.add_comment);
        post=findViewById(R.id.comment_post);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addcomment.getText().toString().equals("")){
                    Toast.makeText(ProductDetailPage.this, "댓글을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    addcomment();
                }
            }
        });

        readComments();     //댓글보여주는함수 부름

        //하트버튼 추가했다.
        heart=findViewById(R.id.detail_heart);

        isLiked(postid,heart); //하트함수

        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( heart.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(firebaseUser.getUid())
                            .child(postid).setValue(true);
                    Toast.makeText(ProductDetailPage.this, "관심상품에 등록되었습니다.", Toast.LENGTH_SHORT).show();
                }else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(firebaseUser.getUid())
                            .child(postid).removeValue();
                    Toast.makeText(ProductDetailPage.this, "관심상품에서 취소되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

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

    //댓글 불러와서 읽기 함수
    private void readComments(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
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


}