package com.lianzheng.h5.jwt.util;

import cn.hutool.core.lang.generator.UUIDGenerator;
import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.lianzheng.h5.entity.SysNotarialOfficeEntity;
import io.jsonwebtoken.*;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.UUID;


public class JwtUtils {

    /*
    生成token
     */
      public static String getToken(String userId,String password ,String username) {

        String token= JWT.create().withClaim("username",username).withAudience(userId).sign(Algorithm.HMAC256(password));// 以 password 作为 token 的密钥
        return token;
      }

    public static String getToken(String userId) {

        String token= JWT.create().withAudience(userId).sign(Algorithm.HMAC256(userId));// 以 password 作为 token 的密钥
        return token;
    }


      public static String getTokenUserId() {
                String token = getRequest().getHeader("token");// 从 http 请求头中取出 token
                String userId = JWT.decode(token).getAudience().get(0);
               return userId;
      }

      public static SysNotarialOfficeEntity getSysNotarialOffice() {
               return (SysNotarialOfficeEntity)getRequest().getAttribute("NOTARIAL_OFFICE");
      }

       public static String getNotarialOfficeSecretKey() {
           return getSysNotarialOffice().getSecretKey();
       }

    /**
    * 获取request
    *
     * @return
     */
     public static HttpServletRequest getRequest() {
             ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
              return requestAttributes == null ? null : requestAttributes.getRequest();
     }


        /**
         * 获取token - json化 map信息
         *
         * @param claimMaps
         * @param encryKey
         * @param secondTimeOut
         * @return
         */
        public static String getTokenByJson(Map<String, Object> claimMaps, String encryKey, int secondTimeOut) {
            return getToken(claimMaps, true, encryKey, secondTimeOut);
        }

        /**
         * 获取token
         *
         * @param claimMaps
         * @param isJsonMpas
         * @param encryKey
         * @param secondTimeOut
         * @return
         */
        public static String getToken(Map<String, Object> claimMaps, boolean isJsonMpas, String encryKey, int secondTimeOut) {
            if (isJsonMpas) {
                claimMaps.forEach((key, val) -> {
                    claimMaps.put(key, JSON.toJSONString(val));
                });
            }
            long currentTime = System.currentTimeMillis();
            return Jwts.builder()
                    .setId(UUID.randomUUID().toString())
                    .setIssuedAt(new Date(currentTime))  //签发时间
                    .setSubject("User")  //说明
                    .setIssuer("shenniu003") //签发者信息
                    .setAudience("custom")  //接收用户
                    .compressWith(CompressionCodecs.GZIP)  //数据压缩方式
                    .signWith(SignatureAlgorithm.HS256, encryKey) //加密方式
                    .setExpiration(new Date(currentTime + secondTimeOut * 1000))  //过期时间戳
                    .addClaims(claimMaps) //cla信息
                    .compact();
        }

        /**
         * 获取token中的claims信息
         *
         * @param token
         * @param encryKey
         * @return
         */
        private static Jws<Claims> getJws(String token, String encryKey) {
            return Jwts.parser()
                    .setSigningKey(encryKey)
                    .parseClaimsJws(token);
        }

        public static String getSignature(String token, String encryKey) {
            try {
                return getJws(token, encryKey).getSignature();
            } catch (Exception ex) {
                return "";
            }
        }

        /**
         * 获取token中head信息
         *
         * @param token
         * @param encryKey
         * @return
         */
        public static JwsHeader getHeader(String token, String encryKey) {
            try {
                return getJws(token, encryKey).getHeader();
            } catch (Exception ex) {
                return null;
            }
        }

        /**
         * 获取payload body信息
         *
         * @param token
         * @param encryKey
         * @return
         */
        public static Claims getClaimsBody(String token, String encryKey) {
            return getJws(token, encryKey).getBody();
        }

        /**
         * 获取body某个值
         *
         * @param token
         * @param encryKey
         * @param key
         * @return
         */
        public static Object getVal(String token, String encryKey, String key) {
            return getJws(token, encryKey).getBody().get(key);
        }

        /**
         * 获取body某个值，json字符转实体
         *
         * @param token
         * @param encryKey
         * @param key
         * @param tClass
         * @param <T>
         * @return
         */
        public static <T> T getValByT(String token, String encryKey, String key, Class<T> tClass) {
            try {
                String strJson = getVal(token, encryKey, key).toString();
                return JSON.parseObject(strJson, tClass);
            } catch (Exception ex) {
                return null;
            }
        }

        /**
         * 是否过期
         *
         * @param token
         * @param encryKey
         * @return
         */
        public static boolean isExpiration(String token, String encryKey) {
            try {
                return getClaimsBody(token, encryKey)
                        .getExpiration()
                        .before(new Date());
            } catch (ExpiredJwtException ex) {
                return true;
            }
        }

        public static String getSubject(String token, String encryKey) {
            try {
                return getClaimsBody(token, encryKey).getSubject();
            } catch (Exception ex) {
                return "";
            }
        }
}
