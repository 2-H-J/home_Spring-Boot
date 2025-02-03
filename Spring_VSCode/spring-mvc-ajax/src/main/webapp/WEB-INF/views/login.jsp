<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script>
	window.onload = () => {
		document.querySelector('button').onclick = (e) => {
			let id = document.querySelector('#id').value;
			let pass = document.querySelector('#pass').value;
			
			fetch("/login",{
				method : "POST",
				headers : {
					"Content-Type" : "application/json" //json으로 데이터 전송시 헤더 설정
				},
				//자바스크립트 객체를 json 형태 문자열로 변경
				body : JSON.stringify({
					id : id,
					pass: pass
				})
			}).then(response => response.json()).then(result =>{
				console.log(result);
			});
		}
	}
</script>
</head>
<body>
	<input type="text" name="id" id="id"><br>
	<input type="password" name="pass" id="pass"><br>
	<button type="button">로그인</button>
</body>
</html>





