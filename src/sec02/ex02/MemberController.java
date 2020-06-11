package sec02.ex02;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//클라이언트가 웹브라우저를 열어 주소창에 요청하는 주소
//1. DB에 저장되어 있는 모든 회원정보를 조회 해줘~
//   http://localhost:8090/pro13/member/listMembers.do로 요청 

//2. 회원가입 디자인 페이지로 이동 시켜 줘~~
//  <a href="/pro13/member/memberForm.do">
//	http://localhost:8090/pro13/member/memberForm.do로 요청

//3. memberForm.jsp에서 가입할 회원정보를 입력 한 후  DB에 INSERT 시켜줘~ 라는 요청
//  http://localhost:8090/pro13/member/addMember.do로 요청 


@WebServlet("/member/*") //웹브라우저에서 요청시  두단계로 요청이 이루어집니다.
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
		
		//request객체의 getPathInfo()메소드를 호출하여 클라이언트가 요청한 주소URL정보를 얻는다
		String action = request.getPathInfo();
		System.out.println(action);  //    /listMembers.do  <--- 모든 회원정보 조회 요청주소
									 //    /memberForm.do <--회원가입 작성페이지 이동 요청주소
									 //    /addMember.do <-- DB에 입력한 회원정보 추가 요청 주소 
									 //	    /listMembers.do  <--- 모든 회원정보 조회 요청주소
									 //    /modMemberForm.do <---회원정보 수정을 위해 먼저 ID에 해당하는 회원을 조회후 보여줘~라는 요청주소
									//    /modMember.do <-- 회원정보 수정 요청주소
									//   /delMember.do <---회원정보 삭제 요청 주소 
		
		
		
		String nextPage = null; //<---- 뷰 경로를 저장할 변수 
		
		//action변수의 값에 따라  if문을 분기해서 요청한 작업을 수행 하는데..

		//만약 action변수의 값이 null이거나   /listMembers.do 인경우에 
		//모든 회원정보 조회 요청이 들어왔을때 를 말함
		if(action == null || action.equals("/listMembers.do")) {
			
			List<MemberVO> membersList = memberDAO.listMembers();
			
			//View(listMembers.jsp)페이지로 응답할 데이터(검색한 회원정보들 ArrayList)를 전달하여
			//출력(응답) 해야하는데... 그러기 위해서 임시로 저장할 공간은? request객체 메모리 영역이므로
			//request객체에 ArrayList를 저장함.
			request.setAttribute("membersList", membersList);
		
			//검색한 회원정보들(ArrayList)의 데이터들을 보여줄 VIEW페이지 주소 설정 
			nextPage = "/test03/listMembers.jsp";
		
		//DB에 입력한 회원정보를 추가 시켜줘~~ 라는 요청 주소를 받았을때..
		}else if (action.equals("/addMember.do")) {
			
			//요청한 값 얻기 (입력한 회원정보들 얻기 ) 
			String id = request.getParameter("id");
			String pwd = request.getParameter("pwd");
			String name = request.getParameter("name");
			String email = request.getParameter("email");
			
			//MemberVO에 임시로 저장
			MemberVO memberVO = new MemberVO(id, pwd, name, email);
			
			//요청한 회원정보를 DB의 테이블에 INSERT하기 위한 메소드 호출
			memberDAO.addMember(memberVO);
			
			//회원추가 에성공 했다면... 성공메세지를 request에 저장
			request.setAttribute("msg", "addMember");
			
			//DB에 회원 등록 후  다시 모든 회원정보를 검색요청(MemberController로)을 위한 요청 주소를 저장 
			nextPage = "/member/listMembers.do";
			
		//회원가입 입력화면 디자인페이지로 이동시켜줘~라는 요청주소를 받았을때	
		}else if(action.equals("/memberForm.do")) {
			
			//회원가입창 화면 VIEW페이지 주소 설정
			nextPage = "/test03/memberForm.jsp";
		
		//listMembers.jsp페이지에서 수정 링크를 클릭 했을때..
		//컨트롤러에 회원정보 수정창 요청시 ID로 회원정보를 조회한 후 수정창으로 포워딩 합니다.
		}else if(action.equals("/modMemberForm.do")) {
			
			String id = request.getParameter("id");//수정할 회원 id얻기
			
			//회원정보 수정창을 요청하면서 전송된 ID를 이용해 수정 전 회원정보를 검색해 옵니다.
			MemberVO memInfo = memberDAO.findMember(id);
			
			//request에 바인딩하여 회원정보 수정창에 수정하기 전의 회원정보를 전달 합니다.
			request.setAttribute("memInfo", memInfo);
			
			//회원정보 수정창 페이지로 포워딩 하기 위한 URL지정
			nextPage = "/test03/modMemberForm.jsp";
			
		//회원정보수정창(modMemberForm.jsp)에서 회원수정 정보를 입력후 수정하기 버튼을 클릭했을때..
		//http://localhost:8090/pro13/member/modMember.do 주소로 
		//DB 테이블의 데이터 수정 요청이 들어오면....
		}else if(action.equals("/modMember.do")) {
			//회원정보 수정창에서 수정시 입력한 정보를 가져온후
			//수정할 회원정보를 MemberVO객체의 각변수에 저장
			MemberVO memberVO = new MemberVO(request.getParameter("id"), 
						                     request.getParameter("pwd"), 
						                     request.getParameter("name"),
						                     request.getParameter("email"));
			
			//DB의 회원 테이블의 데이터 수정 명령
			memberDAO.modMember(memberVO); //UPDATE구문 만들기 위해 전달
			
			//회원정보 수정후 회원목록창(listMembers.jsp)으로 수정작업 완료 메세지를 전달하기위해
			//request내장객체 영역에 완료 메세지를 저장(바인딩)함
			request.setAttribute("msg", "modified");
			
			//수정후 DB로부터 모든회원정보를 검색하여 다시  회원목록창(listMembers.jsp)으로 이동하기위해
			//DB로부터 모든회원정보를 검색 하는 요청주소를 nextPage변수에 저장
			nextPage = "/member/listMembers.do"; //MemberController.java서블릿으로 재요청할 주소
			
			
		//삭제할 회원 ID를 SQL문으로 전달해 t_member테이블의 회원정보 삭제 요청이 들어 왔을때...	
		}else if(action.equals("/delMember.do")) {
			//삭제할 회원 ID 얻기
			String id = request.getParameter("id");
			
			//삭제할 회원 ID를 전달하여 DB에 저장된 회원정보 삭제
			memberDAO.delMember(id);
			
			//삭제후 listMember.jsp로 삭제작업 완료 메세지를 전달 하기 위해
			//응답할 메시지를 request에 저장
			request.setAttribute("msg", "deleted");
			
			//삭제후 모든 회원을 다시 검색하기 위한 주소를 저장
			nextPage = "/member/listMembers.do";
			
		}
						
		//디스패치 방식으로 포워딩(재요청해서이동) (view로 )
		 RequestDispatcher dispatche = 
				 request.getRequestDispatcher(nextPage);
		//실제 포워딩
		dispatche.forward(request, response);
		
	}//doHandle메소드 끝
		
}//MemberController클래스 끝





