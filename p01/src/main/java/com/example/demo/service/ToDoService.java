package com.example.demo.service;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.ToDoItem;
import com.example.demo.model.User;
import com.example.demo.repository.ToDoDao;
import com.example.demo.repository.UserDao;
@Transactional
@Service
public class ToDoService {

	@Autowired
	ToDoDao tododao;
	@Autowired
	UserDao userdao;

	//登録用メソッド
	public boolean insert(ToDoItem todo_item) {

		//登録実行
		int rowNumber = tododao.insertOne(todo_item);

		//判定用変数
		boolean result = false;

		if (rowNumber > 0) {
			//登録成功
			result = true;
		}
		return result;
	}

	//全件取得メソッド
	public List<ToDoItem> selectMany() {

		//全件取得実行
		return tododao.selectMany();
	}

	//検索用メソッド
	public List<ToDoItem> search(String todo_item) {

		//検索実行
		List<ToDoItem> result = tododao.findAll(todo_item);

		return result;
	}

	//1件取得メソッド
	public ToDoItem selectOne(int id) {

		//1件取得実行
		ToDoItem todo = tododao.selectOne(id);

		//ユーザー名と担当者の一致
		User user = userdao.selectOne(todo.getPerson_id());
		todo.setUser_name(user.getUser_name());

		return todo;
	}

	//1件更新メソッド
	public boolean updateOne(ToDoItem todo_item) {

		//1件更新
		int rowNumber = tododao.updateOne(todo_item);

		//判定用変数
		boolean result = false;

		if (rowNumber > 0) {

			//update成功
			result = true;
		}
		return result;
	}

	//1件削除メソッド
	public boolean deleteOne(int id) {

		//1件削除
		int rowNumber = tododao.deleteOne(id);

		//判定用変数
		boolean result = false;

		if (rowNumber > 0) {

			//delete成功
			result = true;
		}
		return result;
	}

	//完了日の完・未完の切り替えメソッド
	public boolean updateFinished(ToDoItem todo_item) {

		//1件完了
		int rowNumber = tododao.updateFinished(todo_item);

		//判定用変数
		boolean result = false;

		if (rowNumber > 0) {

			//完了に成功
			result = true;
		}
		return result;
	}

	public boolean updateUnfinished(ToDoItem todo_item) {

		//1件未完了
		int rowNumber = tododao.updateUnFinished(todo_item);

		//判定用変数
		boolean result = false;

		if (rowNumber > 0) {

			//未完了に成功
			result = true;
		}
		return result;
	}

	//カウント用メソッド
		public int count() {
			return tododao.count();
		}

	//作業一覧をCSV出力する
	public void todoCsvOut() throws DataAccessException {

		//CSV出力
		tododao.todoCsvOut();
	}

	//サーバーに保存されているファイルを取得して、byte配列に変換する。
	public byte[] getFile(String fileName) throws IOException {

		//ファイルシステム(デフォルト)の取得
		FileSystem fs = FileSystems.getDefault();

		//ファイル取得
		Path p = fs.getPath(fileName);

		//ファイルを配列に変換
		byte[] bytes = Files.readAllBytes(p);

		return bytes;
	}
}
