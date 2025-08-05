<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>簡易Twitter</title>
		<link href="./css/style.css" rel="stylesheet" type="text/css">
	</head>
<body>
	<div class="main-contents">
		<div class="header">
			<!-- ログインユーザ情報がなければ登録画面を出し、あればホーム画面表示させる -->
			<c:if test="${ empty loginUser }">
				<a href="login">ログイン</a>
				<!-- a href="signup"は、今いるリソースから相対位置で「～/signup」というURLにアクセスすることを指す -->
				<a href="signup">登録する</a>
			</c:if>
			<c:if test="${ not empty loginUser }">
				<a href="./">ホーム</a>
				<a href="setting">設定</a>
				<a href="logout">ログアウト</a>
			</c:if>
		</div>
		<!--日付の絞り込み-->
		<div class="filter-by-date">
			<form action="./" method="get">
				<span>日付</span>
				<input type="date" name="startDate" value="${startDate}"> ～
				<input type="date" name="endDate" value="${endDate}">
				<input type="submit" value="絞り込み">
			</form>
		</div>

		<!-- ログインユーザ情報を表示させるための処理 -->
		<!-- 「loginUser｣が空でない場合にユーザー情報を表示させる -->
		<c:if test="${not empty loginUser}">
			 <div class="profile">
			 	<!-- 画面で、セッションに格納されたユーザ情報を参照して出力する -->
				<div class="name"><h2><c:out value="${loginUser.name}" /></h2></div>
				<div class="account">@<c:out value="${loginUser.account}" /></div>
				<div class="description"><pre><c:out value="${loginUser.description}" /></pre></div>
			</div>
		</c:if>

		<!-- テキストエリアとサブミット用のボタンを追加 -->
		<c:if test="${not empty errorMessages}">
			<div class="errorMessages">
				<ul>
					<c:forEach items="${errorMessages}" var="errorMessage">
						<li><c:out value="${errorMessage}" /></li>
					</c:forEach>
				</ul>
			</div>
			<c:remove var="errorMessages" scope="session" />
		</c:if>

		<div class="form-area">
			<c:if test="${ isShowMessageForm }">
				<!-- つぶやきをPOSTするURL（action属性）にはmessageを指定 -->
				 <form action="message" method="post">
				 	いま、どうしてる？<br />
				 	<textarea name="text" cols="100" rows="5" class="tweet-box"></textarea>
					<br />
					<input type="submit" value="つぶやく">（140文字まで）<br/>
				</form>
			 </c:if>
		</div>

		<!-- つぶやき、つぶやきへの返信を表示する -->
		<div class="messages">
			<c:forEach items="${messages}" var="message">
				<!-- つぶやきを表示するためのコード -->
				<div class="message">
					<div class="account-name">
						<!-- ユーザアカウント名のリンククリックで、各ユーザ毎のつぶやき表示画面へ遷移できるよう修正 -->
						<!--リンクのURLを設定。 ./?user_id=　は現在のディレクトリを基準にuser_idというクエリパラメータを持つURLを指す -->
						<!-- messageオブジェクトからuserIdプロパティの値を取得し、その値をURLのuser_idパラメータとして出力する -->
						<!-- c:out valueの行：リンクの表示テキストを設定。messageオブジェクトからaccountプロパティの値を取得し、それをリンクとして表示する -->
						<!-- 没コード→span class="account"><c:out value="${message.account}" /></span> -->
						<span class="account">
							<a href="./?user_id=<c:out value="${message.userId}"/> ">
								<c:out value="${message.account}" />
							</a>
						</span>
						<span class="name"><c:out value="${message.name}" /></span>
					</div>
					<div class="text">
						<pre><c:out value="${message.text}" /></pre>
					</div>
					<div class="date"><fmt:formatDate value="${message.createdDate}" pattern="yyyy/MM/dd HH:mm:ss" /></div>
					<!--ログインユーザと対象メッセージユーザが一緒の場合、つぶやきの編集と削除を可能にする-->
					<c:if test="${loginUser.id == message.userId}">
						<!-- つぶやきの編集 -->
						<div class="edit-button">
							<form action="edit" method="get"><!--actionにはurlPatternsで設定したのと同じくeditを指定する必要がある -->
								<input type="hidden" name="messageId" value="${message.id}">
								<!-- ボタン -->
								<input type="submit" value="編集" />
							</form>
						</div>
						<!-- つぶやきの削除 -->
						<div class="delete-button">
							<form action="deleteMessage" method="post">
								<input type="hidden" name="messageId" value="${message.id}">
								<!-- ボタン -->
								<input type="submit" value="削除"/><br/>
							</form>
						</div>
					</c:if>
				</div>
				<!-- つぶやきの返信を表示するためのコード -->
				<c:forEach items="${comments}" var="comment">
					<!-- つぶやきの返信のmessageIdと、つぶやきのidが一緒の場合、コメント表示 -->
					<c:if test="${comment.messageId == message.id}">
						<div class="comments">
							<div class="account-name">
								<span class="account"><c:out value="${comment.account}" /></span>
								<span class="name"><c:out value="${comment.name}" /></span>
							</div>
							<div class="text">
								<pre><c:out value="${comment.text}" /></pre>
							</div>
							<div class="date">
								<fmt:formatDate value="${comment.createdDate}" pattern="yyyy/MM/dd HH:mm:ss" />
							</div>
						</div>
					</c:if>
				</c:forEach>
				<!--つぶやきへの返信-->
				<div class="comment-form-area">
					<c:if test="${ not empty loginUser }">
						<form action="comment" method="post">
							返信<br />
							<!--紐づいているつぶやきのIDをServletに送る-->
							<input type="hidden" name="messageId" value="${message.id}">
							<!-- name属性の値(text)が、servlet側で request.getParameter()の()内で取得する値に対応する  -->
							<textarea name="text" cols="100" rows="5" class="tweet-box"></textarea>
							<!--返信ボタン-->
							<input type="submit" value="返信">（140文字まで）<br/>
						</form>
					</c:if>
					<br/>
				</div>
			</c:forEach>
		</div>
		<div class="copyright"> Copyright(c)kagawa.shiori</div>
	</div>
</body>
</html>