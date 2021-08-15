package com.example.crystalProduct.DTO;

public class CommentsDTO {
    private String comment;
    private String publisher;
    private String commentid;

    public CommentsDTO() { }    //생성자
    public CommentsDTO(String comment, String publisher) {
        this.comment = comment;
        this.publisher = publisher;
    }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public String getCommentid() { return commentid; }
    public void setCommentid(String commentid) { this.commentid = commentid; }
}
