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
	<h2>전체 회원 정보</h2>
	<table border="1">
		<thead>
			<tr>
				<th>아이디</th>
				<th>비밀번호</th>
				<th>이름</th>
				<th>닉네임</th>
				<th>비고</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="member" items="${requestScope.list }">
				<tr>
					<td>${member.id }</td>
					<td>${member.password }</td>
					<td>${member.userName }</td>
					<td>${member.nickName }</td>
					<td><a href="/member/delete/${member.id}">삭제</a>
						/
						<a href="/member/${member.id}">수정</a>					
					</td>
				</tr>
			</c:forEach>		
		</tbody>
	</table>
</body>
</html>


