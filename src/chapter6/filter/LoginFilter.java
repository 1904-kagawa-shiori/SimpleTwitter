package chapter6.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import chapter6.beans.User;

/** フィルター対象の指定
 * 「/setting」と「/edit」というURLにアクセスがあったときに、このフィルターを動かしてください、という設定
 * doFilterメソッドは、ウェブサイトへのリクエストとレスポンスを一時的に受け取る
 */
@WebFilter({"/setting","/edit"})
public class LoginFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			 FilterChain chain) throws IOException, ServletException {
		/**request と response は汎用的なオブジェクトなので、
		 * HttpServletRequest と HttpServletResponse に変換（キャスト）して、
		 * Webアプリケーションでよく使う機能（セッションの取得など）を使えるようにしている
		 */
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpServletResponse httpResponse = (HttpServletResponse)response;

		//ユーザーのセッション（ブラウザを閉じるまでの一時的な状態を保持する領域）を取得
		HttpSession session = httpRequest.getSession();
		//loginUserという名前で保存されているUserオブジェクトを取り出す
		User user = (User)session.getAttribute("loginUser");

		//判定処理 session領域にログインユーザ情報が入っているか/いないか
		if( user == null) {
			List<String> errorMessages = new ArrayList<String>();
			errorMessages.add("ログインしてください");
			session.setAttribute("errorMessages", errorMessages);
			httpResponse.sendRedirect("./login");
			return;
		}
		//chain.doFilter(...) :「次の処理に進んでください」という命令
		//この命令によって、リクエストは無事にフィルターを通過し、本来の /setting または /edit のServletの処理が実行される
		chain.doFilter(request, response);//サーブレットを実行

	}

	@Override
	public void init(FilterConfig config) {
	}
	@Override
	public void destroy() {
	}
}
