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

import chapter6.beans.User;
import chapter6.exception.NoRowsUpdatedRuntimeException;
import chapter6.exception.SQLRuntimeException;
import chapter6.logging.InitApplication;

//DBを操作するのがDaoの役割 実際のデータの取得や更新・削除はDaoで行われる
public class UserDao {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	 * デフォルトコンストラクタ
	 * アプリケーションの初期化を実施する。
	 */
	public UserDao(){
		InitApplication application = InitApplication.getInstance();
		application.init();
	}

	public void insert(Connection connection, User user) {
		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
				" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;//1:SQLの下地を作る SQLを実行するためのPreparedStatementオブジェクトを初期化
		try {
			StringBuilder sql = new StringBuilder();//2:SQL文を作る(まだ作ってるだけの段階)
			sql.append("INSERT INTO users ( ");
			sql.append("    account, ");
			sql.append("    name, ");
			sql.append("    email, ");
			sql.append("    password, ");
			sql.append("    description, ");
			sql.append("    created_date, ");
			sql.append("    updated_date ");
			sql.append(") VALUES ( ");
			sql.append("    ?, ");                  // account
			sql.append("    ?, ");                  // name
			sql.append("    ?, ");                  // email
			sql.append("    ?, ");                  // password
			sql.append("    ?, ");                  // description
			sql.append("    CURRENT_TIMESTAMP, ");  // created_date
			sql.append("    CURRENT_TIMESTAMP ");   // updated_date
			sql.append(")");

			//SQLをConnectionクラスのprepareStatementメソッドの引数に渡すことで、SQLが実行できる準備が整う
			ps = connection.prepareStatement(sql.toString());//3:SQLを実行できるようにする

			/**登録する値はユーザーの入力値によって異なるし、検索（select）する条件や更新（update）する条件も場面によって変わる
			 * こういった動的な処理はバインド変数を利用し、セットする値を変えることで実現させる
			 */
			ps.setString(1, user.getAccount());//4:SQLの動的部分に値をセットする
			ps.setString(2, user.getName());
			ps.setString(3, user.getEmail());
			ps.setString(4, user.getPassword());
			ps.setString(5, user.getDescription());

			//更新系SQL（insertやupdate）の場合は、executeUpdate()を呼び出すことでSQLが実行される
			ps.executeUpdate();//5:SQLを実行する
		} catch (SQLException e)  {
			log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		}finally {
			close(ps);
		}
	}

	public User select(Connection connection, String accountOrEmail, String password) {
		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {
			String sql = "SELECT * FROM users WHERE (account = ? OR email = ?) AND password = ?";

			ps = connection.prepareStatement(sql);

			ps.setString(1, accountOrEmail);
			ps.setString(2, accountOrEmail);
			ps.setString(3, password);

			ResultSet rs = ps.executeQuery();

			List<User> users = toUsers(rs);
			if (users.isEmpty()) {
				return null;
			} else if (2 <= users.size()) {
				log.log(Level.SEVERE,"ユーザーが重複しています",
				new IllegalStateException());
				throw new IllegalStateException("ユーザーが重複しています");
			} else {
				return users.get(0);
			}
		} catch(SQLException e) {
			log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}
	}

	private List<User> toUsers(ResultSet rs) throws SQLException {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		" : " + new Object(){}.getClass().getEnclosingMethod().getName());
		List<User> users = new ArrayList<User>();
		try {
			 while (rs.next()) {
				User user = new User();
				user.setId(rs.getInt("id"));
				user.setAccount(rs.getString("account"));
				user.setName(rs.getString("name"));
				user.setEmail(rs.getString("email"));
				user.setPassword(rs.getString("password"));
				user.setDescription(rs.getString("description"));
				user.setCreatedDate(rs.getTimestamp("created_date"));
				user.setUpdatedDate(rs.getTimestamp("updated_date"));

				users.add(user);
			 }
			 return users;
		} finally {
			close(rs);
		}
	}

	public User select(Connection connection, int id) {
	    log.info(new Object(){}.getClass().getEnclosingClass().getName() +
	     " : " + new Object(){}.getClass().getEnclosingMethod().getName());

	    PreparedStatement ps = null;
	    try {
	    	String sql = "SELECT * FROM users WHERE id = ?";

	        ps = connection.prepareStatement(sql);

	        ps.setInt(1, id);

	        ResultSet rs = ps.executeQuery();

	        List<User> users = toUsers(rs);
	        if (users.isEmpty()) {
	            return null;
	        } else if (2 <= users.size()) {
	    		log.log(Level.SEVERE, "ユーザーが重複しています", new IllegalStateException());
	            throw new IllegalStateException("ユーザーが重複しています");
	        } else {
	            return users.get(0);
	        }
	    } catch (SQLException e) {
		  log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
	        throw new SQLRuntimeException(e);
	    } finally {
	        close(ps);
	    }
	}

	//更新用のメソッド
	public void update(Connection connection, User user) {

	    log.info(new Object(){}.getClass().getEnclosingClass().getName() +
	    	    " : " + new Object(){}.getClass().getEnclosingMethod().getName());

	    	    PreparedStatement ps = null;
	    	    try {
	    	        StringBuilder sql = new StringBuilder();
	    	        sql.append("UPDATE users SET ");
	    	        sql.append("    account = ?, ");
	    	        sql.append("    name = ?, ");
	    	        sql.append("    email = ?, ");
	    	        sql.append("    password = ?, ");
	    	        sql.append("    description = ?, ");
	    	        sql.append("    updated_date = CURRENT_TIMESTAMP ");
	    	        sql.append("WHERE id = ?");

	    	        ps = connection.prepareStatement(sql.toString());

	    	        ps.setString(1, user.getAccount());
	    	        ps.setString(2, user.getName());
	    	        ps.setString(3, user.getEmail());
	    	        ps.setString(4, user.getPassword());
	    	        ps.setString(5, user.getDescription());
	    	        ps.setInt(6, user.getId());

	    	        int count = ps.executeUpdate();
	    	        if (count == 0) {
	    	    		log.log(Level.SEVERE,"更新対象のレコードが存在しません", new NoRowsUpdatedRuntimeException());
	    	            throw new NoRowsUpdatedRuntimeException();
	    	        }
	    	    } catch (SQLException e) {
	    		  log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
	    	        throw new SQLRuntimeException(e);
	    	    } finally {
	    	        close(ps);
	    	    }
	}
}
