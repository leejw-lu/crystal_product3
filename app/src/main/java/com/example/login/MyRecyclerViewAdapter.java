package com.example.login;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<CardViewItemDTO> cardViewItemDTOS = new ArrayList<>();
    public MyRecyclerViewAdapter() {
        cardViewItemDTOS.add(new CardViewItemDTO(R.drawable.snowball,"스노우볼","수요조사 진행 중"));
        cardViewItemDTOS.add(new CardViewItemDTO(R.drawable.mugcup,"머그컵","7,000원"));
        cardViewItemDTOS.add(new CardViewItemDTO(R.drawable.milkkeyring,"우유키링","7,500원"));
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        //XML 세팅

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item,parent,false);

        return new RowCell(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull RecyclerView.ViewHolder holder, int position) {
        ((RowCell)holder).imageView.setImageResource(cardViewItemDTOS.get(position).imageview);
        ((RowCell)holder).title.setText(cardViewItemDTOS.get(position).title);
        ((RowCell)holder).subtitle.setText(cardViewItemDTOS.get(position).subtitle);
        //아이템 세팅
    }

    @Override
    public int getItemCount() {
        //이미지 카운터
        return cardViewItemDTOS.size();
    }

    private static class RowCell extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView title;
        public TextView subtitle;
        public RowCell(View view) {
            super(view);
            imageView = (ImageView)view.findViewById(R.id.cardview_imageview);
            title = (TextView)view.findViewById(R.id.cardview_title);
            subtitle = (TextView)view.findViewById(R.id.cardview_subtitle);
        }
    }
}

