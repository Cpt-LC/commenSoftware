package com.lianzheng.h5.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;


/**
 * 文件操作工具类
 */
public class FileUtil {
	
	/**
	 * 读取文件内容为二进制数组
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static byte[] read2ByteArray(String filePath) throws IOException {

		InputStream in = new FileInputStream(filePath);
		byte[] data = inputStream2ByteArray(in);
		in.close();

		return data;
	}

	/**
	 * 流转二进制数组
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static byte[] inputStream2ByteArray(InputStream in) throws IOException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024 * 4];
		int n = 0;
		while ((n = in.read(buffer)) != -1) {
			out.write(buffer, 0, n);
		}
		return out.toByteArray();
	}
	
	public static void save(String filePath, String fileName, byte[] content) {
		try {
			File filedir = new File(filePath);
			if (!filedir.exists()) {
				filedir.mkdirs();
			}
			File file = new File(filedir, fileName);
			OutputStream os = new FileOutputStream(file);
			os.write(content, 0, content.length);
			os.flush();
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String fileType(MultipartFile file) {
		String getOriginalFilename=file.getOriginalFilename();
		String fileType="";
		if(StringUtils.isNotBlank(getOriginalFilename) &&
				file.getOriginalFilename().lastIndexOf(".")>0) {
			fileType = getOriginalFilename.substring(getOriginalFilename.lastIndexOf(".") + 1);
		}else {
			throw new RuntimeException("文件类型识别错误！");
		}
		return fileType.toLowerCase();
	}
	
	public static void inputStreamToFile(InputStream ins, File file) {
		try {
			OutputStream os = new FileOutputStream(file);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			os.close();
			ins.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 判断文件夹是否存在
   public static void judeDirsExists(File file) {
	   if (!file.exists()) {
		   file.mkdirs();
	   }else {
		   System.out.println("已存在");
	   }
   }
   
   // 判断文件夹是否存在
   public static void delete(File file) {
	   if (file.exists()) {
		   file.delete();
	   }
   }
   
   /**
    * 递归删除文件（夹）
    * @param path 待删除的文件（夹）
    * @return
    */
	public static boolean remove(String path) {
		return remove(new File(path));
	}
	
	/**
     * 递归删除文件（夹）
     * @param file 待删除的文件（夹）
     * @return
     */
	public static boolean remove(File file) {
        if (!file.exists()) {
            return false;
        }
        if (file.isFile()) {
            return file.delete();
        }
        Arrays.asList(file.listFiles()).forEach(FileUtil::remove);
        return file.delete();
    }
	
	/**
    * 指定文件夹下是否存在文件
    * @param path
    * @return
    */
	public static boolean hasFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		if(tempList.length==0) {
			return flag;
		}else {
			flag = true;
		}
		return flag;
	}
	/**
	 * 指定文件夹下是否存在指定文件
	 * @param path
	 * @return
	 */
	public static boolean hasFile(String path,String fileName) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		for (int i = 0; i < tempList.length; i++) {
			if(fileName.equals(tempList[i])){
				return true;
			}
		}
		return flag;
	}
	
	/**
	    * 指定文件夹下是否存在文件
	    * @param path
	    * @return
	    */
		public static String[] findFileList(String path) {
			File file = new File(path);
			String[] tempList = file.list();
			return tempList;
		}
		
	/**
	 * MultipartFile 转 File
	 * @param file
	 * @throws Exception
	 */
	public static File multipartFileToFile(MultipartFile file) throws Exception {
	    File toFile = null;
	    if(file==null ||file.getSize()<=0){
	        file = null;
	    }else {
	            InputStream ins = null;
	            ins = file.getInputStream();
	            toFile = new File(file.getOriginalFilename());
	            inputStreamToFile(ins, toFile);
	            ins.close();
	    }
	    return toFile;
	}
	
	 /**
     * 判断为空
     * @return
     */
    public static boolean isEmpty(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }
	
	/* 读取网络文件 */
	public static InputStream getWebFileInputStream(String path) {
		URL url = null;
		try {
			url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// 设置超时间为3秒
			conn.setConnectTimeout(3 * 1000);
			// 防止屏蔽程序抓取而返回403错误
			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			// 得到输入流
			return conn.getInputStream();
		} catch (Exception e) {
			System.out.println("读取网络文件异常:"+path+ExceptionUtils.getStackTrace(e));
		}
		return null;
	}

	public static void main(String[] args) {
		File writeName = new File("E:\\toolseeeeeeffff\\eeee.txt222\\");
		// judeDirsExists(writeName);

		// delAllFile("E:\\\\toolseeeeeeffff\\\\eeee.txt222\\\\");
		// System.out.println(hasFile("E:\\\\toolseeeeeeffff\\\\eeee.txt222\\\\"));
		System.out.println(hasFile("E:\\\\toolseeeeeeffff\\\\eeee.txt222\\\\", "123.txt"));
	}





	//**************************************************** fileToBase64 开始 ******************************************
	/**
	 * <p>将文件转成base64 字符串</p>
	 * @return
	 * @throws Exception
	 */
//	public static String encodeBase64File(File file) {
//		//File file = new File(path);
//		FileInputStream inputFile = null;
//		byte[] buffer = null;
//		try {
//			inputFile = new FileInputStream(file);
//			buffer = new byte[(int)file.length()];
//			return new BASE64Encoder().encode(buffer);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				inputFile.read(buffer);
//				inputFile.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		return null;
//	}
	/**
	 * <p>将base64字符解码保存文件</p>
//	 * @param base64Code
//	 * @param targetPath
	 * @throws Exception
	 */
//	public static File decoderBase64File(String base64Code,String targetPath) throws Exception {
//		byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
//		FileOutputStream out = new FileOutputStream(targetPath);
//		out.write(buffer);
//		out.close();
//		return new File(targetPath);
//	}

	public static String encodeBase64File(File file) throws Exception {
		FileInputStream inputFile = new FileInputStream(file);
		byte[] buffer = new byte[(int)file.length()];
		inputFile.read(buffer);
		inputFile.close();
		return new BASE64Encoder().encode(buffer);
	}
	/**
	 * <p>将base64字符解码保存文件</p>
	 * @param base64Code
	 * @param targetPath
	 * @throws Exception
	 */
	public static void decoderBase64File(String base64Code,String targetPath) throws Exception {
		byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
		FileOutputStream out = new FileOutputStream(targetPath);
		out.write(buffer);
		out.close();
	}
	/**
	 * <p>将base64字符保存文本文件</p>
	 * @param base64Code
	 * @param targetPath
	 * @throws Exception
	 */
	public static void toFile(String base64Code,String targetPath) throws Exception {
		byte[] buffer = base64Code.getBytes();
		FileOutputStream out = new FileOutputStream(targetPath);
		out.write(buffer);
		out.close();
	}
	//********************************************************** fileToBase64 结束 **************************************

	
}
