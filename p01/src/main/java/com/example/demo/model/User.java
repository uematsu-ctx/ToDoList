package com.example.demo.model;



import java.util.Date;

import lombok.Data;

@Data
public class User {
 private int id;//ID
 private String user_id;//ユーザーID
 private String user_name;//ユーザー名
 private String password;//パスワード
 private Date birthday;//誕生日
 private int age;//年齢
 private String role;//役職
}
