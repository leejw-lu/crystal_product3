package com.example.login;

public class CommentsDTO {

    private String comment;     //댓글
    private String publisher;   //사용자uid

    //생성자
    public CommentsDTO(String comment, String publisher) {
        this.comment = comment;
        this.publisher = publisher;
    }
    public CommentsDTO() { }

    //getter, setter
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
}
