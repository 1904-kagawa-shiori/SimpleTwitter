package chapter6.controller;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import chapter6.beans.User;
import chapter6.beans.UserMessage;
import chapter6.logging.InitApplication;
import chapter6.service.MessageService;

//トップ画面のServlet
@WebServlet(urlPatterns = { "/index.jsp" })

public class TopServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
    * ロガーインスタンスの生成
    * ログが出力できるようにするための実装
    */
    Logger log = Logger.getLogger("twitter");

    /**
    * デフォルトコンストラクタ
    * アプリケーションの初期化を実施する。
    */
    public TopServlet() {
        InitApplication application = InitApplication.getInstance();
        application.init();

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

    	//以下で実際にログを出力
    	log.info(new Object(){}.getClass().getEnclosingClass().getName() +
        " : " + new Object(){}.getClass().getEnclosingMethod().getName());

    	//ログイン
    	boolean isShowMessageForm = false;
    	User user = (User) request.getSession().getAttribute("loginUser");
    	//ログインユーザのオブジェクトが取得できた場合は、trueを設定する
    	if (user != null) {
    		isShowMessageForm = true;
    	}

    	/* 実戦問題②：ユーザーアカウント名にリンクを設定し、クリックすると各ユーザー毎のつぶやき表示画面へ遷移させるようにする
    	 * String型のuser_idの値をrequest.getParameter("user_id")で
    	 * JSPから受け取るように設定
    	 * MessageServiceのselectに引数としてString型のuser_idを追加
    	 */
    	String userId = request.getParameter("user_id");

    	//メッセージを格納するためのリスト
    	List<UserMessage> messages = new MessageService().select(userId);
    	//List<UserMessage> messages = new MessageService().select();

    	request.setAttribute("messages", messages);
    	request.setAttribute("isShowMessageForm", isShowMessageForm);
        request.getRequestDispatcher("/top.jsp").forward(request, response);
    }

}
