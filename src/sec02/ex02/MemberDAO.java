package sec02.ex02;

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
	
	
	//자원해제 메소드
	public void resourceClose() {
		
		//자원 해제~
		try {
			if(rs != null) rs.close();
			if(pstmt != null) pstmt.close();
			if(conn != null) conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
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
			resourceClose();
		}
		return membersList;//사장님에게(MemberController.java)컨트롤러에게 반환
	}

	//새로운 회원 정보를 DB에 추가 시키는 메소드 
	public void addMember(MemberVO memberVO) {
		try {
			//커넥션풀로 부터 커넥션 빌려오기
			conn = dataFactory.getConnection(); //DB연결
			
			String query = "INSERT INTO t_member(id, pwd, name, email) VALUES(?,?,?,?)";
			
			pstmt = conn.prepareStatement(query);
			//?값 설정
			pstmt.setString(1, memberVO.getId());
			pstmt.setString(2, memberVO.getPwd());
			pstmt.setString(3, memberVO.getName());
			pstmt.setString(4, memberVO.getEmail());
			//INSERT실행
			pstmt.executeUpdate();
		
		} catch (Exception e) {
			System.out.println("addMember메소드 내부에서 SQL실행 오류 : " + e);
		}finally {
			//자원 해제~
			resourceClose();
		}
	}

	//회원 id를 매개변수로 전달받아 회원 조회
	public MemberVO findMember(String id) {
		//조회한 회원 한사람의정보를 저장할 용도의 MemberVO객체를 담을 변수 선언
		MemberVO memInfo = null;
		try {
			conn = dataFactory.getConnection();//DB연결
			//매개변수로 전달 받은 id에 해당하는 회원레코드 검색 
			String query = "select * from t_member where id=?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			rs.next();
			//검색한 회원 한줄의 정보를 얻어  MemberVO객체의 각변수에 저장
			memInfo = new MemberVO(rs.getString("id"), 
					               rs.getString("pwd"),
					               rs.getString("name"),
					               rs.getString("email"), 
					               rs.getDate("joinDate"));		
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			//자원 해제~
			resourceClose();
		}
		return memInfo;//매개변수로 전달받은 회원 ID로 조회한 회원정보를 반환
	}
	

	//매개변수로 전달받은 MemberVO객체(수정할 정보)를 이용하여 DB에 저장된 회원 레코드 수정
	public void modMember(MemberVO  memberVO) {
		
		try {
			//DB연결
			conn = dataFactory.getConnection();
			//UPDATE구문 : 매개변수로 전달 받은 MemberVO객체의 각변수에 저장된 정보를 이용하여 UPDATE구문 만들기
			String query = "UPDATE t_member set pwd=?, name=?, email=? where id=?";
			//?기호에 대응되는 값을 제외한 UPDATE전체 문장을 로딩한 OraclePreparedStatementWrapper객체 얻기
			pstmt = conn.prepareStatement(query);
			//?기호에 대응되는 수정할 값을 설정
			pstmt.setString(1, memberVO.getPwd());
			pstmt.setString(2, memberVO.getName());
			pstmt.setString(3, memberVO.getEmail());
			pstmt.setString(4, memberVO.getId());
			//UPDATE전체 구문 DB에 전송하여 실행!
			pstmt.executeUpdate(); 
		} catch (Exception e) {
			e.printStackTrace();//이클립스 콘솔창에 UPDATE실행 예외 출력
		} finally {
			//자원해제
			resourceClose();
		}
	}//modMember메소드 끝
	
	
	//매개변수로 전달받은 삭제할 회원 id를 통해 회원삭제
	public void delMember(String id) {
		
		try {
			//커넥션풀로 부터 커넥션 빌려오기
			conn = dataFactory.getConnection(); //DB연결
			
			//매개변수로 전달받은 ID에 해당 하는 회원레코드 삭제
			String query = "delete from t_member where id=?";
			
			pstmt = conn.prepareStatement(query);
			//?값 설정
			pstmt.setString(1, id);
		
			//DELETE실행
			pstmt.executeUpdate();
		
		} catch (Exception e) {
			System.out.println("delMember메소드 내부에서 SQL실행 오류 : " + e);
		}finally {
			//자원 해제~
			resourceClose();
		}
	}
			
}//MemberDAO클래스 끝











