package com.lianzheng.core.wechat.sessionstore;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lianzheng.core.resource.FileContentUtil;
import com.tencent.wework.Finance;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;

import javax.crypto.Cipher;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 处理企业微信的会话存储的工具类
 * @author: 何江雁
 * @date: 2021年10月19日 11:29
 * <p>
 * //10000	参数错误，请求参数错误
 * //10001	网络错误，网络请求错误
 * //10002	数据解析失败
 * //10003	系统失败
 * //10004	密钥错误导致加密失败
 * //10005	fileid错误
 * //10006	解密失败
 * //10007 找不到消息加密版本的私钥，需要重新传入私钥对
 * //10008 解析encrypt_key出错
 * //10009 ip非法
 * //10010 数据过期
 */
@Component
public class SessionStoreUtil {
    @Autowired
    WechatSessionConfig config;

    private static final boolean ABLE_TO_WRITE_DECRYPTED = true;
    private static String DEFAULT_PRIVATE_KEY_PEM;

    static {
        try {
            DEFAULT_PRIVATE_KEY_PEM = FileContentUtil.getResourceFileContent("classpath:keys/private.pem");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //每个线程持有一个sdk实例
    private ThreadLocal<Long> threadLocal = new ThreadLocal<Long>(){
        @Override
        public Long initialValue(){
            return init();
        }
    };


    private final long getSdk() {
        long sdk = threadLocal.get();
        if (sdk == Long.MIN_VALUE) {
            sdk = init();
            threadLocal.set(sdk);
        }
        Assert.isTrue(sdk != 0, "初始化企业微信sdk失败");
        return sdk;
    }

    private static final String getDefaultRESPrivateKeyPEM() throws IOException {
        return DEFAULT_PRIVATE_KEY_PEM;
    }
    private static final String getRESPrivateKeyPEM(int publicKey_ver) throws IOException {
//        String privateKeyPEM = FileContentUtil.getResourceFileContent("classpath:keys/private.pem");
//        return privateKeyPEM;
        return getDefaultRESPrivateKeyPEM();
    }

    //读取pkcs1格式的private key
    private final PrivateKey getPrivateKey(int publicKey_ver) throws Exception {
        String privKeyPEM = getRESPrivateKeyPEM(publicKey_ver);
        //base64编码的私钥
        String privKeyPEMnew = privKeyPEM.replaceAll("\\n", "").replace("-----BEGIN RSA PRIVATE KEY-----", "").replace("-----END RSA PRIVATE KEY-----", "");
        byte[] bytes = java.util.Base64.getDecoder().decode(privKeyPEMnew);

        DerInputStream derReader = new DerInputStream(bytes);
        DerValue[] seq = derReader.getSequence(0);
        BigInteger modulus = seq[1].getBigInteger();
        BigInteger publicExp = seq[2].getBigInteger();
        BigInteger privateExp = seq[3].getBigInteger();
        BigInteger prime1 = seq[4].getBigInteger();
        BigInteger prime2 = seq[5].getBigInteger();
        BigInteger exp1 = seq[6].getBigInteger();
        BigInteger exp2 = seq[7].getBigInteger();
        BigInteger crtCoef = seq[8].getBigInteger();

        RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(modulus, publicExp, privateExp, prime1, prime2, exp1, exp2, crtCoef);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    private final String decryptRSA(int publicKey_ver, String encrypt_random_key) throws Exception {

        /*
        a) 需首先对每条消息的encrypt_random_key内容进行base64 decode,得到字符串str1.
        b) 使用publickey_ver指定版本的私钥，使用RSA PKCS1算法对str1进行解密，得到解密内容str2.
        c) 得到str2与对应消息的encrypt_chat_msg，调用下方描述的DecryptData接口，即可获得消息明文。
        * */
        PrivateKey privateKey = getPrivateKey(publicKey_ver);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        //64位解码加密后的字符串
        byte[] data = Base64.decodeBase64(encrypt_random_key.getBytes("UTF-8"));
        return new String(cipher.doFinal(data));

    }

    /*
     * @Description: 使用sdk前需要初始化，初始化成功后的sdk可以一直使用。如需并发调用sdk，建议每个线程持有一个sdk实例。
     * @author: 何江雁
     * @date: 2021/10/19 14:45
     * @param null:
     * @Return:
     */
    private final long init() {
        long ret = 0;
        long sdk = Finance.NewSdk();
        ret = Finance.Init(sdk, config.getCorpid(), config.getSecret());
        if (ret != 0) {
            Finance.DestroySdk(sdk);
            throw new WechatSessionStoreAPIException(ret);
        }
        return sdk;
    }
    private final void destorySdk(){
        long sdk = getSdk();
        Finance.DestroySdk(sdk);
        threadLocal.set(Long.MIN_VALUE);
    }

    private final String innerGetChatData(long seq) throws IOException {
        String mockData = config.getMockConfig().getMockData();

        if (!mockData.isEmpty()) {
            return mockData;
        }

        long sdk = getSdk();
        String proxy = config.getProxy();
        String passwd = config.getProxyPassword();
        int timeout = config.getTimeout();
        int limit = config.getLimit();
        //拉取会话存档

        //每次使用GetChatData拉取存档前需要调用NewSlice获取一个slice，在使用完slice中数据后，还需要调用FreeSlice释放。
        long slice = Finance.NewSlice();
        long ret = Finance.GetChatData(sdk, seq, limit, proxy, passwd, timeout, slice);
        if (ret != 0) {
            Finance.FreeSlice(slice);
            throw new WechatSessionStoreAPIException(ret);
        }
        String message = Finance.GetContentFromSlice(slice);
        Finance.FreeSlice(slice);
        return message;
    }

    private final String decryptData(long sdk, int publicKey_ver, String encrypt_random_key, String encrypt_chat_msg) throws Exception {
        //解密会话存档内容
        if (encrypt_random_key == "" || encrypt_random_key == null) {
            return encrypt_chat_msg;
        }

        String encrypt_key = decryptRSA(publicKey_ver, encrypt_random_key);
        //每次使用DecryptData解密会话存档前需要调用NewSlice获取一个slice，在使用完slice中数据后，还需要调用FreeSlice释放。
        long msg = Finance.NewSlice();
        long ret = Finance.DecryptData(sdk, encrypt_key, encrypt_chat_msg, msg);
        if (ret != 0) {
            Finance.FreeSlice(msg);
            throw new WechatSessionStoreAPIException(ret);
        }
        String message = Finance.GetContentFromSlice(msg);
        Finance.FreeSlice(msg);
        return message;
    }

    /*
     * @Description: 获取并解密会话内容
     * @author: 何江雁
     * @date: 2021/10/19 13:08
     * @param null:
     * @Return:
     */
    public final List<ChatItem> getChatData(long seq) throws Exception {

        long sdk = getSdk();
        String chatDataString = innerGetChatData(seq);

        config.getMockConfig().storeData(chatDataString, "mock", false);

        JSONObject chatData = JSON.parseObject(chatDataString, JSONObject.class);
        List<JSONObject> items = JSON.parseArray(chatData.getString("chatdata"), JSONObject.class);

        StringBuilder builder = new StringBuilder();
        if(ABLE_TO_WRITE_DECRYPTED){
            builder.append("[");
        }

        List<ChatItem> result = new ArrayList();
        for (JSONObject obj : items) {
            int publicKeyVersion = obj.getInteger("publickey_ver");
            String encryptRandomKey = obj.getString("encrypt_random_key");
            String encryptChatMsg = obj.getString("encrypt_chat_msg");
            long currentSeq = obj.getLong("seq");

//            System.out.println(currentSeq+". "+"thread-"+Thread.currentThread().getName()+"-->"+sdk+" decryptData starting..");
            String message = decryptData(sdk, publicKeyVersion, encryptRandomKey, encryptChatMsg);
//            System.out.println(currentSeq+". "+"thread-"+Thread.currentThread().getName()+"-->"+sdk+" decryptData ended..");

            if(ABLE_TO_WRITE_DECRYPTED){
                builder.append(message);
                builder.append(",\n");
            }
            JSONObject messageSet = JSON.parseObject(message);
            ChatItem item = new ChatItem(obj, messageSet);

            result.add(item);
        }

        if(ABLE_TO_WRITE_DECRYPTED){
            builder.append("]");
            config.getMockConfig().storeData(builder.toString(), "decrypted", true);
        }
        return result;
    }

}
