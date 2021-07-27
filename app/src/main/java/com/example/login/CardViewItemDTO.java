package com.example.login;

import java.util.HashMap;
import java.util.Map;

public class CardViewItemDTO {
    public int imageview;
    public String title;
    public String subtitle;
    //양성원 추가
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public CardViewItemDTO(int imageview, String title, String subtitle) {
        this.imageview = imageview;
        this.title = title;
        this.subtitle = subtitle;

    }
}
