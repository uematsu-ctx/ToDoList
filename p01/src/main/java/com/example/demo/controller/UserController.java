package com.example.demo.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.model.GroupOrder;
import com.example.demo.model.PasswordUpdateForm;
import com.example.demo.model.SignUpForm;
import com.example.demo.model.User;
import com.example.demo.service.ToDoService;
import com.example.demo.service.UserService;

@Controller
public class UserController {

	@Autowired
	UserService userService;
	@Autowired
	ToDoService todoService;
	@Autowired
	LoginController login;

	//パスワードを暗号化して、データベースに登録。
	PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	//ラジオボタンの実装
	private Map<String, String> radioRole;

	//ラジオボタンの初期化メソッド
	private Map<String, String> initradioRole() {
		Map<String, String> radio = new LinkedHashMap<>();

		//アドミンユーザー、一般ユーザーをMapに格納
		radio.put("管理者", "ROLE_ADMIN");
		radio.put("一般従事者", "ROLE_GENERAL");
		return radio;
	}

	//検索画面のGET用コントローラー
	@GetMapping("/search_u")
	public String getList(Model model, HttpServletRequest request) {
		model.addAttribute("contents", "login/search_u::search_u_contents");

		//検索キーワードを取得
		String r = request.getParameter("s");
		List<User> userList = userService.search(r);
		model.addAttribute("userList", userList);

		//検索数を取得
		model.addAttribute("userListCount", userList.size());

		//search(u).htmlに遷移
		return "login/listLayout";
	}

	//ユーザー登録画面のGET用コントローラー
	@GetMapping("/signUp")
	public String getSignUp(@ModelAttribute SignUpForm form, Model model) {

		model.addAttribute("contents", "login/signUp::signUp_contents");

		//signup.htmlに画面遷移
		return "login/listLayout";
	}

	//ユーザー登録画面のPOST用コントローラー
	//データバインド結果の受け取り
	@PostMapping("/signUp")
	public String postSignUp(@ModelAttribute @Validated(GroupOrder.class) SignUpForm form, BindingResult bindingResult,
			Model model) {

		//入力チェックに引っかかった場合、ユーザー登録画面に戻る。
		if (bindingResult.hasErrors()) {
			//GETリクエスト用のメソッドを呼び出して、ユーザー登録画面に戻る。
			return getSignUp(form, model);
		}

		//insert用変数
		User user = new User();

		user.setUser_id(form.getUserId());//ユーザーID
		user.setUser_name(form.getUserName());//ユーザー名
		user.setPassword(form.getPassword());//パスワード
		user.setBirthday(form.getBirthday());//誕生日
		user.setAge(form.getAge());//年齢

		//パスワードを暗号化してuserテーブルに登録する。
		String digest = passwordEncoder.encode(form.getPassword());

		user.setPassword(digest);

		//ユーザーIDの重複の確認
		boolean result0 = userService.isDuplicated(form.getUserId());

		//ユーザーIDが重複していた時のメッセージの変数。
		String s = "同一ユーザーIDが存在しています。";

		//ユーザーIDの重複結果の判定
		if (result0 == false) {

		} else {
			model.addAttribute("error", s);

			//GETリクエスト用のメソッドを呼び出して、ユーザー登録画面に戻る。
			return getSignUp(form, model);
		}
		try {
			//ユーザー登録処理
			boolean result1 = userService.insert(user);
			if (result1 == true) {
				model.addAttribute("result", "登録成功");
			} else {
				model.addAttribute("result", "登録失敗");
			}
		} catch (DataAccessException e) {
			model.addAttribute("result", "更新失敗(トランザクション)");
		}

		//login.htmlに遷移
		return login.getLogin(model);
	}

	//ユーザー一覧画面のGET用コントローラー(アドミン専用)
	@GetMapping("/userList")
	public String getUserList(Model model) {
		model.addAttribute("contents", "login/userList::userList_contents");

		//ユーザー一覧の生成
		List<User> userList = userService.selectMany();

		//Modelにユーザーリストを登録
		model.addAttribute("userList", userList);

		//データの件数を取得
		int count = userService.count();
		model.addAttribute("userListCount", count);

		//userList.htmlに遷移
		return "login/listLayout";
	}

	//アドミン権限専用画面のGET用コントローラー
	@GetMapping("/admin_work/{id}")
	public String getAdmin_Work(@ModelAttribute SignUpForm form, Model model, @PathVariable("id") String userName) {
		model.addAttribute("contents", "login/admin_work :: admin_work_contents");
		//ラジオボタンの初期化メソッドの呼び出し
		radioRole = initradioRole();

		//ラジオボタン用のMapをModelに登録
		model.addAttribute("radioRole", radioRole);

		//ユーザー情報を取得
		User user = userService.selectOne(form.getId());

		//Userクラスをフォームクラスに変換
		form.setUserId(user.getUser_id());//ユーザーID
		form.setUserName(user.getUser_name());//ユーザー名
		form.setPassword(user.getPassword());//パスワード
		form.setBirthday(user.getBirthday());//誕生日
		form.setAge(user.getAge());//年齢
		form.setRole(user.getRole());//役職

		//admin_work.htmlに遷移
		return "login/listLayout";

	}

	//アドミン権限専用画面の更新用コントローラー
	@PostMapping(value = "/admin_work", params = "update")
	public String postAdmin_Work(@ModelAttribute SignUpForm form, Model model) {

		//Userインスタンスの生成
		User user = new User();

		//フォームクラスをUserクラスに変換
		user.setUser_id(form.getUserId());//ユーザーID
		user.setUser_name(form.getUserName());//ユーザー名
		user.setPassword(form.getPassword());//パスワード
		user.setBirthday(form.getBirthday());//誕生日
		user.setAge(form.getAge());//年齢
		user.setRole(form.getRole());//役職

		try {
			//ユーザーの更新
			boolean result = userService.updateOne(user);

			//ユーザー更新の判定
			if (result == true) {
				model.addAttribute("result", "更新成功");
			} else {
				model.addAttribute("result", "更新失敗");
			}
		} catch (DataAccessException e) {
			model.addAttribute("result", "更新失敗(トランザクション)");
		}

		//userList.htmlを表示
		return getUserList(model);
	}

	//ユーザー自身の更新画面のGET用コントローラー
	@GetMapping("/userDetail_update")
	public String getUserDetail_update(@ModelAttribute PasswordUpdateForm form, Model model) {
		model.addAttribute("contents", "login/userDetail_update::userDetail_update_contents");

		//ログインユーザーのユーザーIDを表示する。
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		model.addAttribute("ud", name);
		form.setUserId(name);

		//userDetail_update.htmlに遷移
		return "login/listLayout";
	}

	//ユーザー自身の更新画面のPOST用コントローラー
	@PostMapping("/userDetail_update")
	public String postUserDetail_update(@ModelAttribute @Validated(GroupOrder.class) PasswordUpdateForm form,
			BindingResult bindingResult,
			Model model) {

		//入力チェックに引っかかった場合、ユーザー自身の更新画面に戻る。
		if (bindingResult.hasErrors()) {
			return getUserDetail_update(form, model);
		}
		//update用変数
		User user = new User();

		user.setUser_id(form.getUserId());

		//ログインユーザーのパスワードを暗号化してusersテーブルを更新する。
		String digest = passwordEncoder.encode(form.getPassword());

		user.setPassword(digest);

		try {
			//ログインユーザー自身の更新
			boolean result = userService.updatePassword(user);
			if (result == true) {
				model.addAttribute("result", "更新成功");
			} else {
				model.addAttribute("result", "更新失敗");
			}
		} catch (DataAccessException e) {
			model.addAttribute("result", "更新失敗(トランザクション)");
		}
		//userDetail_update.htmlに遷移
		return getUserDetail_update(form, model);
	}

	//アドミン権限専用画面のユーザーの削除用コントローラー
	@PostMapping(value = "/admin_work", params = "delete")
	public String postDelete(@ModelAttribute SignUpForm form, Model model) {
		try {
			//ユーザーの削除
			boolean result = userService.deleteOne(form.getUserId());

			if (result == true) {
				model.addAttribute("result", "削除成功");
			} else {
				model.addAttribute("result", "削除失敗");
			}
		} catch (DataAccessException e) {
			model.addAttribute("result", "削除失敗");
		}
		//userList.htmlを表示
		return getUserList(model);
	}

	//ユーザー一覧のCSV出力用メソッド
	@GetMapping("/userList/csv")
	public ResponseEntity<byte[]> getUserListCsv(Model model) {

		//ユーザーを全件取得して、CSVをサーバーに保存する。
		userService.userCsvOut();

		byte[] bytes = null;
		try {

			//サーバーに保存されているpp.csvファイルをbyteで取得する
			bytes = userService.getFile("pp.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}

		//HTTPヘッダーの設定
		HttpHeaders header = new HttpHeaders();
		header.add("Content-Type", "text/csv;charset=UTF-8");
		header.setContentDispositionFormData("filename", "pp.csv");

		//pp.csvを戻す
		return new ResponseEntity<>(bytes, header, HttpStatus.OK);
	}

}
