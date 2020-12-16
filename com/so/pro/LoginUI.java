package com.so.pro;

import java.awt.Graphics;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginUI extends JFrame implements TextListener {

	boolean confirm = false;
	TextField idText;
	TextField pwText;
	JButton loginBtn, signUpBtn;
	JButton ipBtn;
	EightClient client;

	DBJoin jdb;
	JScrollPane scrollPane;
    ImageIcon icon;
    LoginUI login;
	
	// 생성자
	public LoginUI(EightClient eigClient) {
		
		setTitle("로그인");
		ServerAddress sd = new ServerAddress(this);
		this.client = eigClient;
		loginUIInitialize();
	}

	// 메서드
	private void loginUIInitialize() {
		setBounds(100, 100, 335, 218); // 창 크기 조절
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		setResizable(false);

		icon = new ImageIcon("icon2.png");
		this.setIconImage(icon.getImage());//타이틀바에 이미지넣기

		JPanel panel = new JPanel();
		panel.setBounds(12, 10, 295, 160); // 로그인 비밀번호 나타내기
		getContentPane().add(panel); // 로그인 화면 나타내기
		panel.setLayout(null);

		JLabel jbNewLabel1 = new JLabel("아이디");
		jbNewLabel1.setBounds(60, 55, 57, 15); // "아이디" 위치
		panel.add(jbNewLabel1);

		JLabel jbNewLabel2 = new JLabel("비밀번호");
		jbNewLabel2.setBounds(60, 86, 57, 15);
		panel.add(jbNewLabel2);

		idText = new TextField();
		idText.setBounds(129, 52, 116, 21);
		panel.add(idText);
		idText.setColumns(10);

		pwText = new TextField();
		pwText.addKeyListener(new KeyAdapter() {

			// 로그인 엔터 버튼
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					msgSummit();
				}
			}

		});
		pwText.setBounds(129, 83, 116, 21);
		panel.add(pwText);
		pwText.setColumns(10);

		idText.addTextListener(this);
		pwText.addTextListener(this);

		loginBtn = new JButton("로그인");
		loginBtn.setEnabled(false);
		loginBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});

		loginBtn.addMouseListener(new MouseAdapter() {
			// 클릭시 승인 메서드로 넘어간다.
			@Override
			public void mouseClicked(MouseEvent e) {
				if (loginBtn.isEnabled() == true) {
					msgSummit();
				}
			}
		});

		loginBtn.setBounds(50, 111, 97, 23);
		panel.add(loginBtn);

		signUpBtn = new JButton("회원가입");
		signUpBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		signUpBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// 회원가입
				jdb = new DBJoin();
				jdb.JoinDBPanel();
			}
		});
		signUpBtn.setBounds(149, 111, 97, 23);
		panel.add(signUpBtn);

		JLabel jbNewLabe3 = new JLabel("서버 아이피");
		jbNewLabe3.setBounds(12, 10, 78, 15);
		panel.add(jbNewLabe3);

		ipBtn = new JButton("");
		ipBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ServerAddress sd = new ServerAddress(LoginUI.this);
				setVisible(false); // true이면 창이 그냥 닫힌다.
			}
		});
		ipBtn.setBounds(93, 6, 97, 23);
		panel.add(ipBtn);
	}

	// 메시지 승인 메서드
	private void msgSummit() {
		new Thread(new Runnable() {
			public void run() {

				// 소켓생성(로그인 접속이 안된다)
				if (client.serverAccess()) {
					try {
						// 로그인정보(아이디+패스워드) 전송
						client.getDos().writeUTF(User.LOGIN + "/" + idText.getText() + "/" + pwText.getText());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			} // run() start
		}).start();
	} // msgSummit() end

	@Override
	public void textValueChanged(TextEvent e) {
		if (idText.getText().equals("") || pwText.getText().equals("")) {
			loginBtn.setEnabled(false);
		} else {
			loginBtn.setEnabled(true);
		}
	}

}
