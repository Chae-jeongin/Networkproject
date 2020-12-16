package com.so.pro;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

public class WaitRoomUI extends JFrame implements ActionListener {

	MsgeBox msgbox = new MsgeBox();
	String temp,id;
	
	int lastRoomNum = 100;
	JButton makeRoomBtn, getInRoomBtn, whisperBtn, sendBtn;
	JTree userTree;
	JList roomList;
	JTextField chatField;
	JTextArea waitRoomArea;
	JLabel lbid, lbnick;
	JTextField lbip;

	EightClient client;
	ArrayList<User> userArray; // 사용자 목록 배열
	String currentSelectedTreeNode;
	DefaultListModel model;
	DefaultMutableTreeNode level1;		
	DefaultMutableTreeNode level2;	
	DefaultMutableTreeNode level3;	
	
	JScrollPane scrollPane;
    ImageIcon icon;
    LoginUI login;

	public WaitRoomUI(EightClient eigClient) {
		setTitle("Octopus Chatting");
		userArray = new ArrayList<User>();
		client = eigClient;
		initialize();
	}

	private void initialize() {
		
		icon = new ImageIcon("icon2.png");
		this.setIconImage(icon.getImage());//타이틀바에 이미지넣기
		
		setBounds(100, 100, 700, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu basicMenus = new JMenu("파일");
		basicMenus.addActionListener(this);
		menuBar.add(basicMenus);

		JMenuItem mitSaveChatToFile = new JMenuItem("파일로저장");
		mitSaveChatToFile.addActionListener(this);
		basicMenus.add(mitSaveChatToFile);
		
		JMenuItem mitLoadChatFromFile = new JMenuItem("파일열기");
		mitLoadChatFromFile.addActionListener(this);
		basicMenus.add(mitLoadChatFromFile);
		
		JMenuItem exitItem = new JMenuItem("종료");
		exitItem.addActionListener(this);
		basicMenus.add(exitItem);

		JMenu updndel = new JMenu("변경/탈퇴");
		updndel.addActionListener(this);
		menuBar.add(updndel);

		JMenuItem changeInfo = new JMenuItem("회원정보 변경");
		changeInfo.addActionListener(this);
		updndel.add(changeInfo);
		
		JMenuItem withdrawMem = new JMenuItem("회원 탈퇴");
		withdrawMem.addActionListener(this);
		updndel.add(withdrawMem);
		
		JMenu helpMenus = new JMenu("도움말");
		helpMenus.addActionListener(this);
		menuBar.add(helpMenus);

		JMenuItem proInfoItem = new JMenuItem("프로그램 정보");
		proInfoItem.addActionListener(this);
		helpMenus.add(proInfoItem);
		getContentPane().setLayout(null);

		JPanel room = new JPanel();
		room.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "채 팅 방", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		room.setBounds(12, 10, 477, 215);
		getContentPane().add(room);
		room.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		room.add(scrollPane, BorderLayout.CENTER);

		// 리스트 객체와 모델 생성
		roomList = new JList(new DefaultListModel());
		roomList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int i = roomList.getFirstVisibleIndex();
				// System.out.println(">>>>>>>>>>>" + i);
				if (i != -1) {
					// 채팅방 목록 중 하나를 선택한 경우,
					// 선택한 방의 방번호를 전송
					String temp = (String) roomList.getSelectedValue();
					if(temp.equals(null)){
						return;
					}

					try {
						client.getUser().getDos().writeUTF(User.UPDATE_SELECTEDROOM_USERLIST + "/" + temp.substring(0, 3));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		model = (DefaultListModel) roomList.getModel();
		scrollPane.setViewportView(roomList);

		JPanel panel2 = new JPanel();
		room.add(panel2, BorderLayout.SOUTH);

		makeRoomBtn = new JButton("방 만들기");
		makeRoomBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		makeRoomBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// 방만들기 버튼 클릭
				createRoom();
			}
		});
		panel2.setLayout(new GridLayout(0, 2, 0, 0));
		panel2.add(makeRoomBtn);

		getInRoomBtn = new JButton("방 입장하기");
		getInRoomBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// 방 들어가기
				getIn();
			}
		});
		panel2.add(getInRoomBtn);

		JPanel user = new JPanel();
		user.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),	"사용자 목록", TitledBorder.CENTER,	TitledBorder.TOP, null, null));
		user.setBounds(501, 10, 171, 409);
		getContentPane().add(user);
		user.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane1 = new JScrollPane();
		user.add(scrollPane1, BorderLayout.CENTER);

		// 사용자목록, 트리구조
		userTree = new JTree();
		userTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				currentSelectedTreeNode = e.getPath().getLastPathComponent().toString();
			}
		});
		level1 = new DefaultMutableTreeNode("참여자");
		level2 = new DefaultMutableTreeNode("채팅방");
		level3 = new DefaultMutableTreeNode("대기실");
		level1.add(level2);
		level1.add(level3);
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(new ImageIcon("user.png"));
		renderer.setClosedIcon(new ImageIcon("wait.png"));
		renderer.setOpenIcon(new ImageIcon("open.png"));

		userTree.setCellRenderer(renderer);
		userTree.setEditable(false);

		DefaultTreeModel model = new DefaultTreeModel(level1);
		userTree.setModel(model);

		scrollPane1.setViewportView(userTree);

		JPanel panel1 = new JPanel();
		user.add(panel1, BorderLayout.SOUTH);
		panel1.setLayout(new GridLayout(1, 0, 0, 0));

		whisperBtn = new JButton("귓속말");
		
		whisperBtn.addActionListener(this);
		panel1.add(whisperBtn);

		JPanel waitroom = new JPanel();
		
		waitroom.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "대 기 실",	TitledBorder.CENTER, TitledBorder.TOP, null, Color.DARK_GRAY));
		waitroom.setBounds(12, 235, 477, 185);
		getContentPane().add(waitroom);
		waitroom.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		waitroom.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		JScrollPane scrollPane4 = new JScrollPane();
		panel.add(scrollPane4);

		chatField = new JTextField();

		chatField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					msgSummit();
				}
			}

		});
		scrollPane4.setViewportView(chatField);
		chatField.setColumns(10);

		sendBtn = new JButton("보내기");
		sendBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				msgSummit();
				chatField.requestFocus();
			}
		});
		panel.add(sendBtn);

		JScrollPane scrollPane2 = new JScrollPane();
		waitroom.add(scrollPane2, BorderLayout.CENTER);

		waitRoomArea = new JTextArea();
		waitRoomArea.setEditable(false);
		scrollPane2.setViewportView(waitRoomArea);

		JPanel info = new JPanel();
		lbid = new JLabel("-");
		info.add(lbid);
		lbnick = new JLabel("-");
		info.add(lbnick);
		lbip = new JTextField();
		lbip.setEditable(false);
		info.add(lbip);
		lbip.setColumns(10);

		chatField.requestFocus();
		setVisible(true);
		chatField.requestFocus();

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exit01();
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		id = client.user.getId();
		switch (e.getActionCommand()) {
		case "귓속말":
			// 닉네임 제외하고 아이디만 따옴
			StringTokenizer token = new StringTokenizer(currentSelectedTreeNode, "("); // 토큰 생성
			temp = token.nextToken(); // 토큰으로 분리된 스트링
			temp = token.nextToken();
			id = "/" + temp.substring(0, temp.length() - 1) + " ";
			chatField.setText(id);
			chatField.requestFocus();
			break;
		// 메뉴1 파일 메뉴
		case "회원정보 변경":
			DBRevise reDB = new DBRevise();
			reDB.myInfo(id);
			break;
		case "회원 탈퇴":
			DBDelete delDB = new DBDelete();

			int ans = JOptionPane.showConfirmDialog(this, "정말 탈퇴 하시겠습니까?", "탈퇴확인", JOptionPane.OK_CANCEL_OPTION);

			if (ans == 0) {
				int i = 0;
				i = delDB.InfoDel(id);
				if (i == 0) {
					// msgbox.messageBox(this, "탈퇴는 신중히..:)");
				} else {
					msgbox.messageBox(this, "탈퇴 성공하였습니다..:(");
					exit01();
				}
			}
			break;
		case "종료":
			int ans1 = JOptionPane.showConfirmDialog(this, "정말 종료 하시겠습니까?", "종료확인", JOptionPane.OK_CANCEL_OPTION);
			if (ans1 == 0) {
				// System.exit(0); // 강제 종료
				try {
					client.getUser().getDos().writeUTF(User.LOGOUT);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			break;
		case "파일로저장":
			String filename = UtilFileIO.saveFile(waitRoomArea);
			JOptionPane.showMessageDialog(waitRoomArea.getParent(), "채팅내용을 파일명(" + filename + ")으로 저장하였습니다", "채팅백업", JOptionPane.INFORMATION_MESSAGE);
			break;
		case "파일열기":
			filename = UtilFileIO.getFilenameFromFileOpenDialog("./");
			if (filename == "") break;
			String text = UtilFileIO.loadFile(filename);
			TextViewUI textview = new TextViewUI(text);
			break;
		case "프로그램 정보":
			maker();
			break;
		}

	}
	
	private void msgSummit() {
		String string = chatField.getText();// 메시지전송
		if (!string.equals("")) {
			if (string.substring(0, 1).equals("/")) {
				
				StringTokenizer token = new StringTokenizer(string, " "); // 토큰 생성
				String id = token.nextToken(); // 토큰으로 분리된 스트링
				String msg = token.nextToken();
				
				try {
					client.getDos().writeUTF(User.WHISPER + id + "/" + msg);
					waitRoomArea.append(id + "님에게 귓속말 : " + msg + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
				chatField.setText("");
			} else {

				try {
					// 대기실에 메시지 보냄
					client.getDos().writeUTF(User.ECHO01 + "/" + string);
				} catch (IOException e) {
					e.printStackTrace();
				}
				chatField.setText("");
			}
		}
	}

	private void exit01() {
		try {
			client.getUser().getDos().writeUTF(User.LOGOUT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createRoom() {
		String roomname = JOptionPane.showInputDialog("대화방 이름을 입력하세요~");////////////
		if(roomname==null) {	// 취소 버튼
			
		} else {
			Room newRoom = new Room(roomname);	// 방 객체 생성
			newRoom.setRoomNum(lastRoomNum);
			newRoom.setrUI(new RoomUI(client, newRoom));
			
			// 클라이언트가 접속한 방 목록에 추가
			client.getUser().getRoomArray().add(newRoom);
			
			try {
				client.getDos().writeUTF(User.CREATE_ROOM + "/" + newRoom.toProtocol());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	}

	private void getIn() {
		// 선택한 방 정보
		String selectedRoom = (String) roomList.getSelectedValue();
		StringTokenizer token = new StringTokenizer(selectedRoom, "/"); // 토큰 생성
		String rNum = token.nextToken();
		String rName = token.nextToken();

		Room theRoom = new Room(rName); // 방 객체 생성
		theRoom.setRoomNum(Integer.parseInt(rNum)); // 방번호 설정
		theRoom.setrUI(new RoomUI(client, theRoom)); // UI

		// 클라이언트가 접속한 방 목록에 추가
		client.getUser().getRoomArray().add(theRoom);

		try {
			client.getDos().writeUTF(User.GETIN_ROOM + "/" + theRoom.getRoomNum());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void maker() {
		JDialog maker = new JDialog();
		Maker m = new Maker();
		maker.setTitle("프로그램 정보");
		maker.getContentPane().add(m);
		maker.setSize(400, 170);
		maker.setVisible(true);
		maker.setLocation(400, 350);
		maker.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
}

class Maker extends JPanel {
	public Maker() {
		super();
		initialize();
	}

	private void initialize() {
		this.setLayout(new GridLayout(3, 1));

		JLabel j1 = new JLabel("       프로그램 제작자 : CHAEJEONGIN		");
		JLabel j2 = new JLabel("       수정한 사람 : CHAEJEONGIN		");
		JLabel j3 = new JLabel("       프로그램 버전 : 1.6.9 v  ( 2020 . 12 . 15 )		");

		this.add(j1);
		this.add(j2);
		this.add(j3);
	}
}