package sec03.brd02;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

//1.클라이언트가 웹브라우저 주소창에 http://localhost:8090/pro13/board/listArticles.do 
//주소를 입력하여 DB에 저장된 모든 글을 검색하라는 요청이 들어 왔을때 호출되는 서블릿이다.

//2.클라이언트가 listArticles.jsp화면에서  글쓰기 <a>를 클릭했을때(/articleForm.do)
//  글쓰기창을 웹브라우저 화면에 표시하고,
//  action변수의 값이  /addArticle.do이면 다음과정으로 새글을 DB에 추가 합니다.
//  upload()메소드를 호출해 글쓰기 창에서 전송된 글 관련정보를 Map에  key/value 쌍으로 저장합니다.
//  파일을 첨부한 경우  먼저 파일 이름을 Map에 저장한 후 첨부한 파일 을  저장소에 업로드 합니다.
//  upload()메소드를 호출한 후에는 반환한 Map에서 새글 정보를 가져 옵니다.
//  그런다음 service클래스의 addArticle()메소드 호출시 인자로 새글 정보를 전달하면 새글이 등록됩니다.


// 글쓰기 화면으로 이동 시켜줘~ 라는 요청주소를 받은 서블릿     /board/articleForm.do

// articleForm.jsp페이지에서 새로 추가시킬 글내용을 입력받고  입력받은 내용을 DB에 추가 시켜줘~
// /board/addArticle.do

//@WebServlet("/board/*")
public class BoardController extends HttpServlet {

	
	//글에 첨부한 이미지 저장 위치를 상수로 선언합니다.
	private static String ARTICLE_IMAGE_REPO = "C:\\board\\article_image";
	
	BoardService boardService;
	ArticleVO articleVO;
	
	//서블릿 초기화시 BoardService객체를 생성 합니다.
	@Override
	public void init() throws ServletException {
		boardService = new BoardService();
		articleVO = new ArticleVO();
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
												//  /articleForm.do   <-- 글쓰기창 이동 요청주소
												//  /addArticle.do <-- 글쓰기 요청
							
		
		System.out.println("action : " + action);
		
		List<ArticleVO> articlesList = null;
		
		if(action == null) {//요청명이 null일떄...
			
			//모든 글정보를 검색해서 가져 오기 위해 BoardService객체의 listArticles()메소드 호출!
			articlesList = boardService.listArticles();
			
			//검색한 글정보(응답할 데이터)를  VIEW페이지(listArticles.jsp)로 보내서 출력하기 위해
			//임시로 request저장소에 저장 하여 유지 시킨다
			request.setAttribute("articlesList", articlesList);
	
			nextPage = "/board02/listArticles.jsp";		
		
		}else if(action.equals("/listArticles.do")) {// DB에 전체 글을 조회 하라는 요청이 들어오면
			
			//모든 글정보를 검색해서 가져 오기 위해 BoardService객체의 listArticles()메소드 호출!
			articlesList = boardService.listArticles();
			
			//검색한 글정보(응답할 데이터)를  VIEW페이지(listArticles.jsp)로 보내서 출력하기 위해
			//임시로 request저장소에 저장 하여 유지 시킨다
			request.setAttribute("articlesList", articlesList);
	
			nextPage = "/board02/listArticles.jsp";	
			
		}else if(action.equals("/articleForm.do")) {//새글을 DB에 추가 하기 위해
													//글을 작성할수 있는 화면으로 이동시켜줘~라는 
													//요청주소를 받았을때
			//재요청하여 이동할 뷰(V) 의 주소 저장
			nextPage = "/board02/articleForm.jsp";
			
			
		}else if(action.equals("/addArticle.do")) {//새글을 DB에 추가 시켜줘~ 라는 요청주소를 받았을떄
			
			
//upload()메소드를 호출해 글쓰기 화면(articleForm.jsp)에서 전송된 글관련 정보들을
//HashMap에  key/value 쌍으로 저장합니다.
//그런후..........
//글 입력시 추가적으로 업로드할 파일을 선택하여 글쓰기요청을 했다면
//업로드한 파일명, 입력한 글제목, 입력한 글내용을  key/value 형태의 값들로 저장되어 있는 HashMap을 리턴받는다
//그렇지 않을 경우에는????
//업로드한 파일명을 제외한~ 입력한 글제목,입력한 글내용을 key/value 형태의 값들로 저장되어 있는 HashMap을 리턴 받는다.
			Map<String, String> articleMap = upload(request, response);
			
			//HashMap에 저장된 글관련정보(업로드할 파일명, 입력한 글제목, 입력한 글내용)를 다시 꺼내옵니다.
			String title = articleMap.get("title");
			String content = articleMap.get("content");
			String imageFileName = articleMap.get("imageFileName");
			
			//DB에 새글정보를 추가 하기 위해서 사용자가 입력한 글정보 + 업로드할 파일명을? ArticleVO객체의 각변수에 저장
			articleVO.setParentNO(0);//추가할 새글의 부모글번호를 0으로 저장
			articleVO.setId("hong");//추가할 새글 작성자 ID를 hong으로 저장
			articleVO.setTitle(title);//추가할 새글 글제목을 입력받은 값으로 저장
			articleVO.setContent(content);//추가할 새글 글내용을 입력받은 값으로 저장
			articleVO.setImageFileName(imageFileName);//추가할 새글 정보중 업로드한 파일명을 얻어 저장
			
			//그런다음.. 부장BoardService클래스의 addArticle메소드 호출시...
			//인자로 새글정보가 저장된 객체(ArticleVO객체)를 전달 하면 새글이 등록됩니다.
			//요약 : 글쓰기 창에서 입려된 정보를 ArticleVo객체에 설정한후 addArticle()메소드로 전달합니다.
			boardService.addArticle(articleVO);
			
			//DB에 새글을 추가하고 컨트롤러에서  /board02/listArticles.jsp로 이동하여
			//전체글을 다시 DB에서 검색하여 보여주기 위해 다음과 같은 서블릿으로 요청할 요청주소를 지저함.
			nextPage = "/board/listArticles.do";
		}
		
		//디스패치 방식으로 포워딩 (재요청)
		RequestDispatcher dispatch = request.getRequestDispatcher(nextPage);
		dispatch.forward(request, response);
	
	}//doHandle메소드 끝	
	
	
	//파일 업로드 처리를 위한 메소드
	private Map<String, String>  upload(HttpServletRequest request, 
								        HttpServletResponse response) 
								  throws ServletException,IOException{
		
		//해쉬맵 생성
		Map<String, String> articleMap = new HashMap<String, String>();
		
		String encoding="utf-8";
		
		//글쓰기를 할때 첨부한 이미지를 저장할 폴더 경로에 대해 접근 하기 위한 파일 객체를 생성합니다.
		File currentDirPath = new File(ARTICLE_IMAGE_REPO);
		
		//업로드할 파일 데이터를 임시로 저장할 객체 메모리 생성
		DiskFileItemFactory factory = new DiskFileItemFactory();
		
		//파일업로드시 사용할 임시메모리 최대 크기를 1메가 바이트로 지정
		factory.setSizeThreshold(1024*1024*1);
		
		//임시메모리에 파일업로드시~ 지정한 1메가 바이트 크기를 넘길경우
		//업로드될 파일 폴더경로를지정함
		factory.setRepository(currentDirPath);
		
		//파일을 업로드할 메모리를 생성자쪽으로 전달받아 저장한!! 파일업로드를 처리할 객체 생성
		ServletFileUpload upload = new ServletFileUpload(factory);
		
		try {
			//업로드할 파일에 대한 요정정보를 가지고 있는 requet객체를  parseRequest()메소드 호출시
			//인자로 전달하면
			//request객체에 저장되어 있는 업로드할 파일의 정보들을 파싱해서 DiskFileItem객체에 저장후
			//DiskFileItem객체를 ArrayList에 추가합니다.
			//그후 ArrayList를 반환 받습니다.
			List items = upload.parseRequest(request);
			
			//ArrayList크기만큼(DiskFileItem개체의 갯수만큼)반복
			for(int i=0;  i<items.size(); i++) {
				
				//ArrayList가변배열에서 .. DiskFileItem객체(아이템하나정보)를 얻는다.
				DiskFileItem fileItem  = (DiskFileItem)items.get(i);
				
				//DiskFileItem객체(아이템 하나의 정보)가  파일 아이템이 아닐 경우 
				if(fileItem.isFormField()) {
					//articleForm.jsp페이지에서 입력한 글제목, 글내용만 따로  HashMap에 key=value형식으로 저장합니다.
					//HashMap에 저장된 데이터의 예 ->  { title=입력한글제목, content=입력한글내용}
					articleMap.put(fileItem.getFieldName(), fileItem.getString(encoding));
					
				}else {//DiskFileItem객체(아이템 하나의 정보)가 파일 아이템일 경우
					
					System.out.println("파라미터명 : " + fileItem.getFieldName());
					System.out.println("파일명 : " + fileItem.getName());
					System.out.println("파일크기 : " + fileItem.getSize() + "bytes");
					
					//articleForm.jsp페이지에서 입력한 글제목,글내용,요청할 업로드파일등..모든정보?
					//HashMap에 {key=value}형식으로 저장힙니다.
					//{imageFileName=업로드할파일이름, title=입력한글제목, content=입력한글내용}형식으로 저장함.
					//HashMap에 저장된 데이터의 예 ->>>>> {imageFileName=3.png, title=글제목, content=글내용}
					articleMap.put(fileItem.getFieldName(), fileItem.getName());
					
					//전체 : 업로드할 파일이 존재 하는 경우  업로드할 파일의 파일이름으로 저장소에 업로드합니다.
					//파일크기가 0보다 크다면?(업르도할 파일이 존재한다면)
					if(fileItem.getSize() > 0) {
						
						//업로드할 파일명을 얻어 파일명의 뒤에서부터 \\문자열이 들어 있는지 
						//인덱스 위치를 알려주는데... \\문자열이 없으면 -1을 반환함
						int idx = fileItem.getName().lastIndexOf("\\");
						
						if(idx == -1) {
							idx = fileItem.getName().lastIndexOf("/"); //-1얻기
						}
						//업로드할 파일명 얻기
						String fileName = fileItem.getName().substring(idx + 1);
					
						//업로드할 파일 경로 + 파일명  주소를 만들어서 저장할 File객체 생성
						File uploadFile = new File(currentDirPath + "\\" + fileName);
						
						//실제 파일 업로드 
						fileItem.write(uploadFile);
						
					}//end if
				}//end if
			}//end for
		}catch (Exception e) {
			System.out.println("upload메소드 내부에서 업로드 오류 : " + e);
		}
		return articleMap; //해쉬맵을? doHandle메소드쪾으로 리턴		
	}	
	
}//BoardController서블릿 끝






















