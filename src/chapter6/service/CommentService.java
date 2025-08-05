package chapter6.service;

import static chapter6.utils.CloseableUtil.*;
import static chapter6.utils.DBUtil.*;

import java.sql.Connection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import chapter6.beans.Comment;
import chapter6.beans.UserComment;
import chapter6.dao.CommentDao;
import chapter6.dao.UserCommentDao;
import chapter6.logging.InitApplication;

public class CommentService {
	/**
	 * ロガーインスタンスの生成
	 */
	Logger log = Logger.getLogger("twitter");

	/**
	 * デフォルトコンストラクタ
	 * アプリケーションの初期化を実施する。
	 */
	public CommentService() {
		InitApplication application = InitApplication.getInstance();
		application.init();
	}

	//つぶやきへの新規返信
	public void insert(Comment comment) {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		" : " + new Object(){}.getClass().getEnclosingMethod().getName());

			Connection connection = null;
			try {
				connection = getConnection();
				new CommentDao().insert(connection, comment);
				commit(connection);
			} catch (RuntimeException e) {
				rollback(connection);
				log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
				throw e;
			} finally {
				close(connection);
			}
	}

	//つぶやきの返信の画面表示
	public List<UserComment> select() {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		 " : " + new Object(){}.getClass().getEnclosingMethod().getName());

		//取得するレコードの数を最大1000件に制限するため、LIMIT_NUMを設定
		final int LIMIT_NUM = 1000;

		Connection connection = null;
		try {
			connection = getConnection();

			//接続情報（connection）やその他パラメータを引数に、selectメソッドを呼び出す
			List<UserComment> comments = new UserCommentDao().select(connection, LIMIT_NUM);

			//commentsを戻り値とする
			return comments;

		} catch (RuntimeException e) {
			rollback(connection);
			log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		} finally {
			close(connection);
		}
	}

}
