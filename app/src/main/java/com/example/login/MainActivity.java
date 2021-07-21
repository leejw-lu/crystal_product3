package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.login.Frag1;
import com.example.login.Frag2;
import com.example.login.Frag3;
import com.example.login.Frag4;
import com.example.login.Frag5;
import com.example.login.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;


public class MainActivity extends AppCompatActivity {


    private BottomNavigationView bnv; //하단바
    private FragmentManager fm;
    private FragmentTransaction ft;
    private Frag1 f1;
    private Frag2 f2;
    private Frag3 f3;
    private Frag4 f4;
    private Frag5 f5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bnv = findViewById(R.id.bottomNavi);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        setFrag(0);
                        break;
                    case R.id.search:
                        setFrag(1);
                        break;
                    case R.id.calendar:
                        setFrag(2);
                        break;
                    case R.id.write:
                        setFrag(3);
                        break;
                    case R.id.profile:
                        setFrag(4);
                        break;
                }

                return true;
            }
        });
        f1 = new Frag1();
        f2 = new Frag2();
        f3 = new Frag3();
        f4 = new Frag4();
        f5 = new Frag5();

        setFrag(0); // 첫 프래그먼트 화면을 무엇으로 지정해줄 것인지 선택
    }

    private void setFrag(int n) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction(); //실제 교체 시 ..
        switch (n) {
            case 0:
                ft.replace(R.id.Main_Frame, f1);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.Main_Frame, f2);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.Main_Frame, f3);
                ft.commit();
                break;
            case 3:
                ft.replace(R.id.Main_Frame, f4);
                ft.commit();
                break;
            case 4:
                ft.replace(R.id.Main_Frame, f5);
                ft.commit();
                break;
        }
    }


}

