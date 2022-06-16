package com.lianzheng.core.resource;

import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @Description: 资源文件工具类
 * @author: 何江雁
 * @date: 2021年10月19日 11:38
 */
public class FileContentUtil {

    public static String getResourceFileContent(String resourcePath) throws IOException {
        String content = new ClassPathResourceReader(resourcePath.replace(ResourceUtils.CLASSPATH_URL_PREFIX,"")).getContent();
        return content;
    }
    public static InputStreamReader getStreamReader(String resourcePath) throws IOException {
        InputStreamReader streamReader = new ClassPathResourceReader(resourcePath.replace(ResourceUtils.CLASSPATH_URL_PREFIX,"")).getStreamReader();
        return streamReader;
    }
}
