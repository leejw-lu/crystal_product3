package com.example.crystalProduct.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crystalProduct.Adapter.CalendarAdapter;
import com.example.crystalProduct.Decorator.EventDecorator;
import com.example.crystalProduct.DTO.ImageDTO;
import com.example.crystalProduct.R;
import com.example.crystalProduct.Decorator.SaturdayDecorator;
import com.example.crystalProduct.Decorator.SundayDecorator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class Frag3 extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private CalendarAdapter calendarAdapter;
    private List<ImageDTO> imageDTOList = new ArrayList<>();
    private List<String> postList = new ArrayList<>();
    private FirebaseUser firebaseUser;
    String clickdate;

    public TextView text_date;

    //커스텀 달력
    public MaterialCalendarView materialCalendarView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.frag3_calendar, container,false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_calendar);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new CalendarAdapter());
        calendarAdapter = new CalendarAdapter(imageDTOList, postList);


        //달력
        SimpleDateFormat format = new SimpleDateFormat("yyyy/M/d");   //오늘날짜 받아오기.
        String date = format.format(Calendar.getInstance().getTime());

        //커스텀 달력
        materialCalendarView = view.findViewById(R.id.calendarView);
        materialCalendarView.setSelectedDate(CalendarDay.today());

        text_date=view.findViewById(R.id.text_calendar);
        text_date.setText(date);        //처음 세팅할때 오늘날짜 표시되기
        text_date=view.findViewById(R.id.text_calendar);
        recyclerView.setAdapter(calendarAdapter);

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull @NotNull MaterialCalendarView widget, @NonNull @NotNull CalendarDay date, boolean selected) {
                int dayOfMonth = date.getDay();
                int month = date.getMonth();
                int year = date.getYear();
                clickdate=String.format("%d/%d/%d",year,month+1,dayOfMonth);
                text_date.setText(clickdate);
                getHeart();
            }
        });

        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator()
        );

        getHeart();
        return view;
    }

    private int getYear(String year){
        int intYear = Integer.parseInt(year);
        return intYear;
    }

    private int getMonth(String month){
        int intMonth = Integer.parseInt(month);
        return intMonth - 1;
    }

    private int getDay(String day){
        int intDay = Integer.parseInt(day);
        return intDay;
    }

    private void showDeadlineProduct(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                imageDTOList.clear();
                String deadline2;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){       //이거 하면 post의 토크값
                    ImageDTO imageDTO = snapshot.getValue(ImageDTO.class);
                    for (String postid : postList) {
                        if (imageDTO.getPostid().equals(postid)) {
                            //그냥 deadline 스트링 하나만
                            deadline2=imageDTO.getDeadline();   //날짜 받아오기
                            if(clickdate != null) {
                                if (clickdate.equals(deadline2)) {
                                    imageDTOList.add(imageDTO);
                                }
                            }
                            if (deadline2 != null) {
                                String dateArray[] = deadline2.split("/");
                                getYear(dateArray[0]);
                                getMonth(dateArray[1]);
                                getDay(dateArray[2]);
                                materialCalendarView.addDecorators(new EventDecorator(Color.RED, Collections.singleton(CalendarDay.from(getYear(dateArray[0]), getMonth(dateArray[1]), getMonth(dateArray[2]) + 1))));
                            }
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