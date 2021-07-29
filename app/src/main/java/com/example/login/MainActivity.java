package com.example.login;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.content.CursorLoader;


import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import androidx.appcompat.widget.Toolbar;

import java.io.File;

public class MainActivity extends AppCompatActivity {


    private BottomNavigationView bnv; //하단바
    private FragmentManager fm;
    private FragmentTransaction ft;
    private Frag1 f1;
    private Frag2 f2;
    private Frag3 f3;
    private Frag4 f4;
    private Frag5 f5;
    //frag4 >> private static final int GALLERY_CODE = 10;
    private FirebaseAuth auth;
    //frag4 >>
    /*
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private ImageView imageView;
    private EditText title;
    private EditText description;
    private Button button;
    private String imagePath;*/

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        //frag4 >> storage = FirebaseStorage.getInstance();
        //frag4 >> database = FirebaseDatabase.getInstance();

        //frag4
        /*
        imageView = (ImageView)findViewById(R.id.imageView);
        title = (EditText)findViewById(R.id.title);
        description = (EditText)findViewById(R.id.description);
        button = (Button)findViewById(R.id.button_upload);
        //오류생긴부분
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload(imagePath);
            }
        });
        */

        //툴바
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        // 툴바 활성화
        actionBar.setDisplayHomeAsUpEnabled(true);


        // 툴바 기본 타이틀 없애기
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        // 햄버거 버튼 불러오기
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24);

//        // 툴바에 적힐 제목
//        getSupportActionBar().setTitle("수정물산");
//        getSupportActionBar().setHomeButtonEnabled(true);



        /////////////////
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

    //frag4 >>
    /*
    //양성원 추가 이미지 결과값)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE) {
            imagePath = getPath(data.getData());
            File f = new File(imagePath);
            imageView.setImageURI(Uri.fromFile(f));
        }
    }

    public String getPath(Uri uri){
        String [] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader;
        cursorLoader = new CursorLoader(this, uri, proj, null, null, null);

        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return  cursor.getString(index);
    }

    private void upload(String uri){
        StorageReference storageRef = storage.getReference("gs://fir-emailaccount-7b951.appspot.com");

        Uri file = Uri.fromFile(new File(uri));
        StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return riversRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUrl = task.getResult();

                    //this is the uri you needed...
                    //uploaded_image_url = downloadUri.toString();

                    ImageDTO imageDTO = new ImageDTO();
                    imageDTO.imageUrl = downloadUrl.toString();
                    imageDTO.title = title.getText().toString();
                    imageDTO.description = description.getText().toString();
                    imageDTO.uid = auth.getCurrentUser().getUid();
                    imageDTO.userId = auth.getCurrentUser().getEmail();

                    database.getReference().child("images").push().setValue(imageDTO);

                } else {
                    // Handle failures
                    // ...
                    //Toast.makeText(EditUserProfile.this, "Image uploading failed ", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        /* 원래 코드 (버전 때문에 바뀌어야 함)
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests")
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
    }*/
}