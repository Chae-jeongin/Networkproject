package com.so.pro;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class DBLogin {

	String id = null;
	String pw = null;

	Statement stmt = null;
	ResultSet rs = null;
	String url = "jdbc:oracle:thin:@203.236.209.193:1521:xe"; // 오라클 포트번호1521/@이후에는 IP주소
	String sql = null;
	Properties info = null;
	Connection cnn = null;

	int checkIDPW(String id, String pw) {
		this.id = id;
		this.pw = pw;
		int result = 1;

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver"); // 알아서 들어간다..conn로
			info = new Properties();
			info.setProperty("user", "scott");
			info.setProperty("password", "tiger");
			cnn = DriverManager.getConnection(url, info); // 연결할 정보를 가지고있는 드라이버매니저를 던진다
			stmt = cnn.createStatement();

			sql = "select * from joinDB where id='" + id + "'";
			rs = stmt.executeQuery(sql); // 읽어오는거라 다르다 비교해	//리턴타입이 ResultSet

			if (rs.next() == false || (id.isEmpty()) == true) { // id가 존재x
				result = 1;
			} else {
				sql = "select * from (select * from joinDB where id='" + id + "')";
				rs = stmt.executeQuery(sql);
				while (rs.next() == true) { 		// 다음값의
					if (rs.getString(2).equals(pw)) { // pw와 같은지 비교
						result = 0; 		// 같으면 로그인 성공
					} else {				// 아이디는같고 pw가 다른경우
						result = 1;
					}
				}
			}
		} catch (Exception ee) {
			System.out.println("문제있음");
			ee.printStackTrace();
		}
		return result;
	}
}
