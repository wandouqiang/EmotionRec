package com.wandou.emotionrecclient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MySurfaceView extends SurfaceView implements Runnable,
		SurfaceHolder.Callback {
	private static final String TAG = "MySurfaceView";
	private SurfaceHolder mHolder;
	private Bitmap bitmap;
	private Rect RectSrc,RectDst;
	private boolean mRunning, mQuit,mFlag;
	private int viewW, viewH;

	public MySurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (mHolder == null){
			mHolder = this.getHolder();
			mHolder.setFormat(PixelFormat.TRANSPARENT);//
			mHolder.addCallback(this);

		}
	}
	
	public MySurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (mHolder == null){
			mHolder = this.getHolder();
			mHolder.setFormat(PixelFormat.TRANSPARENT);//
			mHolder.addCallback(this);
		}
	}

	public MySurfaceView(Context context) {
		super(context);
		if (mHolder == null) {
			mHolder = this.getHolder();
			mHolder.setFormat(PixelFormat.TRANSPARENT);//
			mHolder.addCallback(this);
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		new Thread(this).start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.e(TAG,"What's the fuck again and again");

	}

	public void draw() {
		Canvas canvas = mHolder.lockCanvas();
		if (canvas == null || mHolder == null){
			return;
		}
		if (bitmap != null){
			canvas.drawColor(Color.WHITE);
			canvas.drawBitmap(bitmap, RectSrc, RectDst, null);
		}
        mHolder.unlockCanvasAndPost(canvas);
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
		if (RectSrc == null) {
			RectSrc = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			Log.e(TAG, bitmap.getWidth() + ":" + bitmap.getHeight());
		}
	}

	public void setRect(int width, int height){
		viewW = width;
		viewH = height;
	}

	public void setRectDst(int width, int height) {
		Log.e(TAG, "imageview width:" + width + ":" + height);
		int x = (viewW - width) / 2;
		int y = (viewH - height) / 2;
		setRectDst(x, y, width, height);
	}

	public void setRectDst(int x, int y, int width, int height) {
		RectDst   = new Rect(x, y, width, height);
	}

	public void quit() {
		mQuit = false;
		while (mRunning) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mRunning = false;
	}

	public synchronized void setFlag(boolean mFlag) {
		this.mFlag = mFlag;
	}
	
	public synchronized boolean isFlag(){//此处由于在PhoneReadThred和本类run方法都会调用，故需要同步
		return mFlag;
	}
	
	@Override
	public void run() {
		mRunning = true;
		mQuit = false;
		while (mRunning) {
			if (mQuit)
				break;
			if (isFlag()) {
				setFlag(false);
				draw();
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mRunning = false;
	}
}
