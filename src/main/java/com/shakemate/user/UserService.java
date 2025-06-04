package com.shakemate.user;



import java.security.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;


import com.shakemate.user.*;
import com.shakemate.util.PasswordCovert;

public class UserService {
    UsersDao dao;
    PasswordCovert pc;
    public UserService() {
        dao = new UsersDaoImpl();
        pc = new PasswordCovert(); // 密碼
    }

    public Users getUserByEmail(String email) {
        return dao.findByEmail(email);
    }

    public boolean login(String email, String inputPassword) {
        Users user = dao.findByEmail(email);
        if (user == null) {
            return false;
        }
        if(pc.passwordVerify(user.getPwd(),inputPassword)){
            System.out.println("Password verified");
            return true;
        }else {
            System.out.println("Wrong password");
            return false;
        }


    }


    public void signIn (String userName, String email, String password, Integer gender, Date birthday, String location,
                        String intro, String interests, String personality) throws NoSuchAlgorithmException {
        Users user = new Users();
        user.setUsername(userName);
        user.setEmail(email);
        user.setPwd(pc.hashing(password));
        user.setGender(gender);
        user.setBirthday(birthday);
        user.setLocation(location);
        user.setIntro(intro);
        user.setInterests(interests);
        user.setPersonality(personality);
        // 因為是會員註冊創建資料(預設待審核)所以權限相關全部false
        user.setUserStatus(0);
        user.setPostStatus(false);
        user.setAtAcStatus(false);
        user.setSellStatus(false);
        dao.addUser(user);
    }

    public List<Users> getAllUsers() {
        return dao.getAllUsers();
    }

    public Users getUserById(Integer userId){
        return dao.getUserById(userId);
    }

}

