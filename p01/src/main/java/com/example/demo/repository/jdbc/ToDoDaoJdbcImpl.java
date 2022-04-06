package com.example.demo.repository.jdbc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.model.ToDoItem;
import com.example.demo.repository.ToDoDao;

@Repository
public class ToDoDaoJdbcImpl implements ToDoDao {
	@Autowired
	JdbcTemplate jdbc;

	//作業一覧のデータを検索
	@Override
	public List<ToDoItem> findAll(String todo) throws DataAccessException {

		//検索用変数
		String t = "%" + todo + "%";

		//検索取得
		List<Map<String, Object>> getList = jdbc.queryForList(
				"SELECT users.user_name,todo_items.* FROM users,todo_items WHERE users.id=todo_items.person_id and(item_name like ? or user_name like ?);",
				t, t);

		//結果返却用の変数
		List<ToDoItem> todoList = new ArrayList<>();

		//取得したデータを結果返却用のListに格納していく
		for (Map<String, Object> map : getList) {

			//ToDoItemのインスタンスの生成
			ToDoItem td = new ToDoItem();
			td.setId((int) map.get("id"));//ID
			td.setItem_name((String) map.get("item_name"));//項目名
			td.setUser_name((String) map.get("user_name"));//担当者
			td.setRegistration_date((Date) map.get("registration_date"));//登録日
			td.setExpire_date((Date) map.get("expire_date"));//期限日
			td.setFinished_date((Date) map.get("finished_date"));//完了日

			//結果返却用のListに追加
			todoList.add(td);
		}
		return todoList;
	}

	//todo_itemsテーブルにデータを1件登録
	@Override
	public int insertOne(ToDoItem todo_item) throws DataAccessException {

		//1件登録
		int rowNumber = jdbc.update(
				"INSERT INTO todo_items(item_name,registration_date,expire_date,finished_date,person_id)VALUES(?,?,?,?,?)",
				todo_item.getItem_name(),todo_item.getRegistration_date(), todo_item.getExpire_date(), todo_item.getFinished_date(),
				todo_item.getPerson_id());

		return rowNumber;
	}

	//todo_itemsテーブルのデータを1件取得
	@Override
	public ToDoItem selectOne(int id) throws DataAccessException {

		// 1件取得
		Map<String, Object> map = jdbc.queryForMap("SELECT * FROM todo_items WHERE id=?", id);

		//結果返却用の変数
		ToDoItem todo = new ToDoItem();

		//取得したデーを結果返却用の変数にセットしていく
		todo.setId((int) map.get("id"));//ID
		todo.setItem_name((String) map.get("item_name"));//項目名
		todo.setPerson_id((int) map.get("person_id"));//担当者ID
		todo.setRegistration_date((Date) map.get("registration_date"));//登録日
		todo.setExpire_date((Date) map.get("expire_date"));//期限日
		todo.setFinished_date((Date) map.get("finished_date"));//完了日

		return todo;
	}

	//todo_itemsテーブルの件数を取得
	@Override
	public int count() throws DataAccessException {

		//全件取得してカウント
		int count = jdbc.queryForObject("SELECT COUNT(*)FROM todo_items", Integer.class);
		return count;
	}

	//todo_itemsテーブルの全データを取得
	@Override
	public List<ToDoItem> selectMany() throws DataAccessException {

		//todo_itemsテーブルのデータを全件取得
		//usersテーブルのidをtodo_itemsテーブルのperson_idを一致させて、usersテーブルのuser_nameを取得する
		List<Map<String, Object>> getList = jdbc.queryForList(
				"SELECT users.user_name,todo_items. * FROM users,todo_items WHERE users.id=todo_items.person_id ORDER BY expire_date ASC ;");

		//結果返却用の変数
		List<ToDoItem> todoList = new ArrayList<>();

		//取得したデータを結果返却用のListに格納していく
		for (Map<String, Object> map : getList) {

			//ToDoItemのインスタンス
			ToDoItem todo = new ToDoItem();

			//ToDoItemインスタンスに取得したデータをセットする
			todo.setId((int) map.get("id"));//ID
			todo.setItem_name((String) map.get("item_name"));//項目名
			todo.setUser_name((String) map.get("user_name"));//担当者
			todo.setPerson_id((int) map.get("person_id"));//担当者ID
			todo.setRegistration_date((Date) map.get("registration_date"));//登録日
			todo.setExpire_date((Date) map.get("expire_date"));//期限日
			todo.setFinished_date((Date) map.get("finished_date"));//完了日

			//結果返却用のListに追加
			todoList.add(todo);
		}
		return todoList;
	}

	//todo_itemsテーブルのデータを1件更新
	@Override
	public int updateOne(ToDoItem todo_item) throws DataAccessException {

		//1件更新
		int rowNumber = jdbc.update(
				"UPDATE todo_items SET item_name=?,person_id=?,expire_date=?,finished_date=? WHERE id=?",
				todo_item.getItem_name(), todo_item.getPerson_id(), todo_item.getExpire_date(),
				todo_item.getFinished_date(), todo_item.getId());

		return rowNumber;
	}

	//todo_itemsテーブルの完了日の完・未完を更新
	@Override
	public int updateFinished(ToDoItem todo_item) throws DataAccessException {

		//1件更新
		int rowNumber = jdbc.update("UPDATE todo_items SET finished_date=? WHERE id=?", todo_item.getFinished_date(),
				todo_item.getId());

		return rowNumber;

	}

	@Override
	public int updateUnFinished(ToDoItem todo_item) throws DataAccessException {

		//1件更新
		int rowNumber = jdbc.update("UPDATE todo_items SET finished_date=null WHERE id=?", todo_item.getId());

		return rowNumber;
	}

	//todo_itemsテーブルを1件削除
	@Override
	public int deleteOne(int id) throws DataAccessException {

		//1件削除
		int rowNumber = jdbc.update("DELETE FROM todo_items WHERE id=?", id);

		return rowNumber;
	}

	//todo_itemsテーブルの全データをcsvに出力する
	@Override
	public void todoCsvOut() throws DataAccessException {

		//todo_itemsテーブルのデータを全件取得するSQL
		//usersテーブルのidをtodo_itemsテーブルのperson_idを一致させて、usersテーブルのuser_nameを取得する
		String sql = "SELECT todo_items. *, users.user_name FROM users,todo_items WHERE users.id=todo_items.person_id ORDER BY expire_date ASC;";

		//ResultSetExtractorの生成
		TodoRowCallbackHandler handler = new TodoRowCallbackHandler();

		//SQL実行&CSV出力
		jdbc.query(sql, handler);
	}
}