package chapter6.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import chapter6.beans.User;
import chapter6.logging.InitApplication;
import chapter6.service.UserService;

//ユーザーの登録機能
//top.jsp内の”signup”に対応するServlet
@WebServlet(urlPatterns = { "/signup" })

public class SignUpServlet extends HttpServlet {

	   /**
	   * ロガーインスタンスの生成
	   */
	    Logger log = Logger.getLogger("twitter");

	    /**
	    * デフォルトコンストラクタ
	    * アプリケーションの初期化を実施する。
	    */
	    public SignUpServlet() {
	        InitApplication application = InitApplication.getInstance();
	        application.init();

	    }
	    /**ServletからJSPを呼び出す
	     * top画面で「登録する」を押すと、このdoGetメソッドが呼ばれる
	     * get呼び出しされると実行され、ユーザ登録画面(signup.jsp)を表示
	     */
	    @Override
	    protected void doGet(HttpServletRequest request, HttpServletResponse response)
	            throws IOException, ServletException {

		  log.info(new Object(){}.getClass().getEnclosingClass().getName() +
	        " : " + new Object(){}.getClass().getEnclosingMethod().getName());

		  /**この1行に、画面を指定するメソッド（1）と、実際に遷移させるメソッド（2）の2つが含まれている
		   * 1 request.getRequestDispatcherメソッドの引数に遷移する画面を指定
		   * 2 forwardメソッドを呼び出すと、遷移が行われる
		   */
	        request.getRequestDispatcher("signup.jsp").forward(request, response);
	    }

	    /**①doPostメソッド
	     * 画面でユーザ情報を入力し、登録ボタンを押下すると、このdoPostメソッドが呼ばれる
	     * ＝リクエストに対する受け口の役割
	     * post呼び出しされると実行され、リクエストパラメータを Userオブジェクトにセットする
	     * Serviceのメソッドを呼び出してDBへユーザーの登録を行う
	     * ユーザーの登録が完了したら再びトップ画面を表示するようになっている
	     */
	    @Override
	    protected void doPost(HttpServletRequest request, HttpServletResponse response)
	            throws IOException, ServletException {


		  log.info(new Object(){}.getClass().getEnclosingClass().getName() +
	        " : " + new Object(){}.getClass().getEnclosingMethod().getName());

	        List<String> errorMessages = new ArrayList<String>();

	        /**②getUserメソッド：リクエストパラメータを取得
	         * (メソッド名(); で、自クラスのメソッドを呼び出している)
	         * 引数に指定したrequest変数は、doPostやdoGetが実行される際に渡されているもの（doPostやdoGetメソッドの引数）
	         */
	        User user = getUser(request);
	        //(そのあと、③doPostメソッドに戻ってくる)

	        /**④isValidメソッド：リクエストパラメータに対するバリデーションを行う
	         * isValidはbooleanを戻り値とするメソッドであり、isValidの実行結果によってその後の処理が分岐している
	         */
	        if (!isValid(user, errorMessages)) {
	            request.setAttribute("errorMessages", errorMessages);
	            request.getRequestDispatcher("signup.jsp").forward(request, response);
	            return;
	        }
	        //(バリデーションが終わると、再び呼び出し元⑤doPostメソッドに戻ってくる)

	        /**⑥UserService.javaのinsertメソッドを呼び出す
	         * 入力値に不備が無い場合は、DBにデータを登録するためにUserService.javaのinsertメソッドへと処理が移る
	         *  外部クラスのメソッドを呼び出す基本構文：戻り値なしパターン
	         *   new クラス名().メソッド名();
	         *  外部クラスのメソッドを呼び出す基本構文：戻り値ありパターン
	         *   戻り値の型 変数名 = new クラス名().メソッド名();
	         */
	        new UserService().insert(user);
	        response.sendRedirect("./");//引数にURLを指定することで、リダイレクトされる
	    }

	    //ユーザー登録画面（signup.jsp ※後述）からの入力値（リクエストパラメータ）を取得する
	    private User getUser(HttpServletRequest request) throws IOException, ServletException {


	  	  log.info(new Object(){}.getClass().getEnclosingClass().getName() +
	          " : " + new Object(){}.getClass().getEnclosingMethod().getName());

	          User user = new User();
	          user.setName(request.getParameter("name"));
	          //★request.getParameterで"account"の値を取得
	          user.setAccount(request.getParameter("account"));
	          user.setPassword(request.getParameter("password"));
	          user.setEmail(request.getParameter("email"));
	          user.setDescription(request.getParameter("description"));
	          return user;
	      }

	     //入力値に対するバリデーションを行う。入力値が不正な場合には再度、自画面(signup)を表示するようにしている。
	      private boolean isValid(User user, List<String> errorMessages) {


	  	  log.info(new Object(){}.getClass().getEnclosingClass().getName() +
	          " : " + new Object(){}.getClass().getEnclosingMethod().getName());

	          String name = user.getName();
	          String account = user.getAccount();
	          String password = user.getPassword();
	          String email = user.getEmail();

	          if (!StringUtils.isEmpty(name) && (20 < name.length())) {
	              errorMessages.add("名前は20文字以下で入力してください");
	          }

	          if (StringUtils.isEmpty(account)) {
	              errorMessages.add("アカウント名を入力してください");
	          } else if (20 < account.length()) {
	              errorMessages.add("アカウント名は20文字以下で入力してください");
	          }

	          if (StringUtils.isEmpty(password)) {
	              errorMessages.add("パスワードを入力してください");
	          }

	          if (!StringUtils.isEmpty(email) && (50 < email.length())) {
	              errorMessages.add("メールアドレスは50文字以下で入力してください");
	          }

	          if (errorMessages.size() != 0) {
	              return false;
	          }
	          return true;
	      }
}
