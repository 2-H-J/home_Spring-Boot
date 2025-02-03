<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<h2>로그인 결과 페이지</h2>
	${sessionScope.id }, ${sessionScope.pass }<br>
	<c:if test="${sessionScope.id == 'admin' }">
		<p>관리자로 로그인 하셨습니다</p>
	</c:if>
	<a href="/loginView.do">로그인 페이지로 이동</a>
</body>
</html>