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


	<c:choose>
		<%--회원 삭제에 성공 했다면 --%>
		<c:when test='${msg=="deleted"}'>
			<script type="text/javascript">
				window.onload = function(){
					alert("회원정보를 삭제 했습니다.");
				}	
			</script>
		</c:when>
	
		<%--회원 추가에 성공 했다면 --%>
		<c:when test='${requestScope.msg == "addMember"  }'>
			<script type="text/javascript">
				window.onload = function(){
					window.alert("회원을 등록했습니다.");	
				}		
			</script>
		</c:when>
		<%--수정에 성공 했다면 --%>
		<c:when test='${requestScope.msg == "modified" }'>
			<script type="text/javascript">
				window.onload = function(){
					window.alert("회원정보를 수정했습니다.");	
				}		
			</script>
		</c:when>	
	</c:choose>


</head>
<body>
<%-- 클라이언트가 웹브라우저 주소창에 입력한 전체 주소중..  컨텍스트 경로 주소 만 얻기
		 http://localhost:8090/pro13/member/listMembers.do
		 /pro13
	   --%>
	<c:set var="contextPath" value="${pageContext.request.contextPath}"/>


	<p class="cls1">회원정보</p>
	<table border="1" align="center" >
		<tr align="center" bgcolor="lightgreen">
			<td width="7%"><b>아이디</b></td>
			<td width="7%"><b>비밀번호</b></td>
			<td width="7%"><b>이름</b></td>
			<td width="7%"><b>이메일</b></td>
			<td width="7%"><b>가입일</b></td>		
			<td width="7%"><b>수정</b></td>
			<td width="7%"><b>삭제</b></td>
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
			<%-- 수정, 삭제를 위해 회원의 ID를 전달해 수정과 삭제 요청을 합니다. --%>
			<td><a href="${contextPath}/member/modMemberForm.do?id=${memberVO.id}">수정</a></td>
			<td><a href="${contextPath}/member/delMember.do?id=${memberVO.id}">삭제</a></td>
		</tr>
		</c:forEach>
	</c:when>
</c:choose>	
	</table>
	
	<a href="${contextPath}/member/memberForm.do">
		<p class="cls2">회원가입하기</p>
	</a>
</body>
</html>










