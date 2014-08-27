package com.eweblib.util;

import java.util.Random;
import java.awt.image.BufferedImage;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class ImgUtil {
	
	public static String getRandomNumber(int wordLength) {// 生成随机数

		StringBuffer buffer = new StringBuffer("0123456789");
		StringBuffer sb = new StringBuffer();
		Random r = new Random();
		int range = buffer.length();
		for (int i = 0; i < wordLength; i++) {
			sb.append(buffer.charAt(r.nextInt(range)));
		}
		return sb.toString();
	}

	public static String getRandomWord(int wordLength) {// 生成随机数

		StringBuffer buffer = new StringBuffer("23456789ABCDEFGHJKLMNPQRSTUVWXYZ");
		StringBuffer sb = new StringBuffer();
		Random r = new Random();
		int range = buffer.length();
		for (int i = 0; i < wordLength; i++) {
			sb.append(buffer.charAt(r.nextInt(range)));
		}
		return sb.toString();
	}

	public static BufferedImage getCaptchaImage(String word, int imageWidth, int imageHeight) {// 生成图片
		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		try {
			Graphics2D g = image.createGraphics();
			g.fillRect(0, 0, imageWidth, imageHeight); // 画一个矩形
			g.setFont(new java.awt.Font("宋体", java.awt.Font.BOLD, 18));// 设置字体
//			g.setBackground(Color.DARK_GRAY);
			g.setStroke(new BasicStroke(1)); 
			g.setColor(Color.BLACK);// 设置背景颜色
			g.drawString(word, 20, 22);// 写入所给字符串
			g.dispose();// 释放此图形的上下文以及它使用的所有系统资源。调用 dispose 之后，就不能再使用 Graphics
			            // 对象。
		} catch (Exception e) {
			e.printStackTrace();
		}
		return image;// 返回 BufferedImage 对象
	}
}
