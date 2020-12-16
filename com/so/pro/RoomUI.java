package com.so.pro;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

public class RoomUI extends JFrame implements ActionListener {

	EightClient client;
	Room room;

	JTextArea chatArea;
	JTextField chatField;
	JList uList;
	DefaultListModel model;
	
    ImageIcon icon;

	public RoomUI(EightClient client, Room room) {
		this.client = client;
		this.room = room;
		setTitle("Octopus ChatRoom : " + room.toProtocol());
		icon = new ImageIcon("icon2.png");
		this.setIconImage(icon.getImage());//타이틀바에 이미지넣기
		initialize();
	}

	private void initialize() {
		setBounds(100, 100, 502, 481);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		setResizable(false);

		// 채팅 패널
		final JPanel panel = new JPanel();
		panel.setBounds(12, 10, 300, 358);
		getContentPane().add(panel);
		panel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane, BorderLayout.CENTER);

		chatArea = new JTextArea();
		chatArea.setBackground(Color.WHITE);
		chatArea.setEditable(false); // 수정불가
		scrollPane.setViewportView(chatArea); // 화면 보임
		chatArea.append("♠매너 채팅 하세요!!\n");

		JPanel panel1 = new JPanel();
		// 글쓰는 패널
		panel1.setBounds(12, 378, 300, 34);
		getContentPane().add(panel1);
		panel1.setLayout(new BorderLayout(0, 0));

		chatField = new JTextField();
		panel1.add(chatField);
		chatField.setColumns(10);
		chatField.addKeyListener(new KeyAdapter() {
			// 엔터 버튼 이벤트
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					msgSummit();
				}
			}

		});

		// 참여자 패널
		JPanel panel2 = new JPanel();
		// 참여자 패널
		panel2.setBounds(324, 10, 150, 358);
		getContentPane().add(panel2);
		panel2.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane1 = new JScrollPane();
		panel2.add(scrollPane1, BorderLayout.CENTER);

		uList = new JList(new DefaultListModel());
		model = (DefaultListModel) uList.getModel();
		scrollPane1.setViewportView(uList);

		// send button
		JButton roomSendBtn = new JButton("보내기");
		roomSendBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				msgSummit();
				chatField.requestFocus();
			}
		});
		roomSendBtn.setBounds(324, 378, 75, 34);
		getContentPane().add(roomSendBtn);

		// exit button
		JButton roomExitBtn = new JButton("나가기");
		roomExitBtn.addMouseListener(new MouseAdapter() {		// 나가기 버튼
			@Override
			public void mouseClicked(MouseEvent e) {
				int ans = JOptionPane.showConfirmDialog(panel, "방을 삭제 하시겠습니까?", "삭제확인", JOptionPane.OK_CANCEL_OPTION);

				if (ans == 0) { // 삭제
					try {
						client.getUser().getDos().writeUTF(User.GETOUT_ROOM + "/" + room.getRoomNum());
						for (int i = 0; i < client.getUser().getRoomArray().size(); i++) {
							if (client.getUser().getRoomArray().get(i).getRoomNum() == room.getRoomNum()) {
								client.getUser().getRoomArray().remove(i);
							}
						}
						setVisible(false);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else { // 그냥 나가기
					setVisible(false);
				}
			}
		});
		roomExitBtn.setBounds(400, 378, 75, 34);
		getContentPane().add(roomExitBtn);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		setVisible(true);
		
		//////////////////////////////////////
		// 채팅내용 저장 및 가져오기 메뉴
		JMenu mnuSaveChat = new JMenu("채팅저장");
		mnuSaveChat.addActionListener(this);
		menuBar.add(mnuSaveChat);
		JMenuItem mitSaveChatToFile = new JMenuItem("파일로저장");
		mitSaveChatToFile.addActionListener(this);
		mnuSaveChat.add(mitSaveChatToFile);
		
		JMenu mnuLoadChat = new JMenu("저장채팅확인");
		mnuLoadChat.addActionListener(this);
		menuBar.add(mnuLoadChat);
		JMenuItem mitLoadChatFromFile = new JMenuItem("파일열기");
		mitLoadChatFromFile.addActionListener(this);
		mnuLoadChat.add(mitLoadChatFromFile);
		
		///////////////////////////////////////////////
		
		chatField.requestFocus();
		this.addWindowListener(new WindowAdapter() {	// 윈도우 나가기
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					client.getUser().getDos().writeUTF(User.GETOUT_ROOM + "/" + room.getRoomNum());
					for (int i = 0; i < client.getUser().getRoomArray().size(); i++) {
						if (client.getUser().getRoomArray().get(i).getRoomNum() == room.getRoomNum()) {
							client.getUser().getRoomArray().remove(i);
						}
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	private void msgSummit() {
		String string = chatField.getText();
		if (!string.equals("")) {
			try {
				// 채팅방에 메시지 보냄
				client.getDos()
						.writeUTF(User.ECHO02 + "/" + room.getRoomNum() + "/" + client.getUser().toString() + string);
				chatField.setText("");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
			case "파일로저장":
				String filename = UtilFileIO.saveFile(chatArea);
				JOptionPane.showMessageDialog(chatArea.getParent(), 
						"채팅내용을 파일명(" + filename + ")으로 저장하였습니다", 
						"채팅백업", JOptionPane.INFORMATION_MESSAGE);
				break;
			case "파일열기":
				filename = UtilFileIO.getFilenameFromFileOpenDialog("./");
				String text = UtilFileIO.loadFile(filename);
				TextViewUI textview = new TextViewUI(text);
				break;
		}
	}
}
	