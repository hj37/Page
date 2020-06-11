package sec03.brd03;

import java.util.List;

//BoardDAO객체를 생성 한후  selectAllArticles()메소드를 호출해
//전체 글을 검색해 가져옵니다.
//가져온 검색한 모든 글 정보를 사장님?(BoardController)에게 반납!(반환)
public class BoardService {

	BoardDAO boardDAO;  
	
	public BoardService() {
		boardDAO = new BoardDAO();//생성자 호출시 BoardDAO객체를 생성함.
	}
	
	//BoardController서블릿에서 호출한 메소드로써...
	//글쓰기창(articleForm.jsp)에서 입력된 정보를 ArticleVO객체의 각변수에 설정한후 매개변수로 전달 받아..
	//다시~ BoardDAO객체의 insertNewArticle()메소드를 호출하면서 추가할 새글정보(ArticleVO객체)를
	//매개변수로 전달하여 DB에 INSERT작업을 하게됨.
	//...INSERT작업후  새글 번호를 컨트롤러로 반환합니다.
	public int addArticle(ArticleVO article) {
		
	  return boardDAO.insertNewArticle(article);
		
	}
	//BoardController서블릿 클래스에서 호출 당하는 메소드로
	//BoardDAO객체의 selectAllArtices()메소드를 호출해 전체 글을 검색해서 가져와서 
	//BoardController서블릿으로 리턴(반환)해주는 메소드 
	public List<ArticleVO> listArticles(){
		List<ArticleVO> articlesList = boardDAO.selectAllArticles();
		return articlesList;//서블릿으로 반환
	}
	
	
	
	
}




