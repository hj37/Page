<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%-- JSTL의 core라이브러리, fmt라이브러리 안의 태그 사용을 위한 선언 --%>    
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
 
 <%-- 톰캣이 프로젝트에 접근하기 위한 주소인? 컨텍스트 주소 얻기 --%>
 <c:set  var="contextPath"  value="${pageContext.request.contextPath}"/>
 
 <%
 	request.setCharacterEncoding("UTF-8");
 %>   
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
		<%--
				FileUpload.java서블릿으로 요청해 파일을 업로드 하기 위해 가상 경로 주소는
				action속성에 작성 하고  파일 업로드시 반드시 enctype="multipart/form-data"로 
				설정 해야 합니다.
		 --%>

		<form action="${contextPath}/upload.do" 
			  method="post" enctype="multipart/form-data">
			
			파일1 : <input type="file" name="file1"> <br>
			파일2 : <input type="file" name="file2"> <br>
			파라미터1 : <input type="text" name="param1"> <br>
			파라미터2 : <input type="text" name="param2"> <br>
			파라미터3 : <input type="text" name="param3"> <br>
			
			<input type="submit" value="업로드요청">
		</form>
</body>
</html>







