<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title>Register</title>
</head>
<body>

<h2>Регистрация</h2>

<form method="post">

    <label>Login:</label><br/>
    <input type="text" name="login" required/><br/><br/>

    <label>Password:</label><br/>
    <input type="password" name="password" required/><br/><br/>

    <label>Email:</label><br/>
    <input type="email" name="email" required/><br/><br/>

    <button type="submit">Register</button>

</form>

<br/>

<a href="login">Уже есть аккаунт? Войти</a>

<!-- Ошибка -->
<p style="color:red;">
    ${error}
</p>

</body>
</html>