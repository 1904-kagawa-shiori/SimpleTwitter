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

import chapter6.beans.Message;
import chapter6.logging.InitApplication;
import chapter6.service.MessageService;

//つぶやき編集画面のServlet
@WebServlet(urlPatterns = { "/edit" })

public class EditServlet extends HttpServlet {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	 * デフォルトコンストラクタ
	 * アプリケーションの初期化を実施する。
	 */
	public EditServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();
	}

	//つぶやき編集画面を表示させるためのdoGet
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		 " : " + new Object(){}.getClass().getEnclosingMethod().getName());

		//セッション取得（ユーザーの情報や一時的なデータを保存する場所）
		HttpSession session = request.getSession();
		//編集したい「つぶやきのID」を受け取る
		//messageIdのバリデーションチェックを行うため、この時点ではString型として判定処理をする
		//int id = Integer.parseInt(request.getParameter("messageId"));
		String messageId = request.getParameter("messageId");//パラメータ名間違えないように注意！
		Message message = null;//つぶやきの情報を格納する箱

		//受け取ったmessageIdが空でなく、正しい数字かどうかをチェック
		if(!(StringUtils.isBlank(messageId)) && (messageId.matches("^[0-9]*$"))){
			int id = Integer.parseInt(messageId);//
			message = new MessageService().select(id);//そのIDのつぶやきをDBから取得
		}

		//もしmessage(つぶやきの情報)が取れなかったら(IDが不正だったり存在しないつぶやきだったら)
		//エラーメッセージを表示(つぶやき編集機能追加の際に追加
		if(message == null) {
			List<String> errorMessages = new ArrayList<String>();
			errorMessages.add("不正なパラメータが入力されました");
			session.setAttribute("errorMessages", errorMessages);//エラーメッセージをセッションに保存
			response.sendRedirect("./");//トップページに強制的に戻す
			return;//ここで処理を終了
		}

		//つぶやきの情報が正常に取れたら
		request.setAttribute("message", message);// 取得したつぶやき情報をリクエストにセット(JSPで使えるようにする)
		request.getRequestDispatcher("edit.jsp").forward(request, response);// edit.jspを表示する
	}

	//つぶやき編集画面の編集内容を反映させるためのdoPost
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		 " : " + new Object(){}.getClass().getEnclosingMethod().getName());

		List<String> errorMessages = new ArrayList<String>();//エラーメッセージ格納用

		String text = request.getParameter("text");//ユーザが入力した新しいつぶやき内容
		int messageId = Integer.parseInt(request.getParameter("messageId"));//編集対象のつぶやきID

		/* 編集したつぶやき(text)内容のバリデーション処理をしているisValidメソッドを呼び出す
		 * もしisValidが「不正だ」と判断したら（つまりisValidがfalseを返したら）、
		 * ①再度編集画面で入力をやり直す必要があるので、最初に表示されていたつぶやき情報をDBから再取得する
		 * 　MessageService().select(messageId)：MessageService クラスの selectメソッドを呼び出し、messageIdに基づいて元のつぶやきを取得する
		 * ②①で取得した、元のつぶやきオブジェクト（Message）をリクエストスコープにセット
		 * ③編集画面にforwardする
		 */
		if(!isValid(text, errorMessages)) {
			Message message = new MessageService().select(messageId);//①

			request.setAttribute("errorMessages", errorMessages);
			request.setAttribute("text", text);//ユーザが入力したtextを再表示するため
			request.setAttribute("message", message);//②
			request.getRequestDispatcher("./edit.jsp").forward(request, response);//③
			return;//ここで処理を終了　下の更新処理には進まない
		}
		//つぶやき内容に問題なかった時に以下の処理に進む
		Message message = new Message();//新しいMessageオブジェクトを作成
		message.setId(messageId);//どのつぶやきを更新するかを伝えるためにつぶやきIDをセット
		message.setText(text);//ユーザが入力した新しいテキストをセット

		new MessageService().update(message);//新しい内容でDBを更新
		response.sendRedirect("./");//更新が成功したら、トップページにリダイレクトする
	}

	//つぶやき編集内容のバリデーションチェック(MessageServlet内のチェックと同様)
	private boolean isValid(String text, List<String> errorMessages) {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
		 " : " + new Object(){}.getClass().getEnclosingMethod().getName());

		//メッセージが空または空白のみだったらエラーメッセージ出力
		if (StringUtils.isBlank(text)) {
			errorMessages.add("メッセージを入力してください");
		//メッセージが140文字を超えていたらエラーメッセージ出力
		} else if (140 < text.length()) {
    		errorMessages.add("140文字以下で入力してください");
    	}
		//errorMessagesリストに何かエラーが追加されていれば、false（不正）を返す
		if (errorMessages.size() != 0) {
			return false;
		}
		//エラーが何もなければ、true（正しい）を返す
		return true;
	}

}
