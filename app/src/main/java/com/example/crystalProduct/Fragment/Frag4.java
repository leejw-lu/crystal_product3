package com.example.crystalProduct.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;

import com.example.crystalProduct.IOnBackPressed;
import com.example.crystalProduct.DTO.ImageDTO;
import com.example.crystalProduct.MainActivity;
import com.example.crystalProduct.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Calendar;


public class Frag4 extends Fragment implements IOnBackPressed {

    private static final int RESULT_OK = -1;
    private static final int RESULT_CANCELED = 0;

    @Override
    public void onBackPressed() {
        Toast.makeText(context, "이미지 선택 안함", Toast.LENGTH_SHORT).show();
    }

    private static final int GALLERY_CODE = 10;
    private View view;
    private Context context;

    private Button btnOk;
    private TextView btnUpload;
    private ImageView ivProfile, uploadIcon;
    private EditText etTitle, etDesc, etPrice, etDeadLine, etPurchaseLink;
    private String imageUrl="";

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    DatabaseReference mDatabase;

    //date input
    TextView dateTv;
    DatePickerDialog.OnDateSetListener setListener;
    private String date;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag4_create, container, false);

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        context = container.getContext();
        btnUpload = (TextView) view.findViewById(R.id.btn_upload);
        uploadIcon = (ImageView) view.findViewById(R.id.uploadIcon);

        btnOk = (Button) view.findViewById(R.id.btn_profile_Ok);       //사진과 글_ 업로드 버튼
        ivProfile = (ImageView) view.findViewById(R.id.iv_profile);          //사진이미지
        etTitle = (EditText) view.findViewById(R.id.title);    //제목
        etDesc = (EditText) view.findViewById(R.id.description);      //추가 내용
        etPrice = (EditText) view.findViewById(R.id.price);   //가격
        //etDeadLine =(EditText) view.findViewById(R.id.deadline);      //마감일
        etPurchaseLink = (EditText) view.findViewById(R.id.purchaseLink);   //구매 링크

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

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //앨범 불러오는 코드
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,GALLERY_CODE);

                //// 사진 첨부되면 사진 업로드 아이콘 및 텍스트 숨기기
                btnUpload.setVisibility(View.GONE);
                uploadIcon.setVisibility(View.GONE);
            }
        });

        //date input
        dateTv = (TextView) view.findViewById(R.id.deadline);
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        dateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth
                        ,setListener,year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();

            }
        });

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                date = year + "/" + month + "/" + dayOfMonth ;
                dateTv.setText(date);
            }
        };

        etDesc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() ==R.id.description) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction()&MotionEvent.ACTION_MASK){
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GALLERY_CODE) {
            if (resultCode == RESULT_OK) {
                //원래 코드
                imageUrl = getRealPathFromUri(data.getData());
                File f = new File(imageUrl);
                ivProfile.setImageURI(Uri.fromFile(f));

            }
            else if(resultCode == RESULT_CANCELED){
                Toast.makeText(context, "이미지 선택 안함", Toast.LENGTH_SHORT).show();
            }
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
                        imageDTO.setPrice(etPrice.getText().toString());
                        imageDTO.setDeadline(date);
                        imageDTO.setPurchaseLink(etPurchaseLink.getText().toString());
                        imageDTO.setPostid(database.getReference().child("Post").push().getKey());
                        imageDTO.setUid(mAuth.getCurrentUser().getUid());

                        database.getReference().child("Post").push().setValue(imageDTO);

                        //edit_text 초기화하기
                        etTitle.setText("");
                        etDesc.setText("");
                        etPrice.setText("");
                        etPurchaseLink.setText("");
                        date = "";

                        startActivity(new Intent(getActivity(), MainActivity.class));

                    } else {
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