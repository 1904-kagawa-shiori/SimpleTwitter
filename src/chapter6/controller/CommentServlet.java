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

//commentに対応するServlet(JSPのフォームの action 属性と紐づく)
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
			//sessionにエラーメッセージを入れる（JSPで表示するため）
			session.setAttribute("errorMessages", errorMessages);
			// トップ画面（"./"）にリダイレクトして、エラーメッセージを表示させる
			response.sendRedirect("./");
			return;
		}

		//Commentオブジェクト（返信の情報を保持する箱）を作成
		Comment comment = new Comment();
		//ユーザが入力したつぶやきへの返信を、commentにセットする
		comment.setText(text);
		/**メッセージIDもセット
		 * どの「つぶやき」に対する返信なのか、そのメッセージIDを受け取り、Commentオブジェクトにセットする
		 * 隠しフィールド <input type="hidden" name="messageId" value="${message.id}"> から取得
		 */
		comment.setMessage_id(Integer.parseInt(request.getParameter("messageId")));

		/**ログインユーザのセッション取得(誰が返信したのかの情報)して、Commentオブジェクトにセット
		 * ログイン時にセッションに保存しておいたユーザー情報（Userオブジェクト）を取り出す
		 */
		User user = (User) session.getAttribute("loginUser");
		comment.setUser_id(user.getId());//ログインユーザのIDのセット必要！

		/**CommentServiceクラスを使って、
		 * 作成したCommentオブジェクトの情報をDBに挿入（保存）する
		 */
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
