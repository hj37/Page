package sec03.brd02;

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
		
	//DB에 새글을 추가하기전에.. DB에 존재하는 가장 최신글번호를 검색해서 가져와 제공하는 메소드
	private int getNewArticleNO() {
		try {
			//DB연결
			//커넥셔풀 DataSource로부터 DB와 미리연결을 맺은 Connection객체 얻기
			conn = dataFactory.getConnection();
			//SQL문 : 글번호중 가장 큰글번호를 조회하는 SQL문
			String query = "SELECT max(articleNO) from t_board";
			
			pstmt = conn.prepareStatement(query);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {//가장 최신글번호가 검색되었다면...
				//아래의 insertNewArticle메소드로 반환
				return (rs.getInt(1) + 1);//가장 큰글번호에 1을 더한 번호를 반환
										  //이유 : 새글 추가시 글번호를 지정하기 위함
			}
			//자원해제
			rs.close();
			pstmt.close();
			conn.close();	
		} catch (Exception e) {
			System.out.println("getNewArticleNO메소드 에서 예외 : " + e);
		}
		return 0; //아래의 insertNewArticle메소드로 반환
	}
	
	//DB에 새글을 추가하는 메소드
	//insertNewAricle메소드의 SQL문(insert문)을 실행하기전에
	//바로 위의 getNewArticleNO()메소드를 호출해
	//DB에 추가할 새 글번호(최신글번호 + 1)를 얻어 옵니다.
	//그런후 insert문장을 만들어 insert실행
	public void insertNewArticle(ArticleVO  article){
		
		try {
			//DB연결
			conn = dataFactory.getConnection();
		
			int articleNo = getNewArticleNO(); //DB에 새로 추가할 새글 글번호를 얻습니다.
			int parentNO = article.getParentNO();//새글의 부모글번호 얻기
			String title = article.getTitle();//새글의 글제목 얻기
			String content = article.getContent();//새글의 글내용 얻기
			String id = article.getId();//새글을 작성하는 사람의 id -> hong얻기 
			String imageFileName = article.getImageFileName(); //새글 작성시 첨부하여 업로드한 파일명얻기
			
			//SQL문 작성 : INSERT
			String query = "INSERT INTO t_board(articleNO, parentNO, title,"
					     + " content, imageFileName, id)"
					     + " VALUES(?, ?, ?, ?, ?, ?)";
					
			pstmt = conn.prepareStatement(query);
			//?에 대응되는 추가할 새글 정보 설정
			pstmt.setInt(1, articleNo);
			pstmt.setInt(2, parentNO);
			pstmt.setString(3, title);
			pstmt.setString(4, content);
			pstmt.setString(5, imageFileName);
			pstmt.setString(6, id);
			
			pstmt.executeUpdate();//INSERT
			
			//자원해제
			pstmt.close();
			conn.close();
			
		} catch (Exception e) {
			System.out.println("insertNewArticle메소드 내부에서 예외발생: " + e);
		}
	}
	
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













