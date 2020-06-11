<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>파일 다운로드 요청하기</title>
</head>
<body>
<%--
	 다운로드할 파일이름을 hidden태그의 value속성의 값으로 설정해서 result.jsp에 다운로드 요청함
 --%>
	<form action="result.jsp" method="post">
		
		<input type="hidden" name="param1" value="파일1.jpg" /> <br>
		<input type="hidden" name="param2" value="파일2.jpg" /> <br>
		
		<input type="submit" value="이미지 다운로드 요청">
		
	</form>

</body>
</html>