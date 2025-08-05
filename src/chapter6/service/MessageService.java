package chapter6.service;

import static chapter6.utils.CloseableUtil.*;
import static chapter6.utils.DBUtil.*;

import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import chapter6.beans.Message;
import chapter6.beans.UserMessage;
import chapter6.dao.MessageDao;
import chapter6.dao.UserMessageDao;
import chapter6.logging.InitApplication;

public class MessageService {

    /**
    * ロガーインスタンスの生成
    */
    Logger log = Logger.getLogger("twitter");

    /**
    * デフォルトコンストラクタ
    * アプリケーションの初期化を実施する。
    */
    public MessageService() {
        InitApplication application = InitApplication.getInstance();
        application.init();
    }

    //新規つぶやき
    public void insert(Message message) {

  	  log.info(new Object(){}.getClass().getEnclosingClass().getName() +
  	  " : " + new Object(){}.getClass().getEnclosingMethod().getName());

  	        Connection connection = null;
  	        try {
  	            connection = getConnection();
  	            new MessageDao().insert(connection, message);
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

    //つぶやきの削除
    public void delete(int id) {

    	log.info(new Object(){}.getClass().getEnclosingClass().getName() +
    	" : " + new Object(){}.getClass().getEnclosingMethod().getName());

    	Connection connection = null;
    	try {
    		connection = getConnection();
    		new MessageDao().delete(connection, id);
    		commit(connection);
    	} catch (RuntimeException e) {
    		rollback(connection);
    		log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
    		throw e;
    	} finally {
    		close(connection);
    	}

    }

    //つぶやきの編集画面表示
	public Message select(int id) {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		Connection connection = null;
		try {
			connection = getConnection();
			Message message = new MessageDao().select(connection, id);
			commit(connection);
			//戻り値にmessageを指定
			return message;
		} catch (RuntimeException e) {
			rollback(connection);
			log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		} finally {
			close(connection);
		}
	}

	//つぶやきの編集
	public void update(Message message) {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		Connection connection = null;
		try {
			connection = getConnection();
			new MessageDao().update(connection, message);
			commit(connection);
		} catch(RuntimeException e) {
			rollback(connection);
			log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		} finally {
			close(connection);
		}
	}

    /* 実戦問題②：ユーザーアカウント名にリンクを設定し、クリックすると各ユーザー毎のつぶやき表示画面へ遷移させるようにする
     * selectの引数にString型のuserIdを追加
     */
    public List<UserMessage> select(String userId, String startDate, String endDate) {

    	log.info(new Object(){}.getClass().getEnclosingClass().getName() +
    			" : " + new Object(){}.getClass().getEnclosingMethod().getName());

    	//取得するレコードの数を最大1000件に制限するため、LIMIT_NUMを設定
    	final int LIMIT_NUM = 1000;

    	Connection connection = null;
    	try {
    		connection = getConnection();
    		/* 実戦問題②
    		 * idをnullで初期化
    		 * ServletからuserIdの値が渡ってきていたら
    		 * 整数型に型変換し、idに代入
    		 */
    		Integer id = null;
    		if(!StringUtils.isEmpty(userId)) {
    			id = Integer.parseInt(userId);
    		}

    		//startが入力されていたら、開始時刻を00:00:00にセット
    		if(!StringUtils.isBlank(startDate)) {
    			startDate += " 00:00:00";
    		} else {
    			//デフォルト値を設定
    			startDate = "2020/01/01 00:00:00";
    		}
    		//endが入力されていたら、終了時刻を23:59:59にセット
    		if(!StringUtils.isBlank(endDate)) {
    			endDate += " 23:59:59";
    		} else {
    			//デフォルト値を設定
    			//Date型のdateクラス
    			Date date = new Date();
    			//日時フォーマット：SimpleDateFormat、DateTimeFormatter(Java8以降でつかえる 試したけど使えなさげ)など色々ある
    			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    			//終了日について、SimpleDateFormatクラスを用いて指定したフォーマットで、日付を代入
    			endDate = dateFormat.format(date);
    		}
    		/* Java8以降、java.timeパッケージを使う方法
    		 * import java.time.LocalDate;
    		 * import java.time.LocalDateTime;
    		 * import java.time.format.DateTimeFormatter;
    		 * // ユーザーが日付を入力しなかった場合のデフォルト値
    		 * if (StringUtils.isBlank(endDate)) {
    		 * // 現在の日付と時刻を取得
    		 * LocalDateTime now = LocalDateTime.now();
    		 * // フォーマッターを定義
    		 * DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    		 * // 現在の日付と時刻を指定したフォーマットで文字列に変換
    		 * endDate = now.format(formatter);
}
    		 */

    		/*
    		 * messageDao.selectに引数としてInteger型のidを追加
    		 * idがnullだったら全件取得する
    		 * idがnull以外だったら、その値に対応するユーザーIDの投稿を取得する
    		 * つぶやきの絞り込み機能追加にて、引数にstartDate,endDateを追加
    		 */
    		List<UserMessage> messages = new UserMessageDao().select(connection, id, startDate, endDate, LIMIT_NUM);
    		commit(connection);

    		return messages;
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
