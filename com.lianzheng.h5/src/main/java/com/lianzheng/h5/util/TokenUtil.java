package com.lianzheng.h5.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenUtil {

    //过期时间设置
    private static final long EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000;

    //私钥设置(随便乱写的)
    private static final String TOKEN_SECRET = "kunshan123!";

    private final static String USER_UUID = "uuid";

    public static void main(String[] args) {
//        String uuid = UUID.randomUUID().toString();
//        System.out.println(uuid);

        //eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0eXBlIjoidXNlciIsImV4cCI6MTYzNTE1MDEwOCwidXVpZCI6ImUwOTQwYTYxLTcwMTMtNDMzZC04MjUzLWY0Y2Y4OTU0MTExYiJ9.l7hT31-JV9IOC--rEEcfi3zS5iaW320vjWhH0_ND6ZU
        String token = createToken("a717d4bf-d343-4c56-8c85-360ae23293b8" );
        System.out.println("编码: >> " + token);

        token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0eXBlIjoiOTk5IiwiZXhwIjoxNjM2ODUwMzMyLCJ1dWlkIjoiYjFiZWUyOGMtNGM3ZS00NDUzLWE2YTMtOGEyYmExMmNjMjRiIn0.4JLo9Vt8hrDz02mnK0B7A03JW8keNoCxR5_H-w9Pv-s";

        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).build();
        DecodedJWT _decodedJwt = verifier.verify(token);
        String uuid = _decodedJwt.getClaim(USER_UUID).asString();
        Date tokenDate = _decodedJwt.getExpiresAt();
        System.out.println("解码: >> " + uuid  );
    }

    public static String createToken(String id ) {
        //过期时间和加密算法设置
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        //头部信息
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS256");

        return JWT.create()
                .withHeader(header)
                .withClaim(USER_UUID, id)
                .withExpiresAt(date)
                .sign(Algorithm.HMAC256(TOKEN_SECRET));
    }

    public static String getTokenUserId(String token) {
        String parseToUserId = null;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).build();
            parseToUserId = "" + verifier.verify(token).getClaim(USER_UUID).asString();
        } catch (Exception e) {
            System.out.println("用户UUID错误 >>>> " + token);
            e.printStackTrace();
        }
        return parseToUserId;
    }



    public static boolean isTokenDated(String token) {
        ///是否已经过期
        boolean isDated = false;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).build();
            Date tokenDate = verifier.verify(token).getExpiresAt();
            if (tokenDate.before(new Date())) { ///token时间更小,表示过期
                System.out.println("Token最大有效时间: " + tokenDate.getTime());
                System.out.println("当前的服务器时间: " + new Date().getTime());
                isDated = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            isDated = true;
        }
        return isDated;
    }
}
