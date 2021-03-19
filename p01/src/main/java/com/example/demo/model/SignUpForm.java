package com.example.demo.model;

import java.util.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class SignUpForm {

	private int id;//ID

	//必須入力、メールアドレス形式
	@NotBlank(message = "{require_check}", groups = ValidGroup1.class)
	@Email(message = "{email_check}", groups = ValidGroup2.class)
	private String userId;//ユーザーID

	//必須入力、長さ1文字から50文字まで
	@NotBlank(message = "{require_check}", groups = ValidGroup1.class)
	@Length(min = 1, max = 50, groups = ValidGroup2.class)
	private String userName;//ユーザー名

	//必須入力、長さ4から100桁まで、半角英数字のみ
	@NotBlank(message = "{require_check}", groups = ValidGroup1.class)
	@Length(min = 4, max = 255, groups = ValidGroup2.class)
	@Pattern(regexp = "^[a-zA-Z0-9]+$", groups = ValidGroup2.class)
	private String password;//パスワード

	//必須入力、
	@NotNull(message = "{require_check}", groups = ValidGroup1.class)
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private Date birthday;//誕生日

	//値が18から100まで
	@Min(value = 18, message = "{min_check}", groups = ValidGroup2.class)
	@Max(value = 100, message = "{max_check}", groups = ValidGroup2.class)
	private int age;//年齢

	private String role;//役職

}