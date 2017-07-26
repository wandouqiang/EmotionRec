package com.wandou.Server;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.FlavorTable;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;

public class PCWriteThread extends Thread {
	private DataOutputStream dos = null;
	private Robot robot;
	private Dimension dimension;
	private Rectangle screenRect;
	private int imageWidth, imageHeight;
	private String imgPath;
	private ByteArrayOutputStream imageOutStream;
	private BufferedImage targetImage;

	private boolean mRunning, mQuit;
	private boolean writeStart = false;

	public PCWriteThread(OutputStream os) throws AWTException {
		this.dos = new DataOutputStream(os);
		this.robot = new Robot();
		this.dimension = Toolkit.getDefaultToolkit().getScreenSize();
		// int []size = getViewSize();

		Constant.pcScreenWidth = dimension.width;
		Constant.pcScreenHeight = dimension.height;
		System.out.println("pc =" + Constant.pcScreenWidth + ":" + Constant.pcScreenHeight);
		// this.screenRect = new Rectangle(size[0],size[1],size[2],size[3]);
		
		this.imgPath = this.getClass().getResource("").getFile() + "mouse.png";
		this.imageOutStream = new ByteArrayOutputStream();
	}

	public void setBitmapSize(int w, int h) {
		float ratioPC = (float) (Constant.pcScreenWidth) / Constant.pcScreenHeight;
		float ratioPhone = (float) (w) / h;
		if (ratioPC > ratioPhone){
			imageHeight = (int) (w / ratioPC);
			imageWidth = w;
		}else{
			imageHeight = h;
			imageWidth = (int) (h * ratioPC);
		}
//		System.out.println("w:h = ("+ w + ":" + h + ")" + ratioPC + ":" + ratioPhone);
//		System.out.println("pcScreenWidth:pcScreenHeight = ("+ Constant.pcScreenWidth + ":" + Constant.pcScreenHeight + ")");
		System.out.println("imageWidth:imageHeight = (" + imageWidth + ": " + imageHeight + ")");
		this.targetImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		this.screenRect = new Rectangle(Constant.pcScreenWidth, Constant.pcScreenHeight);
		this.writeStart = true;
	}

	private void _sendImg() throws IOException {
		mQuit = false;
		mRunning = true;
		System.out.println(imgPath);
		
		BufferedImage cursor = ImageIO.read(new File(imgPath));// 将图片加载到内存中

		while (mRunning) {
			if (mQuit)
				break;
			Point point = MouseInfo.getPointerInfo().getLocation();
			BufferedImage bfImg = robot.createScreenCapture(screenRect);// 获取电脑屏幕截图但不包含光标
			bfImg.createGraphics().drawImage(cursor, point.x, point.y, null);// 将cursor画到(x,y)的位置
			compressImage(bfImg);
		}
		mRunning = false;
	}

	private void compressImage(BufferedImage _bfImg) throws IOException {
		Graphics2D grph = targetImage.createGraphics();
		grph.drawImage(_bfImg, 0, 0, imageWidth, imageHeight, null);// 画一个跟手机屏幕一样大的图片
		grph.dispose();
		targetImage.flush();// 更新目标图像
		imageOutStream.reset();// Resets the count field of this byte array
								// output stream to zero
		boolean resultWrite = ImageIO.write(targetImage, "jpg", imageOutStream);
		if (!resultWrite) {
			return;
		}
		byte[] tagInfo = imageOutStream.toByteArray();
		dos.writeInt(tagInfo.length + 5);
		dos.writeByte((byte) 5);
		dos.write(tagInfo);
		dos.flush();
		// System.out.println("Write image successfully!");
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void quit() {
		try {
			dos.close();
			dos = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		mQuit = true;
		interrupt();
		while (mRunning) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
		mRunning = false;
	}

	@Override
	public void run() {
		try {
			dos.write((byte) 5);
			dos.writeInt(Constant.ACK_CMD);
			System.out.println("ack_cmd send!");
			dos.writeInt(imageWidth);
			dos.write((byte) 5);
			dos.writeInt(imageHeight); 
			while (!writeStart) {
				Thread.sleep(200);
			}
			_sendImg();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}
