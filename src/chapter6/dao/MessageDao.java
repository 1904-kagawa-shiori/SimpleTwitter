package chapter6.dao;

import static chapter6.utils.CloseableUtil.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import chapter6.beans.Message;
import chapter6.exception.SQLRuntimeException;
import chapter6.logging.InitApplication;

public class MessageDao {

    /**
    * ロガーインスタンスの生成
    */
    Logger log = Logger.getLogger("twitter");

    /**
    * デフォルトコンストラクタ
    * アプリケーションの初期化を実施する。
    */
    public MessageDao() {
        InitApplication application = InitApplication.getInstance();
        application.init();
    }

    public void insert(Connection connection, Message message) {

	  log.info(new Object(){}.getClass().getEnclosingClass().getName() +
       " : " + new Object(){}.getClass().getEnclosingMethod().getName());

        PreparedStatement ps = null;
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO messages ( ");
            sql.append("    user_id, ");
            sql.append("    text, ");
            sql.append("    created_date, ");
            sql.append("    updated_date ");
            sql.append(") VALUES ( ");
            sql.append("    ?, ");                  // user_id
            sql.append("    ?, ");                  // text
            sql.append("    CURRENT_TIMESTAMP, ");  // created_date
            sql.append("    CURRENT_TIMESTAMP ");   // updated_date
            sql.append(")");

            ps = connection.prepareStatement(sql.toString());
            //上記SQL文の、?の部分(=プレースホルダ)に具体的な値を埋め込む
            //1:一番目のプレースホルダ
            //2:二番目のプレースホルダ
            ps.setInt(1, message.getUserId());
            ps.setString(2, message.getText());

            ps.executeUpdate();
        } catch (SQLException e) {
        	log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
            throw new SQLRuntimeException(e);
        } finally {
            close(ps);
        }
   }

    //つぶやきの削除用
	public void delete(Connection connection, int id) {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("DELETE FROM messages ");
			sql.append("WHERE id = ? ");

			ps = connection.prepareStatement(sql.toString());
			//プレースホルダにidを埋め込む
			ps.setInt(1, id);

			ps.executeUpdate();

		} catch (SQLException e) {
			log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}
	}

	//つぶやきの編集画面表示用
	public Message select(Connection connection, int id) {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;//DBに送る命令を準備するための箱
		try {
			String sql = "SELECT * FROM messages WHERE id = ?";
			//DBへの命令を準備 (psにSQL文をセット)
			ps = connection.prepareStatement(sql);
			// SQL文の「?」の部分に、引数で受け取った「id」の値をセット
			ps.setInt(1, id);

			/* SQL文を実行して、結果をデータベースから受け取る
			 * ResultSetは、DBから返された「表（テーブル）」のようなデータ
			 */
			ResultSet rs = ps.executeQuery();

			//toMessagesメソッド呼び出し: ResultSet（DBの表データ）を、Messageオブジェクトのリストに変換
			List<Message> messages = toMessages(rs);

			// もし、変換されたMessageのリストが空だったら（該当するつぶやきがなかったら）
			if (messages.isEmpty()) {
				return null;//何も見つからなかったのでnullを返す
			} else {
				//見つかったつぶやきが1つ以上あれば、最初の一つを返す
				//IDで検索しているため、通常は一つしか見つからないはず
				return messages.get(0);
			}
		} catch (SQLException e) {
			//DB操作でエラー発生sたらエラーログを出し例外を投げる
			log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			//DB関連のリソース(psなど)を確実に閉じる
			close(ps);
		}
	}

	/* toMessageメソッドの役割:
	 * DBから受け取った生データを、Javaのプログラムで扱いやすい、
	 * Message オブジェクトのリストに変換する処理を行う
	 */
	private List<Message> toMessages (ResultSet rs) throws SQLException {

		List<Message> messages = new ArrayList<Message>();

		try {
			/* rs.next()は、ResultSetの次の行に移動する
			 * データがある限り(行がある限り)、ループが繰り返される
			 */
			while(rs.next()) {
				Message message = new Message();//新しいMessageオブジェクトを一つ作る
				//各列のデータを、Messageオブジェクトの対応するプロパティにセットしていく
				message.setId(rs.getInt("id"));//id列のint値を取得しsetIdにセット
				message.setUserId(rs.getInt("user_id"));//user_id列のint値を取得してsetUserIdにセット
				message.setText(rs.getString("text"));
				message.setCreatedDate(rs.getTimestamp("created_date"));
				message.setUpdatedDate(rs.getTimestamp("updated_date"));

				messages.add(message);//完成したMessageオブジェクトをリストに追加
			}
			return messages;// 全ての行を変換したら、Messageオブジェクトのリストを返す
		} finally {
			close(rs);
		}
	}

	//つぶやきの編集用
	public void update(Connection connection, Message message) {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE messages SET ");//UPDATE文
			sql.append("text = ?, ");
			sql.append("created_date = CURRENT_TIMESTAMP ");//CURRENT_TIMESTAMPは、NOW()のシノニム。NOW()は現在の日付と時間を返す。
			sql.append("WHERE id = ? ");

			ps = connection.prepareStatement(sql.toString());

			ps.setString(1, message.getText() );
			ps.setInt(2, message.getId());

			ps.executeUpdate();

		} catch (SQLException e) {
			log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}
	}
}
