<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style>
.container {
	width: 1200px;
	margin: 0 auto;
}

h2, form {
	text-align: center;
}

ul {
	list-style-type: none;
	display: flex;
	flex-flow: row nowrap;
}

li {
	width: 200px;
	text-align: center;
}
</style>
</head>
<body>
	<h2>자동차 관리 프로그램</h2>

	<form action="/insert" method="post">
		<input type="text" name="carId" placeholder="자동차 모델번호 입력"> <input
			type="text" name="carName" placeholder="자동차 모델명 입력"> <input
			type="text" name="carMaker" placeholder="자동차 제조사 입력"> <input
			type="text" name="carMakeYear" placeholder="자동차 제조년도 입력"> <input
			type="text" name="carPrice" placeholder="자동차 금액 입력">
		<button>등록하기</button>
	</form>

	<hr>
	<div class="container">
		<c:forEach var="car" items="${requestScope.list }">
			<ul>
				<li>${car.carId }</li>
				<li>${car.carName }</li>
				<li>${car.carMaker }</li>
				<li>${car.carMakeYear }</li>
				<li>${car.carPrice }</li>
			</ul>
		</c:forEach>
	</div>

</body>
</html>