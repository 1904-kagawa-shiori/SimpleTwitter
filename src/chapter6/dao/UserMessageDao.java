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

import chapter6.beans.UserMessage;
import chapter6.exception.SQLRuntimeException;
import chapter6.logging.InitApplication;

public class UserMessageDao {

    /**
    * ロガーインスタンスの生成
    */
    Logger log = Logger.getLogger("twitter");

    /**
    * デフォルトコンストラクタ
    * アプリケーションの初期化を実施する。
    */
    public UserMessageDao() {
        InitApplication application = InitApplication.getInstance();
        application.init();
    }

    //つぶやき情報の取得(SELECT文)
    public List<UserMessage> select(Connection connection, Integer id, String startDate, String endDate, int num) {

	  log.info(new Object(){}.getClass().getEnclosingClass().getName() +
        " : " + new Object(){}.getClass().getEnclosingMethod().getName());

        PreparedStatement ps = null;
        try {
            StringBuilder sql = new StringBuilder();

            sql.append("SELECT ");
            sql.append("    messages.id as id, ");
            sql.append("    messages.text as text, ");
            sql.append("    messages.user_id as user_id, ");
            sql.append("    users.account as account, ");
            sql.append("    users.name as name, ");
            sql.append("    messages.created_date as created_date ");
            sql.append("FROM messages ");
            sql.append("INNER JOIN users ");
            sql.append("ON messages.user_id = users.id ");
            /**つぶやきの絞り込み機能追加で以下を追加
             * 開始日時～終了日時を指定
             */
            sql.append("WHERE messages.created_date ");
            sql.append("BETWEEN ? and ? ");

            /**実践問題②
            * idがnullだったら全件取得し、idがnull以外だったら、その値に対応するユーザーIDの投稿を取得するようにする
            * WHERE句の条件にuser_idを指定する※つぶやきの絞り込み機能追加時に、冒頭をWHEREでなく、ANDに修正済み
            */
            if (id != null){
            	sql.append("AND messages.user_id = ? ");
            }
            sql.append("ORDER BY created_date DESC limit " + num);

            ps = connection.prepareStatement(sql.toString());

            //つぶやきの絞り込み機能追加で以下を追加
            //各デフォルト値の変数　開始時刻:startDate、終了時刻:endDate　デフォルト値の準備はService側でやる
            ps.setString(1, startDate);
            ps.setString(2, endDate);

            //実践問題②　idがnull以外だったら、psに1,userIdの値をセットする
            if(id != null) {
            	ps.setInt(3, id);
            }

            //DB→このDao
            ResultSet rs = ps.executeQuery();

            List<UserMessage> messages = toUserMessages(rs);
            return messages;
        } catch (SQLException e) {
        	log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
            throw new SQLRuntimeException(e);
        } finally {
            close(ps);
        }
    }

    private List<UserMessage> toUserMessages(ResultSet rs) throws SQLException {

  	  log.info(new Object(){}.getClass().getEnclosingClass().getName() +
          " : " + new Object(){}.getClass().getEnclosingMethod().getName());

          List<UserMessage> messages = new ArrayList<UserMessage>();
          try {
              while (rs.next()) {
                  UserMessage message = new UserMessage();
                  message.setId(rs.getInt("id"));
                  message.setText(rs.getString("text"));
                  message.setUserId(rs.getInt("user_id"));
                  message.setAccount(rs.getString("account"));
                  message.setName(rs.getString("name"));
                  message.setCreatedDate(rs.getTimestamp("created_date"));

                  messages.add(message);
              }
              return messages;
          } finally {
              close(rs);
          }
      }

}
