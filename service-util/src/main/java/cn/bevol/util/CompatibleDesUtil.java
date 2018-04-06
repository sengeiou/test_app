package cn.bevol.util;

import com.io97.utils.MD5Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.UnsupportedEncodingException;
import java.util.Date;

public class CompatibleDesUtil {
	public static Log log = LogFactory.getLog(CompatibleDesUtil.class);
	private static final String MCRYPT_TRIPLEDES = "DESede";

	private static final String TRANSFORMATION = "DESede/CBC/PKCS5Padding";

	private static void asciiToBcdBytes(String str, byte[] hex, int count)// throws
	// Exception
	{
		byte[] inputBytes = str.getBytes();
		for (int i = 0, j = 0; j < inputBytes.length && i < count; ++i) {
			byte v = inputBytes[j];
			++j;
			if (v <= 0x39)
				hex[i] = (byte) (v - 0x30);
			else
				hex[i] = (byte) (v - 0x41 + 10);

			hex[i] <<= 4;

			if (j >= inputBytes.length)
				break;

			v = inputBytes[j];
			++j;

			if (v <= 0x39)
				hex[i] += (byte) (v - 0x30);
			else
				hex[i] += (byte) (v - 0x41 + 10);
		}
	}

	public static String decrypt(String data, String key, byte[] vis) {
		if (data == null)
			return null;

		String result = null;

		try {

 			DESedeKeySpec spec = new DESedeKeySpec(new BASE64Decoder().decodeBuffer(key));
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(MCRYPT_TRIPLEDES);
			SecretKey sec = keyFactory.generateSecret(spec);

			
 			IvParameterSpec IvParameters = new IvParameterSpec(vis);

			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, sec, IvParameters);

			result = new String(cipher.doFinal(new BASE64Decoder().decodeBuffer(data)), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public static String encrypt(String data,String key, byte[] vis ) {
 
		String result = null;
		try {
			DESedeKeySpec spec = new DESedeKeySpec(new BASE64Decoder().decodeBuffer(key));

			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(MCRYPT_TRIPLEDES);
			SecretKey sec = keyFactory.generateSecret(spec);

			IvParameterSpec IvParameters = new IvParameterSpec(vis);

			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, sec, IvParameters);
			result = new BASE64Encoder().encodeBuffer(cipher.doFinal(data.getBytes("UTF-8")));
		} catch (Exception e) {
			log.error("encrypt error", e);
		}

		return result;
	}

	public static byte[] getSecretKey() {
		return "PA18SHOPLIFEINCUSTOMERACTIVESA".getBytes();
	}

	public static byte[] getIVBytes() {
		byte[] bytes = new byte[8];
		bytes[0] = (byte) 'm';
		bytes[1] = (byte) 'b';
		bytes[2] = (byte) 'e';
		bytes[3] = (byte) 'v';
		bytes[4] = (byte) 'o';
		bytes[5] = (byte) 'l';
		return bytes;
	}

	public static String processDecrypt(String str) {
		if (str == null)
			return null;

		str = str.replaceAll("+", "+");
		str = str.replaceAll("/", "/");
		str = str.replaceAll("=", "=");

		return str;
	}

	public static String processEncrypt(String str) {
		if (str == null)
			return null;

		str = str.replaceAll("[+]", "+");
		str = str.replaceAll("[/]", "/");
		str = str.replaceAll("[=]", "=");

		return str;
	}

	public static void main(String[] args) {
		/*加密*/
		String idcode="219dv";
		String id="890";
		String keycode= MD5Utils.encode("bevol20160107");
		String msg = id+""+idcode+""+new Date().getTime()/1000; //动态cookie
		byte[] bytes = new byte[8];
		bytes[0] = (byte) 'm';
		bytes[1] = (byte) 'b';
		bytes[2] = (byte) 'e';
		bytes[3] = (byte) 'v';
		bytes[4] = (byte) 'o';
		bytes[5] = (byte) 'l';

		//String encryptData= encrypt(msg,new BASE64Encoder().encodeBuffer(keycode.getBytes()),bytes);
		String encryptData="okEGE4c3d5vR6FnE2uVlrA==";
		if(encryptData.indexOf("%")!=-1) {
	 		try {
	 			encryptData = java.net.URLDecoder.decode(encryptData,   "utf-8");
	 		} catch (UnsupportedEncodingException e) {
	 			// TODO Auto-generated catch block
	 			e.printStackTrace();
	 		}
		}

		/*echo $rs1 . '<br />';
		$rs2 = $des->decrypt($rs1);
		echo $rs2;*/
		/*解密*/
		/**
		 * 3des 解密
		 */
 		// encryptData = "+Q0Ji/X7V0EVPrdv4id/Ev1kpc+5IGoL";// encrypt(source,
		String str = decrypt(encryptData, new BASE64Encoder().encodeBuffer(keycode.getBytes()),
				bytes);
	//	String str = decrypt(encryptData, CommonUtils.getMd5(key),vi);
		System.out.println(str);
		 id=str.substring(0, str.lastIndexOf(idcode));
		System.out.println(id);

		
		

	}

}
