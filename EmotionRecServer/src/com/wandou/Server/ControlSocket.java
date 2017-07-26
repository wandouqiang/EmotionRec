package com.wandou.Server;

/*
 * 用的是IP协议
 * */
import java.awt.AWTException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ControlSocket implements Runnable {
	private Socket mSocket = null;
	private ServerSocket serverSocket = null;
	private PCWriteThread writeThread = null;
	private PCReadThread readThread = null;
	private int portNum;
	private boolean mFlag = true;;

	public ControlSocket(int portNum) {
		this.portNum = portNum;
	}

	public void launchServer() {
		try {
			serverSocket = new ServerSocket(portNum);
			System.out.println("服务器打开！");
			while (mFlag) {
				mSocket = serverSocket.accept();// 同一客户端执行一次，否则阻塞
				System.out.println("Client connected:" + mSocket.getInetAddress() + ":" + mSocket.getPort());
				// server.setClientIP( mSocket.getInetAddress() + ":" +
				// mSocket.getPort());
				this.readThread = new PCReadThread(this, mSocket.getInputStream());
				this.writeThread = new PCWriteThread(mSocket.getOutputStream());
				this.startReadWrite();
			}
		} catch (AWTException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setImgSize(int w, int h) {
		writeThread.setBitmapSize(w, h);
	}

	public void setFlag(boolean flag) {
		this.mFlag = flag;
	}

	public void startReadWrite() {

		if (readThread != null) {
			readThread.start();
			System.out.println("ReadThread running!");
		}

		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (writeThread != null) {
			writeThread.start();
			System.out.println("WriteThread running!");
		}
	}

	public void quit() {
		try {
			if (serverSocket != null) {
				serverSocket.close();
			}
			if (mSocket != null) {
				mSocket.close();
			}
			System.out.println("Service closed!");
		} catch (IOException e) {
			System.out.println("failed to  close!");
			e.printStackTrace();
		}
		writeThread.quit();
		readThread.quit();
	}

	@Override
	public void run() {
		launchServer();
	}
}
