package com.so.pro;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.swing.*;

public class DBJoin implements MouseListener {
	JFrame frame;
	JPanel logPanel;
	JPanel logPanel1;
	JPanel logPanel2;
	JPanel logPanel3;
	JTextField idTf, pwTf, nameTf, birthTf = null;
	JButton joinBtn, checkBt;
    ImageIcon icon;

	MsgeBox msgbox = new MsgeBox();

	void JoinDBPanel() {
		
		frame = new JFrame("회원가입");
		logPanel = new JPanel();
		logPanel1 = new JPanel(new GridLayout(4, 1));
		logPanel2 = new JPanel(new GridLayout(4, 1));
		logPanel3 = new JPanel();

		icon = new ImageIcon("icon2.png");
		frame.setIconImage(icon.getImage());//타이틀바에 이미지넣기
		
		JLabel idLabel = new JLabel(" I D ", JLabel.CENTER);
		JLabel pwLabel = new JLabel(" P W ", JLabel.CENTER);
		JLabel nameLabel = new JLabel("이 름", JLabel.CENTER);
		JLabel baLabel = new JLabel("생 년 월 일 ", JLabel.CENTER);
		logPanel1.add(idLabel);
		logPanel1.add(pwLabel);
		logPanel1.add(nameLabel);
		logPanel1.add(baLabel);

		idTf = new JTextField(20);
		idTf.addMouseListener(this);
		pwTf = new JTextField(20);
		pwTf.addMouseListener(this);
		nameTf = new JTextField(20);
		nameTf.addMouseListener(this);
		birthTf = new JTextField("ex)901231", 20);
		birthTf.addMouseListener(this);
		logPanel2.add(idTf);
		logPanel2.add(pwTf);
		logPanel2.add(nameTf);
		logPanel2.add(birthTf);

		checkBt = new JButton("ID Check");
		logPanel3.add(checkBt, BorderLayout.NORTH);
		checkBt.addMouseListener(this); // addMouseListener이벤트

		frame.add(logPanel, BorderLayout.NORTH);
		frame.add(logPanel1, BorderLayout.WEST);
		frame.add(logPanel2, BorderLayout.CENTER);
		frame.add(logPanel3, BorderLayout.EAST);

		JPanel logPanel4 = new JPanel();
		JLabel askLabel = new JLabel("가입하시겠습니까?");
		joinBtn = new JButton("가입");
		// joinBtn.setEnabled(false);
		JButton cancleBtn = new JButton("취소");
		joinBtn.addMouseListener(this); // addMouseListener이벤트
		logPanel4.add(askLabel);
		logPanel4.add(joinBtn);
		logPanel4.add(cancleBtn);
		frame.add(logPanel4, BorderLayout.SOUTH);

		// if((idTf.getText().isEmpty())==true ||
		// (pwTf.getText().isEmpty())==true){ 
		// joinBtn.setEnabled(true);
		// }

		// 취소 버튼
		cancleBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				frame.dispose();
				dbClose();
			}
		});

		frame.setBounds(450, 250, 350, 200);
		frame.setResizable(false);
		frame.setVisible(true);
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 살리면 로그인창도 함께
		// 사라진다

	}// JoinDBPanel() end
		// ///////////////////////////////////////////////////////////////////////////////////////////////////////////

	Statement stmt = null;
	ResultSet rs = null;
	String url = "jdbc:oracle:thin:@203.236.209.193:1521:xe"; // 오라클 포트번호1521/@이후에는 IP주소
	String sql = null;
	Properties info = null;
	Connection cnn = null;

	@Override
	public void mouseClicked(MouseEvent e) {

		// TextField 클릭시 예시지워주기
		if (e.getSource().equals(idTf)) {
			idTf.setText("");
		} else if (e.getSource().equals(pwTf)) {
			pwTf.setText("");
		} else if (e.getSource().equals(nameTf)) {
			nameTf.setText("");
		} else if (e.getSource().equals(birthTf)) {
			birthTf.setText("");
		}

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver"); // 알아서 들어간다..conn로
			info = new Properties();
			info.setProperty("user", "scott");
			info.setProperty("password", "tiger");
			cnn = DriverManager.getConnection(url, info); // 연결할 정보를 가지고있는 드라이버매니저를 던진다
			stmt = cnn.createStatement();

			// 테이블이 생성
			/*
			 * sql =
			 * "create table joinDB(id varchar2(20) primary key,pw varchar2(20) not null,name varchar2(30),barth number(6))"
			 * ; stmt.execute(sql); System.out.println("테이블생성완료");
			 */

			// id 체크버튼
			if (e.getSource().equals(checkBt)) {
				sql = "select * from joinDB where id='" + idTf.getText() + "'";
				rs = stmt.executeQuery(sql); // 읽어오는거라 다르다 비교해//리턴타입이 ResultSet

				if (rs.next() == true || (idTf.getText().isEmpty()) == true) { // 이미 id가 존재한다면
					msgbox.messageBox(logPanel3, "해당 ID는 사용이 불가능합니다. 다시 작성해주세요.");
				} else {
					msgbox.messageBox(logPanel3, "사용 가능한 ID 입니다.");
				}
			}

			// 가입 버튼
			if (e.getSource().equals(joinBtn)) {
				sql = "select * from joinDB where id='" + idTf.getText() + "'";

				rs = stmt.executeQuery(sql); // 읽어오는거라 다르다 비교해	//리턴타입이 ResultSet

				if (rs.next() == true) { // 이미 id가 존재한다면
					msgbox.messageBox(logPanel3, "ID Check가 필요합니다.");

				} else if ((idTf.getText().isEmpty()) == true || (pwTf.getText().isEmpty()) == true
						|| (nameTf.getText().isEmpty()) || (birthTf.getText().isEmpty())) {		// id 혹은 pw 비어있을경우
					msgbox.messageBox(logPanel3, "비어있는 칸이 존재합니다.");
				} else if ((birthTf.getText().length()) != 6) {
					msgbox.messageBox(logPanel3, "생년월일 서식이 잘못되었습니다."); 	// 아닌경우
				} else {

					sql = "insert into joinDB values ('" + idTf.getText() + "','" + pwTf.getText() + "','"
							+ nameTf.getText() + "','" + birthTf.getText() + "')";
					stmt.executeUpdate(sql);
					msgbox.messageBox(logPanel3, "축하합니다.가입 되셨습니다.");
					frame.dispose(); // 창 닫기
					dbClose();
				}
			}
		} catch (Exception ee) {
			System.out.println("문제있음");
			ee.printStackTrace();
		}
	}// mouseClicked 이벤트 end

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	public void dbClose() {
		try {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (cnn != null)
				cnn.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

}// class end
