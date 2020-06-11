package sec01.ex01;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

//MVC중에 M의 역할을 함
//-> DB연결 등 비즈니스로직 처리 
public class MemberDAO {

	private DataSource dataFactory;//커넥션풀을 저장할 변수
	private Connection conn; //커넥션을 저장할 변수
	private PreparedStatement pstmt;//SQL문을 DB에 전송해서 실행할 객체를 저장할 변수
	private ResultSet rs;
	
	
	//MemberDAO객체 생성시 생성자를 호출하게 되는데 ...
	//생성자를 호출하면 DataSource커넥션풀을 얻는다.
	public MemberDAO() {
		try {
			Context ctx = new InitialContext();
			Context envContext = (Context)ctx.lookup("java:/comp/env");
			//커넥션풀 얻기
			dataFactory = (DataSource)envContext.lookup("jdbc/oracle");
		} catch (Exception e) {
			System.out.println("커넥션풀 얻기 실패 : " + e.getMessage());
		}
	}//생성자 끝
	
	//SQL문을 이용해 모든 회원정보를 조회한후  그결과를 ArrayList로 반환 하는 메소드
	public List<MemberVO>  listMembers(){
		
		List<MemberVO>  membersList = new ArrayList<MemberVO>();
		
		try {
			//커넥션풀 에 저장되어 있는 커넥션객체(미리 DB와 연결을 맺고 있는 객체)를 빌려온다.
			//DB연결
			conn =  dataFactory.getConnection();
			//SQL문  : 가입 날짜를 기준으로 내림차순 정렬 해서 검색
			String query = "select * from t_member order by joinDate desc";
			//SQL문을 실행할 객체 얻기
			pstmt = conn.prepareStatement(query);
			//SQL문 SELECT구문 실행후  검색한 결과데이터들을 테이블 구조로 ResultSet에 저장후 얻기
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				
				//DB로부터 검색한 회원 한명(한줄 정보)를  ResulstSet객체에서 꺼내어서
				//MemberVO객체의 각변수에 저장
				MemberVO memberVO = new MemberVO(rs.getString("id"), 
												 rs.getString("pwd"), 
												 rs.getString("name"), 
												 rs.getString("email"), 
												 rs.getDate("joinDate"));
				//ArrayList에 위의 MemberVO객체 추가
				membersList.add(memberVO);
			}
			
		} catch (Exception e) {
			System.out.println("listMembers메소드 내부에서 SQL실행 예외 발생 : " + e);
		} finally {
			//자원 해제~
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return membersList;//사장님에게(MemberController.java)컨트롤러에게 반환
	}
}//클래스 끝






