<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>회원가입</title>
    <style>
        body {
            font-family: Arial, sans-serif;
        }
        .form-container {
            width: 400px;
            margin: 50px auto;
            padding: 20px;
            border: 1px solid #ccc;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
        .form-container h1 {
            text-align: center;
            margin-bottom: 20px;
        }
        .form-group {
            margin-bottom: 15px;
        }
        .form-group label {
            display: block;
            margin-bottom: 5px;
        }
        .form-group input {
            width: 100%;
            padding: 8px;
            box-sizing: border-box;
        }
        .form-group button {
            width: 100%;
            padding: 10px;
            background-color: #28a745;
            color: #fff;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }
        .form-group button:hover {
            background-color: #218838;
        }
    </style>
</head>
<body>
    <div class="form-container">
        <h1>수정페이지</h1>
        <form action="/member/update" method="post">
            <div class="form-group">
                <label for="userId">아이디</label>
                <input type="text" id="userId" name="id" value="${member.id }" readonly>
            </div>
            <div class="form-group">
                <label for="password">암호</label>
                <input type="password" id="password" name="password" required>
            </div>
            <div class="form-group">
                <label for="userName">이름</label>
                <input type="text" id="userName" name="userName" value="${member.userName}" required>
            </div>
            <div class="form-group">
                <label for="nickname">닉네임</label>
                <input type="text" id="nickname" name="nickName" value="${member.nickName}" required>
            </div>
            <div class="form-group">
                <button type="submit">수정</button>
            </div>
        </form>
    </div>
</body>
</html>
