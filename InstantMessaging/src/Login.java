import java.awt.Color;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Login extends JFrame implements ActionListener {
	private static final long serialVersionUID = 7663206300308166519L;
	
	private JPanel loginPanel;
	private JButton loginButton, registerButton, exitButton;
	private JLabel serverLabel, userNameLabel, passwordLabel;
	private JTextField userNameTextField, serverTextField;
	private JPasswordField passwordPasswordField;
	private String serverIp;

	//���캯������ʼ����¼����
	public Login() {
		super("��¼");
		loginPanel = new JPanel();
		this.getContentPane().add(loginPanel);

		serverLabel = new JLabel("������:");
		userNameLabel = new JLabel("�û���:");
		passwordLabel = new JLabel("����:");
		serverTextField = new JTextField(20);
		serverTextField.setText("127.0.0.1");
		userNameTextField = new JTextField(20);
		passwordPasswordField = new JPasswordField(20);
		loginButton = new JButton("��¼");
		registerButton = new JButton("ע��");
		exitButton = new JButton("�˳�");

		loginPanel.setLayout(null);

		serverLabel.setBounds(130, 50, 150, 30);
		serverTextField.setBounds(200, 50, 150, 25);
		userNameLabel.setBounds(130, 100, 150, 30);
		userNameTextField.setBounds(200, 100, 150, 25);
		passwordLabel.setBounds(130, 150, 150, 30);
		passwordPasswordField.setBounds(200, 150, 150, 25);
		loginButton.setBounds(50, 200, 80, 25);
		registerButton.setBounds(200, 200, 80, 25);
		exitButton.setBounds(350, 200, 80, 25);

		Font fontstr = new Font("����", Font.PLAIN, 12);
		serverLabel.setFont(fontstr);
		serverTextField.setFont(fontstr);
		userNameLabel.setFont(fontstr);
		userNameTextField.setFont(fontstr);
		passwordLabel.setFont(fontstr);
		passwordPasswordField.setFont(fontstr);
		loginButton.setFont(fontstr);
		registerButton.setFont(fontstr);
		exitButton.setFont(fontstr);

		userNameLabel.setForeground(Color.BLACK);
		passwordLabel.setForeground(Color.BLACK);
		loginButton.setBackground(Color.WHITE);
		registerButton.setBackground(Color.WHITE);
		exitButton.setBackground(Color.WHITE);

		loginPanel.add(serverLabel);
		loginPanel.add(serverTextField);
		loginPanel.add(userNameLabel);
		loginPanel.add(userNameTextField);
		loginPanel.add(passwordLabel);
		loginPanel.add(passwordPasswordField);
		loginPanel.add(loginButton);
		loginPanel.add(registerButton);
		loginPanel.add(exitButton);

		// ���õ�¼����
		setResizable(false);
		setSize(500, 300);
		setVisible(true);
		
		// ������ťע�����
		loginButton.addActionListener(this);
		registerButton.addActionListener(this);
		exitButton.addActionListener(this);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object source = e.getSource();
		if (source.equals(loginButton)) {
			if (userNameTextField.getText().equals("") || passwordPasswordField.getText().equals("")) {	// �ж��û����������Ƿ�Ϊ��
				JOptionPane op1 = new JOptionPane();
				op1.showMessageDialog(null, "�û��������벻��Ϊ��");
			} else {
				serverIp = serverTextField.getText();
				login();
			}
		}
		if (source.equals(registerButton)) {
			serverIp = serverTextField.getText();
			this.dispose();
			System.out.println("ע��");
			new Register(serverIp);
		}
		if (source == exitButton) {
			System.exit(0);
		}
	}

	public void login() {
		// TODO Auto-generated method stub
		// ��ȡ�ͻ�����ϸ����
		User temp = new User();
		temp.name = userNameTextField.getText();
		temp.password = passwordPasswordField.getText();

		try {
			// ���������������
			Socket toServer;
			toServer = new Socket(serverIp, 6668);
			ObjectOutputStream streamToServer = new ObjectOutputStream(toServer.getOutputStream());//�����������User����
			// д�ͻ���ϸ���ϵ�������socket
			streamToServer.writeObject((User) temp);
			// �����Է�����socket�ĵ�¼״̬
			BufferedReader fromServer = new BufferedReader(new InputStreamReader(toServer.getInputStream()));
			String status = fromServer.readLine();					//�ӷ�������ȡ��Ϣ
			if (status.equals("��¼�ɹ�")) {							//�����ܵ�����ϢΪ����¼�ɹ�������ʾ�û��ɹ���¼
				new ChatWin((String)temp.name, serverIp);			//�û��ɹ���¼����ʾ���촰��
				this.dispose();
				// �ر�������
				streamToServer.close();
				fromServer.close();
				toServer.close();
			} else {
				JOptionPane.showMessageDialog(null, status);
				// �ر�������
				streamToServer.close();
				fromServer.close();
				toServer.close();
			}
		} catch (HeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
