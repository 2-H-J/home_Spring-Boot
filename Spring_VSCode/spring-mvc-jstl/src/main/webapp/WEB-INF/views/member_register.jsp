<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style>
	.container{
		width:1200px;
		margin: 50px auto;	
		font-size: 0px;
	}
	input, select, button{
		width: 200px;
		height : 50px;
		font-size: 1.5rem;
		box-sizing: border-box;
	}
	button{
		display : inline-block;
		box-sizing: border-box;
		font-size: 1.5rem;	
		height : 50px;	
	}
</style>
</head>
<body>
<div class="container">
	<form action="/register.do" method="post">
		<input type="text" name="memberId" placeholder="아이디 입력"><br>		
		<input type="password" name="passwd" placeholder="암호 입력"><br>		
		<input type="text" name="name" placeholder="이름 입력"><br>		
		<input type="text" name="age" placeholder="나이 입력"><br>
		<select name="gender">
		<option>M</option>
		<option>F</option>
		</select><br>
		<button>회원정보 추가</button>
	</form>
</div>
</body>
</html>










