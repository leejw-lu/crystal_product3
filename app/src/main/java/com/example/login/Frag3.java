package com.example.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Frag3 extends Fragment {
    private View view;
    //양성원
    private RecyclerView recyclerView;
    private CalendarAdapter calendarAdapter;
    private List<ImageDTO> imageDTOList = new ArrayList<>();
    private List<String> postList = new ArrayList<>();
    private List<String> deadlineList = new ArrayList<>();
    private FirebaseUser firebaseUser;
    String clickdate;

    //달력
    Calendar cal = Calendar.getInstance(); //오늘날짜 입력하기.
    public CalendarView calendarView;
    public TextView text_date;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.frag3_calendar, container,false);

        //양
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_calendar);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new CalendarAdapter());
        calendarAdapter = new CalendarAdapter(imageDTOList, postList);


        //달력
        SimpleDateFormat format = new SimpleDateFormat("yyyy / M /d");   //오늘날짜 받아오기.
        String date = format.format(Calendar.getInstance().getTime());

        text_date=view.findViewById(R.id.text_calendar);
        text_date.setText(date);        //처음 세팅할때 오늘날짜 표시되기
        text_date=view.findViewById(R.id.text_calendar);
        calendarView=view.findViewById(R.id.calendarView);
        recyclerView.setAdapter(calendarAdapter);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                clickdate=String.format("%d/%d/%d",year,month+1,dayOfMonth);
                text_date.setText(clickdate);
                System.out.println(clickdate);

                getHeart();
            }
        });

        return view;
    }


    private void showDeadlineProduct(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                imageDTOList.clear();
                //deadlineList.clear();
                String deadline2;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){       //이거 하면 post의 토크값
                    ImageDTO imageDTO = snapshot.getValue(ImageDTO.class);
                    for (String postid : postList) {
                        if (imageDTO.getPostid().equals(postid)) {
                            //그냥 deadline 스트링 하나만
                            deadline2=imageDTO.getDeadline();   //날짜 받아오기

                            if (clickdate.equals(deadline2)){
                                //System.out.println("클릭한날짜");
                                //System.out.println(clickdate);
                                imageDTOList.add(imageDTO);
                            }
                        }
                    }
                }
                //System.out.println("관심상품한 날짜만 가져오기");
                //System.out.println(deadlineList);
                calendarAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void getHeart() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Likes").child(firebaseUser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    postList.add(snapshot.getKey());
                }
                showDeadlineProduct();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
