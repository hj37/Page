package sec03.brd08;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardService {
	BoardDAO boardDAO;

	public BoardService() {
		boardDAO = new BoardDAO();
	}
	
	//답글내용을 DB에 추가후~ 추가시킨 답글의 글번호를  얻어  BoardController로 반환 
	public int addReply(ArticleVO articleVO) {
		//새글 추가시 사용한  insertNewArticle()메소드를 이용해 답글을 추가합니다.
		return boardDAO.insertNewArticle(articleVO);
	}
	
	
	
	public  List<Integer> removeArticle(int articleNO){
		//글을 삭제 하기 전 삭제할?글 번호들 검색해서 ArrayList객체에 저장합니다.
		List<Integer> articleNOList = 
								boardDAO.selectRemovedArticles(articleNO);
		
		boardDAO.deleteArticle(articleNO);//삭제 요청시 삭제할 글번호 전달
		
		return articleNOList; //삭제할 글 번호 목록을 컨트롤러로 반환합니다.
	}
	
	//페이징 기능에 필요한 글 목록과 전체 글수를 각각 조회할수 있도록 구현
	//HashMap을 생성한 후 조회한 두 정보를 각각 속성으로 저장함
	public Map listArticles(Map pagingMap) {
		
		Map articlesMap = new HashMap();
		
		//매개변수로 전달된  pagingMap을 사용해 글목록을 조회함
		List<ArticleVO> articlesList = boardDAO.selectAllArticles(pagingMap);
		//테이블에 존재하는 전체 글수를 조회함
		int totArticles = boardDAO.selectToArticles();
		
		//조회된  글목록을 ArrayList에 저장한후  다시 HashMap에 저장
		//조회된 전체 글 수를 HashMap 에 저장
		articlesMap.put("articlesList", articlesList);
		articlesMap.put("totArticles", totArticles);
		
		return articlesMap; //사장(BoardController)로 리턴
		
	}
	

	public List<ArticleVO> listArticles() {
		List<ArticleVO> articlesList = boardDAO.selectAllArticles();
		return articlesList;
	}

	public int addArticle(ArticleVO article) {
		return boardDAO.insertNewArticle(article);
	}

	public ArticleVO viewArticle(int articleNO) {
		ArticleVO article = null;
		article = boardDAO.selectArticle(articleNO);
		return article;
	}
	
	//BoardController서블릿 컨트롤러 클래스에서 modArticle()메소드를 호출하면서
	//다시~ BoardDAO의  updateArticle()메소드를 호출하면서 수정할 데이터(ArticleVO객체)를 전달 합니다.
	public void modArticle(ArticleVO article) {
		boardDAO.updateArticle(article);
	}

}




