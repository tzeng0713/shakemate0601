<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Login</title>
</head>
<body>
	<center>
		<form action="<%=request.getContextPath()%>/login/loginHandler"
			method="post">

			<table border=1>
				<tr>
					<td colspan=2>
						<p align=center>
							輸入<b>(測試登入)</b>:<br> 帳號:<b>tomcat</b><br> 密碼:<b>tomcat</b><br>
					</td>
				</tr>

				<tr>
					<td>
						<p align=right>
							<b>account:</b>
					</td>
					<td>
						<p>
							<input type=text name="account" value="" size=15>
					</td>
				</tr>

				<tr>
					<td>
						<p align=right>
							<b>password:</b>
					</td>
					<td>
						<p>
							<input type=password name="password" value="" size=15>
					</td>
				</tr>


				<tr>
					<td colspan=2 align=center><input type=submit  name= "login" value="login">

					</td>
				</tr>
			</table>
		</form>
	</center>
	<br>

</body>
</html>
