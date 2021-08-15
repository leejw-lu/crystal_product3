package com.example.crystalProduct.DTO;

//사용자 계정 정보 모델 클래스


public class UserAccount {

    private String idToken;  //Firebase Uid (고유 토큰정보)
    private String emailId;  //이메일 아이디
    private String password; //비밀번호
    private String nickname; //닉네임

    public UserAccount(){ } //firebase 빈 생성자 필수

    public String getIdToken() { return idToken; }
    public void setIdToken(String idToken) { this.idToken = idToken; }

    public String getEmailId() { return emailId; }
    public void setEmailId(String emailId) { this.emailId = emailId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNickname() { return nickname; }

    public void setNickname(String nickname) { this.nickname = nickname; }
}
