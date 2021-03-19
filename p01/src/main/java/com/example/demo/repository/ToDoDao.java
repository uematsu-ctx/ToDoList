package com.example.demo.repository;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.example.demo.model.ToDoItem;

public interface ToDoDao {

	//作業一覧のデータを検索
	public List<ToDoItem> findAll(String todo) throws DataAccessException;

	//todo_itemsテーブルの件数を取得
	public int count() throws DataAccessException;

	//todo_itemsテーブルにデータを1件登録
	public int insertOne(ToDoItem todo_item) throws DataAccessException;

	//todo_itemsテーブルのデータを1件取得
	public ToDoItem selectOne(int id) throws DataAccessException;

	//todo_itemsテーブルの全データを取得
	public List<ToDoItem> selectMany() throws DataAccessException;

	//todo_itemsテーブルを1件更新
	public int updateOne(ToDoItem todo_item) throws DataAccessException;

	//todo_itemsテーブルの完了日の完・未完の更新
	public int updateFinished(ToDoItem todo_item) throws DataAccessException;

	public int updateUnFinished(ToDoItem todo_item) throws DataAccessException;

	//todo_itemsテーブルのデータを1件削除
	public int deleteOne(int id) throws DataAccessException;

	//SQL取得結果をサーバーにCSVで保存する
	public void todoCsvOut() throws DataAccessException;

}
