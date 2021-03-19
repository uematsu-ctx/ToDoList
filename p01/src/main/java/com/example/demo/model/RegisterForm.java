package com.example.demo.model;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class RegisterForm {

	private int id;//ID

	//必須入力、長さ1から100まで
	@NotBlank(message = "{require_check}" ,groups = ValidGroup1.class)
	private String itemName;//項目名

	//必須入力、yyyy/MM/ddで入力してください
	@NotNull(message = "{require_check}",groups = ValidGroup1.class)
	@DateTimeFormat(pattern="yyyy/MM/dd")
	private Date expireDate;//期限日


	private String userName;//担当者
	private int personId;//担当者のID

	private Date finishedDate;//完了日
	private Boolean check;//完了・未完了用のチェックボタン
    private int age;//年齢
}
