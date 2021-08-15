package com.example.crystalProduct;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.crystalProduct.Fragment.Frag1;
import com.example.crystalProduct.Fragment.Frag2;
import com.example.crystalProduct.Fragment.Frag3;
import com.example.crystalProduct.Fragment.Frag4;
import com.example.crystalProduct.Fragment.Frag5;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    //뒤로가기
    private final long FINISH_INTERVAL_TIME = 2000;  //이 시간내에 연속적으로 뒤로가기 버튼을 누를 경우 종료
    private long backPressedTime = 0;  //뒤로가기가 일어난 시간

    private BottomNavigationView bnv; //하단바
    private FragmentManager fm;
    private FragmentTransaction ft;
    private Frag1 f1;
    private Frag2 f2;
    private Frag3 f3;
    private Frag4 f4;
    private Frag5 f5;
    private FirebaseAuth auth;

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();

        //툴바
        Toolbar toolbar = findViewById(R.id.toolbar);

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

    //수정
    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }
}