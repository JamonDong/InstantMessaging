import java.io.*;
import java.net.*;
import java.util.*;

public class Server extends Thread {

	private ServerSocket serverSocket;
	private ServerFrame serverFrame;
	private static Vector userOnline = new Vector(1, 1);
	private static Vector v = new Vector(1, 1);
	//���캯��
	public Server() {
		serverFrame = new ServerFrame();
		try {
			serverSocket = new ServerSocket(6668);// ����˿ں�6668
			InetAddress address = InetAddress.getLocalHost();
			serverFrame.serverNameTextField.setText(address.getHostName());// ��ȡ��������
			System.out.println(address.getHostName());
			serverFrame.IPTextField.setText(address.getHostAddress());// ��ȡ������IP��ַ
			System.out.println(address.getHostAddress());
			serverFrame.portTextField.setText("6668");
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.start(); // �����߳�
	}

	public void run() {

		try {
			while (true) {
				Socket client = serverSocket.accept();
				new Connection(serverFrame, client, userOnline, v); // ֧�ֶ��߳�
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Server();

	}

}
