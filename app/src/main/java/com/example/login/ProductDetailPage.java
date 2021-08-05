package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ProductDetailPage extends AppCompatActivity {


    ImageView imageView_image;
    TextView textView_title,textView_price,textView_deadline,textView_form,textView_description;
    //private String imageUrl="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);
        //TextView textView = findViewById(R.id.detailText);

        Intent intent = getIntent();

        imageView_image=findViewById(R.id.detailImage);
        textView_title=findViewById(R.id.detailTitle);
        textView_price=findViewById(R.id.detailPrice);
        textView_deadline=findViewById(R.id.detailDeadline);
        textView_form=findViewById(R.id.detailForm);
        textView_description=findViewById(R.id.detailDescription);

        String image_url=intent.getStringExtra("image");
        String title = intent.getStringExtra("title");
        String price = intent.getStringExtra("price");
        String deadline=intent.getStringExtra("deadline");
        String form=intent.getStringExtra("form");
        String description=intent.getStringExtra("description");

        Glide.with(ProductDetailPage.this).load(image_url).into(imageView_image);

        textView_title.setText(title);
        textView_price.setText(price);
        textView_deadline.setText(deadline);
        textView_form.setText(form);
        textView_description.setText(description);

    }


}
