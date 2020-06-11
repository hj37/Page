import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



//파일 다운로드 기능을 할 서블릿 클래스.
//파일 다운로드 기능은 자바IO(입출력)를 이용해 구현합니다.

//@WebServlet("/download.do")
public class FileDownload extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//요청한 데이터 한글처리
		req.setCharacterEncoding("UTF-8");
		//응답할 데이터의 타입을 기본값들로 설정
		resp.setContentType("text/html;charset=utf-8");
		
		//다운로드할 파일이 저장되어 있는 장소 
		String file_repo = "C:\\file_repo";
		
		//전송된 다운로드할 파일 이름얻기
		String fileName = req.getParameter("fileName");
		
		//org.apache.catalina.connector.CoyoteOutputStream@32d8fcdd
		//파일 다운로드를 요청한 클라이언트의 웹브라우저에
		//다운로드할 파일을 바이트 단위로 읽어들인 버퍼공간의 데이터를 내보내기 위한 출력 스트림 통로 객체 생성
		OutputStream out = resp.getOutputStream();
		
		// C:\file_repo\파일1.jpg
		String downFile = file_repo + "\\" + fileName;
		
		//실제 다운로드할 파일에 접근 하기 위해  C:\file_repo\파일1.jpg 경로에 대한 File객체 생성
		File f = new File(downFile);
		
		/*웹브라우저 캐시에 대해 설명하기 위한 내용*/
		//웹 개발을 하다보면 게시글 등의 데이터를  DB에 등록 했는데도  
		//웹브라우저에서 새로고침 시 해당 데이터에 대한 내용이 반영되지 않는 경우가 있습니다.
		//혹은 데이터 뿐만이 아닌  javascript나, HTML, CSS등의 정적 자원을 서버에서 수정 했는데도
		//새로고침시 적용되지 않는 경우도 있습니다.
		//많은 원인이 있을수 있으나 이는 웹브라우저 캐시가 원인일수 있습니다.
		
/*웹브라우저의 캐시 공간이란?*/
//브라우저에는 캐시 공간이 있는데.. 이것은 서버와의 불필요한 통신을 하지 않기 위해 마련된 공간입니다.
//최초 서버로부터 요청한 자원들(javascript, HTML, CSS, 이미지등)을 내려받고
//같은 자원을 새로고침등을 통해서 다시 서버페이지로 요청하는 경우
//웹브라우저는 실제로 서버로 HTTP요청을 하지 않고 브라우저 자신의 캐시공간에 저장해 두었던 자원을 사용하게 됩니다.
		
//예를 들어 test.jsp를 최초 요청한경우 서버페이지로 부터 응답된 자원들을 웹브라우저 캐시공간에 저장하고
//F5나 주소표시줄에 주소를 입력해 다시 test.jsp를 요청한 경우   불필요하게 HTTP요청을 하는 것이 아니라
//웹브라우저의 캐시공간에 저장해둔 자원을 꺼내서 화면에 보여주는 것입니다.
//이러한 웹브라우저 캐시 공간의 기능은 성능상 이점을 가져다 줄수는 있겠으나
//게시판이나, 네이버의 실시간 검색어처럼 자주 변하는 부분까지 브라우저 캐시공간을 사용한다면
//사용자는 변화화는 결과를 볼수 없고 같은 화면만 계속해서 보게 될것입니다.
		
/*응답 헤더를 통한 캐시공간 제어 설명*/		
// HTTP응답메세지의 몇가지 헤더 속성을 통해서 
// 웹브라우저가 현재 페이지 내용을 캐시공간에 저장하지 않도록 설정 할수 있습니다.		
// response객체의 해당 속성들에 값을 설정해  웹브라우저가 캐시공간을 사용하지 않고
// 매번 새로운 요청을 통해 결과를 얻어올수 있도록 합시다.
		
		//HTTP 1.1버전에서 지원하는 헤더로 no-cache로 설정하면 
		//브라우저는 응답받는 결과를 캐싱 하지 않습니다.
		//또한 뒤로가기 등을 통해서 페이지 이동하는 경우 페이지를 캐싱할수 있으므로
		//no-store 값 또한 추가해 주어야 합니다.
		resp.setHeader("Cache-Control", "no-cache");
		resp.addHeader("Cache-Control", "no-store");
		
//웹브라우저에서 다운로드할 파일명 클릭시
//1.Content-disposition속성에 attachment;을 지정하여 
//  다운로드시 무조건 "파일다운로드" 대화상자가 뜨도록 하는 헤더 속성의 값 설정
//2.다운로드할 파일명이 한글일 경우 깨지는 현상을 방지 하기 위해 Content-Disposition속성에 filename설정
		resp.setHeader(
				"Content-Disposition", 
				"attachment; fileName=\"" + URLEncoder.encode(fileName, "UTF-8")+"\";");
	/*	
	   참고
	   	Content-Disposition : attachment
	   	브라우저 인식 파일 확장자를 포함하여 모든 확장자의 파일들에 대해,
	   	다운로드시 무조건 "파일 다운로드" 대화상자가 뜨도록 하는 헤더 속성
	   
	    Content-Disposition : inline
	       브라우저 인식 파일 확장자를 가진 파일들에 대해서는 웹브라우저 상에서 바로 파일을 열고,
	       그외의 파일들에 대해서는 "파일 다운로드" 대화상자가 뜨도록 하는 헤더 속성
	 */
		//다운로드할 파일을 바이트 단위로 읽어 들일 버퍼 공간 통로 생성
		FileInputStream in = new FileInputStream(f);
		
		//다운로드할 파일전체에서 데이터를 8KB씩 읽어와 저장할 용도의 byte단위의 배열 생성
		byte[] buffer = new byte[1024*8];
		
		while (true) {
			//다운로드할 파일의 전체 내용중.. 약8kb를 한번 읽어와 저장
			int count = in.read(buffer);
			
			if(count == -1) {//읽어 들일 내용이 더이상 없으면?(-1일경우)
				break;
			}
			//다운로드할 파일로부터 읽어 들인 값이 존재 하면?
			//읽어 들인 8KB전체를 출력 스트림 통로를 통해 웹브라우저로 내보낸다.(출력한다)
			out.write(buffer, 0, count);		
		}
		//자원해제
		in.close();
		out.close();
	}
	
}







