package sec01.ex01;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//클라이언트가 웹브라우저의 주소창에 주소를 입력하는데..
//주소 : http://localhost:8090/pro13/mem.do
//모든회원정보를 조회(검색)해줘? 라는 요청주소로 서블릿을 요청함.

@WebServlet("/mem.do")
public class MemberController extends HttpServlet {

	MemberDAO memberDAO;
	
	//역할 :  클라이언트가 웹브라우저로 서블릿요청할떄.. MemberController서블릿 class를 
	//       톰캣 메모리에 올릴때 호출되는 메소드 
	@Override
	public void init() throws ServletException {
		//init()메소드 내부에서 MemberDAO객체를 생성(초기화)함
		memberDAO = new MemberDAO();
	}
	
	//GET방식으로 요청이 들어오면 호출되는 콜백 메소드
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
				   throws ServletException, IOException {
		
		doHandle(request, response);//다시 doHandle메소드 호출시 request와 response전달
		
	}
	//POST방식으로 요청이 들어오면 호출되는 콜백 메소드
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
				   throws ServletException, IOException {
		
		doHandle(request, response);//다시 doHandle메소드 호출시 request와 response전달
	}
	//클라이언드가 GET방식 또는 POST방식으로 요청이 들어오면 호출되는 일반 메소드	
	protected void doHandle(HttpServletRequest request, HttpServletResponse response) 
				   throws ServletException, IOException {
		
		//request영역에 데이터가 존재하면 한글처리
		request.setCharacterEncoding("UTF-8");
		//클라이언트의 웹브라우저로 내보낼(응답할,출력할) 응답데이터 형식 MIME-TYPE을 response객체에 설정
		response.setContentType("text/html;charset=utf-8");
		
		//MemberDAO의 listMembers()메소드를 호출하여 
		//요청주소 http://localhost:8090/pro13/mem.do에 대해..
		//DB로부터 검색한 회원정보를 하나의 회원정보씩 MemberVO객체에 각각 저장한후
		//다시~ MemberVO객체들을 ArrayList배열에 추가한후  ArrayList를 반환 받는다.
		List<MemberVO> membersList = memberDAO.listMembers();
		
		//View(listMembers.jsp)페이지로 응답할 데이터(검색한 회원정보들 ArrayList)를 전달하여
		//출력(응답) 해야하는데... 그러기 위해서 임시로 저장할 공간은? request객체 메모리 영역이므로
		//request객체에 ArrayList를 저장함.
		request.setAttribute("membersList", membersList);
		
		//디스패치 방식으로 포워딩(재요청해서이동) (listMembers.jsp로 ........view로 .........)
		 RequestDispatcher dispatche = 
				 request.getRequestDispatcher("/test01/listMembers.jsp");
		//실제 포워딩
		dispatche.forward(request, response);
		
	}//doHandle메소드 끝
		
}//MemberController클래스 끝





