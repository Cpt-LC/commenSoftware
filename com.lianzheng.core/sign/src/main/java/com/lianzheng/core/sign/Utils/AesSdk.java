package com.lianzheng.core.sign.Utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * AES 加解密工具类
 *
 * @author Frank
 */
@Slf4j
public class AesSdk {

    /**
     * 密钥算法
     */
    private static final String ALGORITHM = "AES";

    /**
     * 加解密算法/工作模式/填充方式
     */
    private static final String ALGORITHM_STR = "AES/ECB/PKCS5Padding";

    /**
     * 密钥缓存
     */
    private static final Map<String, SecretKeySpec> SECRET_KEY_SPEC_MAP = new HashMap<>();

    /**
     * 生成秘钥
     *
     * @param password 密码原文
     * @return 返回密钥对象
     */
    private static SecretKeySpec getSecretKey(String password) throws NoSuchAlgorithmException {
        if (!SECRET_KEY_SPEC_MAP.containsKey(password)) {
            KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM);

            // 需要自己手动设置，解决linux下的异常
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(password.getBytes(StandardCharsets.UTF_8));

            //AES 要求密钥长度为 128
            kg.init(128, random);

            //生成一个密钥
            SecretKey secretKey = kg.generateKey();

            // 转换为AES专用密钥
            SECRET_KEY_SPEC_MAP.put(password, new SecretKeySpec(secretKey.getEncoded(), ALGORITHM));
        }

        return SECRET_KEY_SPEC_MAP.get(password);
    }

    /**
     * AES 加密操作
     *
     * @param content  待加密内容
     * @param password 加密密码
     * @return 返回Base64转码后的加密数据
     */
    public static String encrypt(String content, String password) {
        try {
            // 创建密码器
            Cipher cipher = Cipher.getInstance(ALGORITHM_STR);
            // 初始化
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(password));
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
            byte[] result = cipher.doFinal(bytes);
            return Base64.encodeBase64String(result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * AES 解密操作
     *
     * @param content  待解密内容
     * @param password 解密密码
     * @return 返回原始数据
     */
    public static String decrypt(String content, String password) {
        try {
            // 创建密码器
            Cipher cipher = Cipher.getInstance(ALGORITHM_STR);
            // 初始化
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(password));
            byte[] bytes = Base64.decodeBase64(content);
            byte[] result = cipher.doFinal(bytes);
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 随机产生一个AES密钥
     *
     * @return AES密钥
     */
    public static String generateKey() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static void main(String[] args) {
        String secret = "123456";

        String s0 = "hello,world!";
        System.out.println("原文:" + s0);
        String s1 = AesSdk.encrypt(s0, secret);
        System.out.println("密文:" + s1);
        String s2 = AesSdk.decrypt(s1, secret);
        System.out.println("译文:" + s2);
    }
}