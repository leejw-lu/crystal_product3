package com.example.crystalProduct.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.crystalProduct.DTO.ImageDTO;
import com.example.crystalProduct.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter {


    private FirebaseUser firebaseUser;

    private List<ImageDTO> imageDTOList = new ArrayList<>();
    private List<String> uidList = new ArrayList<>();
    private FirebaseStorage storage;
    private Context context;

    public CalendarAdapter(){}    //생성자

    public CalendarAdapter(List<ImageDTO> imageDTOList, List<String> uidList)
    {
        this.imageDTOList = imageDTOList;
        this.uidList = uidList;
        storage = FirebaseStorage.getInstance();
    }

    public CalendarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.calendar_cardview,parent,false);

        return new CalendarAdapter.ViewHolder(view);
    }

    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, final int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        ((CalendarAdapter.ViewHolder)holder).textViewTitle.setText(imageDTOList.get(position).getTitle());

        context = holder.itemView.getContext();
        String url = imageDTOList.get(position).getImageUrl();

        Glide.with(context).load(url).placeholder(R.drawable.base_image_frag4)   // 로딩전 잠깐 보여주는 이미지.
                .into(((CalendarAdapter.ViewHolder) holder).imageView);

    }


    public int getItemCount() {
        return imageDTOList.size();
    }

    //ViewHolder 클래스
    class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textViewTitle;
        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.calendar_item_title);
            imageView = itemView.findViewById(R.id.calendar_item_image);

        }

    }
}
