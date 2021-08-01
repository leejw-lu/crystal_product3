package com.example.login;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //카드뷰변수
    private List<String> uidLists = new ArrayList<>();
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;

    private final ArrayList<CardViewItemDTO> cardViewItemDTOS = new ArrayList<>();
    private Object CustomViewHolder;

    //지우--변수추가
    private List<ImageDTO> imageDTOList = new ArrayList<>();
    private List<String> uidList = new ArrayList<>();
    private FirebaseStorage storage;
    private Context context;
    //private Instant Glide;

    //
    public MyRecyclerViewAdapter(){}    //생성자
    public MyRecyclerViewAdapter(List<ImageDTO> imageDTOList, List<String> uidList)
    {
        this.imageDTOList = imageDTOList;
        this.uidList = uidList;
        this.context=context;   //이건 내가 추가.
        storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.uploaded_image_item,parent,false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, final int position) {
        //holder.textViewUser.setText(imageDTOList.get(position).getUserId()); //fragment라 그런지 ViewHolder안하면 오류남.
        //((ViewHolder)holder).textViewUser.setText(imageDTOList.get(position).getUserId());


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final ImageDTO imageDTO = imageDTOList.get(position);


        ((ViewHolder)holder).textViewTitle.setText(imageDTOList.get(position).getTitle());
        ((ViewHolder)holder).textViewDesc.setText(imageDTOList.get(position).getDescription());
        ((ViewHolder)holder).imageViewHeart.setImageResource(R.drawable.heart_off);

        context = holder.itemView.getContext();
        String url = imageDTOList.get(position).getImageUrl();

        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.base_image_frag4)   // 로딩전 잠깐 보여주는 이미지.
                .into(((ViewHolder) holder).imageView);


        isLiked(imageDTO.getPostid(), ((ViewHolder) holder).imageViewHeart);

        ((ViewHolder) holder).imageViewHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( ((ViewHolder) holder).imageViewHeart.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(imageDTO.getPostid())
                            .child(firebaseUser.getUid()).setValue(true);
                }else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(imageDTO.getPostid())
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return imageDTOList.size();
    }

    //ViewHolder 클래스
    class ViewHolder extends RecyclerView.ViewHolder
    {
        //public TextView textViewUser;
        public TextView textViewTitle;
        public TextView textViewDesc;
        public ImageView imageView;
        public ImageView imageViewHeart;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            //textViewUser = itemView.findViewById(R.id.item_user);
            textViewTitle = itemView.findViewById(R.id.item_title); //파라메타 id 찾기
            textViewDesc = itemView.findViewById(R.id.item_desc);
            imageView = itemView.findViewById(R.id.item_image);
            imageViewHeart= itemView.findViewById(R.id.item_heart);
        }
    }

    private void isLiked(final String postid, final ImageView imageView){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.heart_on);
                    imageView.setTag("liked");
                } else{
                    imageView.setImageResource(R.drawable.heart_off);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    /////////////  아래로 성원이 코드 ///////////////////////
    /*------기본 item3개 세팅하기.*/
//    public MyRecyclerViewAdapter() {
//        cardViewItemDTOS.add(new CardViewItemDTO(R.drawable.snowball,"스노우볼","수요조사 진행 중"));
//        cardViewItemDTOS.add(new CardViewItemDTO(R.drawable.mugcup,"머그컵","7,000원"));
//        cardViewItemDTOS.add(new CardViewItemDTO(R.drawable.milkkeyring,"우유키링","7,500원"));
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        //XML 세팅
//
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item,parent,false);
//
//        return new RowCell(view);
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        database = FirebaseDatabase.getInstance();
//        ((RowCell)holder).imageView.setImageResource(cardViewItemDTOS.get(position).imageview);
//        ((RowCell)holder).title.setText(cardViewItemDTOS.get(position).title);
//        ((RowCell)holder).subtitle.setText(cardViewItemDTOS.get(position).subtitle);
//        //아이템 세팅
//
//        ((RowCell) holder).heartButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                onStarClicked(database.getReference().child("images").child(uidLists.get(position)));
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        //이미지 카운터
//        return cardViewItemDTOS.size();
//    }
//
//    private void onStarClicked(DatabaseReference postRef) {
//        auth = FirebaseAuth.getInstance();
//        postRef.runTransaction(new Transaction.Handler() {
//            @Override
//            public Transaction.Result doTransaction(MutableData mutableData) {
//                CardViewItemDTO cardViewItemDTO = mutableData.getValue(CardViewItemDTO.class);
//                if (cardViewItemDTO == null) {
//                    return Transaction.success(mutableData);
//                }
//
//                if (cardViewItemDTO.stars.containsKey(auth.getCurrentUser().getUid())) {
//                    // Unstar the post and remove self from stars
//                    cardViewItemDTO.starCount = cardViewItemDTO.starCount - 1;
//                    cardViewItemDTO.stars.remove(auth.getCurrentUser().getUid());
//                } else {
//                    // Star the post and add self to stars
//                    cardViewItemDTO.starCount = cardViewItemDTO.starCount + 1;
//                    cardViewItemDTO.stars.put(auth.getCurrentUser().getUid(), true);
//                }
//
//                // Set value and report transaction success
//                mutableData.setValue(cardViewItemDTO);
//                return Transaction.success(mutableData);
//            }
//
//            @Override
//            public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
//                // Transaction completed
//
//            }
//
//        });
//    }
//
//
//    private static class RowCell extends RecyclerView.ViewHolder {
//        public ImageView imageView;
//        public TextView title;
//        public TextView subtitle;
//        ImageView heartButton;
//
//        public RowCell(View view) {
//            super(view);
//            imageView = (ImageView)view.findViewById(R.id.cardview_imageview);
//            title = (TextView)view.findViewById(R.id.cardview_title);
//            subtitle = (TextView)view.findViewById(R.id.cardview_subtitle);
//            heartButton = (ImageView)view.findViewById(R.id.btn_heart);
//        }
//    }
//
//

}