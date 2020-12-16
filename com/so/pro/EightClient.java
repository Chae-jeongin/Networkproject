package com.so.pro;

import java.awt.Graphics;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;

public class EightClient implements Runnable {

	static int PORT = 8888; // 서버포트번호
	static String IP = "203.236.209.193"; // 서버아이피주소
	Socket socket; // 소켓
	User user; // 사용자

	LoginUI login;
	WaitRoomUI waitRoom;
	DataInputStream dis = null;
	DataOutputStream dos = null;
	boolean ready = false;

	EightClient() {
        login = new LoginUI(this);
		
		Thread thread = new Thread(this);
		thread.start();
	}

	public static void main(String[] args) {
		System.out.println("Client start...1");
		new EightClient();
		
	} // main end
	
	

	@Override
	public void run() {
		//
		// 소켓 통신 시작
		//
		while (!ready) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// 사용자가 객체 생성 및 아이피설정
		user = new User(dis, dos);
		user.setIP(socket.getInetAddress().getHostAddress());

		// 메시지 리딩
		while (true) {
			try {
				String receivedMsg = dis.readUTF(); // 메시지 받기(대기)
				dataParsing(receivedMsg); // 메시지 해석
			} catch (IOException e) {
				e.printStackTrace();
				try {
					user.getDis().close();
					user.getDos().close();
					socket.close();
					break;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		alarmMsg("서버프로그램이 먼저 종료되었습니다.");
		// 채팅프로그램 종료
		waitRoom.dispose();
	}

	public boolean serverAccess() {
		if (!ready) {
			// 소켓이 연결이 이루어지지 않은 경우에만 실행
			// 처음 연결시에만 실행
			socket = null;
			IP = login.ipBtn.getText();
			try {
				// 서버접속
				InetSocketAddress inetSockAddr = new InetSocketAddress(InetAddress.getByName(IP), PORT);
				socket = new Socket();

				// 지정된 주소로 접속 시도 (3초동안)
				socket.connect(inetSockAddr, 3000);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// 접속이 되면 실행
			if (socket.isBound()) {
				// 입력, 출력 스트림 생성
				try {
					dis = new DataInputStream(socket.getInputStream());
					dos = new DataOutputStream(socket.getOutputStream());
				} catch (IOException e) {
					e.printStackTrace();
				}
				ready = true;
			}
		}
		return ready;
	}

	// 데이터를 구분
	public synchronized void dataParsing(String data) {
		StringTokenizer token = new StringTokenizer(data, "/"); // 토큰 생성
		String protocol = token.nextToken(); // 토큰으로 분리된 스트링
		String id, pw, rNum, nick, rName, msg, result;
		System.out.println("받은 데이터 : " + data);

		switch (protocol) {
		case User.LOGIN: // 로그인
			// 사용자가 입력한(전송한) 아이디와 패스워드
			result = token.nextToken();
			if (result.equals("OK")) {
				alarmMsg("로그인에 성공했습니다!");
				nick = token.nextToken();
				login(nick);
			} else {
				msg = token.nextToken();
				alarmMsg(msg);
			}
			break;
		case User.LOGOUT:
			logout();
			break;
		case User.MEMBERSHIP: // 회원가입 승인
			result = token.nextToken();
			break;
		case User.UPDATE_USERLIST: // 대기실 사용자 목록
			userList(token);
			break;
		case User.UPDATE_ROOM_USERLIST: // 채팅방 사용자 목록
			// 방번호읽기
			rNum = token.nextToken();
			userList(rNum, token);
			break;
		case User.UPDATE_SELECTEDROOM_USERLIST: // 대기실에서 선택한 채팅방의 사용자 목록
			selectedRoomUserList(token);
			break;
		case User.UPDATE_ROOMLIST: // 방 목록
			roomList(token);
			break;
		case User.ECHO01: // 대기실 에코
			msg = token.nextToken();
			echoMsg(msg);
			break;
		case User.ECHO02: // 채팅방 에코
			rNum = token.nextToken();
			msg = token.nextToken();
			echoMsgToRoom(rNum, msg);
			break;
		case User.WHISPER: // 귓속말
			id = token.nextToken();
			nick = token.nextToken();
			msg = token.nextToken();
			whisper(id, nick, msg);
			break;
		}
	}

	private void logout() {
		try {
			alarmMsg("채팅 프로그램을 종료합니다!");
			waitRoom.dispose();
			user.getDis().close();
			user.getDos().close();
			socket.close();
			waitRoom = null;
			user = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 채팅방 내부 사용자 리스트
	private void userList(String rNum, StringTokenizer token) {
		for (int i = 0; i < user.getRoomArray().size(); i++) {
			if (Integer.parseInt(rNum) == user.getRoomArray().get(i).getRoomNum()) {

				// 기존에 리스트가 있을 경우 지워줌
				if (user.getRoomArray().get(i).getrUI().model != null)
					user.getRoomArray().get(i).getrUI().model.removeAllElements();

				while (token.hasMoreTokens()) {
					// 아이디와 닉네임을 읽어서 유저 객체 하나를 생성
					String id = token.nextToken();
					String nick = token.nextToken();
					User tempUser = new User(id, nick);

					user.getRoomArray().get(i).getrUI().model.addElement(tempUser.toString());
				}
			}
		}
	}

	// 선택한 채팅방의 사용자 리스트
	private void selectedRoomUserList(StringTokenizer token) {
		// 서버로부터 유저리스트(채팅방)를 업데이트하라는 명령을 받음

		if (!waitRoom.level2.isLeaf()) {
			// 리프노드가 아니고, 차일드가 있다면 모두 지움
			waitRoom.level2.removeAllChildren();
		}
		while (token.hasMoreTokens()) {
			// 아이디와 닉네임을 읽어서 유저 객체 하나를 생성
			String id = token.nextToken();
			String nick = token.nextToken();
			User tempUser = new User(id, nick);

			// 채팅방 사용자노드에 추가
			waitRoom.level2.add(new DefaultMutableTreeNode(tempUser.toString()));
		}
		waitRoom.userTree.updateUI();
	}

	// 대기실 사용자 리스트
	private void userList(StringTokenizer token) {
		// 서버로부터 유저리스트(대기실)를 업데이트하라는 명령을 받음

		if (waitRoom == null) {
			return;
		}

		if (!waitRoom.level3.isLeaf()) {
			// 리프노드가 아니고, 차일드가 있다면 모두 지움
			waitRoom.level3.removeAllChildren();
		}
		while (token.hasMoreTokens()) {
			// 아이디와 닉네임을 읽어서 유저 객체 하나를 생성
			String id = token.nextToken();
			String nick = token.nextToken();
			User tempUser = new User(id, nick);

			for (int i = 0; i < waitRoom.userArray.size(); i++) {
				if (tempUser.getId().equals(waitRoom.userArray.get(i))) {
				}
				if (i == waitRoom.userArray.size()) {
					// 배열에 유저가 없으면 추가해줌
					waitRoom.userArray.add(tempUser);
				}
			}
			// 대기실 사용자노드에 추가
			waitRoom.level3.add(new DefaultMutableTreeNode(tempUser.toString()));
		}
		waitRoom.userTree.updateUI();
	}

	// 서버로부터 방리스트를 업데이트하라는 명령을 받음
	private void roomList(StringTokenizer token) {
		String rNum, rName;
		Room room = new Room();

		// 기존에 리스트가 있을 경우 지워줌
		if (waitRoom.model != null) {
			waitRoom.model.removeAllElements();
		}

		while (token.hasMoreTokens()) {
			rNum = token.nextToken();
			rName = token.nextToken();
			int num = Integer.parseInt(rNum);

			// 라스트룸넘버를 업데이트 (최대값+1)
			if (num >= waitRoom.lastRoomNum) {
				waitRoom.lastRoomNum = num + 1;
			}
			room.setRoomNum(num);
			room.setRoomName(rName);

			waitRoom.model.addElement(room.toProtocol());
		}
	}

	private void alarmMsg(String string) {
		int i = JOptionPane.showConfirmDialog(null, string, "메시지", JOptionPane.CLOSED_OPTION, JOptionPane.INFORMATION_MESSAGE);
		// 확인 누르면 종료
		if (i == 0) { }
	}

	private void login(String nick) {
		// 로그인정보 가져옴
		user.setId(login.idText.getText());
		user.setNickName(nick);

		// 로그인창 닫고 대기실창 열기
		login.dispose();
		waitRoom = new WaitRoomUI(EightClient.this);
		waitRoom.lbid.setText(user.getId());
		waitRoom.lbip.setText(user.getIP());
		waitRoom.lbnick.setText(user.getNickName());
	}

	private void whisper(String id, String nick, String msg) {
		waitRoom.waitRoomArea.append("(" + id + ")님의 귓속말 : " + msg + "\n");
	}

	private void echoMsg(String msg) {
		// 커서 위치 조정
		if (waitRoom != null) {
			waitRoom.waitRoomArea.setCaretPosition(waitRoom.waitRoomArea.getText().length());
			waitRoom.waitRoomArea.append(msg + "\n");
		}
	}

	private void echoMsgToRoom(String rNum, String msg) {
		for (int i = 0; i < user.getRoomArray().size(); i++) {
			if (Integer.parseInt(rNum) == user.getRoomArray().get(i).getRoomNum()) {
				// 사용자 -> 방배열 -> 유아이 -> 텍스트에어리어
				// 커서 위치 조정
				user.getRoomArray().get(i).getrUI().chatArea.setCaretPosition(user.getRoomArray().get(i).getrUI().chatArea.getText().length());
				// 에코
				user.getRoomArray().get(i).getrUI().chatArea.append(msg + "\n");
			}
		}
	}

	// getter, setter
	public static int getPORT() {
		return PORT;
	}

	public static void setPORT(int pORT) {
		PORT = pORT;
	}

	public static String getIP() {
		return IP;
	}

	public static void setIP(String iP) {
		IP = iP;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public LoginUI getLogin() {
		return login;
	}

	public void setLogin(LoginUI login) {
		this.login = login;
	}

	public WaitRoomUI getRestRoom() {
		return waitRoom;
	}

	public void setRestRoom(WaitRoomUI restRoom) {
		this.waitRoom = restRoom;
	}

	public DataInputStream getDis() {
		return dis;
	}

	public void setDis(DataInputStream dis) {
		this.dis = dis;
	}

	public DataOutputStream getDos() {
		return dos;
	}

	public void setDos(DataOutputStream dos) {
		this.dos = dos;
	}

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

}