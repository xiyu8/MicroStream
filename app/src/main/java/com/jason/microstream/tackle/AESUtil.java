package com.jason.microstream.tackle;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author: chenjunhang
 * @date: 2020/5/28
 */
public class AESUtil {

    // 密匙
    private static final String KEY = "26lttg16gwc2phr9";
    // 偏移量
    private static final String OFFSET = "qltixgp6kmi6zhal";
    // 编码
    private static final String ENCODING = "UTF-8";
    //算法
    private static final String ALGORITHM = "AES";
    // 默认的加密算法
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

//    /**
//     * 加密
//     */
//    public static String encrypt(String data) throws Exception {
//        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
//        SecretKeySpec skeySpec = new SecretKeySpec(KEY.getBytes("ASCII"), ALGORITHM);
//        //使用CBC模式，需要一个向量iv，可增加加密算法的强度
//        IvParameterSpec iv = new IvParameterSpec(OFFSET.getBytes());
//        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
//        byte[] encrypted = cipher.doFinal(data.getBytes(ENCODING));
//        //此处使用BASE64做转码。
//        return Encoder.encodeBase64String(encrypted);
//    }

    /**
     * 解密
     */
    public static String decrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            SecretKeySpec skeySpec = new SecretKeySpec(KEY.getBytes("ASCII"), ALGORITHM);
            //使用CBC模式，需要一个向量iv，可增加加密算法的强度
            IvParameterSpec iv = new IvParameterSpec(OFFSET.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] buffer = Base64.getDecoder().decode(data);
            byte[] encrypted = cipher.doFinal(buffer);
            //此处使用BASE64做转码。
            return new String(encrypted, ENCODING);
        } catch (Exception e) {
            return "";
        }
    }

}
