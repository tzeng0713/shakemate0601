package com.shakemate.user;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "USERS")
public class Users implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Integer userId;

    @Column(name = "USERNAME", nullable = false, length = 30)
    private String username;

    @Column(name = "EMAIL", nullable = false, length = 30, unique = true)
    private String email;

    @Column(name = "PWD", nullable = false, length = 20)
    private String pwd;

    @Column(name = "GENDER", nullable = false)
    private Integer gender;

    @Column(name = "BIRTHDAY")
    private Date birthday;

    @Column(name = "LOCATION", length = 50)
    private String location;

    @Column(name = "INTRO", length = 200)
    private String intro;

    @Column(name = "CREATED_TIME", nullable = false)
    private Timestamp createdTime;

    @Column(name = "IMG1", length = 300)
    private String img1;

    @Column(name = "IMG2", length = 300)
    private String img2;

    @Column(name = "IMG3", length = 300)
    private String img3;

    @Column(name = "IMG4", length = 300)
    private String img4;

    @Column(name = "IMG5", length = 300)
    private String img5;

    @Column(name = "INTERESTS", length = 300)
    private String interests;

    @Column(name = "PERSONALITY", length = 300)
    private String personality;

    @Column(name = "UPDATED_TIME", nullable = false)
    private Timestamp updatedTime;

    @Column(name = "USER_STATUS", nullable = false)
    private Integer userStatus;

    @Column(name = "POST_STATUS", nullable = false)
    private Boolean postStatus;

    @Column(name = "AT_AC_STATUS", nullable = false)
    private Boolean atAcStatus;

    @Column(name = "SELL_STATUS", nullable = false)
    private Boolean sellStatus;

    public Users() {
    }

    public Users(Integer userId, String username, String email, String pwd, Integer gender, Date birthday, String location, String intro, Timestamp createdTime, String interests, String personality, Timestamp updatedTime, Integer userStatus, Boolean postStatus, Boolean atAcStatus, Boolean sellStatus) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.pwd = pwd;
        this.gender = gender;
        this.birthday = birthday;
        this.location = location;
        this.intro = intro;
        this.createdTime = createdTime;
        this.interests = interests;
        this.personality = personality;
        this.updatedTime = updatedTime;
        this.userStatus = userStatus;
        this.postStatus = postStatus;
        this.atAcStatus = atAcStatus;
        this.sellStatus = sellStatus;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public String getImg3() {
        return img3;
    }

    public void setImg3(String img3) {
        this.img3 = img3;
    }

    public String getImg4() {
        return img4;
    }

    public void setImg4(String img4) {
        this.img4 = img4;
    }

    public String getImg5() {
        return img5;
    }

    public void setImg5(String img5) {
        this.img5 = img5;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getPersonality() {
        return personality;
    }

    public void setPersonality(String personality) {
        this.personality = personality;
    }

    public Timestamp getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Timestamp updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Integer getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Integer userStatus) {
        this.userStatus = userStatus;
    }

    public Boolean getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(Boolean postStatus) {
        this.postStatus = postStatus;
    }

    public Boolean getAtAcStatus() {
        return atAcStatus;
    }

    public void setAtAcStatus(Boolean atAcStatus) {
        this.atAcStatus = atAcStatus;
    }

    public Boolean getSellStatus() {
        return sellStatus;
    }

    public void setSellStatus(Boolean sellStatus) {
        this.sellStatus = sellStatus;
    }

    @Override
    public String toString() {
        return "Users{" +
                "userId=" + userId +
                ", username='" + username +
                ", email='" + email +
                ", pwd='" + pwd +
                ", gender=" + gender +
                ", birthday=" + birthday +
                ", location='" + location +
                ", intro='" + intro +
                ", createdTime=" + createdTime +
                ", interests='" + interests +
                ", personality='" + personality +
                ", updatedTime=" + updatedTime +
                ", userStatus=" + userStatus +
                ", postStatus=" + postStatus +
                ", atAcStatus=" + atAcStatus +
                ", sellStatus=" + sellStatus +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Users users = (Users) o;
        return Objects.equals(userId, users.userId) && Objects.equals(username, users.username) && Objects.equals(email, users.email) && Objects.equals(pwd, users.pwd) && Objects.equals(gender, users.gender) && Objects.equals(birthday, users.birthday) && Objects.equals(location, users.location) && Objects.equals(intro, users.intro) && Objects.equals(createdTime, users.createdTime) && Objects.equals(interests, users.interests) && Objects.equals(personality, users.personality) && Objects.equals(updatedTime, users.updatedTime) && Objects.equals(userStatus, users.userStatus) && Objects.equals(postStatus, users.postStatus) && Objects.equals(atAcStatus, users.atAcStatus) && Objects.equals(sellStatus, users.sellStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, email, pwd, gender, birthday, location, intro, createdTime, interests, personality, updatedTime, userStatus, postStatus, atAcStatus, sellStatus);
    }
}

