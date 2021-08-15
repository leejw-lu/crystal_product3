package com.example.crystalProduct.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.crystalProduct.DTO.ImageDTO;
import com.example.crystalProduct.R;
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

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private FirebaseUser firebaseUser;

    private List<ImageDTO> imageDTOList = new ArrayList<>();
    private List<String> uidList = new ArrayList<>();
    private FirebaseStorage storage;
    private Context context;

    private ItemClickListener mItemClickListener;

    //검색 필터
    private List<ImageDTO> un_imageDTOList = new ArrayList<>();   //unfiltered

    public MyRecyclerViewAdapter(){}    //생성자
    public MyRecyclerViewAdapter(List<ImageDTO> imageDTOList, List<String> uidList, ItemClickListener itemClickListener)
    {
        this.imageDTOList = imageDTOList;
        this.un_imageDTOList = imageDTOList;     //unfiltered
        this.mItemClickListener = itemClickListener;
        this.uidList = uidList;
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

        holder.itemView.setOnClickListener(view-> {         //리사이클러뷰에서 item 눌렀을때 정보.
            mItemClickListener.onItemClick(imageDTOList.get(position),uidList.get(position)); //uidList에서 클릭한 글의 토큰값 가져오기.
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final ImageDTO imageDTO = imageDTOList.get(position);

        ((ViewHolder)holder).textViewTitle.setText(imageDTOList.get(position).getTitle());      //fragment라 그런지 ViewHolder안하면 오류남.
        ((ViewHolder)holder).textViewPrice.setText(imageDTOList.get(position).getPrice());
        ((ViewHolder)holder).textViewDeadline.setText(imageDTOList.get(position).getDeadline());
        ((ViewHolder)holder).textViewDescription.setText(imageDTOList.get(position).getDescription());
        ((ViewHolder)holder).imageViewHeart.setImageResource(R.drawable.heart_off);

        context = holder.itemView.getContext();
        String url = imageDTOList.get(position).getImageUrl();

        Glide.with(context).load(url).placeholder(R.drawable.base_image_frag4)   // 로딩전 잠깐 보여주는 이미지.
                .into(((ViewHolder) holder).imageView);


        isLiked(imageDTO.getPostid(), ((ViewHolder) holder).imageViewHeart);

        ((ViewHolder) holder).imageViewHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( ((ViewHolder) holder).imageViewHeart.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(firebaseUser.getUid())
                            .child(imageDTO.getPostid())
                            .setValue(true);

                    Toast.makeText(context.getApplicationContext(), "관심상품에 등록되었습니다.", Toast.LENGTH_SHORT).show();
                }else {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(firebaseUser.getUid())
                            .child(imageDTO.getPostid())
                            .removeValue();
                    Toast.makeText(context.getApplicationContext(), "관심상품에서 취소되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if(charString.isEmpty()) {
                    imageDTOList = un_imageDTOList;
                } else {
                    List<ImageDTO> filteringList = new ArrayList<>();
                    for(ImageDTO name : un_imageDTOList) {
                        if(name.getTitle().toLowerCase().contains(charString.toLowerCase())) {
                            filteringList.add(0,name);  //최신순정렬()
                        }
                    }
                    imageDTOList = filteringList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = imageDTOList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                imageDTOList = (List<ImageDTO>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    public interface ItemClickListener{
        void onItemClick(ImageDTO details,String pos);
    }

    @Override
    public int getItemCount() {
        return imageDTOList.size();
    }

    //ViewHolder 클래스
    class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textViewTitle;
        public TextView textViewPrice;
        public TextView textViewDeadline;
        public TextView textViewDescription;
        public ImageView imageView;
        public ImageView imageViewHeart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.item_title); //파라메타 id 찾기
            textViewPrice = itemView.findViewById(R.id.item_price);
            textViewDeadline = itemView.findViewById(R.id.item_deadline);
            textViewDescription = itemView.findViewById(R.id.item_description);
            imageView = itemView.findViewById(R.id.item_image);
            imageViewHeart = itemView.findViewById(R.id.item_heart);

        }

    }

    private void isLiked(final String postid, final ImageView imageView){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postid).exists()){
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

}