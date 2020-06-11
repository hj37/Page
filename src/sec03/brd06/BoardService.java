package sec03.brd06;

import java.util.List;

public class BoardService {
	BoardDAO boardDAO;

	public BoardService() {
		boardDAO = new BoardDAO();
	}
	
	public  List<Integer> removeArticle(int articleNO){
		//글을 삭제 하기 전 삭제할?글 번호들 검색해서 ArrayList객체에 저장합니다.
		List<Integer> articleNOList = 
								boardDAO.selectRemovedArticles(articleNO);
		
		boardDAO.deleteArticle(articleNO);//삭제 요청시 삭제할 글번호 전달
		
		return articleNOList; //삭제할 글 번호 목록을 컨트롤러로 반환합니다.
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




