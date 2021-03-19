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

import com.example.demo.model.User;
import com.example.demo.repository.UserDao;

@Transactional
@Service
public class UserService {
	@Autowired
	UserDao dao;

	//検索用メソッド
	public List<User> search(String user) {

		//検索実行
		List<User> result = dao.findAll(user);

		return result;
	}

	//inser用メソッド
	public boolean insert(User user) {

		//insert実行
		int rowNumber = dao.insertOne(user);

		//判定用変数
		boolean result = false;

		if (rowNumber > 0) {
			//insert成功
			result = true;
		}
		return result;
	}

	//カウント用メソッド
	public int count() {
		//件数取得
		return dao.count();
	}

	//全件取得用メソッド
	public List<User> selectMany() {
		//全件取得
		return dao.selectMany();
	}

	//1件取得メソッド
	public User selectOne(int id) {
		return dao.selectOne(id);
	}

	//管理者権限の1件更新メソッド
	public boolean updateOne(User user) {
		//1件更新
		int rowNumber = dao.updateOne(user);

		//判定用変数
		boolean result = false;

		if (rowNumber > 0) {
			//update成功
			result = true;
		}
		return result;
	}

	//ログインユーザー名の表示メソッド
	public User loginUser(String userId) {
		return dao.loginUser(userId);
	}

	//ログインユーザーのパスワードを更新
	public boolean updatePassword(User user) {
		//1件更新
		int rowNumber = dao.updatePassword(user);

		//判定用変数
		boolean result = false;

		if (rowNumber > 0) {
			//update成功
			result = true;
		}
		return result;
	}

	//1件削除メソッド
	public boolean deleteOne(String userId) {
		//1件削除
		int rowNumber = dao.deleteOne(userId);

		//判定用変数
		boolean result = false;

		if (rowNumber > 0) {
			//delete成功
			result = true;
		}
		return result;
	}

	//ユーザー一覧をCSV出力する
	public void userCsvOut() throws DataAccessException {

		//CSV出力
		dao.userCsvOut();
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

	//ユーザーIDの重複確認のメソッド
	public boolean isDuplicated(String userId) {
		//1件確認
		int rowNumber = dao.countByUserName(userId);

		//判定用変数
		boolean result = false;

		if (rowNumber > 0) {
			//登録可能
			result = true;
		}
		return result;
	}

}