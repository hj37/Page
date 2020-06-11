<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%--JSTL의 core라이브러리, fmt라이브러리 태그들을 사용하기 위한 선언 --%>    
<%@taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c" %>    
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt" %>    

<%
	//MemberController서블릿으로 부터 전달받은 request객체 메모리 영역에는
	//DB로 부터 검색한 모든 회원정보(ArryList)가 저장되어 있다.
	
	//한글처리
	request.setCharacterEncoding("UTF-8");
%>
    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>

<style type="text/css">
	.cls1{
		font-size: 40px;
		text-align: center;
	}
	.cls2{
		font-size: 20px;
		text-align: center;
	}
</style>
</head>
<body>
	<p class="cls1">회원정보</p>
	<table border="1" align="center" >
		<tr align="center" bgcolor="lightgreen">
			<td width="7%"><b>아이디</b></td>
			<td width="7%"><b>비밀번호</b></td>
			<td width="7%"><b>이름</b></td>
			<td width="7%"><b>이메일</b></td>
			<td width="7%"><b>가입일</b></td>		
		</tr>
<c:choose>
	<%--request객체영역에 검색한 회원정보(ArrayList)가 존재 하지 않으면? --%>
	<c:when test="${requestScope.membersList == null}">
		<tr>
			<td colspan="5"><b>등록된 회원이 없습니다.</b></td>
		</tr>
	</c:when>
	<%--검색한 회원정보 가 존재 하면? --%>
	<c:when test="${membersList != null}">
		<c:forEach  var="memberVO"  items="${membersList}">
		<tr align="center">
			<td>${memberVO.id}</td>
			<td>${memberVO.pwd }</td>
			<td>${memberVO.name }</td>
			<td>${memberVO.email }</td>
			<td>${memberVO.joinDate }</td>
		</tr>
		</c:forEach>
	</c:when>
</c:choose>	
	</table>
	<%-- 클라이언트가 웹브라우저 주소창에 입력한 전체 주소중..  컨텍스트 경로 주소 만 얻기
		 http://localhost:8090/pro13/member/listMembers.do
		 /pro13
	   --%>
	<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
	
	<a href="${contextPath}/member/memberForm.do">
		<p class="cls2">회원가입하기</p>
	</a>
</body>
</html>










