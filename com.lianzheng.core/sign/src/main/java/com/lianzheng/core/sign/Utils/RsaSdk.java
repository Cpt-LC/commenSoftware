package com.lianzheng.core.sign.Utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA 加解密工具类
 *
 * @author Frank
 */
@Slf4j
public class RsaSdk {

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 私钥缓存
     */
    private static final Map<String, PrivateKey> PRIVATE_KEY_MAP = new HashMap<>();

    /**
     * 公钥缓存
     */
    private static final Map<String, PublicKey> PUBLIC_KEY_MAP = new HashMap<>();

    /**
     * 获取密钥对
     *
     * @return 密钥对
     */
    public static KeyPair getKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(1024);
        return generator.generateKeyPair();
    }

    /**
     * 获取私钥
     *
     * @param password 私钥字符串
     * @return 返回私钥
     */
    private static PrivateKey getPrivateKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (!PRIVATE_KEY_MAP.containsKey(password)) {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] decodedKey = Base64.decodeBase64(password.getBytes());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
            PRIVATE_KEY_MAP.put(password, keyFactory.generatePrivate(keySpec));
        }
        return PRIVATE_KEY_MAP.get(password);
    }

    /**
     * 获取公钥
     *
     * @param password 公钥字符串
     * @return 返回公钥
     */
    private static PublicKey getPublicKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (!PUBLIC_KEY_MAP.containsKey(password)) {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] decodedKey = Base64.decodeBase64(password.getBytes());
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
            PUBLIC_KEY_MAP.put(password, keyFactory.generatePublic(keySpec));
        }
        return PUBLIC_KEY_MAP.get(password);
    }

    /**
     * 加密
     *
     * @param data     待加密数据
     * @param password 密钥（公钥）
     * @return 返回加密字符串
     */
    public static String encrypt(String data, String password) {
        try {
            PublicKey publicKey = getPublicKey(password);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            int inputLen = data.getBytes().length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offset = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段加密
            while (inputLen - offset > 0) {
                if (inputLen - offset > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(data.getBytes(), offset, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data.getBytes(), offset, inputLen - offset);
                }
                out.write(cache, 0, cache.length);
                i++;
                offset = i * MAX_ENCRYPT_BLOCK;
            }
            byte[] encryptedData = out.toByteArray();
            out.close();
            // 加密后的字符串
            return Base64.encodeBase64String(encryptedData);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 解密
     *
     * @param data     待解密数据
     * @param password 密钥（私钥）
     * @return 返回解密字符串
     */
    public static String decrypt(String data, String password) {
        try {
            PrivateKey privateKey = getPrivateKey(password);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] dataBytes = Base64.decodeBase64(data);
            int inputLen = dataBytes.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offset = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offset > 0) {
                if (inputLen - offset > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(dataBytes, offset, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(dataBytes, offset, inputLen - offset);
                }
                out.write(cache, 0, cache.length);
                i++;
                offset = i * MAX_DECRYPT_BLOCK;
            }
            byte[] decryptedData = out.toByteArray();
            out.close();
            // 解密后的内容
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 签名
     *
     * @param data     待签名数据
     * @param password 密钥（私钥）
     * @return 返回签名
     */
    public static String sign(String data, String password) throws Exception {
        PrivateKey privateKey = getPrivateKey(password);
        byte[] keyBytes = privateKey.getEncoded();
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey key = keyFactory.generatePrivate(keySpec);
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(key);
        signature.update(data.getBytes());
        return new String(Base64.encodeBase64(signature.sign()));
    }

    /**
     * 验签
     *
     * @param srcData  原始字符串
     * @param password 密钥（公钥）
     * @param sign     签名
     * @return 是否验签通过
     */
    public static boolean verify(String srcData, String password, String sign) throws Exception {
        PublicKey publicKey = getPublicKey(password);
        byte[] keyBytes = publicKey.getEncoded();
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey key = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(key);
        signature.update(srcData.getBytes());
        return signature.verify(Base64.decodeBase64(sign.getBytes()));
    }

    public static void main(String[] args) {
        try {
            // 生成密钥对
            KeyPair keyPair = getKeyPair();
            String privateKey = new String(Base64.encodeBase64(keyPair.getPrivate().getEncoded()));
            String publicKey = new String(Base64.encodeBase64(keyPair.getPublic().getEncoded()));
            System.out.println("私钥:" + privateKey);
            System.out.println("公钥:" + publicKey);
            // RSA加密
            String data = "待加密的文字内容";
            String encryptData = encrypt(data, publicKey);
            System.out.println("加密后内容:" + encryptData);
            // RSA解密
            String decryptData = decrypt(encryptData, privateKey);
            System.out.println("解密后内容:" + decryptData);

            // RSA签名
            String sign = sign(data, privateKey);
            // RSA验签
            boolean result = verify(data, publicKey, sign);
            System.out.print("验签结果:" + result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.out.print("加解密异常");
        }
    }
}