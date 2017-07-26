package com.wandou.emotionrecclient;

import java.io.IOException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class DisplayActivity extends Activity {
	private static final String TAG = "DisplayActivity TAG";

	private RelativeLayout loginLayoutVR, loginLayoutEEG;
	private LinearLayout linearLayout;
	private MySurfaceView surfaceViewVR, surfaceViewEEG;

	private SharedPreferences preferences = null;
	private ConnectionManager cmEEG = null, cmVR = null;

	private EditText serverIPTextVR, portNumTextVR, serverIPTextEEG, portNumTextEEG;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_display);

		//xml布局文件中相应的需要定义为com.wandou.emotionrecclient.MySurfaceView
		DisplayMetrics dm  = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int phoneScreenWidth = dm.widthPixels;
		int phoneScreenHeight = dm.heightPixels;

		int heightVR = (int)(phoneScreenHeight * 0.45);
		int heightEEG = (int)(phoneScreenHeight * 0.45);

		preferences = getSharedPreferences(Constant.FILE_NAME, MODE_PRIVATE);
		initVR(phoneScreenWidth, heightVR);
		initEEG(phoneScreenWidth, heightEEG);
	}

	private void initVR(int width, int height){
		loginLayoutVR = (RelativeLayout) findViewById(R.id.loginLayoutVR);
		surfaceViewVR = (MySurfaceView) findViewById(R.id.surfaceViewVR);
		Button buttonConnectVR = (Button) findViewById(R.id.buttonConnectVR);
		serverIPTextVR = (EditText) findViewById(R.id.serverIPVR);
		portNumTextVR = (EditText) findViewById(R.id.portNumVR);
		cmVR = new ConnectionManager(preferences, "VR");

		String ADD = cmVR.getADD(preferences);
		int PORT = cmVR.getPORT(preferences);

		if (ADD != null) { //ADD内有返回值
			serverIPTextVR.setText(ADD);
			portNumTextVR.setText(String.valueOf(PORT));
		}

		Constant.viewWidth =  width;
		Constant.viewHeight = height;
		surfaceViewVR.setRect(width, height);

		buttonConnectVR.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String serverIP = serverIPTextVR.getText().toString();
				int portNum = Integer.parseInt(portNumTextVR.getText().toString());
				cmVR.saveConfiguration(serverIP, portNum);
				Constant.SOCKET = null;
				connect(cmVR, surfaceViewVR, loginLayoutVR);
			}
		});
	}

	private void initEEG(int width, int height){
		loginLayoutEEG = (RelativeLayout)findViewById(R.id.loginLayoutEEG);
		surfaceViewEEG = (MySurfaceView)findViewById(R.id.surfaceViewEEG);
		linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
		Button buttonConnectEEG = (Button) findViewById(R.id.buttonConnectEEG);
		serverIPTextEEG = (EditText) findViewById(R.id.serverIPEEG);
		portNumTextEEG = (EditText) findViewById(R.id.portNumEEG);

		cmEEG = new ConnectionManager(preferences, "EEG");

		String ADD = cmEEG.getADD(preferences);
		int PORT = cmEEG.getPORT(preferences);

		if (ADD !=null){ //ADD内有返回值
			serverIPTextEEG.setText(ADD);
			portNumTextEEG.setText(String.valueOf(PORT));
		}

		Constant.viewWidth =  width;
		Constant.viewHeight = height;
		surfaceViewEEG.setRect(width, height);

		buttonConnectEEG.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String serverIP = serverIPTextEEG.getText().toString();
				int portNum = Integer.parseInt(portNumTextEEG.getText().toString());
				cmEEG.saveConfiguration(serverIP, portNum);
				linearLayout.setVisibility(View.VISIBLE);
				Constant.SOCKET = null;
				connect(cmEEG, surfaceViewEEG, loginLayoutEEG);
			}
		});
	}

	private void connect(ConnectionManager cm, MySurfaceView surfaceView, RelativeLayout loginLayout){
		//检查网络状态
		ConnectivityManager con = (ConnectivityManager) getSystemService(DisplayActivity.CONNECTIVITY_SERVICE);
		boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
		boolean internet = con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
		if (wifi | internet) {
			//执行相关操作
            cm.start();
			try{
				Thread.sleep(500);
			}catch (Exception e){
				e.printStackTrace();
			}
			if(cm.getFlag()) {
				loginLayout.setVisibility(View.INVISIBLE);
				surfaceView.setVisibility(View.VISIBLE);
				runThread(surfaceView);
			}
		} else {
			Toast.makeText(getApplicationContext(),
					"亲，网络连了么？", Toast.LENGTH_LONG)
					.show();
		}
	}

	private void runThread(MySurfaceView surfaceView){
		// 启动写线程，将鼠标的坐标传回PC
		try {
			PhoneWriteThread writeThread = new PhoneWriteThread(Constant.SOCKET.getOutputStream());
			writeThread.start();
			Log.e(TAG, "the WriteThread turn on!");
		} catch (IOException e) {
			Log.e(TAG, "WriteFuck");
			e.printStackTrace();
		}

		// 启动读线程，将VR屏幕在phone端显示
		try {
			PhoneReadThread readThread = new PhoneReadThread(Constant.SOCKET.getInputStream());
			readThread.setSurfaceView(surfaceView);
			readThread.start();
			Log.e(TAG, "the ReadThread turn on!");
		} catch (IOException e) {
			Log.e(TAG, "ReadFuck");
			e.printStackTrace();
		}
	}

}
