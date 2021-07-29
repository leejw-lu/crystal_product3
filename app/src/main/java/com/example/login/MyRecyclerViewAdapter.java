package com.example.login;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> uidLists = new ArrayList<>();
    private FirebaseDatabase database;
    private FirebaseAuth auth;

    private final ArrayList<CardViewItemDTO> cardViewItemDTOS = new ArrayList<>();
    private Object CustomViewHolder;

    public MyRecyclerViewAdapter() {
        cardViewItemDTOS.add(new CardViewItemDTO(R.drawable.snowball,"스노우볼","수요조사 진행 중"));
        cardViewItemDTOS.add(new CardViewItemDTO(R.drawable.mugcup,"머그컵","7,000원"));
        cardViewItemDTOS.add(new CardViewItemDTO(R.drawable.milkkeyring,"우유키링","7,500원"));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //XML 세팅

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item,parent,false);

        return new RowCell(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        database = FirebaseDatabase.getInstance();
        ((RowCell)holder).imageView.setImageResource(cardViewItemDTOS.get(position).imageview);
        ((RowCell)holder).title.setText(cardViewItemDTOS.get(position).title);
        ((RowCell)holder).subtitle.setText(cardViewItemDTOS.get(position).subtitle);
        //아이템 세팅

        ((RowCell) holder).heartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onStarClicked(database.getReference().child("images").child(uidLists.get(position)));
            }
        });
    }

    @Override
    public int getItemCount() {
        //이미지 카운터
        return cardViewItemDTOS.size();
    }

    private void onStarClicked(DatabaseReference postRef) {
        auth = FirebaseAuth.getInstance();
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                CardViewItemDTO cardViewItemDTO = mutableData.getValue(CardViewItemDTO.class);
                if (cardViewItemDTO == null) {
                    return Transaction.success(mutableData);
                }

                if (cardViewItemDTO.stars.containsKey(auth.getCurrentUser().getUid())) {
                    // Unstar the post and remove self from stars
                    cardViewItemDTO.starCount = cardViewItemDTO.starCount - 1;
                    cardViewItemDTO.stars.remove(auth.getCurrentUser().getUid());
                } else {
                    // Star the post and add self to stars
                    cardViewItemDTO.starCount = cardViewItemDTO.starCount + 1;
                    cardViewItemDTO.stars.put(auth.getCurrentUser().getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(cardViewItemDTO);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                // Transaction completed

            }

        });
    }


    private static class RowCell extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView title;
        public TextView subtitle;
        ImageView heartButton;

        public RowCell(View view) {
            super(view);
            imageView = (ImageView)view.findViewById(R.id.cardview_imageview);
            title = (TextView)view.findViewById(R.id.cardview_title);
            subtitle = (TextView)view.findViewById(R.id.cardview_subtitle);
            heartButton = (ImageView)view.findViewById(R.id.btn_heart);
        }
    }
}

