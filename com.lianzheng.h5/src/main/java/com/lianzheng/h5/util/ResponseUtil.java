package com.lianzheng.h5.util;

import com.lianzheng.h5.common.ApiResult;
import com.lianzheng.h5.common.RedisHelper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

public class ResponseUtil {
    public static void renderJson(HttpServletResponse response, ApiResult<?> apiResult) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");

        PrintWriter out = response.getWriter();
        out.write(apiResult.toString());
        out.flush();
    }

}
