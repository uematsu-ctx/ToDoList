package com.example.demo.repository.jdbc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.model.User;
import com.example.demo.repository.UserDao;

@Repository
public class UserDaoJdbcImpl implements UserDao {
	@Autowired
	JdbcTemplate jdbc;

	//ユーザー一覧のデータを検索
	@Override
	public List<User> findAll(String user) throws DataAccessException {

		//検索用変数
		String u = "%" + user + "%";

		//検索取得
		List<Map<String, Object>> getList = jdbc
				.queryForList("SELECT * FROM users WHERE user_name LIKE ?",u);

		//結果返却用の変数
		List<User> userList = new ArrayList<>();

		//取得したデータを結果返却用の変数にセットしていく。
		for (Map<String, Object> map : getList) {

			//Userのインスタンスの生成
			User ur = new User();
			ur.setId((int) map.get("id"));//ID
			ur.setUser_id((String) map.get("user_id"));//ユーザーID
			ur.setUser_name((String) map.get("user_name"));//ユーザー名
			ur.setBirthday((Date) map.get("birthday"));//誕生日
			ur.setAge((int)map.get("age"));//年齢
			ur.setRole((String) map.get("role"));//役職
			//結果返却用のListに追加
			userList.add(ur);
		}
		return userList;
	}

	//usersテーブルの件数を取得
	@Override
	public int count() throws DataAccessException {

		//全件取得してカウント
		int count = jdbc.queryForObject("SELECT COUNT(*)FROM users", Integer.class);
		return count;
	}

	//usersテーブルのデータを1件insert
	@Override
	public int insertOne(User user) throws DataAccessException {

		//1件登録
		int rowNumber = jdbc.update(
				"INSERT INTO users(user_id , user_name, password, birthday, age,role) VALUES(?,?,?,?,?,'ROLE_GENERAL')", user.getUser_id(),
				user.getUser_name(), user.getPassword(), user.getBirthday(),
				user.getAge());
		return rowNumber;
	}

	//usersテーブルのデータを1件取得
	@Override
	public User selectOne(int id) throws DataAccessException {

		//1件取得
		Map<String, Object> map = jdbc.queryForMap("SELECT * FROM users WHERE id=?", id);

		//結果返却用の変数
		User user = new User();

		//取得したデータを結果返却用の変数にセットしていく
		user.setId((int) map.get("id"));//ID
		user.setUser_id((String) map.get("user_id"));//ユーザーID
		user.setUser_name((String) map.get("user_name"));//ユーザー名
		user.setPassword((String) map.get("password"));//パスワード
		user.setBirthday((Date) map.get("birthday"));//誕生日
		user.setAge((int) map.get("age"));//年齢
		user.setRole((String) map.get("role"));//ロール
		return user;
	}

	//usersテーブルの全データを取得
	@Override
	public List<User> selectMany() throws DataAccessException {

		//usersテーブルのデータを全件取得
		List<Map<String, Object>> getList = jdbc.queryForList("SELECT*FROM users");

		//結果返却用の変数
		List<User> userList = new ArrayList<>();

		//取得したデータを結果返却用のListに格納していく
		for (Map<String, Object> map : getList) {

			//Userインスタンスの生成
			User user = new User();

			//Userインスタンスに取得したデータをセットする
			user.setId((int) map.get("id"));//ID
			user.setUser_id((String) map.get("user_id"));//ユーザーID
			user.setUser_name((String) map.get("user_name"));//ユーザー名
			user.setPassword((String) map.get("password"));//パスワード
			user.setBirthday((Date) map.get("birthday"));//誕生日
			user.setAge((int) map.get("age"));//年齢
			user.setRole((String) map.get("role"));//役職

			//結果返却用のListに追加
			userList.add(user);

		}
		return userList;
	}

	//usersテーブルを1件更新(アドミン権限専用画面)
	@Override
	public int updateOne(User user) throws DataAccessException {

		//1件更新
		int rowNumber = jdbc.update("UPDATE users SET role=? WHERE user_id=?", user.getRole(), user.getUser_id());

		return rowNumber;
	}

	//usersテーブルを1件更新(ログインユーザー自身)
	@Override
	public int updatePassword(User user) throws DataAccessException {

		//1件更新
		int rowNumber = jdbc.update("UPDATE users SET password=? WHERE user_id=?", user.getPassword(),
				user.getUser_id());
		return rowNumber;
	}

	//usersテーブルを1件削除
	@Override
	public int deleteOne(String userId) throws DataAccessException {

		//1件削除
		int rowNumber = jdbc.update("DELETE FROM users WHERE user_id=?", userId);
		return rowNumber;
	}

	//ログインユーザーの名前を取得
	@Override
	public User loginUser(String userId) {

		//1件取得
		Map<String, Object> map = jdbc.queryForMap("SELECT user_name FROM users WHERE user_id=?", userId);

		//結果返却用の変数
		User user = new User();

		//取得したデータを結果返却用の変数にセットしていく
		user.setUser_name((String) map.get("user_name"));//ログインユーザー名
		return user;
	}

	//usersテーブルの全データをcsvに出力する
	@Override
	public void userCsvOut() throws DataAccessException {

		//usersテーブルのデータを全件取得するSQL
		String sql = "SELECT*FROM users";

		//ResultSetExtractorの生成
		UserRowCallbackHandler handler = new UserRowCallbackHandler();

		//SQL実行&CSV出力
		jdbc.query(sql, handler);
	}

	//usersテーブルに同一ユーザーIDの登録時の重複を防ぐ
	@Override
	public int countByUserName(String userId) throws DataAccessException {
		//ユーザー名の確認
		int rowNumber = jdbc.queryForObject("SELECT COUNT(id) FROM users WHERE user_id= ?", Integer.class, userId);
		return rowNumber;
	}
}
