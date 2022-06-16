package com.lianzheng.h5.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;

public class ImageBase64Util {

	/*
	 * 图片转化成base64字符串 imagefilePath 图片路径
	 */
	public static String getImageStr(String imagefilePath) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
		InputStream in = null;
		byte[] data = null;
		// 读取图片字节数组
		try {
			in = new FileInputStream(imagefilePath);
			data = new byte[in.available()];
			in.read(data);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 对字节数组Base64编码
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(data).replace("\r\n", "").replace("\n", "");//返回Base64编码过的字节数组字符串
	}

	public static String getBase64Str(File file) {
		InputStream in = null;
		byte[] data = null;
		try {
			in = new FileInputStream(file);
			data = new byte[in.available()];
			in.read(data);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 对字节数组Base64编码
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(data).replace("\r\n", "").replace("\n", "");
	}

	/*
	 * 创建文件夹路径 imagefilePath 图片路径
	 */
	public static void createMk(String imageMKPath) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理

		File file = new File(imageMKPath);
		// 如果文件夹不存在则创建
		if (!file.exists() && !file.isDirectory()) {
			System.out.println("//不存在");
			file.mkdir();
		} else {
			System.out.println("//目录存在");
		}

	}

	/*
	 * base64字符串转化成图片 imgStr 图片base64编码 newFilePath 生成图片指定路径
	 */
	public static boolean generateImage(String imgStr, String newFilePath) {
		// 对字节数组字符串进行Base64解码并生成图片
		if (imgStr == null) { // 图像数据为空
			return false;
		}
		BASE64Decoder decoder = new BASE64Decoder();
		OutputStream out = null;
		try {
			// Base64解码
			byte[] b = decoder.decodeBuffer(imgStr);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {// 调整异常数据
					b[i] += 256;
				}
			}
			out = new FileOutputStream(newFilePath);
			out.write(b);
			out.flush();
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}