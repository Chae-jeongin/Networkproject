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
		
		frame = new JFrame("ȸ������");
		logPanel = new JPanel();
		logPanel1 = new JPanel(new GridLayout(4, 1));
		logPanel2 = new JPanel(new GridLayout(4, 1));
		logPanel3 = new JPanel();

		icon = new ImageIcon("icon2.png");
		frame.setIconImage(icon.getImage());//Ÿ��Ʋ�ٿ� �̹����ֱ�
		
		JLabel idLabel = new JLabel(" I D ", JLabel.CENTER);
		JLabel pwLabel = new JLabel(" P W ", JLabel.CENTER);
		JLabel nameLabel = new JLabel("�� ��", JLabel.CENTER);
		JLabel baLabel = new JLabel("�� �� �� �� ", JLabel.CENTER);
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
		checkBt.addMouseListener(this); // addMouseListener�̺�Ʈ

		frame.add(logPanel, BorderLayout.NORTH);
		frame.add(logPanel1, BorderLayout.WEST);
		frame.add(logPanel2, BorderLayout.CENTER);
		frame.add(logPanel3, BorderLayout.EAST);

		JPanel logPanel4 = new JPanel();
		JLabel askLabel = new JLabel("�����Ͻðڽ��ϱ�?");
		joinBtn = new JButton("����");
		// joinBtn.setEnabled(false);
		JButton cancleBtn = new JButton("���");
		joinBtn.addMouseListener(this); // addMouseListener�̺�Ʈ
		logPanel4.add(askLabel);
		logPanel4.add(joinBtn);
		logPanel4.add(cancleBtn);
		frame.add(logPanel4, BorderLayout.SOUTH);

		// if((idTf.getText().isEmpty())==true ||
		// (pwTf.getText().isEmpty())==true){ 
		// joinBtn.setEnabled(true);
		// }

		// ��� ��ư
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
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // �츮�� �α���â�� �Բ�
		// �������

	}// JoinDBPanel() end
		// ///////////////////////////////////////////////////////////////////////////////////////////////////////////

	Statement stmt = null;
	ResultSet rs = null;
	String url = "jdbc:oracle:thin:@203.236.209.193:1521:xe"; // ����Ŭ ��Ʈ��ȣ1521/@���Ŀ��� IP�ּ�
	String sql = null;
	Properties info = null;
	Connection cnn = null;

	@Override
	public void mouseClicked(MouseEvent e) {

		// TextField Ŭ���� ���������ֱ�
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
			Class.forName("oracle.jdbc.driver.OracleDriver"); // �˾Ƽ� ����..conn��
			info = new Properties();
			info.setProperty("user", "scott");
			info.setProperty("password", "tiger");
			cnn = DriverManager.getConnection(url, info); // ������ ������ �������ִ� ����̹��Ŵ����� ������
			stmt = cnn.createStatement();

			// ���̺��� ����
			/*
			 * sql =
			 * "create table joinDB(id varchar2(20) primary key,pw varchar2(20) not null,name varchar2(30),barth number(6))"
			 * ; stmt.execute(sql); System.out.println("���̺�����Ϸ�");
			 */

			// id üũ��ư
			if (e.getSource().equals(checkBt)) {
				sql = "select * from joinDB where id='" + idTf.getText() + "'";
				rs = stmt.executeQuery(sql); // �о���°Ŷ� �ٸ��� ����//����Ÿ���� ResultSet

				if (rs.next() == true || (idTf.getText().isEmpty()) == true) { // �̹� id�� �����Ѵٸ�
					msgbox.messageBox(logPanel3, "�ش� ID�� ����� �Ұ����մϴ�. �ٽ� �ۼ����ּ���.");
				} else {
					msgbox.messageBox(logPanel3, "��� ������ ID �Դϴ�.");
				}
			}

			// ���� ��ư
			if (e.getSource().equals(joinBtn)) {
				sql = "select * from joinDB where id='" + idTf.getText() + "'";

				rs = stmt.executeQuery(sql); // �о���°Ŷ� �ٸ��� ����	//����Ÿ���� ResultSet

				if (rs.next() == true) { // �̹� id�� �����Ѵٸ�
					msgbox.messageBox(logPanel3, "ID Check�� �ʿ��մϴ�.");

				} else if ((idTf.getText().isEmpty()) == true || (pwTf.getText().isEmpty()) == true
						|| (nameTf.getText().isEmpty()) || (birthTf.getText().isEmpty())) {		// id Ȥ�� pw ����������
					msgbox.messageBox(logPanel3, "����ִ� ĭ�� �����մϴ�.");
				} else if ((birthTf.getText().length()) != 6) {
					msgbox.messageBox(logPanel3, "������� ������ �߸��Ǿ����ϴ�."); 	// �ƴѰ��
				} else {

					sql = "insert into joinDB values ('" + idTf.getText() + "','" + pwTf.getText() + "','"
							+ nameTf.getText() + "','" + birthTf.getText() + "')";
					stmt.executeUpdate(sql);
					msgbox.messageBox(logPanel3, "�����մϴ�.���� �Ǽ̽��ϴ�.");
					frame.dispose(); // â �ݱ�
					dbClose();
				}
			}
		} catch (Exception ee) {
			System.out.println("��������");
			ee.printStackTrace();
		}
	}// mouseClicked �̺�Ʈ end

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
