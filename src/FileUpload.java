import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/*
 파일업로드를 처리하는 서블릿인 FileUpload클래스 입니다.
 라이브러리에서 제공하는 DiskFileItemFactory클래스를 이용해
 저장 위치와 업로드 가능한 최대 파일 크기를 설정 합니다.
 그리고  ServletFileUpload클래스를 이용해 파일 업로드 창에서 업로드된 파일과 
 매개변수에 대한 정보를 가져와 파일업로드 하고  매개변수 값을 출력합니다.
 */

@WebServlet("/upload.do")
public class FileUpload extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		//1. 한글처리
		req.setCharacterEncoding("UTF-8");
		
		//인코딩 방식 UTF-8문자열을 변수에 저장
		String encoding = "UTF-8";
		
		//업로드할 파일 경로에 접근 하기 위해 연결할 통로인? File객체 생성
		File currentDirPath = new File("C:\\file_repo"); //<---업로드할 파일의 폴더 경로 
		
		//업로드할 파일 데이터를 임시로 저장할 객체 메모리 생성 
		DiskFileItemFactory factory = new DiskFileItemFactory();
		//파일업로드시 사용할 임시 메모리 최대 크기를 1메가 바이트로 지정
		factory.setSizeThreshold(1024*1024*1);
		//임시 메모리에 파일 업로드시 ~~ 지정한 1메가바이트 크기를 넘길경우  업로도될 파일의 폴더 경로를 지정함
		factory.setRepository(currentDirPath);
			
		//참고
		//DiskFileItemFactory클래스는 업로드 파일의 크기가 지정한 크기를 넘기 전까지는
		//업로드 한 파일 데이터를 임시 메모리에 저장하고 지정한 크기를 넘길경우 임시 디렉터리에 파일로 저장한다.
		
		
		//파일업로드할 메모리를 생성자쪾으로 전달받아 저장한!! 파일 업로드를 처리할 객체 생성
		ServletFileUpload upload = new ServletFileUpload(factory);
	
		try {
			//uploadForm.jsp에서 요청한 값들을 저장하고 있는? request객체를? 
			//parseRequest()메소드 호출시
			//인자로 전달 하면  request객체에 저장되어 있는 요청한 아이템 5개의 정보들을 추출해서
			//DiskFileItem객체들에 각각 저장한후 다시? DiskFileItem객체들을?
			//ArrayList에 추가 합니다. 그후 ~~ ArrayList를 반환 받습니다.
			List items = upload.parseRequest(req);
			
			//ArryList크기만큼(DiskFileItem객체의 갯수만큼) 반복
			for(int i=0;  i<items.size(); i++) {
				
				//ArrayList가변길이 배열에서 DiskFileItem객체(요청한 아이템하나를 말함)를 얻는다.
				FileItem fileItem =(FileItem)items.get(i);
				
				//DiskFileItem객체의 정보가 업로드할 파일이 아닐경우~
				if(fileItem.isFormField()) {
						System.out.println( fileItem.getFieldName() + "=" 
											+ fileItem.getString(encoding)
										  );
				}else {//DiskFileItem객체의 정보가 업로드할 파일일 경우~
					
					System.out.println("파라미터명:" + fileItem.getFieldName());
					System.out.println("파일명:" + fileItem.getName());
					System.out.println("파일크기 : " + fileItem.getSize() +"bytes");
					
					//업로드할 파일크기가 0보다 크다면?(업로드할 파일이  있다면)
					if(fileItem.getSize() >  0) {
						
						//업로드할 파일명을 얻어 파일명의 뒤에서 부터  \\문자열이 들어있는지 
						//인덱스 위치를 알려주는데.. 없으면 -1을 반환함.
						//뒤에서 부터 문자가 들어 있는 인덱스를 알려준다.
						int idx = fileItem.getName().lastIndexOf("\\");
						System.out.println(idx);
						
						if(idx == -1) {
							idx = fileItem.getName().lastIndexOf("/");//-1얻기
							System.out.println("안녕 : " + idx);
						}
						//업로드할 파일명 얻기
						String fileName = fileItem.getName().substring(idx+1);
						//업로드할 파일 경로  + 파일명 에 대한 객체 생성
						File uploadFile = new File(currentDirPath + "\\" + fileName);
						//실제 파일 업로드
						fileItem.write(uploadFile);
					}//end if
				}//end if
			}//end for
		} catch (Exception e) {
			System.out.println("업로드 실패!:" + e);
		}		
	}//doPost메소드 끝
}//FileUplad클래스 끝








