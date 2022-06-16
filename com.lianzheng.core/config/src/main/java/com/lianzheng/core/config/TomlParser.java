package com.lianzheng.core.config;

import com.lianzheng.core.resource.FileContentUtil;
import com.moandjiezana.toml.Toml;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @Description: 解析toml配置文件，以解决日志记录发生在系统加载配置之前
 * @author: 何江雁
 * @date: 2021年10月11日 10:55
 */
public class TomlParser {
    /*
     * @Description: 解析toml配置文件
     * @author: 何江雁
     * @date: 2021/10/11 12:56
     * @param filePath: only support classpath
     * @Return:
     */
    public static <TConfig> TConfig parse(String filePath, String starterKey, Class<TConfig> classObj) throws IOException {
        Toml toml = rawParse(filePath);
        boolean existed = toml.contains(starterKey);
        Assert.isTrue(existed, String.format("%s not existed", starterKey));
        Toml root = toml.getTable(starterKey);
        TConfig result = root.to(classObj);
        return result;
    }

    /*
     * @Description: 解析toml配置文件
     * @author: 何江雁
     * @date: 2021/10/11 12:56
     * @param filePath: only support classpath
     * @Return:
     */
    public static Toml rawParse(String filePath) throws IOException {
        File file = new File(filePath);
//        InputStreamReader streamReader = FileContentUtil.getStreamReader(filePath);
        InputStreamReader streamReader = new InputStreamReader(new FileInputStream(file));
        Toml toml = new Toml().read(streamReader);

        return toml;
    }
}
