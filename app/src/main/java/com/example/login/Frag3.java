package com.example.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

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
    private String postid;

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
                text_date.setText(String.format("%d / %d / %d",year,month+1,dayOfMonth));
                getHeart();
            }
        });
        getHeart();

        return view;
    }



    private void getDeadline(String postid) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post").child(postid).child("deadline");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                deadlineList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    deadlineList.add(snapshot.getKey());
/*
                    System.out.println("############################################################");
                    System.out.println(deadlineList);
                    System.out.println("############################################################");
*/
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showDeadlineProduct(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                imageDTOList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ImageDTO imageDTO = snapshot.getValue(ImageDTO.class);
                    for (String deadline : deadlineList) {
                        if (imageDTO.getDeadline().equals(deadline)) {
                            imageDTOList.add(imageDTO);
                        }
                    }
                }
                calendarAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void getHeart() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");   //post 토큰값 가져오기
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    postList.add(snapshot.getKey());            //postList에 post 개수만큼 토큰값이 제대로 담겨있음
                }
                for (String postid : postList) {
                    getDeadline(postid);
                }
                showDeadlineProduct();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

