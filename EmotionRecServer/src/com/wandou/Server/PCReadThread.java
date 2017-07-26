package com.wandou.Server;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.logging.Logger;

public class PCReadThread extends Thread {
	private static final String TAG = "ReadThread";
	private DataInputStream dis;
	private ControlSocket mSocket;
	private boolean mRunning, mQuit;

	public PCReadThread(ControlSocket mSocket, InputStream is) {
		this.mSocket = mSocket;
		dis = new DataInputStream(is);
	}
	
	public void quit() {
		try {
			dis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mRunning = false;
		mQuit = true;
	}

	@Override
	public void run() {
		mRunning = true;
		mQuit = false;
		try {
			while (mRunning) {
				if (mQuit)
					break;
				System.out.println("Ready to read");
				dis.readByte();
				int start = dis.readInt();
				if (start == Constant.START) {
					System.out.println("start = " + start);
					dis.readByte();
					int phoneScreenWidth = dis.readInt();
					dis.readByte();
					int phoneScreenHeight = dis.readInt();
					mSocket.setImgSize(phoneScreenWidth, phoneScreenHeight);
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		mRunning = false;
	}
}