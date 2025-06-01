package com.shakemate.VO;

import java.util.List;

public class UserProfileVO {
    private int userId;
    private String username;
    private int age;
    private String zodiac;
    private List<String> avatarList;
    private String personality;
    private String interests;
    private String intro;

    public UserProfileVO(int userId, String username, int age, String zodiac, List<String> avatarList,
                         String personality, String interests, String intro) {
        this.userId = userId;
        this.username = username;
        this.age = age;
        this.zodiac = zodiac;
        this.avatarList = avatarList;
        this.personality = personality;
        this.interests = interests;
        this.intro = intro;
    }

    // Getter 和 Setter 方法（可選擇是否加入）

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public int getAge() {
        return age;
    }

    public String getZodiac() {
        return zodiac;
    }

    public List<String> getAvatarList() {
        return avatarList;
    }

    public String getPersonality() {
        return personality;
    }

    public String getInterests() {
        return interests;
    }

    public String getIntro() {
        return intro;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setZodiac(String zodiac) {
        this.zodiac = zodiac;
    }

    public void setAvatarList(List<String> avatarList) {
        this.avatarList = avatarList;
    }

    public void setPersonality(String personality) {
        this.personality = personality;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }
}
