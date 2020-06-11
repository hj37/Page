package sec03.brd08;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
/*
	/listArticle.do로 최초 요청시.. section과 pageNum의 기본값을 1로 초기화합니다.
	컨트롤러에서 전달된 section과 pageNum을  HashMap에 저장한 후 DAO로 전달합니다.
*/
@WebServlet("/board/*")
public class BoardController extends HttpServlet {
	private static String ARTICLE_IMAGE_REPO = "C:\\board\\article_image";
	BoardService boardService;
	ArticleVO articleVO;
	
	public void init(ServletConfig config) throws ServletException {
		boardService = new BoardService();
		articleVO = new ArticleVO();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(request, response);
	}

	private void doHandle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String nextPage = "";
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		
		//답글에 대한 부모글번호를 저장하기 위해 세션영역을 사용합니다
		HttpSession session;
		
		//삭제 요청 주소 중 2단계 요청주소 얻기  : /removeArticle.do
		//답글을 작성할수 있는 창으로 이동시켜 줘~ 라는 2단계 요청주소 얻기  : /replyForm.do 
		String action = request.getPathInfo();
		System.out.println("action:" + action);
		try {
			List<ArticleVO> articlesList = new ArrayList<ArticleVO>();
			
			if (action == null) {
				
				//최초 요청시 또는 /listArticle.do로 요청시
				//section값과 pageNum값을 구합니다.
				String _section = request.getParameter("section");
				String _pageNum = request.getParameter("pageNum");
				//최초 요청시 section값과 pageNum값이 없으면? 각각 1로 초기화합니다.
				int section = Integer.parseInt(((_section == null) ? "1" : _section));
				int pageNum = Integer.parseInt(((_pageNum == null) ? "1" : _pageNum));
				
				//section값과 pageNum값을 HashMap에 저장한후  
				//BoardService의 메소드호출시 인자로 전달함
				Map pagingMap = new HashMap();
				pagingMap.put("section", section);
				pagingMap.put("pageNum", pageNum);
				
				//section값과 pageNum값으로 해당 섹션과 페이지에 해당되는 글목록을 조회 명령함
				Map articlesMap = boardService.listArticles(pagingMap);
				
				//브라우저에서 전송된 section과 pageNum값을 HashMap에 저장
				articlesMap.put("section", section);
				articlesMap.put("pageNum", pageNum);
				
				//조회된 글목록dmf articlesMap으로 바인딩하여  listArticle.jsp로 넘김니다
				request.setAttribute("articlesMap", articlesMap);
				
				//검색후 보여질 VIEW주소 저장
				nextPage = "/board07/listArticles.jsp";
		
			} else if (action.equals("/listArticles.do")) {
				
				//글목록에서 명시적으로 페이지 번호를 눌러서 요청한 경우 
				//section값과 pageNum값을 가져옵니다.
				String _section = request.getParameter("section");
				String _pageNum = request.getParameter("pageNum");
				
				//최초 요청시 section값과 pageNum값이 없으면? 각각 1로 초기화합니다.
				int section = Integer.parseInt(((_section == null) ? "1" : _section));
				int pageNum = Integer.parseInt(((_pageNum == null) ? "1" : _pageNum));
				
				//section값과 pageNum값을 HashMap에 저장한후  
				//BoardService의 메소드호출시 인자로 전달함
				Map pagingMap = new HashMap();
				pagingMap.put("section", section);
				pagingMap.put("pageNum", pageNum);
				
				//section값과 pageNum값으로 해당 섹션과 페이지에 해당되는 글목록을 조회 명령함
				Map articlesMap = boardService.listArticles(pagingMap);
				
				//브라우저에서 전송된 section과 pageNum값을 HashMap에 저장
				articlesMap.put("section", section);
				articlesMap.put("pageNum", pageNum);
				
				//조회된 글목록dmf articlesMap으로 바인딩하여  listArticle.jsp로 넘김니다
				request.setAttribute("articlesMap", articlesMap);
				
				//검색후 보여질 VIEW주소 저장
				nextPage = "/board07/listArticles.jsp";
		
					
			} else if (action.equals("/articleForm.do")) {
				nextPage = "/board07/articleForm.jsp";
			} else if (action.equals("/addArticle.do")) {
				int articleNO = 0;
				Map<String, String> articleMap = upload(request, response);
				String title = articleMap.get("title");
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName");

				articleVO.setParentNO(0);
				articleVO.setId("hong");
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setImageFileName(imageFileName);
				articleNO = boardService.addArticle(articleVO);
				if (imageFileName != null && imageFileName.length() != 0) {
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
					destDir.mkdirs();
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
					srcFile.delete();
				}
				PrintWriter pw = response.getWriter();
				pw.print("<script>" + "  alert('새글을 추가했습니다.');" + " location.href='" + request.getContextPath()
						+ "/board/listArticles.do';" + "</script>");

				return;
			} else if (action.equals("/viewArticle.do")) {
				String articleNO = request.getParameter("articleNO");
				articleVO = boardService.viewArticle(Integer.parseInt(articleNO));
				request.setAttribute("article", articleVO);
				nextPage = "/board07/viewArticle.jsp";
				
			/*	
			  컨트롤러에서 수정을 요청하면  upload()메소드를 이용해
			 수정할 데이터를  Map에 담아 가져옵니다. Map의 데이터를 다시 ArticleVo객체의 속성에 저장한후에 
			 SQL문(UPDATE)문으로 전달 하여 수정할 데이터를 t_board테이블에 UPDATE 시킵니다.
			 마지막으로 temp폴더에 업로드된 수정 이미지를 다시 글번호 폴더로 이동하고 
			 글번호 폴더의 원래 이미지를 삭제 합니다.
			*/
			} else if (action.equals("/modArticle.do")) {
				
				Map<String, String> articleMap = upload(request, response);
				
				int articleNO = Integer.parseInt(articleMap.get("articleNO"));
				articleVO.setArticleNO(articleNO);
				String title = articleMap.get("title");
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName");
				articleVO.setParentNO(0);
				articleVO.setId("hong");
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setImageFileName(imageFileName);
				
				//전송된 글정보를 이용해 글을 수정 합니다.
				boardService.modArticle(articleVO);
				
				if (imageFileName != null && imageFileName.length() != 0) {
					String originalFileName = articleMap.get("originalFileName");
					
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					
					//수정한 글의 글번호를 이용해 글번호 폴더 생성
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
					destDir.mkdirs();
					
					//수정된 이미지파일을 글번호 폴더로 이동~
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
					
					//전송된 originalFileName을 이용해 기존의 파일을 삭제 합니다.
					File oldFile = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO + "\\" + originalFileName);
					oldFile.delete();
				}
				PrintWriter pw = response.getWriter();
				pw.print("<script>" 
						+ "  alert('글을 수정했습니다.');" 
						+ " location.href='" + request.getContextPath()
						+ "/board/viewArticle.do?articleNO=" + articleNO + "';" 
						+ "</script>");
				return;
				
			}else if(action.equals("/removeArticle.do")) {//삭제 요청이 들어 왔을때
				//삭제할 글번호 얻기 (요청한 값 얻기)
				int articleNO = Integer.parseInt(request.getParameter("articleNO"));
				//articleNO값에 대한 글을 삭제한 후 
				//삭제된~ 부모 글과 자식 글의 articleNO 목록을 검색해서 가져옵니다.
				List<Integer> articleNOList = 
						boardService.removeArticle(articleNO);
				
				//삭제된 글들의 이미지 저장 폴더를 삭제 합니다.
				for(int _articleNO : articleNOList) {
				
					File imgDir = new File(ARTICLE_IMAGE_REPO + "\\" + _articleNO);
					
					 if(imgDir.exists()) {
						 
						 FileUtils.deleteDirectory(imgDir);
					 }			
				}
				
				PrintWriter pw = response.getWriter();	
				pw.print("<script>" 
						+ " alert('글을 삭제 했습니다.');" 
						+ " location.href='"
						+ request.getContextPath() +"/board/listArticles.do';"
						+ "</script>");
				return;
			
			}else if(action.equals("/replyForm.do")) {//답글을 작성할수 있는 화면으로 이동시켜줘
				//부모글번호 전달 받기
				int parentNO = Integer.parseInt(request.getParameter("parentNO"));
				//세션영역 하나 생성
				session = request.getSession(); 
				//답글창 요청시 미리 부모 글번호를
				//parentNO속성으로 세션에 저장합니다. 
				session.setAttribute("parentNO", parentNO); 	
				//답글을 작성할수 있는 화면의 뷰 주소를 저장 
				nextPage = "/board07/replyForm.jsp";
				
			
			}else if(action.equals("/addReply.do")) {//작성한 답글 내용을 DB에 INSERT시켜줘~
				
				session = request.getSession();
				//세션영역에 저장되어 있는 부모글 번호를 꺼내오고 
				int parentNO = (Integer)session.getAttribute("parentNO");
				 //세션영역에 저장되어 있는 부모글번호 제거
				session.removeAttribute("parentNO");
				
				//replyForm.jsp답글쓰기 화면에서 입력한 정보들을 Map에 담아 반환받기
				Map<String, String> articleMap = upload(request, response);
				//Map에 저장되어 있는 입력한 정보들을 꺼내오기
				String title = articleMap.get("title");
				String content = articleMap.get("content");
				String imageFileName =articleMap.get("imageFileName");
				
				//답글의 부모 글번호를 VO에 설정함
				articleVO.setParentNO(parentNO);
				//답글 작성자 ID를 lee로 설정함
				articleVO.setId("lee");
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setImageFileName(imageFileName);
				
				//BoardService객체의 addReply()메소드 호출시 articleVO객체를 인수로 전달하여
				//입력한 답글 정보를 DB에 추가 하는 명령을 함
				//답글 내용을 추가한 후 ~ 답글 글번호를 반환 받는다.
				int articleNO = boardService.addReply(articleVO);
				
				//답글에 첨부한 이미지를 temp폴더에서 답글번호 폴더로 이동합니다
				if(imageFileName != null && imageFileName.length() != 0) {
					
					File srcFile = 
					new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					
					File destDir = 
					new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
					
					//답글번호 폴더 생성
					destDir.mkdirs();
					
					//temp폴더의 업로드한 이미지를 답글번호 폴더경로로 이동시킨다
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
					
				}
				//웹브라우저로 응답(출력,내보내기)시 동영상같은 사이즈가 큰파일을 내보낼 출력스트림통로
//				OutputStream out = response.getOutputStream();
				
				//텍스트만~~~~~~내보낼 출력 스트림 통로  -> "답글을 추가 했습니다" 메세지창을 웹브라우저 전송
				PrintWriter pw = response.getWriter();
				pw.print("<script>" + "  alert('답글을 추가했습니다.');" 
						+ " location.href='" + request.getContextPath()
						+ "/board/viewArticle.do?articleNO="+articleNO+"';" + "</script>");
				return;
				
			}

			RequestDispatcher dispatch = request.getRequestDispatcher(nextPage);
			dispatch.forward(request, response);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Map<String, String> upload(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Map<String, String> articleMap = new HashMap<String, String>();
		String encoding = "utf-8";
		File currentDirPath = new File(ARTICLE_IMAGE_REPO);
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setRepository(currentDirPath);
		factory.setSizeThreshold(1024 * 1024);
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			List items = upload.parseRequest(request);
			for (int i = 0; i < items.size(); i++) {
				FileItem fileItem = (FileItem) items.get(i);
				if (fileItem.isFormField()) {
					System.out.println(fileItem.getFieldName() + "=" + fileItem.getString(encoding));
					articleMap.put(fileItem.getFieldName(), fileItem.getString(encoding));
				} else {
					System.out.println("파라미터명:" + fileItem.getFieldName());
					//System.out.println("파일명:" + fileItem.getName());
					System.out.println("파일크기:" + fileItem.getSize() + "bytes");
					//articleMap.put(fileItem.getFieldName(), fileItem.getName());
					if (fileItem.getSize() > 0) {
						int idx = fileItem.getName().lastIndexOf("\\");
						if (idx == -1) {
							idx = fileItem.getName().lastIndexOf("/");
						}

						String fileName = fileItem.getName().substring(idx + 1);
						System.out.println("파일명:" + fileName);
						//익스플로러에서 업로드 파일의 경로 제거 후 map에 파일명 저장
						articleMap.put(fileItem.getFieldName(), fileName);  
						File uploadFile = new File(currentDirPath + "\\temp\\" + fileName);
						fileItem.write(uploadFile);

					} // end if
				} // end if
			} // end for
		} catch (Exception e) {
			e.printStackTrace();
		}
		return articleMap;
	}

}
