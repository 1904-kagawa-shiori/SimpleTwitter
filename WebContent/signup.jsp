<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>ユーザー登録</title>
		<link href="./css/style.css" rel="stylesheet" type="text/css">
	</head>
	<body>
		<div class="main-contents">

			<!-- バリデーションエラーなどのエラーメッセージを表示するためのエリア -->
			<c:if test="${ not empty errorMessages }">
				<div class="errorMessages">
					<ul>
						<c:forEach items="${errorMessages}" var="errorMessage">
							<li><c:out value="${errorMessage}" />
						</c:forEach>
					</ul>
				</div>
			</c:if>

			<!-- ユーザが入力するエリア。登録ボタンを押すと、入力値をパラメータとしてServletへ送信 -->
			<!-- ①action属性で、呼び出すServletを指定 ②method属性で、呼び出すメソッド(doGetもしくはdoPost)を指定 -->
			<form action="signup" method="post"><br />
				<label for="name">名前</label><!--labelタグで、そのラベルがどの入力項目に対応したラベルなのかを示している-->
				<input name="name" id="name" />（名前はあなたの公開プロフィールに表示されます）<br />

				<label for="account">アカウント名</label>
				<!-- ★"account"という名前(key)で入力値を送信している -->
				<input name="account" id="account" /> <br />

				<label for="password">パスワード</label>
				<input name="password" type="password" id="password" /> <br />

				<label for="email">メールアドレス</label>
				<input name="email" id="email" /> <br />

				<label for="description">説明</label>
				<textarea name="description" cols="35" rows="5" id="description"></textarea> <br />
				<input type="submit" value="登録" /> <br />
				<a href="./">戻る</a>
			</form>

			<div class="copyright">Copyright(c)kagawa.shiori</div>

		</div>
	</body>
</html>