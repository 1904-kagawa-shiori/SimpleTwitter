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

import chapter6.beans.User;
import chapter6.exception.NoRowsUpdatedRuntimeException;
import chapter6.logging.InitApplication;
import chapter6.service.UserService;

//ユーザ情報編集画面のServlet
@WebServlet(urlPatterns = { "/setting" })

public class SettingServlet extends HttpServlet {

	/**
	* ロガーインスタンスの生成
	*/
    Logger log = Logger.getLogger("twitter");

    /**
    * デフォルトコンストラクタ
    * アプリケーションの初期化を実施する。
    */
    public SettingServlet() {
        InitApplication application = InitApplication.getInstance();
        application.init();

    }

    //ユーザ情報編集画面を表示させるためのdoGet
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

	  log.info(new Object(){}.getClass().getEnclosingClass().getName() +
        " : " + new Object(){}.getClass().getEnclosingMethod().getName());

        HttpSession session = request.getSession();
        User loginUser = (User) session.getAttribute("loginUser");

        User user = new UserService().select(loginUser.getId());

        request.setAttribute("user", user);
        request.getRequestDispatcher("setting.jsp").forward(request, response);
    }

    //ユーザ情報編集画面の編集内容を反映させるためのdoPost
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

	  log.info(new Object(){}.getClass().getEnclosingClass().getName() +
        " : " + new Object(){}.getClass().getEnclosingMethod().getName());

        HttpSession session = request.getSession();
        List<String> errorMessages = new ArrayList<String>();

        User user = getUser(request);
        if (isValid(user, errorMessages)) {
            try {
                new UserService().update(user);
            } catch (NoRowsUpdatedRuntimeException e) {
		    log.warning("他の人によって更新されています。最新のデータを表示しました。データを確認してください。");
                errorMessages.add("他の人によって更新されています。最新のデータを表示しました。データを確認してください。");
            }
        }

        if (errorMessages.size() != 0) {
            request.setAttribute("errorMessages", errorMessages);
            request.setAttribute("user", user);
            request.getRequestDispatcher("setting.jsp").forward(request, response);
            return;

        }

        session.setAttribute("loginUser", user);
        response.sendRedirect("./");
    }

    private User getUser(HttpServletRequest request) throws IOException, ServletException {


	  log.info(new Object(){}.getClass().getEnclosingClass().getName() +
        " : " + new Object(){}.getClass().getEnclosingMethod().getName());

        User user = new User();
        user.setId(Integer.parseInt(request.getParameter("id")));
        user.setName(request.getParameter("name"));
        user.setAccount(request.getParameter("account"));
        user.setPassword(request.getParameter("password"));
        user.setEmail(request.getParameter("email"));
        user.setDescription(request.getParameter("description"));
        return user;
    }

    private boolean isValid(User user, List<String> errorMessages) {


	  log.info(new Object(){}.getClass().getEnclosingClass().getName() +
        " : " + new Object(){}.getClass().getEnclosingMethod().getName());

        String name = user.getName();
        String account = user.getAccount();
        //String password = user.getPassword();
        String email = user.getEmail();

        /**実践課題3 アカウントの重複登録を防ぐため、登録前にDBに同じアカウントがないかチェックしにいく
	     * updateする前にisValidメソッドの中のバリデーションでselect
	     * まず、アカウント重複確認用のuser型変数を準備し、ユーザアカウント情報のselect結果を格納できるようにする
	     * UserServiceクラスのインスタンスを作成し、selectメソッドを呼び出す
	     * selectメソッドには、登録しようとしているユーザーアカウント名 (user.getAccount()) が引数として渡されている
	     */
        User checkDuplicateAccounts = new UserService().select(user.getAccount());//selectの()の中身は account でもOK

        if (!StringUtils.isEmpty(name) && (20 < name.length())) {
            errorMessages.add("名前は20文字以下で入力してください");
        }
        if (StringUtils.isEmpty(account)) {
            errorMessages.add("アカウント名を入力してください");
        } else if (20 < account.length()) {
            errorMessages.add("アカウント名は20文字以下で入力してください");
        }

        /**実践課題3 アカウントの重複登録を防ぐため、登録前にDBに同じアカウントがないかチェックしにいく
         * ①checkDuplicateAccountsに該当ユーザアカウントが見つかれば格納されるため、これがnullでない場合はアカウント重複のエラーを出力する
         * ②検索して見つかったアカウントのIDが、現在設定を更新しようとしているユーザー自身のIDと異なることをチェックする★
         * ★なぜ必要？
         * ユーザーが自分のアカウント名を変更しないまま設定保存しようとした場合、
         * 当然checkDuplicateAccountsでそのユーザ自身のアカウントをDBから見つけて返すことになる。(checkDuplicateAccountsはnullじゃない状態)
         * もしcheckDuplicateAccounts.getId() != user.getId()のチェックがなければ、「自分自身のアカウント名が重複している」という誤ったエラーになるため
         */
        if (checkDuplicateAccounts != null && checkDuplicateAccounts.getId() != user.getId()) {
        	errorMessages.add("すでに存在するアカウントです");
        }

        //パスワードに入力がない場合はエラーメッセージを表示させないようにした
        //if (StringUtils.isEmpty(password)) {
        //    errorMessages.add("パスワードを入力してください");
        //}
        if (!StringUtils.isEmpty(email) && (50 < email.length())) {
            errorMessages.add("メールアドレスは50文字以下で入力してください");
        }

        if (errorMessages.size() != 0) {
            return false;
        }
        return true;
    }

}
