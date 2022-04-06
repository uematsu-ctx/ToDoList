package com.example.demo.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.model.GroupOrder;
import com.example.demo.model.RegisterForm;
import com.example.demo.model.ToDoItem;
import com.example.demo.model.User;
import com.example.demo.service.ToDoService;
import com.example.demo.service.UserService;

@Controller
public class ToDoController {

	@Autowired
	ToDoService todoService;
	@Autowired
	UserService userService;

	//作業一覧画面のGET用コントローラー
	@GetMapping("/list")
	public String getToDoList(Model model) {
		model.addAttribute("contents", "login/list :: list_contents");

		//作業リストの全件取得
		List<ToDoItem> todolist = todoService.selectMany();
		model.addAttribute("todoList", todolist);

		//作業リストにログインユーザーの名前を表示
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		User user = userService.loginUser(name);
		model.addAttribute("loginUser", user.getUser_name());

		//データの件数を取得
		int count = todoService.count();
		model.addAttribute("todoListCount", count);

		//list.htmlに遷移
		return "login/listLayout";
	}

	//検索画面のGET用コントローラー
	@GetMapping("/search")
	public String getList(Model model, HttpServletRequest request) {
		model.addAttribute("contents", "login/search :: search_contents");

		//検索画面にログインユーザーの名前を表示
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		User user = userService.loginUser(name);
		model.addAttribute("loginUser", user.getUser_name());

		//検索キーワードを取得
		String s = request.getParameter("t");
		List<ToDoItem> todoList = todoService.search(s);
		model.addAttribute("todoList", todoList);

		//検索数の取得
		model.addAttribute("todoListCount", todoList.size());

		//search.htmlに遷移
		return "login/listLayout";
	}

	//作業登録画面のGET用コントローラー
	@GetMapping("/register")
	public String getRegister(@ModelAttribute RegisterForm form, Model model) {
		model.addAttribute("contents", "login/register::register_contents");

		//ユーザーのデータを全件取得
		List<User> userList = userService.selectMany();
		model.addAttribute("selectItems", userList);

		//ToDoItemのインスタンスの生成
		ToDoItem t = new ToDoItem();
		t.setFinished_date(form.getFinishedDate());

		//完了日の完・未完の表示
		if (t.getFinished_date() == null) {
			form.setCheck(false);
		} else {

		}

		//register.htmlに遷移
		return "login/listLayout";
	}

	//作業登録画面のPOST用コントローラー
	@PostMapping("/register")
	public String postRegister(@ModelAttribute @Validated(GroupOrder.class) RegisterForm form,
			BindingResult bindingResult, Model model) {

		//入力チェックに引っかかった場合、作業登録に戻る。
		if (bindingResult.hasErrors()) {
			return getRegister(form, model);
		}

		//insert用変数
		ToDoItem td = new ToDoItem();

		//フォームクラスをToDoItemに変換する
		td.setId(form.getId());//ID
		td.setItem_name(form.getItemName());//項目名
		td.setExpire_date(form.getExpireDate());//期限日
		td.setFinished_date(form.getFinishedDate());//完了日
		td.setCheck(form.getCheck());//チェックボタン
		td.setPerson_id(form.getPersonId());//担当者

		//日付のインスタンスの生成
		Date date = new Date();

		td.setRegistration_date(date);
		//完了していたら日付を、完了していなかったら未と表示。
		if (form.getCheck()) {
			td.setFinished_date(date);
		} else {
			td.setFinished_date(null);
		}

		//作業登録の判定
		try {
			boolean result = todoService.insert(td);

			if (result == true) {
				model.addAttribute("result", "登録成功");
			} else {
				model.addAttribute("result", "登録失敗");
			}
		} catch (DataAccessException e) {
			model.addAttribute("result", "登録失敗(トランザクション)");
			e.printStackTrace();
		}
		
		//list.htmlに画面遷移
		return getToDoList(model);
	}

	//作業更新画面のGET用コントローラー
	@GetMapping("/renew/{id}")
	public String getRenew(@ModelAttribute RegisterForm form, Model model, @PathVariable("id") int id) {
		model.addAttribute("contents", "login/renew::renew_contents");

		//ユーザー名の全件取得
		List<User> userList = userService.selectMany();
		model.addAttribute("selectItems", userList);

		//全作業リストから一つの作業を取得
		ToDoItem todo = todoService.selectOne(id);
		model.addAttribute("selectItems", userList);

		//ToDoItemクラスをフォームクラスに変換
		form.setId(todo.getId());//ID
		form.setItemName(todo.getItem_name());//項目名
		form.setPersonId(todo.getPerson_id());//担当者
		form.setExpireDate(todo.getExpire_date());//期限日
		form.setFinishedDate(todo.getFinished_date());//完了日

		//完了日の完・未完の表示
		if (todo.getFinished_date() == null) {
			form.setCheck(false);
		} else {
			form.setCheck(true);
		}

		//renew.htmlに遷移
		return "login/listLayout";
	}

	//作業更新画面のPOST用コントローラー
	@PostMapping("/renew")
	public String postRenew(@ModelAttribute @Validated(GroupOrder.class) RegisterForm form, BindingResult bindingResult,
			Model model) {

		//入力チェックに引っかかった場合、作業更新に戻る。
		if (bindingResult.hasErrors()) {
			//GETリクエスト用のメソッドを呼び出して、作業更新画面に戻る。
			return getRenew(form, model, form.getId());
		}

		//Dateクラスのオブジェクトを生成する。
		Date date = new Date();

		//完了日のチェックが入ったら、今日の日付を表示。入ってなかったら未と表示。
		if (form.getCheck()) {
			form.setFinishedDate(date);
		} else {
			form.setFinishedDate(null);
		}

		//ToDoItemの更新処理。
		ToDoItem todo = todoService.selectOne(form.getId());

		//フォームクラスからToDoItemに変換する。
		todo.setId(form.getId());//ID
		todo.setItem_name(form.getItemName());//項目名
		todo.setPerson_id(form.getPersonId());//担当者ID
		todo.setExpire_date(form.getExpireDate());//期限日
		todo.setFinished_date(form.getFinishedDate());//完了日
		todo.setCheck(form.getCheck());//チェック

		try {
			//作業の更新
			boolean result = todoService.updateOne(todo);

			//作業更新の判定
			if (result == true) {
				model.addAttribute("result", "更新成功");
			} else {
				model.addAttribute("result", "更新失敗");
			}
		} catch (DataAccessException e) {
			model.addAttribute("result", "更新失敗(トランザクション)");
		}
		//list.htmlに画面遷移
		return getToDoList(model);
	}

	//完了している作業を選択したときのGET用コントローラー
	@GetMapping("/finished_date/{id}")
	public String getFinished_Date(Model model, @PathVariable("id") int id) {

		//Dateクラスのオブジェクトを生成する。
		Date date = new Date();

		//ToDoItemの更新処理
		ToDoItem todo = todoService.selectOne(id);
		if (todo.getId() == id) {
			todo.setFinished_date(date);
			todo.setCheck(true);
		}

		//ToDoItemの更新結果の判定
		boolean td = todoService.updateOne(todo);
		if (td) {
			System.out.println("切り替え成功");
		} else {
			System.out.println("切り替え失敗");
		}

		//List.htmlにリダイレクト
		return "redirect:/list";
	}

	//未完了の作業を選択したときのGET用コントローラー
	@GetMapping("/un_finished_date/{id}")
	public String getUn_Finished_Date(Model model, @PathVariable("id") int id) {

		//TodoItemの更新処理
		ToDoItem todo = todoService.selectOne(id);
		if (todo.getId() == id) {
			todo.setFinished_date(null);
			todo.setCheck(false);
		}

		//ToDoItemの更新結果の判定
		boolean to = todoService.updateUnfinished(todo);
		if (to) {
			System.out.println("切り替え成功");
		} else {
			System.out.println("切り替え失敗");
		}

		//list.htmlにリダイレクト
		return "redirect:/list";
	}

	//作業削除画面のGET用コントローラー
	@GetMapping("/delete/{id}")
	public String getDelete(@ModelAttribute RegisterForm form, Model model, @PathVariable("id") int id) {
		model.addAttribute("contents", "login/delete::delete_contents");

		//ユーザー全件取得
		List<User> userList = userService.selectMany();

		//全作業リストから一つの作業を取得
		ToDoItem todo = todoService.selectOne(form.getId());
		model.addAttribute("selectItems", userList);

		if (todo.getId() == id) {
			form.setId(todo.getId());//ID
			form.setItemName(todo.getItem_name());//項目名
			form.setPersonId(todo.getPerson_id());//担当者ID
			form.setUserName(todo.getUser_name());//ユーザー名
			form.setExpireDate(todo.getExpire_date());//期限日
			form.setFinishedDate(todo.getFinished_date());//完了日

			if (todo.getFinished_date() == null) {
				form.setCheck(false);
			} else {
				form.setCheck(true);
			}
		}
		//delete.htmlに遷移
		return "login/listLayout";
	}

	//作業削除画面のPOST用コントローラー
	@PostMapping("/delete")
	public String postDelete(@ModelAttribute RegisterForm form, Model model) {

		//Dateクラスのオブジェクトを生成する。
		Date date = new Date();

		if (form.getCheck()) {
			form.setFinishedDate(date);
		} else {
			form.setFinishedDate(null);
		}

		try {
			boolean result = todoService.deleteOne(form.getId());
			if (result == true) {
				model.addAttribute("result", "削除成功");
			} else {
				model.addAttribute("result", "削除失敗");
			}
		} catch (DataAccessException e) {
			model.addAttribute("result", "削除失敗");
		}
		//list.htmlに画面遷移
		return getToDoList(model);
	}

	//作業一覧のCSV出力用メソッド
	@GetMapping("/list/csv")
	public ResponseEntity<byte[]> getToDoListCsv(Model model) {

		//ユーザーを全件取得して、CSVをサーバーに保存する。
		todoService.todoCsvOut();

		byte[] bytes = null;
		try {

			//サーバーに保存されているpp.csvファイルをbyteで取得する
			bytes = todoService.getFile("ppp.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}

		//HTTPヘッダーの設定
		HttpHeaders header = new HttpHeaders();
		header.add("Content-Type", "text/csv;charset=UTF-8");
		header.setContentDispositionFormData("filename", "ppp.csv");

		//ppp.csvを戻す
		return new ResponseEntity<>(bytes, header, HttpStatus.OK);
	}

}
