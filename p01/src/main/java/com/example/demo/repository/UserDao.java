package com.example.demo.repository;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.example.demo.model.User;

public interface UserDao {

	//ユーザー一覧のデータを検索
	public List<User>findAll(String user)throws DataAccessException;

	//userテーブルの件数を取得
	public int count() throws DataAccessException;

	//userテーブルに1件insert
	public int insertOne(User user) throws DataAccessException;

	//userテーブルのデータを1件取得
	public User selectOne(int id) throws DataAccessException;

	//userテーブルの全データを取得
	public List<User> selectMany() throws DataAccessException;

	//userテーブルを1件更新(アドミン権限専用画面)
	public int updateOne(User user) throws DataAccessException;

	//ログインユーザーの名前を表示
	public User loginUser(String userId) throws DataAccessException;

	//userテーブルを1件更新(ログインユーザー自身)
	public int updatePassword(User user) throws DataAccessException;

	//userテーブルを1件削除(アドミン権限専用画面)
	public int deleteOne(String userId) throws DataAccessException;

	//SQL取得結果をサーバーにCSVで保存する
	public void userCsvOut() throws DataAccessException;

	//ユーザーIDの重複を防ぐ
	public int countByUserName(String userId) throws DataAccessException;

}