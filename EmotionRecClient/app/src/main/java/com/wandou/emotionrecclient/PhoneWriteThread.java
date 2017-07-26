package com.wandou.emotionrecclient;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PhoneWriteThread extends Thread {
	private static final String TAG = "WriteThread TAG";
	private DataOutputStream dos = null;
	private boolean mRunning, mQuit;

	public PhoneWriteThread(OutputStream os) {
		this.dos = new DataOutputStream(os);
	}

	public void quit() {
		try {
			dos.close();
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
		while (mRunning) {
			if (mQuit)
				break;
			try {
				dos.writeByte((byte) 5); 
				dos.writeInt(Constant.START);
				dos.writeByte((byte) 5);
				dos.writeInt(Constant.viewWidth);
				dos.writeByte((byte) 5);
				dos.writeInt(Constant.viewHeight);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
       mRunning = false;
	}
}
