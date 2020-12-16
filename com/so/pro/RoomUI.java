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
		this.setIconImage(icon.getImage());//Ÿ��Ʋ�ٿ� �̹����ֱ�
		initialize();
	}

	private void initialize() {
		setBounds(100, 100, 502, 481);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		setResizable(false);

		// ä�� �г�
		final JPanel panel = new JPanel();
		panel.setBounds(12, 10, 300, 358);
		getContentPane().add(panel);
		panel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane, BorderLayout.CENTER);

		chatArea = new JTextArea();
		chatArea.setBackground(Color.WHITE);
		chatArea.setEditable(false); // �����Ұ�
		scrollPane.setViewportView(chatArea); // ȭ�� ����
		chatArea.append("���ų� ä�� �ϼ���!!\n");

		JPanel panel1 = new JPanel();
		// �۾��� �г�
		panel1.setBounds(12, 378, 300, 34);
		getContentPane().add(panel1);
		panel1.setLayout(new BorderLayout(0, 0));

		chatField = new JTextField();
		panel1.add(chatField);
		chatField.setColumns(10);
		chatField.addKeyListener(new KeyAdapter() {
			// ���� ��ư �̺�Ʈ
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					msgSummit();
				}
			}

		});

		// ������ �г�
		JPanel panel2 = new JPanel();
		// ������ �г�
		panel2.setBounds(324, 10, 150, 358);
		getContentPane().add(panel2);
		panel2.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane1 = new JScrollPane();
		panel2.add(scrollPane1, BorderLayout.CENTER);

		uList = new JList(new DefaultListModel());
		model = (DefaultListModel) uList.getModel();
		scrollPane1.setViewportView(uList);

		// send button
		JButton roomSendBtn = new JButton("������");
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
		JButton roomExitBtn = new JButton("������");
		roomExitBtn.addMouseListener(new MouseAdapter() {		// ������ ��ư
			@Override
			public void mouseClicked(MouseEvent e) {
				int ans = JOptionPane.showConfirmDialog(panel, "���� ���� �Ͻðڽ��ϱ�?", "����Ȯ��", JOptionPane.OK_CANCEL_OPTION);

				if (ans == 0) { // ����
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
				} else { // �׳� ������
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
		// ä�ó��� ���� �� �������� �޴�
		JMenu mnuSaveChat = new JMenu("ä������");
		mnuSaveChat.addActionListener(this);
		menuBar.add(mnuSaveChat);
		JMenuItem mitSaveChatToFile = new JMenuItem("���Ϸ�����");
		mitSaveChatToFile.addActionListener(this);
		mnuSaveChat.add(mitSaveChatToFile);
		
		JMenu mnuLoadChat = new JMenu("����ä��Ȯ��");
		mnuLoadChat.addActionListener(this);
		menuBar.add(mnuLoadChat);
		JMenuItem mitLoadChatFromFile = new JMenuItem("���Ͽ���");
		mitLoadChatFromFile.addActionListener(this);
		mnuLoadChat.add(mitLoadChatFromFile);
		
		///////////////////////////////////////////////
		
		chatField.requestFocus();
		this.addWindowListener(new WindowAdapter() {	// ������ ������
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
				// ä�ù濡 �޽��� ����
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
			case "���Ϸ�����":
				String filename = UtilFileIO.saveFile(chatArea);
				JOptionPane.showMessageDialog(chatArea.getParent(), 
						"ä�ó����� ���ϸ�(" + filename + ")���� �����Ͽ����ϴ�", 
						"ä�ù��", JOptionPane.INFORMATION_MESSAGE);
				break;
			case "���Ͽ���":
				filename = UtilFileIO.getFilenameFromFileOpenDialog("./");
				String text = UtilFileIO.loadFile(filename);
				TextViewUI textview = new TextViewUI(text);
				break;
		}
	}
}
	