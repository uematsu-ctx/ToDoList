package com.example.demo.model;

import java.util.Date;

import lombok.Data;
@Data
public class ToDoItem {
	private int id;//ID
	private String item_name;//項目名
	private Date expire_date;//期限日
	private boolean check;//完了・未完了用のチェックボタン
	private Date finished_date;//完了日
	private Date registration_date;//登録日
	private int person_id;//担当者ID
	private String user_name;//ユーザー名

}
