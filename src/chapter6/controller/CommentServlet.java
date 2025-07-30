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
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import chapter6.beans.Comment;
import chapter6.beans.User;
import chapter6.logging.InitApplication;
import chapter6.service.CommentService;

//commentに対応するServlet
@WebServlet(urlPatterns = { "/comment" })

public class CommentServlet extends HttpServlet {
	/**
	 * ロガーインスタンスの生成
	 */
	Logger log = Logger.getLogger("twitter");

	/**
	 * デフォルトコンストラクタ
	 * アプリケーションの初期化を実施する。
	 */
	public CommentServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		HttpSession session = request.getSession();
		List<String> errorMessages = new ArrayList<String>();

		//textというKeyで、リクエストスコープに格納されたtext(=つぶやきへの返信)を受け取る
		String text = request.getParameter("text");
		//isValidがfalseを返したら、エラーメッセージを表示しつつTOP画面を表示させる
		if (!isValid(text, errorMessages)) {
			session.setAttribute("errorMessages", errorMessages);
			response.sendRedirect("./");
			return;
		}

		Comment comment = new Comment();
		//つぶやきへの返信を、commentにセットする
		comment.setText(text);
		//メッセージIDもセット
		comment.setMessage_id("message_Id");
		System.out.println(comment.getMessage_id());
		//ログインユーザのセッション取得
		User user = (User) session.getAttribute("loginUser");
		//ユーザIDのセット必要！
		comment.setUser_id(user.getId());

		new CommentService().insert(comment);
		response.sendRedirect("./");
	}

	private boolean isValid(String comment, List<String> errorMessages) {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		//StringUtils.isBlankで、空白、改行、nullの場合エラーとなるようにする
		if (StringUtils.isBlank(comment)) {
			errorMessages.add("メッセージを入力してください");
		} else if (140 < comment.length()) {
			errorMessages.add("140文字以下で入力してください");
		}

		if (errorMessages.size() != 0) {
			return false;
		}
		return true;
    }


}
