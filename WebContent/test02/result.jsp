<%@page import="java.net.URLEncoder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<%
		//요청한 데이터 한글 처리
		request.setCharacterEncoding("UTF-8");

		//요청한 주소중.. 컨텍스트 주소 알아내기
		String contextPath = request.getContextPath();
	
		//다운로드할 요청한 파일명 얻기
		//다운로드할 파일명이 한글일 경우 아래쪽의 <a>태그를 클릭했을때 다운로드 요청을 할수 없으므로
		//인코딩 방식을 UTF-8로 설정후 얻는다.
		String file1 = URLEncoder.encode(request.getParameter("param1"), "UTF-8");
		String file2 = URLEncoder.encode(request.getParameter("param2"), "UTF-8");
	%>

	파일 내려 받기 1 :  <br>
	<a href="<%=contextPath%>/download.do?fileName=<%=file1%>">파일 내려받기</a> <br>

	파일 내려 받기 2 :  <br>
	<a href="<%=contextPath%>/download.do?fileName=<%=file2%>">파일 내려받기</a> <br>

</body>
</html>











