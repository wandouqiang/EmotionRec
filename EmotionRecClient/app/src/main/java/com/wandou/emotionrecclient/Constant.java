package com.wandou.emotionrecclient;

import java.net.Socket;

import android.R.integer;

public class Constant {
	public static final int  ACK_CMD      = 0XAC;
	public static final int  START        = 0XA0;
	
	public static Socket SOCKET  =  null;	
	
	public static final String FILE_NAME = "filename";
	public static  String SERVERIP = null;
	public static  int PORT = 20175;

	public static int imageWidth = 1280;
	public static int imageHeight = 1024;

	public static int viewWidth = 1280;
	public static int viewHeight = 1024;
	
}
