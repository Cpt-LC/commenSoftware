package com.lianzheng.core.exceptionhandling;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @Description: 对异常的信息的格式化
 * @author: 何江雁
 * @date: 2021年10月15日 15:26
 */
public class ExceptionFormatter {

    /*
    * @Description: 获得异常的完整输出的字符串
    * @author: 何江雁
    * @date: 2021/10/15 15:29
    * @param null:
    * @Return:
    */
    public static String Format(Exception ex){
        if(ex==null){
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String msg=sw.toString().substring(0,250);
        System.out.println("thread-"+Thread.currentThread().getName());
        return msg;
    }
}
