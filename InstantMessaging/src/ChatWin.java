import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class ChatWin extends Thread implements ActionListener {

	static JFrame chatFrame;
	JPanel chatPanel;
	JButton sendButton;
	JLabel userListLabel, userMessageLabel, chatUserLabel, backLabel;
	JTextField messageTextField;
	java.awt.List UserList;
	TextArea messageTextArea;
	JComboBox userComboBox;
	JCheckBox privateChatCheckBox;
	String serverIp, loginName;
	Thread thread;
	Message messageObj = null;
	String serverMessage = "";

	public ChatWin(String name, String IP) {
		// TODO Auto-generated constructor stub
		serverIp = IP;
		loginName = name;
		chatFrame = new JFrame("��ӭ"+ name +"ʹ�ñ���ʱͨѶ���" );
		chatPanel = new JPanel();
		chatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		chatFrame.getContentPane().add(chatPanel);

		Font fntDisp1 = new Font("����", Font.PLAIN, 12);

		String list[] = { "������" };
		//logoutButton = new JButton("�˳�");
		sendButton = new JButton("����");
		userListLabel = new JLabel("�����û�");
		userMessageLabel = new JLabel("������Ϣ");
		chatUserLabel = new JLabel("���Ͷ���:");
		UserList = new java.awt.List();
		messageTextField = new JTextField(170);
		userComboBox = new JComboBox(list);
		privateChatCheckBox = new JCheckBox("˽��");
		messageTextArea = new TextArea("", 300, 200, TextArea.SCROLLBARS_VERTICAL_ONLY);// ֻ�����¹���
		messageTextArea.setForeground(new Color(0, 0, 0));
		messageTextArea.setEditable(false); // ����д��

		chatPanel.setLayout(null);
		//logoutButton.setBounds(520, 500, 160, 40);
		sendButton.setBounds(120, 500, 160, 40);

		userListLabel.setBounds(620, 0, 120, 40);
		userMessageLabel.setBounds(10, 0, 120, 40);
		chatUserLabel.setBounds(470, 450, 80, 40);

		UserList.setBounds(620, 40, 150, 400);
		messageTextArea.setBounds(10, 40, 600, 400);
		messageTextField.setBounds(10, 450, 450, 40);
		userComboBox.setBounds(540, 455, 70, 30);
		privateChatCheckBox.setBounds(620, 455, 60, 20);
		//logoutButton.setFont(fntDisp1);
		sendButton.setFont(fntDisp1);
		userListLabel.setFont(fntDisp1);
		userMessageLabel.setFont(fntDisp1);
		chatUserLabel.setFont(fntDisp1);
		userComboBox.setFont(fntDisp1);
		privateChatCheckBox.setFont(fntDisp1);

		userListLabel.setForeground(Color.black);
		userMessageLabel.setForeground(Color.black);
		chatUserLabel.setForeground(Color.black);
		userComboBox.setForeground(Color.black);
		privateChatCheckBox.setForeground(Color.black);
		UserList.setBackground(Color.white);
		messageTextArea.setBackground(Color.white);
		//logoutButton.setBackground(Color.WHITE);
		sendButton.setBackground(Color.WHITE);
		//chatPanel.add(logoutButton);
		chatPanel.add(sendButton);
		chatPanel.add(userListLabel);
		chatPanel.add(userMessageLabel);
		chatPanel.add(chatUserLabel);
		chatPanel.add(UserList);
		chatPanel.add(messageTextArea);
		chatPanel.add(messageTextField);
		chatPanel.add(userComboBox);
		chatPanel.add(privateChatCheckBox);

		chatFrame.addWindowListener(new Windowclose());
		//logoutButton.addActionListener(this);
		sendButton.addActionListener(this);
		UserList.addActionListener(this);
		messageTextField.addActionListener(this);

		// ��������ҳ����Ϣˢ���߳�
		Thread thread = new Thread(this);
		thread.start();

		chatFrame.setSize(800, 590);
		chatFrame.setVisible(true);
		chatFrame.setResizable(false);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object source = (Object) e.getSource();
//		if (source.equals(logoutButton)) {
//			logout();
//		}
		if (source.equals(sendButton)) {
			sendMessage();
		}
		if (source.equals(UserList)) // ˫���б��
		{
			changeUser();
		}
	}



	public void run() {
		int intMessageCounter = 0;//��Ҫ��ʾ����Ϣ����ʼλ��
		int intUserTotal = 0;
		boolean isFirstLogin = true;	// �ж��Ƿ�յ�½
		boolean isFound; 				// �ж��Ƿ��ҵ��û�
		Vector user_logout = new Vector();

		try {
			while(true) {
				
				Socket toServer;
				//���������������
				toServer = new Socket(serverIp, 6668);
				
				// ��Message��Ϣ����������
				messageObj = new Message();
				ObjectOutputStream streamtoserver = new ObjectOutputStream(toServer.getOutputStream());
				streamtoserver.writeObject((Message) messageObj);
				
				// �������Է�������Message��Ϣ
				ObjectInputStream streamfromserver = new ObjectInputStream(toServer.getInputStream());
				messageObj = (Message) streamfromserver.readObject();
				
				// ˢ��������Ϣ�б�
				if (isFirstLogin) 										// ����û��ոյ�½
				{
					intMessageCounter = messageObj.chatVector.size();	// ����ʾ���û���½ǰ����������
					isFirstLogin = false;
				}
				
				if (!serverMessage.equals(messageObj.notice)) {
					serverMessage = messageObj.notice;
					messageTextArea.append("[ϵͳ��Ϣ]��" + serverMessage+"\n");
				}
				
				//����ÿһ����Ϣ
				for (int i = intMessageCounter; i < messageObj.chatVector.size(); i++) {
					Communication temp = (Communication) messageObj.chatVector.elementAt(i);

					String temp_message;
					if (temp.chatUser.equals(loginName)) {			//������������Լ�
						if (temp.chatToUser.equals(loginName)) {	//�������������Լ�
							temp_message = "�û����ã������Լ����Լ�˵��Ŷ~" + "\n";
						}else{										//�����������������û�
							if (!temp.privateChat){ 				// �������˽��	
								temp_message = " �� �� " + temp.chatToUser + " " + "˵��" + temp.chatMessage + "\n";
							}else{ 									//�����˽��
								temp_message = " �� ˽���� " + temp.chatToUser + " " + "˵��" + temp.chatMessage + "\n";
							}
						}
					} else {										//����������������û�
						if (temp.chatToUser.equals(loginName)) {	//��������������
							if (!temp.privateChat){ 				// �������˽��
								temp_message = " " + temp.chatUser + " �� �� " + "˵��" + temp.chatMessage + "\n";
							} else {								//�����˽��
								temp_message = " " + temp.chatUser + " ˽���� �� " + "˵��" + temp.chatMessage + "\n";
							}										
						} else {									//��������������
							if (!temp.chatUser.equals(temp.chatToUser)){ // ����Է�û����������
								if (!temp.privateChat){				// �������˽��						
									temp_message = " " + temp.chatUser + " �� " + temp.chatToUser + " " + "˵��" + temp.chatMessage + "\n";
								} else {							// �����˽�ģ�����ʾ��Ϣ
									temp_message = "";
								}
							} else {								// ����Է������������ʾ��Ϣ
								temp_message = "";
							}
						}
					}
					messageTextArea.append(temp_message);
					intMessageCounter++;
				}

				//ˢ�������û�
				UserList.clear();
				for (int i = 0; i < messageObj.userOnlineVector.size(); i++) {
					String user = ((User) messageObj.userOnlineVector.elementAt(i)).name;
					UserList.addItem(user);
				}
				
				//��ʾ�û����������ҵ���Ϣ
				if (messageObj.userOnlineVector.size() > intUserTotal) {
					String tempstr = ((User) messageObj.userOnlineVector.elementAt(messageObj.userOnlineVector.size() - 1)).name;
					if (!tempstr.equals(loginName)) {				//����ս��������ҵĲ����Լ�������ʾ�û����������ҵ���Ϣ
						messageTextArea.append("��ӭ " + tempstr + " ������������" + "\n");
					}
				}
				
				//��ʾ�û��뿪�����ҵ���Ϣ
				if (messageObj.userOnlineVector.size() < intUserTotal) {
					for (int i = 0; i < user_logout.size(); i++) {
						isFound = false;
						for (int j = 0; j < messageObj.userOnlineVector.size(); j++) {
							String tempstr = ((User) user_logout.elementAt(i)).name;
							if (tempstr.equals(((User) messageObj.userOnlineVector.elementAt(j)).name)) {
								isFound = true;
								break;
							}
						}
						if (!isFound) 								//û�з��ָ��û�
						{
							String tempstr = ((User) user_logout.elementAt(i)).name;
							if (!tempstr.equals(loginName)) {		//������뿪�����ҵĲ����Լ�������ʾ�û��뿪�����ҵ���Ϣ
								messageTextArea.append("�û� " + tempstr + " �뿪��������" + "\n");
							}
						}
					}
				}
				user_logout = messageObj.userOnlineVector;
				intUserTotal = messageObj.userOnlineVector.size();
				streamtoserver.close();
				streamfromserver.close();
				toServer.close();
				Thread.sleep(3000);
			}

		} catch (Exception e) {
			@SuppressWarnings("unused")
			JOptionPane jop = new JOptionPane();
			JOptionPane.showMessageDialog(null, "�������ӷ�������");
			e.printStackTrace();
			chatFrame.dispose();
		}

	}
	
	// "�˳�"��ť
	public void logout() {
		Logout logout = new Logout();
		logout.logoutName = loginName;
		// �����˳���Ϣ
		try {
			Socket toServer = new Socket(serverIp, 6668);
			// �������������Ϣ
			ObjectOutputStream outObj = new ObjectOutputStream(toServer.getOutputStream());
			outObj.writeObject(logout);
			System.out.println("�˳�");
			outObj.close();
			toServer.close();
			
			chatFrame.dispose();
		} catch (Exception e) {
		}

	}

	//�������ڹر���Ӧ
	class Windowclose extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			logout();
		}
	}
	
	private void sendMessage() {
		// TODO Auto-generated method stub
		Communication communication = new Communication();
		communication.chatUser = loginName;
		communication.chatMessage = messageTextField.getText();
		communication.chatToUser = String.valueOf(userComboBox.getSelectedItem());
		communication.privateChat = privateChatCheckBox.isSelected() ? true : false;
		try {
			Socket toServer = new Socket(serverIp, 6668);
			ObjectOutputStream outObj = new ObjectOutputStream(toServer.getOutputStream());
			outObj.writeObject(communication);
			messageTextField.setText(""); // ����ı���
			outObj.close();
			toServer.close();
		} catch (Exception e) {
		}
	}

	// ����ѡ�û���ӵ�cmbUser��
		public void changeUser() {

			boolean inComboBox = false;
			String selected = UserList.getSelectedItem();
			for (int i = 0; i < userComboBox.getItemCount(); i++) {
				if (selected.equals(userComboBox.getItemAt(i))) {
					inComboBox = true;
					break;
				}
			}
			if (!inComboBox) {
				userComboBox.insertItemAt(selected, 0);
			}
			userComboBox.setSelectedItem(selected);
		}
}
