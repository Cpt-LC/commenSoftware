package com.lianzheng.core.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
/**
 * @Description: 读取jar包里的资源文件的工具类
 * https://cloud.tencent.com/developer/article/1466854
 * @author: 何江雁
 * @date: 2021年11月27日 16:42
 */
public class ClassPathResourceReader {
    /**
     * path:文件路径
     * @since JDK 1.8
     */
    private final String path;

    /**
     * content:文件内容
     * @since JDK 1.6
     */
    private String content;

    public ClassPathResourceReader(String path) {
        this.path = path;
    }

    public String getContent() {
        if (content == null) {
            try {
                ClassPathResource resource = new ClassPathResource(path);
                BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
                content = reader.lines().collect(Collectors.joining("\n"));
                reader.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return content;
    }

    public InputStreamReader getStreamReader() {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            return new InputStreamReader(resource.getInputStream());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
