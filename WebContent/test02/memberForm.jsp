<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%-- JSTL 의 core 라이브러리 태그들 사용을 위한 선언 --%>    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    
    
<%--컨텍스트 주소 얻기 --%>  
<%--   
<%
	String cp = request.getContextPath();
%>
 --%>
 <c:set var="contextPath"  value="${pageContext.request.contextPath}"/>    
    
    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원 가입창</title>
</head>
<body>
	<!-- 회원가입창에서 회원정보를 입력한 후  MemberController.java서블릿으로 가입 요청 전송하도록  
	     memberForm.html을 작성합니다. -->
	<form action="${contextPath}/member/addMember.do" method="post">
	
		<h1 style="text-align: center;">회원 가입창</h1>
	  
	  	<table  align="center">
	  		<tr>
	  			<td width="200"><p align="right">아이디</p></td>
	  			<td width="400"><input type="text" name="id"></td>
	  		</tr>
	  		<tr>
	  			<td width="200"><p align="right">비밀번호</p></td>
	  			<td width="400"><input type="password" name="pwd"></td>
	  		</tr>
	  		<tr>
	  			<td width="200"><p align="right">이름</p></td>
	  			<td width="400"><input type="text" name="name"></td>
	  		</tr>
	  		<tr>
	  			<td width="200"><p align="right">이메일</p></td>
	  			<td width="400"><input type="text" name="email"></td>
	  		</tr>
	  		<tr>
	  			<td width="200"><p>&nbsp;</p></td>
	  			<td width="400">
	  				<input type="submit" value="가입하기">
	  				<input type="reset" value="다시입력">
	  			</td>
	  		</tr>
	  	</table>
	</form>

</body>
</html>



    