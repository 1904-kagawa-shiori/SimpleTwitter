<%@page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>つぶやき編集画面</title>
		<link href="./css/style.css" rel="stylesheet" type="text/css">
	</head>
	<body>
		<div class="main-contents">
			<div class="header">
				<c:if test="${empty loginUser}">
					<a href="login">ログイン</a>
					<a href="signup">登録する</a>
				</c:if>
				<c:if test="${not empty loginUser}">
					<a href="./">ホーム</a>
					<a href="setting">設定</a>
					<a href="logout">ログアウト</a>

					<div class="profile">
						<div class="name"><h2><c:out value="${loginUser.name}"/></h2></div>
						<div class="account">@<c:out value="${loginUser.account}"/></div>
					</div>
				</c:if>
			</div>
			<!--エラーメッセージ表示エリア-->
			<c:if test="${ not empty errorMessages }">
				<div class="errorMessages">
					<ul>
						<c:forEach items="${errorMessages}" var="errorMessage">
							<li><c:out value="${errorMessage}" />
						</c:forEach>
					</ul>
				</div>
				<!-- エラーメッセージがユーザーに表示された後、不要になった時点でセッションから削除する処理 -->
				<c:remove var="errorMessages" scope="session" />
			</c:if>

			<div class="form-area">
				<form action="edit" method="post"><br />
					<input type="hidden" name="messageId" value="${message.id}">
					<label for="message">つぶやき</label>
					<!--textarea の name 属性をservletと整合性合うようにすること -->
					<textarea name="text" cols="100" rows="5" class="tweet-box"><c:out value="${message.text}"/></textarea>
					<input type="submit" value="更新">(140文字まで)
				</form>
				<a href="./">戻る</a>
			</div>
			<div class="copyright"> Copyright(c)kagawa.shiori</div>

		</div>
	</body>
</html>