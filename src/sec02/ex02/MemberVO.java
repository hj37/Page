package sec02.ex02;

import java.sql.Date;

//MVC 중에...... Model의 역할을 하는 VO클래스
//1. DB로부터 회원 한명의 정보를 검색해서 가져와서  저장할 용도
//2. DB에 새회원한명의 정보를 추가하기전에  임시로 정보를 저장할 용도
public class MemberVO {

	private String id,pwd,name,email;
	private Date joinDate;
	
	public MemberVO() {
		System.out.println("객체 생성시 MemberVO 생성자가 호출됨");
	}
	//MemberVO객체 생성시 생성자를 호출해 위의 멤버변수값들을 초기화할 생성자
	public MemberVO(String id, String pwd, String name, String email, Date joinDate) {
		this.id = id;
		this.pwd = pwd;
		this.name = name;
		this.email = email;
		this.joinDate = joinDate;
	}
	public MemberVO(String id, String pwd, String name, String email) {
		this.id = id;
		this.pwd = pwd;
		this.name = name;
		this.email = email;
	}
	//getter ,setter메소드 
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Date getJoinDate() {
		return joinDate;
	}
	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}
	
	
	
	
}



