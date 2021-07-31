package com.example.login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;


public class Frag4 extends Fragment {
    private static final int GALLERY_CODE = 10;
    private View view;
    private Context context;

    private Button btnOk;
    private ImageView ivProfile;
    private EditText etTitle, etDesc;
    private String imageUrl="";

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag4_create, container, false);

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        context = container.getContext();

        btnOk = (Button) view.findViewById(R.id.btn_profile_Ok);          //사진과 글_ 업로드 버튼
        ivProfile = (ImageView) view.findViewById(R.id.iv_profile);     //사진이미지
        etTitle = (EditText) view.findViewById(R.id.title);    //제목
        etDesc = (EditText) view.findViewById(R.id.description);      //내용

        //접근 권한
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
        }

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //파이어베이스에 파일 업로드와 데이터 베이스 저장
                uploadImg(imageUrl);
            }
        });

        //앨범 불러오는 코드
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent,GALLERY_CODE);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GALLERY_CODE) {

            imageUrl = getRealPathFromUri(data.getData());
            File f = new File(imageUrl);
            ivProfile.setImageURI(Uri.fromFile(f));
        }
    }

    public String getRealPathFromUri(Uri uri){      //주소

        String [] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(getActivity().getApplicationContext(),uri,proj,null,null,null);

        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(index);
    }

    private void uploadImg(String uri)      //이미지와 글 업로드
    {
        try {
            // Create a storage reference from our app
            StorageReference storageRef = storage.getReferenceFromUrl("gs://fir-emailaccount-7b951.appspot.com");

            Uri file = Uri.fromFile(new File(uri));
            final StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
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
                    if (task.isSuccessful())
                    {
                        Toast.makeText(context,"업로드 되었습니다.",Toast.LENGTH_SHORT).show();

                        //파이어베이스에 데이터베이스 업로드
                        @SuppressWarnings("VisibleForTests")
                        Uri downloadUrl = task.getResult();

                        ImageDTO imageDTO = new ImageDTO();
                        imageDTO.setImageUrl(downloadUrl.toString());
                        imageDTO.setTitle(etTitle.getText().toString());
                        imageDTO.setDescription(etDesc.getText().toString());
                        imageDTO.setUid(mAuth.getCurrentUser().getUid());
                        imageDTO.setUserId(mAuth.getCurrentUser().getEmail());

                        database.getReference().child("Profile").push().setValue(imageDTO);

                    } else {
                        // Handle failures
                        Toast.makeText(context,"업로드 되지 않았습니다.",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }catch (NullPointerException e)
        {
            Toast.makeText(context, "이미지 선택 안함", Toast.LENGTH_SHORT).show();
        }
    }

}