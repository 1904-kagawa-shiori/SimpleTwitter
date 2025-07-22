package chapter6.service;

import static chapter6.utils.CloseableUtil.*;
import static chapter6.utils.DBUtil.*;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import chapter6.beans.User;
import chapter6.dao.UserDao;
import chapter6.logging.InitApplication;
import chapter6.utils.CipherUtil;

//UserServiceでやっていること＝ServletとDaoの橋渡し
//今回の処理では、DBを利用できる状態にしている
public class UserService {

	/**
	 * ロガーインスタンスの生成
	 */
	Logger log = Logger.getLogger("twitter");

	/**
	 * デフォルトコンストラクタ
	 * アプリケーションの初期化を実施する。
	 */
	public UserService() {
		InitApplication application = InitApplication.getInstance();
		application.init();
	}

	public void insert(User user) {
		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
			" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		Connection connection = null;
		try {
			// パスワード暗号化
			String encPassword = CipherUtil.encrypt(user.getPassword());
			user.setPassword(encPassword);

			/**1～3:接続情報を用意して、操作して、コミットする
			 * 1 getConnection()を呼び出すことで、DBに接続できるようになる
			 *   DBにアクセスするための接続情報を変数connectionとして用意するようなイメージ
			 */
			connection = getConnection();
			/**2 接続情報（connection）やその他必要なパラメータを引数に、insertメソッドを呼び出す
			 *   実際にDBの操作を行うのはUserDaoの役割
			 */
			new UserDao().insert(connection, user);
			// 3 DBの操作を確定させるためにコミットする
			commit(connection);
		} catch (RuntimeException e) {
			rollback(connection);
			log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		} finally {
			close(connection);
		}
	}

	//ログイン機能実装にあたり、パスワード暗号化周りの処理を追加
	public User select(String accountOrEmail, String password) {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		Connection connection = null;
		try {
			 // パスワード暗号化
			String encPassword = CipherUtil.encrypt(password);
			connection = getConnection();
			User user = new UserDao().select(connection, accountOrEmail, encPassword);
			commit(connection);

			return user;
		}catch(RuntimeException e) {
			rollback(connection);
			log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		}catch (Error e) {
			rollback(connection);
			log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		}finally {
			close(connection);
		}

	}

	public User select(int userId) {

	    log.info(new Object(){}.getClass().getEnclosingClass().getName() +
	    " : " + new Object(){}.getClass().getEnclosingMethod().getName());

	    Connection connection = null;
	    try {
	        connection = getConnection();
	        User user = new UserDao().select(connection, userId);
	        commit(connection);

	        return user;
	    } catch (RuntimeException e) {
	        rollback(connection);
		  log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
	        throw e;
	    } catch (Error e) {
	        rollback(connection);
		  log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
	        throw e;
	    } finally {
	        close(connection);
	    }
	}

	//更新用のメソッド
	public void update(User user) {

	    log.info(new Object(){}.getClass().getEnclosingClass().getName() +
	    " : " + new Object(){}.getClass().getEnclosingMethod().getName());

	    Connection connection = null;
	    try {
	        // パスワード暗号化
	        String encPassword = CipherUtil.encrypt(user.getPassword());
	        user.setPassword(encPassword);

	        connection = getConnection();
	        new UserDao().update(connection, user);
	        commit(connection);
	    } catch (RuntimeException e) {
	        rollback(connection);
		  log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
	        throw e;
	    } catch (Error e) {
	        rollback(connection);
		  log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
	        throw e;
	    } finally {
	        close(connection);
	    }
	}
}
