package chapter6.service;

import static chapter6.utils.CloseableUtil.*;
import static chapter6.utils.DBUtil.*;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import chapter6.beans.Comment;
import chapter6.dao.CommentDao;
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

}
