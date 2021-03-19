package com.example.demo.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class PasswordUpdateForm {

	//必須入力、メールアドレス形式
	@NotBlank(message = "{require_check}", groups = ValidGroup1.class)
	@Email(message = "{email_check}", groups = ValidGroup2.class)
	private String userId;//ユーザーID

	//必須入力、長さ4から100桁まで、半角英数字のみ
	@NotBlank(message = "{require_check}", groups = ValidGroup1.class)
	@Length(min = 4, max = 255, groups = ValidGroup2.class)
	@Pattern(regexp = "^[a-zA-Z0-9]+$", groups = ValidGroup2.class)
	private String password;//パスワード

}