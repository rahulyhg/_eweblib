package com.eweblib.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataEncrypt {

	private final static Base64 base64encoder = new Base64();
	private final static String encoding = "UTF-8";


	private final static Logger logger = LogManager.getLogger(DataEncrypt.class);

	// 十六进制下数字到字符的映射数组
	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	/** * 把inputString加密 */
	public static String generatePassword(String inputString) {
		return encodeByMD5(inputString);
	}

	/** 对字符串进行MD5加密 */
	public static String encodeByMD5(String originString) {
		if (originString != null) {
			try {
				// 创建具有指定算法名称的信息摘要
				// MessageDigest md = MessageDigest.getInstance("MD5");
				// 使用指定的字节数组对摘要进行最后更新，然后完成摘要计算
				byte[] results = MessageDigest.getInstance("MD5").digest(originString.getBytes());
				// 将得到的字节数组变成字符串返回
				String resultString = byteArrayToHexString(results);
				return resultString.toLowerCase();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 转换字节数组为十六进制字符串
	 * 
	 * @param 字节数组
	 * @return 十六进制字符串
	 */
	private static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	/** 将一个字节转化成十六进制形式的字符串 */
	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	/**
	 * 加密字符串
	 * 
	 */
	public static String encrypt(String encodeStr, String encryptKey) {
		if (encodeStr != null && encodeStr.length() > 0) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				int mode = Cipher.ENCRYPT_MODE;
				SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
				byte[] keyData = encryptKey.getBytes();
				DESKeySpec keySpec = new DESKeySpec(keyData);
				SecretKey key = keyFactory.generateSecret(keySpec);
				Cipher cipher = Cipher.getInstance("DES");
				cipher.init(mode, key);
				return base64encoder.encodeToString(cipher.doFinal(((String) encodeStr).getBytes(encoding)));
			} catch (Exception e) {
				logger.error(String.format("Encrypt data:[ %s ] error", encodeStr), e);
			} finally {
				try {
					baos.close();
				} catch (IOException e) {
					logger.error(String.format("Close ByteArrayOutputStream error after encrypt [ %s ]", encodeStr), e);
				}
			}

		}
		return null;
	}

	/**
	 * 解密字符串
	 */
	public static String decrypt(String decodeStr, String decryptKey) {
		if (decodeStr != null && decodeStr.length() > 0) {

			byte[] encodeByte = base64encoder.decode(decodeStr);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				int mode = Cipher.DECRYPT_MODE;
				SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
				byte[] keyData = decryptKey.getBytes();
				DESKeySpec keySpec = new DESKeySpec(keyData);
				SecretKey key = keyFactory.generateSecret(keySpec);
				Cipher cipher = Cipher.getInstance("DES");
				cipher.init(mode, key);

				return new String(cipher.doFinal(encodeByte), encoding);
			} catch (Exception e) {
				logger.error(String.format("Decrypt data:[ %s ] error", decodeStr), e);
			} finally {
				try {
					baos.close();
				} catch (IOException e) {
					logger.error(String.format("Close ByteArrayOutputStream error after decrypt [ %s ]", decodeStr), e);
				}
			}

		}
		return null;
	}

}
