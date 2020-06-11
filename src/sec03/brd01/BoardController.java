package sec03.brd01;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//클라이언트가 웹브라우저 주소창에 http://localhost:8090/pro13/board/listArticles.do 
//주소를 입력하여 DB에 저장된 모든 글을 검색하라는 요청이 들어 왔을때 호출되는 서블릿이다.

//@WebServlet("/board/*")
public class BoardController extends HttpServlet {

	BoardService boardService;
	ArticleVO articleVO;
	
	//서블릿 초기화시 BoardService객체를 생성 합니다.
	@Override
	public void init() throws ServletException {
		boardService = new BoardService();
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
					throws ServletException, IOException {
		doHandle(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
					throws ServletException, IOException {
		doHandle(request, response);
	}
	
	protected void doHandle(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {

		//MVC 중 View이름을 저장할 변수
		String nextPage = "";
		//한글처리
		request.setCharacterEncoding("utf-8");
		//클라이언트의 웹브라우저로 응답할 데이터 유형 설정
		response.setContentType("text/html;charset=utf-8");
		
		//요청 URL중 2단계 요청 주소를 알아내온다
		String action = request.getPathInfo();  //  /listArticles.do 
		System.out.println("action : " + action);
		
		List<ArticleVO> articlesList = null;
		
		if(action == null) {//요청명이 null일떄...
			
			//모든 글정보를 검색해서 가져 오기 위해 BoardService객체의 listArticles()메소드 호출!
			articlesList = boardService.listArticles();
			
			//검색한 글정보(응답할 데이터)를  VIEW페이지(listArticles.jsp)로 보내서 출력하기 위해
			//임시로 request저장소에 저장 하여 유지 시킨다
			request.setAttribute("articlesList", articlesList);
	
			nextPage = "/board01/listArticles.jsp";		
		
		}else if(action.equals("/listArticles.do")) {// DB에 전체 글을 조회 하라는 요청이 들어오면
			
			//모든 글정보를 검색해서 가져 오기 위해 BoardService객체의 listArticles()메소드 호출!
			articlesList = boardService.listArticles();
			
			//검색한 글정보(응답할 데이터)를  VIEW페이지(listArticles.jsp)로 보내서 출력하기 위해
			//임시로 request저장소에 저장 하여 유지 시킨다
			request.setAttribute("articlesList", articlesList);
	
			nextPage = "/board01/listArticles.jsp";	
			
		}
		
		//디스패치 방식으로 포워딩 (재요청)
		RequestDispatcher dispatch = request.getRequestDispatcher(nextPage);
		dispatch.forward(request, response);
	
	}	
}






