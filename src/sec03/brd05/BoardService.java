package sec03.brd05;

import java.util.List;

public class BoardService {
	BoardDAO boardDAO;

	public BoardService() {
		boardDAO = new BoardDAO();
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




