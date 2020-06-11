package sec03.brd01;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

//DB작업 하는 클래스 
public class BoardDAO {

	private DataSource dataFactory;
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	
	public BoardDAO() {
		try {
			Context ctx = new InitialContext();
			Context envContext = (Context)ctx.lookup("java:/comp/env");
			//커넥션풀 얻기
			dataFactory = (DataSource)envContext.lookup("jdbc/oracle");
		} catch (Exception e) {
			System.out.println("커넥션풀 얻기 실패 : " + e.getMessage());
		}
	}//생성자 끝
		
	//BoardService클래스에서 BoardDAO의 selectAllArticles()메소드를 호출하면
	//계층형 SQL문을 이용해 계층형 구조로 전체 글을 조회한 후 BoardSerivece로 반환합니다.
	public List selectAllArticles() {
		//검색한 글 정보들을 저장할 용도
		List articlesList = new ArrayList();
		
		try {
			//커넥션풀로 부터 커넥션 빌려오기 (DB와 미리연결을 맺은 Conneciton접속객체 빌려오기)
			conn = dataFactory.getConnection();
			//계층형 구조로 전체글을 조회하는 오라클의 계층형 SQL문 
			String query = "SELECT LEVEL,articleNo,parentNO,title,content,id,writeDate"
						 + " from t_board"
						 + " START WITH parentNO=0"
						 + " CONNECT BY PRIOR articleNO=parentNO"
						 + " ORDER SIBLINGS BY articleNO DESC";
			
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				int level = rs.getInt("level"); //각 글의 깊이(계층)를 level변수에 저장합니다.
				int articleNO = rs.getInt("articleNO");//글번호는 숫자형이므로 getInt()로 값을 가져옴
				int parentNO = rs.getInt("parentNO");//부모글번호
				String title = rs.getString("title");
				String content = rs.getString("content");
				String id = rs.getString("id");
				Date writeDate = rs.getDate("writeDate");//글을작성한 날짜
				
				//검색한 하나의 글정보씩 ArticleVO객체의 각변수(속성)에 저장합니다
				ArticleVO article = new ArticleVO();
				article.setLevel(level);
				article.setArticleNO(articleNO);
				article.setParentNO(parentNO);
				article.setTitle(title);
				article.setContent(content);
				article.setId(id);
				article.setWriteDate(writeDate);
				
				//ArryList배열에  ArticleVO객체 추가
				articlesList.add(article);			
			}
			//자원해제
			rs.close();
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//BoardService로 리턴
		return articlesList;
		
	}//selectAllArticles()메소드 끝
	
}//BoardDAO클래스 끝













