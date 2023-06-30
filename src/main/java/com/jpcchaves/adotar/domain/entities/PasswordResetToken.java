package com.jpcchaves.adotar.domain.entities;

import jakarta.persistence.*;

import java.util.Calendar;
import java.util.Date;

@Entity
public class PasswordResetToken {
    private static final int EXPIRATION_TIME = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;
    private String token;
    private Date expirationTime;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public PasswordResetToken() {
    }

    public PasswordResetToken(Long tokenId,
                              String token,
                              Date expirationTime,
                              User user) {
        this.tokenId = tokenId;
        this.token = token;
        this.expirationTime = expirationTime;
        this.user = user;
    }

    public PasswordResetToken(String token,
                              User user) {
        super();
        this.token = token;
        this.user = user;
        this.expirationTime = this.getExpirationTime();
    }

    public PasswordResetToken(String token) {
        super();
        this.token = token;
        this.expirationTime = this.getExpirationTime();
    }

    public Date getExpirationTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, EXPIRATION_TIME);
        return new Date(calendar.getTime().getTime());
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Long getTokenId() {
        return tokenId;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

