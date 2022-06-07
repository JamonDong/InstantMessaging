import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Date;
import java.util.Vector;

public class Connection extends Thread {

	private Socket clientSocket;//��ͻ���ͨѶ
	private Vector<User> userOnline;//���ߵ��û�
	private Vector<Communication> userChat;//����ļ���
	private ObjectInputStream fromClient;//�ӿͻ�����������������
	private PrintStream toClient;//�����ͻ��˵Ĵ�ӡ��
	private static Vector registerList = new Vector();//ע����û�
	private ServerFrame serverFrame;//����������
	private Object tempObj;//��ʱ����
	//���캯��
	public Connection(ServerFrame sFrame, Socket client, Vector useronline, Vector userchat) {
		// TODO Auto-generated constructor stub
		serverFrame = sFrame;
		clientSocket = client;
		userOnline = useronline;
		userChat = userchat;

		try {
			fromClient = new ObjectInputStream(clientSocket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			try {
				clientSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				return;
			}
		}
		
		// ������д���ͻ���
		try {
			toClient = new PrintStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			try {
				clientSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				return;
			}
		}	
		this.start();	
	}

	public void run() {	//������ͻ��˵�ͨѶ�߳�
		try {
			tempObj = (Object) fromClient.readObject();
			if (tempObj.getClass().getName().equals("User")) {	//�ӿͻ��˴����������Ķ�����������ΪUser����ζ��ִ�е��ǵ�¼
				loginService();
			}
			if (tempObj.getClass().getName().equals("RegisterInfo")) {	//�ӿͻ��˴����������Ķ�����������ΪRegisterInfo����ζ��ִ�е���ע��
				registerService();
			}
			if (tempObj.getClass().getName().equals("Message")) {	//�ӿͻ��˴����������Ķ�����������ΪMessage����ζ��ִ�е��Ƿ�������
				messageService();
			}
			if (tempObj.getClass().getName().equals("Communication")) {	//�ӿͻ��˴����������Ķ�����������ΪCommunication����ζ��ִ�е���?
				communicationService();
			}
			if (tempObj.getClass().getName().equals("Logout")) {	//�ӿͻ��˴����������Ķ�����������ΪLogout����ζ��ִ�е����˳���¼
				logoutService();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e1) {
			System.out.println("�������ʧ��...");
			e1.printStackTrace();
		} finally {
			try {
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

	//��¼����
	public void loginService() {

		try {
			User clientMessage2 = (User) tempObj;

			// ���ļ�
			FileInputStream file3 = new FileInputStream("RegisterInformation.txt");	//�����ļ�������ʵ��
			ObjectInputStream objInput1 = new ObjectInputStream(file3);	//��������������ʵ��
			registerList = (Vector) objInput1.readObject();				//���ļ��ж������

			int find = 0; // 1��ʾ�ҵ����û��������û��Ѿ�ע���ˣ�0��ʾ���û�δע��
			for (int i = 0; i < registerList.size(); i++) {				//����ע����е�ÿһ���û�
				RegisterInfo registerInfo = (RegisterInfo) registerList.elementAt(i);

				if (registerInfo.name.equals(clientMessage2.name)) {	//�ҵ����û�
					find = 1;
					if (!registerInfo.password.equals(clientMessage2.password)) {//�������
						toClient.println("���벻��ȷ");
						break;
					} else {											// �ж��Ƿ��Ѿ���¼		
						int login_flag = 0;								//0��ʾδ��¼��1��ʾ�Ѿ���¼
						for (int j = 0; j < userOnline.size(); j++) {	//���������û�
							String _custName = ((User) userOnline.elementAt(j)).name;
							if (clientMessage2.name.equals(_custName)) {//�������û����ҵ����û������û��Ѿ���¼���޷��ٴε�¼
								login_flag = 1;
								break;
							}
						}

						if (userOnline.size() >= 50) {
							toClient.println("��¼�������࣬���Ժ�����");		//��ͻ��˷��ص�¼�����������Ϣ
							break;
						}

						if (login_flag == 0) {					//���û�δ��¼
							userOnline.addElement(clientMessage2);// �����û���ӵ������û��б���
							toClient.println("��¼�ɹ�");			//��ͻ��˷��ص�¼�ɹ�����Ϣ
							Date time = new Date();
							logWrite("�û� " + clientMessage2.name + "��"+ time.toLocaleString() + "��¼" + "\n");//�ѵ�¼��Ϣ��¼�ڷ�������־��
							freshServerUserList();				//ˢ�������û���Ϣ
							break;
						} else {
							toClient.println("���û��ѵ�¼");		//��ͻ��˷��ظ��û��ѵ�¼����Ϣ
						}
					}
				} else {
					continue;
				}
			}
			if (find == 0) {									//δ�ҵ����û�
				toClient.println("û������û�������ע��");			//��ͻ��˷��ظ��û�δע�����Ϣ
			}

			file3.close();										//�ر��ļ�������
			objInput1.close();									//�رն���������
			fromClient.close();									//�رմӿͻ�����������������
		} catch (ClassNotFoundException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	public void logWrite(String logInfo) {
		String newlogInfo = serverFrame.logTextArea.getText() + "\n" + logInfo;
		serverFrame.logTextArea.setText(newlogInfo);
	}
	
	private void freshServerUserList() {
		String[] userList = new String[50];
		User user = null;
		for (int j = 0; j < userOnline.size(); j++) {
			user = (User) userOnline.get(j);
			userList[j] = user.name;
		}
		serverFrame.list.setListData(userList);						//��������û����û���
		serverFrame.numberTextField.setText("" + userOnline.size());//��������û�������
		//serverFrame.lblUserCount.setText("��ǰ��������:" + userOnline.size());
	}
	
	//ע��
	public void registerService() {								
		try {
			int flag = 0; 										//0��ʾ��������1��ʾ����
			RegisterInfo clientInfo = (RegisterInfo) tempObj;
			File userList = new File("RegisterInformation.txt");
			if (userList.length() != 0)							// �ж��Ƿ��ǵ�һ��ע���û�������ǵ�һ��ע���û����Ͳ���Ҫ�ж��Ƿ�������ֱ��ע�ἴ��
			{
				ObjectInputStream objInput = new ObjectInputStream(new FileInputStream(userList));
				registerList = (Vector) objInput.readObject();
				// �ж��Ƿ�������
				for (int i = 0; i < registerList.size(); i++) {
					RegisterInfo registerInfo = (RegisterInfo) registerList.elementAt(i);
					if (registerInfo.name.equals(clientInfo.name)) {
						toClient.println("ע�����ظ�,����������");
						flag = 1;
						break;
					} else if (registerInfo.name.equals("������")) {	//�û����������ˡ���ΪȺ���ı�־���ͻ�����ռ�ø�����
						toClient.println("��ֹʹ�ô�ע����,����������");
						flag = 1;
						break;
					}
				}
			}
			if (flag == 0) {										// �����ע���û�
				registerList.addElement(clientInfo);				//��ע���ĩβ����ע����û���Ϣ
				FileOutputStream file = new FileOutputStream(userList);
				ObjectOutputStream objout = new ObjectOutputStream(file);
				objout.writeObject(registerList);					// �������е���д���ļ�
				
				toClient.println(clientInfo.name + "ע��ɹ�");		
				Date t = new Date();
				logWrite("�û�" + clientInfo.name + "ע��ɹ�, " + "ע��ʱ��:" + t.toLocaleString() + "\n");// ��ע��ɹ���Ϣд���������־
				file.close();										//�ر��ļ������
				objout.close();										//�رն��������
				fromClient.close();									//�رմӿͻ�����������������
			}
		} catch (ClassNotFoundException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	public void messageService() {
		try {
			Message message = new Message();
			message.userOnlineVector = userOnline;
			message.chatVector = userChat;
			message.notice = "" + serverFrame.serverMessage;

			ObjectOutputStream outputstream = new ObjectOutputStream(clientSocket.getOutputStream());
			outputstream.writeObject((Message)message);

			clientSocket.close();
			outputstream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	//������Ϣ����
	public void communicationService() {
		// �����յ��Ķ���ֵ����������Ϣ�����л�����
		Communication communication = new Communication();
		communication = (Communication) tempObj;

		communicationLog(communication);

		// ��������Ϣ�����л�������ӵ�����������Ϣ��ʸ����
		userChat.addElement((Communication) communication);
		return;
	}
	
	public void communicationLog(Communication communication) {
		String newlog = serverFrame.messageTextArea.getText();
		Date date = new Date();
		if (!communication.privateChat) {
			newlog += "\n";
			newlog += ("[" + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() + "]");
			newlog += communication.chatUser;
			newlog += "->";
			newlog += communication.chatToUser;
			newlog += ":";
			newlog += communication.chatMessage;
		}
		serverFrame.messageTextArea.setText(newlog);
	}
	
	public void logoutService() {
		Logout tempLogout = new Logout();
		tempLogout = (Logout) tempObj;

		removeUser(tempLogout);
		Date t = new Date();

		logWrite("�û� " + tempLogout.logoutName + " ��" + t.toLocaleString() + "�˳�" + '\n');

		freshServerUserList();
	}
	
	private void removeUser(Logout tempLogout) {
		Vector<User> tempVec = new Vector<User>();
		User tempUser = null;
		for (int j = 0; j < userOnline.size(); j++) {
			tempUser = (User) userOnline.get(j);
			if (!tempLogout.logoutName.equals(tempUser.name)) {
				tempVec.add(tempUser);
			}
		}
		userOnline.removeAllElements();
		for (int j = 0; j < tempVec.size(); j++) {
			tempUser = (User) tempVec.get(j);
			userOnline.add(tempUser);
		}

	}
	
	
}
