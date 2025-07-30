package chapter6.dao;

import java.util.logging.Logger;

import chapter6.logging.InitApplication;

public class UserCommentDao {

	/**
	 * ロガーインスタンスの生成
	 */
	Logger log = Logger.getLogger("twitter");

	/**
	 * デフォルトコンストラクタ
	 * アプリケーションの初期化を実施する。
	 */
	public UserCommentDao() {
		InitApplication application = InitApplication.getInstance();
		application.init();
	}

	//つぶやき返信情報の取得(SELECT文)
}
