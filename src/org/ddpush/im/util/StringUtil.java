package org.ddpush.im.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.UUID;

import org.ddpush.im.v1.node.Constant;
import org.ddpush.im.v1.node.PushMessage;


public class StringUtil {
	
	public static String checkBlankString(String param) {
		if (param == null) {
			return "";
		}
		return param;
	}

	public static String md5(String encryptStr) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(encryptStr.getBytes("UTF-8"));
		byte[] digest = md.digest();
		StringBuffer md5 = new StringBuffer();
		for (int i = 0; i < digest.length; i++) {
			md5.append(Character.forDigit((digest[i] & 0xF0) >> 4, 16));
			md5.append(Character.forDigit((digest[i] & 0xF), 16));
		}

		encryptStr = md5.toString();
		return encryptStr;
	}
	
	public static String sha1(String encryptStr) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA1");
		md.update(encryptStr.getBytes("UTF-8"));
		byte[] digest = md.digest();
		StringBuffer sha1 = new StringBuffer();
		for (int i = 0; i < digest.length; i++) {
			sha1.append(Character.forDigit((digest[i] & 0xF0) >> 4, 16));
			sha1.append(Character.forDigit((digest[i] & 0xF), 16));
		}

		encryptStr = sha1.toString();
		return encryptStr;
	}

	public static byte[] md5Byte(String encryptStr) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(encryptStr.getBytes("UTF-8"));
		return md.digest();
	}
	
	public static byte[] sha1Byte(String encryptStr) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA1");
		md.update(encryptStr.getBytes("UTF-8"));
		return md.digest();
	}

	public static String genUUIDHexString(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	public static UUID parseUUIDFromHexString(String hexUUID) throws Exception{
		byte[] data = hexStringToByteArray(hexUUID);
		long msb = 0;
        long lsb = 0;

        for (int i=0; i<8; i++)
            msb = (msb << 8) | (data[i] & 0xff);
        for (int i=8; i<16; i++)
            lsb = (lsb << 8) | (data[i] & 0xff);
        
        return new java.util.UUID(msb,lsb);
	}

    private static char convertDigit(int value) {

        value &= 0x0f;
        if (value >= 10)
            return ((char) (value - 10 + 'a'));
        else
            return ((char) (value + '0'));

    }
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
	
    public static String convert(final byte bytes[]) {

        StringBuffer sb = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            sb.append(convertDigit((int) (bytes[i] >> 4)));
            sb.append(convertDigit((int) (bytes[i] & 0x0f)));
        }
        return (sb.toString());

    }
    
    public static String convert(final byte bytes[],int pos, int len) {

        StringBuffer sb = new StringBuffer(len * 2);
        for (int i = pos; i < pos+len; i++) {
            sb.append(convertDigit((int) (bytes[i] >> 4)));
            sb.append(convertDigit((int) (bytes[i] & 0x0f)));
        }
        return (sb.toString());

    }
    
    public static String convert(final PushMessage message) {
    	String str = null;
		try{
			str = new String(message.getData(),Constant.PUSH_MSG_HEADER_LEN,message.getContentLength(), "UTF-8");
		}catch(Exception e){
			str = convert(message.getData(),Constant.PUSH_MSG_HEADER_LEN,message.getContentLength());
		}
		return str;
    }
    
	/**
	 * @param ip
	 * @return 有符号的整形数；当ip>128.0.0.0时为负数；
	 * @throws UnknownHostException
	 * @author wenc
	 */
	public static long ip2int(String ip) throws UnknownHostException   {
		InetAddress address = InetAddress.getByName(ip);// 在给定主机名的情况下确定主机的 IP 址。
		byte[] bytes = address.getAddress();// 返回此 InetAddress 对象的原始 IP 地址
		return int2long( byte2int(bytes[0])<<24 | byte2int(bytes[1])<<16 | byte2int(bytes[2])<<8 | byte2int(bytes[3])<<1); 
	}

	public static long int2long(int i) {
		long l = i & 0x7fffffffL;
		if (i < 0) {
			l |= 0x080000000L;
		}
		return l;
	}

	public static int byte2int(byte b) {
		int l = b & 0x07f;
		if (b < 0) {
			l |= 0x80;
		}
		return l;
	}
}
