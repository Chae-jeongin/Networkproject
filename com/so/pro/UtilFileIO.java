package com.so.pro;


import java.awt.FileDialog;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JTextArea;
import javax.swing.JTextPane;

public class UtilFileIO {
	
	public static void main(String[] args) {
		File file = new File("./test.txt");
		saveFile(file, "다시test임다");
		
		
		saveFile("ABCDEF\n~~~~");
		
		//파일 읽어오기
		String str = loadFile(file);
		System.out.println("<파일내용>\n" + str);
		
		File filedt = new File("./20201213_network.txt");
		System.out.println(loadFile(filedt));
	}

	
	// 파일저장 : File에 str 저장하여 파일 생성
	public static boolean saveFile(File file, String str) {
		boolean result = false;
		//File에 String 데이타 저장
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			bw.write(str);
			bw.flush();
			result = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) bw.close();
				if (fw != null) fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	//파일저장 : File에 JTextArea Text 저장
	public static boolean saveFile(File file, JTextArea jta) {
		boolean result = false;

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		
		//File에 String 데이타 저장
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			bw.write(jta.getText());
			bw.flush();
			result = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) bw.close();
				if (fw != null) fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	//파일저장 : File에 JTextPane Text 저장
	public static boolean saveFile(File file, JTextPane jtp) {
		boolean result = false;

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		
		//File에 String 데이타 저장
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			bw.write(jtp.getText());
			bw.flush();
			result = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) bw.close();
				if (fw != null) fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	//현재시간을 문자열(형태: yyyymmdd_hh24miss) 반환
	public static String getCurrentDateTime() {
		GregorianCalendar calendar = new GregorianCalendar();
		String strDateTime = ""; 
		int year   = calendar.get(Calendar.YEAR);
		int month  = calendar.get(Calendar.MONTH) + 1;
		int day    = calendar.get(Calendar.DAY_OF_MONTH);
//		int hour   = calendar.get(Calendar.HOUR);
		int hour24 = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		String strYear  = String.valueOf(year);
		String strMonth = month < 10 ? "0" + month : String.valueOf(month);
		String strDay = day < 10 ? "0" + day : String.valueOf(day);
		String strHour = hour24 < 10 ? "0" + hour24 : String.valueOf(hour24);
		String strMinute = minute < 10 ? "0" + minute : String.valueOf(minute);
		String strSecond = second < 10 ? "0" + second : String.valueOf(second);
		
		strDateTime = strYear + strMonth + strDay + "_" + strHour + strMinute + strSecond;
		return strDateTime;
	}
	
	//현재시간을 문자열(형태: yyyymmdd + delimeter + hh24miss) 반환
	public static String getCurrentDateTime(String delimeter) {
		GregorianCalendar calendar = new GregorianCalendar();
		String strDateTime = ""; 
		int year   = calendar.get(Calendar.YEAR);
		int month  = calendar.get(Calendar.MONTH) + 1;
		int day    = calendar.get(Calendar.DAY_OF_MONTH);
//				int hour   = calendar.get(Calendar.HOUR);
		int hour24 = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		String strYear  = String.valueOf(year);
		String strMonth = month < 10 ? "0" + month : String.valueOf(month);
		String strDay = day < 10 ? "0" + day : String.valueOf(day);
		String strHour = hour24 < 10 ? "0" + hour24 : String.valueOf(hour24);
		String strMinute = minute < 10 ? "0" + minute : String.valueOf(minute);
		String strSecond = second < 10 ? "0" + second : String.valueOf(second);
		
		strDateTime = strYear + strMonth + strDay 
				    + delimeter 
				    + strHour + strMinute + strSecond;
		return strDateTime;
	}	
	
	//파일저장 : JTextPane 의 데이타를 yyyymmdd_hh24miss.txt 에 저장
	// return : 저장된 파일명("./ + yyyymmdd_hh24miss.txt")
	public static String saveFile(JTextPane jtp) {
		String filename = "";
		String strFile = "./" + getCurrentDateTime() + ".txt";
		File file = new File(strFile);

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		
		//File에 String 데이타 저장
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			bw.write(jtp.getText());
			bw.flush();
			filename = strFile;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) bw.close();
				if (fw != null) fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return filename;
	}
	
	//파일저장 : JTextArea 의 데이타를 yyyymmdd_hh24miss.txt 에 저장
	// return : 저장된 파일명("./ + yyyymmdd_hh24miss.txt")
	public static String saveFile(JTextArea jta) {
		String filename = "";
		String strFile = "./" + getCurrentDateTime() + ".txt";
		File file = new File(strFile);

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		
		//File에 String 데이타 저장
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			bw.write(jta.getText());
			bw.flush();
			filename = strFile;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) bw.close();
				if (fw != null) fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return filename;
	}
	
	// 파일저장 : 문자열 데이타를 yyyymmdd_hh24miss.txt 에 저장
	// return : 저장된 파일명("./ + yyyymmdd_hh24miss.txt")
	public static String saveFile(String str) {
		String filename = "";
		String strFile = "./" + getCurrentDateTime() + ".txt";
		File file = new File(strFile);

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		
		//File에 String 데이타 저장
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			bw.write(str);
			bw.flush();
			filename = strFile;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) bw.close();
				if (fw != null) fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return filename;
	}
	
	// 파일읽어오기 : File에 데이타를 읽어와서 String 문자열 반환
	public static String loadFile(File file) {
		String result = null;
		// 파일에서 내용 가져오기
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer(4096);
		
		try {
			br = new BufferedReader(new FileReader(file));
			String str = null;
			while ((str = br.readLine()) != null) {
				sb.append(str +"\n");
			}
			result = sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (br != null) br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	// 파일읽어오기 : 파일명(dir + filename)을 String으로 받아 내용을 String으로 반환
	// filename : 디렉토리 + 파일명 + 확장자 정보가 포함된 문자열
	// return : filename의 내용을 String으로 return
	public static String loadFile(String filename) {
		if (filename == "") return "";
		String result = null;
		// 파일에서 내용 가져오기
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer(4096);
		File file = new File(filename);
		
		try {
			br = new BufferedReader(new FileReader(file));
			String str = null;
			while ((str = br.readLine()) != null) {
				sb.append(str + "\n");
			}
			result = sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (br != null) br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	// 파일다이얼로그를 이용하여 파일명(디렉토리 + 파일) 가져오기
	// currDir : 파일검색할 디렉토리 위치
	// return : 파일 다이얼로그를 통해 선택한 파일명(디렉토리 + 파일)
	public static String getFilenameFromFileOpenDialog(String currDir) {
		String filename = null;
		FileDialog fd = null;
		fd = new FileDialog(fd, "파일열기", FileDialog.LOAD);
		fd.setVisible(true);
		String dir = fd.getDirectory();
		String fname = fd.getFile();
		filename = dir + fname;
		return filename;
	}
}
