package com.wandou.Server;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class ServiceApp extends JFrame implements ActionListener{

	private Button start;
	private Button stop  ;
	private JLabel localIPLabel ;
	private JLabel portLabel ;
	private JLabel 	clientIPlaJLabel ;
	private JLabel authorJLabel;
	//private JTextField  localIPfField ;	
	private JTextField portTextField ;
	private JTextField  clienIPField ;
	private JComboBox localIPCheck;

	public static String localIP;
	public static int portNum = 20172;
	
	ControlSocket mControlSocket;
	
	public ServiceApp() {
		// 窗体初始化
		init();
		getServerIp();
		portNum = Integer.valueOf(portTextField.getText());

		// 注册监听事件
		start.addActionListener(this);
		stop.addActionListener(this);
	}

	//事件响应
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == start) {
			start.setEnabled(false);
			stop.setEnabled(true);
			portNum = Integer.valueOf(portTextField.getText());
			mControlSocket = new ControlSocket(portNum);
			mControlSocket.setFlag(true);
			new Thread(mControlSocket).start();
		}
		if (e.getSource() == stop) {
			start.setEnabled(true);
			stop.setEnabled(false);
			mControlSocket.setFlag(false);
			mControlSocket.quit();
			System.exit(0);
		}
	}

	public void getServerIp() {
		String address;
		Enumeration netInterfaces;
		NetworkInterface ni;
		Enumeration cardipaddress;
		InetAddress ip;

		try {
			netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				ni = (NetworkInterface) netInterfaces.nextElement();
				cardipaddress = ni.getInetAddresses();
				while (cardipaddress.hasMoreElements()) {
					ip = (InetAddress) cardipaddress.nextElement();
					if (!ip.getHostAddress().equalsIgnoreCase("127.0.0.1")) {
						address = ip.getHostAddress();
						localIPCheck.addItem(address);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);//关闭窗口时关闭应用程序

		// 安全退出程序即关闭窗口
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				mControlSocket.setFlag(false);
				mControlSocket.quit();
				System.exit(0);
			}
		});

		start = new Button("Start");
		start.setEnabled(true);
		start.setBackground(Color.LIGHT_GRAY);
		stop = new Button("Stop");
		stop.setEnabled(false);
		stop.setBackground(Color.LIGHT_GRAY);
		
		localIPLabel = new JLabel("服务端IP:");
		portLabel = new JLabel("监听端口:");
		clientIPlaJLabel = new JLabel("客户端IP:");
		authorJLabel = new JLabel("请在客户端输入本机IP地址和端口号");
		
		localIPCheck = new JComboBox();
		
		//localIPfField = new JTextField("");
		//localIPfField.setEditable(false);
		portTextField = new JTextField(String.valueOf(portNum));
		clienIPField = new JTextField("");
		clienIPField.setEditable(false);
		
		JPanel panel= new JPanel();
		 panel.setLayout(null);

		localIPLabel.setBounds(10, 20, 60, 25);
		localIPLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(localIPLabel);

		localIPCheck.setBounds(80, 20, 150, 25);
		panel.add(localIPCheck);
		
		start.setBounds(240, 20, 100, 40);
		panel.add(start);
		
		portLabel.setBounds(10, 50, 60, 25);
		portLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(portLabel);
		
		portTextField.setBounds(80, 50, 150, 25);
		portTextField.setHorizontalAlignment(JTextField.LEFT);
		panel.add(portTextField);
		
		stop.setBounds(240, 65, 100, 40);
		panel.add(stop);
		
		clientIPlaJLabel.setBounds(10, 80, 60, 25);
		clientIPlaJLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(clientIPlaJLabel);
		
		clienIPField.setBounds(80, 80, 150, 25);
		clienIPField.setHorizontalAlignment(JTextField.LEFT);
		panel.add(clienIPField);
	
		authorJLabel.setBounds(10, 125, 250, 35);
		authorJLabel.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(authorJLabel);
		
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel,BorderLayout.CENTER);	
        
    	this.setBackground(Color.LIGHT_GRAY);
		this.setTitle("情绪识别系统服务端");
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setLocation(500, 300);
		this.setSize(400, 180);
		this.setVisible(true);
		this.setResizable(false);
	}

	public static void main(String []args){
		ServiceApp service = new ServiceApp();
		
	}
}
