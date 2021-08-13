package com.example.crystalProduct;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
    /*
    //팝업 알림 버튼 (임시)
    Button button;
    NotificationManager manager;
    NotificationCompat.Builder builder;
    private static String CHANNEL_ID = "channel1";
    private static String CHANNEL_NAME = "Channel1";

    //팝업 타이머
    CountDownTimer countDownTimer;
    TextView tv_timer;
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();

        /*
        FirebaseApp.initializeApp(this);

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w("FirebaseSettingEx", "getInstanceId failed", task.getException());
                return;
            }

            // 토큰을 읽고, 텍스트 뷰에 보여주기
            String token = task.getResult().getToken();
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.println(token);
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        });*/
        /*
        //팝업 알림 버튼 (임시)
        button = (Button) findViewById(R.id.btn_notification);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNoti();
            }
        });


        tv_timer = (TextView) findViewById(R.id.tv_timer);
        //마감기한 타이머   (제한시간(200초동안 작동), 몇초마다 타이머 작동(1초 단위로 작동))
        CountDownTimer countDownTimer = new CountDownTimer(200000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tv_timer.setText(getTime());
            }

            @Override
            public void onFinish() {

            }
        };
        countDownTimer.start();
        */

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



    /*
    //팝업 알림 버튼 (임시)
    public void showNoti(){
        builder = null;
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //버전 오레오 이상일 경우
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            manager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT));
            builder = new NotificationCompat.Builder(this,CHANNEL_ID);

            //하위 버전일 경우
        } else{
            builder = new NotificationCompat.Builder(this);
        }

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 101, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //알림창 제목
        builder.setContentTitle("수정물산");

        //알림창 메시지
        builder.setContentText("관심 상품의 마감기한이 내일까지예요!");

        //알림창 아이콘
        builder.setSmallIcon(R.drawable.crystal_logo);

        //알림창 터치시 상단 알림상태창에서 알림이 자동으로 삭제
        builder.setAutoCancel(true);

        //pendingIntent를 builder에 설정
        //알림창 터치시 인텐트가 전달
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();

        //알림창 실행
        manager.notify(1,notification);

    }
    */


    /*
    //마감 기한 타이머
    private String getTime(){

        //현재 코드는 언제나 작동할 수 있도록 현재날짜+2일을 한 상태입니다. 그러니 말일쯤에는 작동하지 않을 수 있으니 주의하시기 바랍니다
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int c_hour = calendar.get(Calendar.HOUR_OF_DAY);
        int c_min = calendar.get(Calendar.MINUTE);
        int c_sec = calendar.get(Calendar.SECOND);

        Calendar baseCal = new GregorianCalendar(year,month+1,day,c_hour,c_min,c_sec);
        Calendar targetCal = new GregorianCalendar(year,month+1,day+1,0,0,0);  //비교대상날짜

        long diffSec = (targetCal.getTimeInMillis() - baseCal.getTimeInMillis()) / 1000;
        long diffDays = diffSec / (24*60*60);

        targetCal.add(Calendar.DAY_OF_MONTH, (int)(-diffDays));

        int hourTime = (int)Math.floor((double)(diffSec/3600));
        int minTime = (int)Math.floor((double)(((diffSec - (3600 * hourTime)) / 60)));
        int secTime = (int)Math.floor((double)(((diffSec - (3600 * hourTime)) - (60 * minTime))));

        String hour = String.format("%02d", hourTime);
        String min = String.format("%02d", minTime);
        String sec = String.format("%02d", secTime);

        System.out.println(year+"년"+(month+1)+"월"+ (day+1)+"일 까지 " + hour + " 시간 " +min + " 분 "+ sec + "초 남았습니다.");


        if (Integer.parseInt(hour) < 24){
            showNoti();
        }


        return year+"년"+(month+1)+"월"+ (day+1)+"일 까지 " + hour + " 시간 " +min + " 분 "+ sec + "초 남았습니다.";

    }
    */

}