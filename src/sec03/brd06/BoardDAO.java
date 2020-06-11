package sec03.brd06;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;


public class BoardDAO {
	private DataSource dataFactory;
	Connection conn;
	PreparedStatement pstmt;

	public BoardDAO() {
		try {
			Context ctx = new InitialContext();
			Context envContext = (Context) ctx.lookup("java:/comp/env");
			dataFactory = (DataSource) envContext.lookup("jdbc/oracle");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}//생성자
	
	//매개변수로 전달된 articleNO에 대한 글을 삭제 합니다
	public void deleteArticle(int articleNO) {
		
		try {
			//커넥션풀로부터 커넥션얻기
			conn = dataFactory.getConnection();
			//SQL문 만들기 : 오라클의 계층형 SQL문을 이용해 삭제 글과 관련된 자식글까지 모두 삭제
			String query = "DELETE FROM t_board ";
				   query += "WHERE articleNO in (";
				   query += " SELECT articleNO FROM t_board ";
				   query += " START WITH articleNO=? ";
				   query += " CONNECT BY PRIOR articleNO = parentNO )";
				   
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, articleNO);
			pstmt.executeUpdate(); //delete
			
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public List<Integer> selectRemovedArticles(int articleNO){
	
		//삭제할 글들의 articleNO를 저장할 용도
		List<Integer> articleNOList = new ArrayList<Integer>();
		
		try {
			//커넥션풀로 부터 커넥션 얻기(DB연결)
			conn = dataFactory.getConnection();
			//SQL(SELECT)문 : 삭제할 글들의 articleNO를 조회 합니다.
			String query = "SELECT articleNO FROM t_board ";
				   query += " START WITH articleNO = ?";
				   query += " CONNECT BY PRIOR articleNO = parentNO";
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, articleNO);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				articleNO = rs.getInt("articleNO");
				articleNOList.add(articleNO);
			}
			//자원해제
			pstmt.close();
			rs.close();
			conn.close();		
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return articleNOList;
	}
	

	public List<ArticleVO> selectAllArticles() {
		List<ArticleVO> articlesList = new ArrayList<ArticleVO>();
		try {
			conn = dataFactory.getConnection();
			String query = "SELECT LEVEL,articleNO,parentNO,title,content,id,writeDate" + " from t_board"
					+ " START WITH  parentNO=0" + " CONNECT BY PRIOR articleNO=parentNO"
					+ " ORDER SIBLINGS BY articleNO DESC";
			System.out.println(query);
			pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int level = rs.getInt("level");
				int articleNO = rs.getInt("articleNO");
				int parentNO = rs.getInt("parentNO");
				String title = rs.getString("title");
				String content = rs.getString("content");
				String id = rs.getString("id");
				Date writeDate = rs.getDate("writeDate");
				ArticleVO article = new ArticleVO();
				article.setLevel(level);
				article.setArticleNO(articleNO);
				article.setParentNO(parentNO);
				article.setTitle(title);
				article.setContent(content);
				article.setId(id);
				article.setWriteDate(writeDate);
				articlesList.add(article);
			}
			rs.close();
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return articlesList;
	}

	private int getNewArticleNO() {
		try {
			conn = dataFactory.getConnection();
			String query = "SELECT  max(articleNO) from t_board ";
			System.out.println(query);
			pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery(query);
			if (rs.next())
				return (rs.getInt(1) + 1);
			rs.close();
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int insertNewArticle(ArticleVO article) {
		int articleNO = getNewArticleNO();
		try {
			conn = dataFactory.getConnection();
			int parentNO = article.getParentNO();
			String title = article.getTitle();
			String content = article.getContent();
			String id = article.getId();
			String imageFileName = article.getImageFileName();
			String query = "INSERT INTO t_board (articleNO, parentNO, title, content, imageFileName, id)"
					+ " VALUES (?, ? ,?, ?, ?, ?)";
			System.out.println(query);
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, articleNO);
			pstmt.setInt(2, parentNO);
			pstmt.setString(3, title);
			pstmt.setString(4, content);
			pstmt.setString(5, imageFileName);
			pstmt.setString(6, id);
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return articleNO;
	}

	public ArticleVO selectArticle(int articleNO) {
		ArticleVO article = new ArticleVO();
		try {
			conn = dataFactory.getConnection();
			String query = "select articleNO,parentNO,title,content, imageFileName,id,writeDate" +
				         " from t_board where articleNO=?";
			System.out.println(query);
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, articleNO);
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			int _articleNO = rs.getInt("articleNO");
			int parentNO = rs.getInt("parentNO");
			String title = rs.getString("title");
			String content = rs.getString("content");
									//�����̸��� Ư�����ڰ� ���� ��� ���ڵ��մϴ�.
			String imageFileName = URLEncoder.encode(rs.getString("imageFileName"), "UTF-8"); 
			String id = rs.getString("id");
			Date writeDate = rs.getDate("writeDate");

			article.setArticleNO(_articleNO);
			article.setParentNO(parentNO);
			article.setTitle(title);
			article.setContent(content);
			article.setImageFileName(imageFileName);
			article.setId(id);
			article.setWriteDate(writeDate);
			rs.close();
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return article;
	}

	//매개변수로 전달된 수정할 데이터에 대해 이미지 파일을 수정하는  경우와
	//이미지 파일을 수정하지 않는 경우를 구분해 동적으로 SQL문을 생성하여 UPDATE수정데이터를 DB에 반영 하는 메소드 
	public void updateArticle(ArticleVO article) {
		int articleNO = article.getArticleNO();
		String title = article.getTitle();
		String content = article.getContent();
		String imageFileName = article.getImageFileName();
		try {
			conn = dataFactory.getConnection();
			String query = "update t_board  set title=?,content=?";
			
			//수정된 이미지 파일이 있을때만 imageFileNmae을 SQL문에 추가 합니다.
			if (imageFileName != null && imageFileName.length() != 0) {
				query += ",imageFileName=?";
			}
			//수정된 이미지 파일이 없을때는 뒤에 where절만 붙인다.
			query += " where articleNO=?";
			
			System.out.println(query);
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, title);
			pstmt.setString(2, content);
			
			//이미지 파일을 수정하는 경우 설정
			if (imageFileName != null && imageFileName.length() != 0) {
				pstmt.setString(3, imageFileName);
				pstmt.setInt(4, articleNO);
			
			//이미지 파일을 수정하지 않은 경우를 구분해서  설정 
			} else {
				pstmt.setInt(3, articleNO);
			}
			
			pstmt.executeUpdate(); //UPDATE구문 실행
			
			//자원해제
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
