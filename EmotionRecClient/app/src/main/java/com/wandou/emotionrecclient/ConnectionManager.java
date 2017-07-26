package com.wandou.emotionrecclient;
//还需要添加一段保存配置的程序
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;

public class ConnectionManager extends  Thread{
	private static final String TAG = "DisplayActivity TAG";
	private Socket socket = null;

	private  SharedPreferences sp = null;
	private String model =  null;

	private String serverIP = null;
	private int portNum ;
	private boolean mFlag = false;

	public ConnectionManager(SharedPreferences sp, String model){
		this.model = model;
		this.sp = sp;
	}

	public void saveConfiguration (String IP, int port){
		serverIP = IP;
		portNum = port;
		Log.e(TAG, "host:" + IP + "  portNum:" + port);
		Editor editor = sp.edit();
		editor.putString("ADD_"+ model, IP);
		editor.putInt("PORT_" + model, port);
		editor.commit();
	}

	public void getConfiguration (){
		String ADD = sp.getString("ADD_"+ model, null);  //defValue Value to return if this preference does not exist.
		int PORT = sp.getInt("PORT_" + model, 20175);
		Constant.SERVERIP = ADD;
		Constant.PORT = PORT;
	}

	public String getADD (SharedPreferences sp){
		String add = sp.getString("ADD_" + model, null);  //defValue Value to return if this preference does not exist.
		return add;
	}

	public int getPORT (SharedPreferences sp){
		int port = sp.getInt("PORT_" + model, 20175);
		return port;
	}

	public void setFlag(boolean flag){
		this.mFlag = flag;
	}

	public boolean getFlag(){
		return mFlag;
	}

	private boolean login(String add, int port) {
		try {
			socket = new Socket(add, port);
			Log.e(TAG, "welldown");
		} catch (UnknownHostException e) {
			Log.e(TAG, "UnknownHost");
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			Log.e(TAG, "what's the fuck!");
			if (socket != null) {
				Log.e(TAG, "socket != null");
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
			return false;
		}
		Constant.SOCKET = socket;// 保存在全局变量中，以供DisplayActivity中使用
		return true;
	}

	@Override
	public void run() {
		try {

			if (login(serverIP, portNum)){
				setFlag(true);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
