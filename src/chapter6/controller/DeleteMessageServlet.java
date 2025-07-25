package chapter6.controller;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import chapter6.logging.InitApplication;
import chapter6.service.MessageService;

//deleteMessageに対応するServlet
@WebServlet(urlPatterns = { "/deleteMessage" })

public class DeleteMessageServlet extends HttpServlet {

	/**
	 * ロガーインスタンスの生成
	 */
	Logger log = Logger.getLogger("twitter");

	/**
	 * デフォルトコンストラクタ
	 * アプリケーションの初期化を実施する。
	 */
	public DeleteMessageServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();
	}

	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		 " : " + new Object(){}.getClass().getEnclosingMethod().getName());

		/**IDをもとに削除するという処理なので、セッション取得の処理は不要
		 * idだけだとわかりづらいので、messageIdという変数名とした
		 * DBに登録しているidはint型なので、それにあわせる(String型にはしない)
		 */
		int messageId = Integer.parseInt(request.getParameter("messageId"));
		//DBの削除処理を呼び出す
		//new MessageService().delete(messageId);　でもOK
		MessageService messageService = new MessageService();
		messageService.delete(messageId);
		//トップ画面へ遷移
		response.sendRedirect("./");
	}

}
