package sec03.brd03;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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
import org.apache.commons.io.FileUtils;


//upload()메소드를 호출해 첨부한 파일(업로드한파일)을 temp폴더에 임시로 업로드한 후 
//articleForm.jsp페이지에서 입력하여 요청한 새글정보를 HashMap에 저장후 Map을 리턴받습니다.(가져옵니다)
//그리고 새글을 t_board테이블에 추가한후 (insert한후) 반환받은 검색한 새글번호로  폴더를 생성하고 
//temp폴더의 이미지파일을  새글 번호 폴더로 이동 시킵니다.

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
			
			//t_board테이블에 새글을 추가한후 추가시킨 새글에 대한 글번호를 얻습니다.
			int articleNo = boardService.addArticle(articleVO);
			
			//파일을 첨부한 경우에만 수행합니다
			if(imageFileName != null && imageFileName.length() != 0) {
				
				//temp폴더에 임시로 업로드된 파일 객체를 생성 합니다.
				File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
				
				// C:\\board\\article_image의 경로 하위에 글 번호로 폴더를 생성 합니다.
				File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNo);
				destDir.mkdirs();//글 번호로 폴더를 생성
				
				//temp폴더의 파일을 글번호를 이름으로 하는 폴더로 이동 시킵니다.
				FileUtils.moveFileToDirectory(srcFile, destDir, true);	
			}
			
			//새글 등록 메세지를 나타낸 후 자바스크립트 location객체의 href속성을 이용해 글 목록화면을 요청함
			PrintWriter out = response.getWriter();
			out.print("<script>");
			out.print("window.alert('새글을 추가 했습니다.');");
			out.print(" location.href='" + request.getContextPath() +"/board/listArticles.do';");
			out.print("</script>");
				
			return;
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
						File uploadFile = new File(currentDirPath + "\\temp\\" + fileName);
						
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






















