package com.so.pro;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TextViewUI extends JFrame {
	
	final int FRAME_WIDTH  = 500;
	final int FRAME_HEIGHT = 600; 
	JTextArea taChatText;
	
	public TextViewUI() {
		setTitle("채팅내용확인\n");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		setSize(500, 600);
		
		JPanel pnlCenter = new JPanel(new BorderLayout());
		taChatText = new JTextArea();
		taChatText.setBackground(Color.WHITE);
//		taChatText.setText("글자가 표시되는가?");
//		pnlCenter.add(taChatText, BorderLayout.CENTER);
		JScrollPane spChatText = new JScrollPane(taChatText, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pnlCenter.add(spChatText, BorderLayout.CENTER);
		
		add(pnlCenter, BorderLayout.CENTER);
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getScreenSize();
		int screenHeight = d.height;
		int screenWidth  = d.width;
		
		setLocation((screenWidth - FRAME_WIDTH) / 2, (screenHeight - FRAME_HEIGHT) / 2);
		setVisible(true);
	}
	
	public TextViewUI(String text) {
		this();
		taChatText.setText(text+"\n");
	}
	public TextViewUI(JTextArea jta) {
		this();
		taChatText.setText(jta.getText());
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new TextViewUI();
	}

}
