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

import chapter6.beans.User;
import chapter6.logging.InitApplication;
import chapter6.service.UserService;

//top.jspの、URLが"login"に対応するServlet
@WebServlet(urlPatterns = { "/login" })

public class LoginServlet extends HttpServlet {

    /**
    * ロガーインスタンスの生成
    */
    Logger log = Logger.getLogger("twitter");

    /**
    * デフォルトコンストラクタ
    * アプリケーションの初期化を実施する。
    */
    public LoginServlet() {
        InitApplication application = InitApplication.getInstance();
        application.init();
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

	  log.info(new Object(){}.getClass().getEnclosingClass().getName() +
        " : " + new Object(){}.getClass().getEnclosingMethod().getName());

        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

	  log.info(new Object(){}.getClass().getEnclosingClass().getName() +
        " : " + new Object(){}.getClass().getEnclosingMethod().getName());

        String accountOrEmail = request.getParameter("accountOrEmail");
        String password = request.getParameter("password");

        //Serviceを経由してUserオブジェクトを取得
        User user = new UserService().select(accountOrEmail, password);
        if (user == null) {
            List<String> errorMessages = new ArrayList<String>();
            errorMessages.add("ログインに失敗しました");
            request.setAttribute("errorMessages", errorMessages);
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession();
        //取得したUserオブジェクトをセッションにセット
        //ログイン時に、ユーザ情報をsessionに確認しておく
        session.setAttribute("loginUser", user);
        //トップ画面へ遷移
        response.sendRedirect("./");
    }

}
