package com.example.login;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class Frag1 extends Fragment {
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.frag1_home, container,false);

        return view;
           }

           // 카드 뷰 올리는 코드인데 오류,,
//    private RecyclerView mRecyclerView;
//    private RecyclerView.Adapter mAdapter;
//    private RecyclerView.LayoutManager mLayoutManager;
//
//    public Frag1(){
//
//
//    }
//
//    @Nullable
//    @org.jetbrains.annotations.Nullable
//    @Override
//    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
//       View view = inflater.inflate(R.layout.frag1_home, container, false);
//
//        if (view instanceof RecyclerView) {
//
//
//            Context context = view.getContext();
//
//            RecyclerView mRecyclerView = (RecyclerView) view;
//
//
//            mRecyclerView.setHasFixedSize(true);
//
//
//            // use a linear layout manager
//
//            mLayoutManager = new LinearLayoutManager(context);
//
//            mRecyclerView.setLayoutManager(mLayoutManager);
//
//
//            // specify an adapter (see also next example)
//
//            mAdapter = new CollectionListAdapter(myList);
//
//            mRecyclerView.setAdapter(mAdapter);
//
//        }
//        return view;
//    }
}

