package com.wandou.emotionrecclient;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class PhoneReadThread extends Thread {
    private static final String TAG = "ReadThread";
    private MySurfaceView surfaceView;
    private Bitmap bitmap;
    private DataInputStream dis;
    private boolean mRunning, mQuit;
    // 放接收到数据的数组
    private byte[] mData;

    public PhoneReadThread(InputStream in) {
        dis = new DataInputStream(in);
    }

    @Override
    public void run() {
        try {
            while (true) {
                Log.e(TAG, "ready to read");
                dis.readByte();
                int cmd = dis.readInt();
                Log.e(TAG, "cmd:" + String.valueOf(cmd));
                if (cmd == Constant.ACK_CMD) {
                    Log.e(TAG, "accept the ack_cmd");
                    int imageWidth = dis.readInt();
                    dis.readByte();
                    int imageHeight = dis.readInt();
                    this.surfaceView.setRectDst(imageWidth, imageHeight);
                    mRunning = true;
                    mQuit = false;
                    while (mRunning) {
                        if (mQuit || dis == null || surfaceView == null) {
                            break;
                        }
                        mData = new byte[dis.readInt() - 5];
                        dis.readByte();
                        dis.readFully(mData);
                        if (bitmap != null) {
                            bitmap.recycle();
                            bitmap = null;
                        }
                        bitmap = BitmapFactory.decodeByteArray(mData, 0, mData.length);
                        surfaceView.setBitmap(bitmap);
                        surfaceView.setFlag(true);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        } catch (IOException e) {
            Log.e(TAG, "what's the fuck again!");
            mRunning = false;
            quit();
            e.printStackTrace();
        }
        mRunning = false;
    }


    public void quit() {
        mQuit = true;
        if (dis != null) {
            try {
                dis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            dis = null;
        }
        while (mRunning) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void setSurfaceView(MySurfaceView surfaceView) {
        this.surfaceView = surfaceView;
    }
}
