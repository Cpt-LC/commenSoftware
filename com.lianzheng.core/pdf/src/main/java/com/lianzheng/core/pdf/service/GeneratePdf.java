package com.lianzheng.core.pdf.service;

import cn.hutool.crypto.SecureUtil;
import com.lianzheng.core.pdf.AsposeWordsUtils;
import com.lianzheng.core.pdf.pdfbox.PdfBoxGenerate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GeneratePdf {


    /**
     * 文件生成pdf
     * @param var1 需替换的相关参数
     * @param var2 模板原文件路径
     * @param var3 目标文件路径doc
     * @param var4 目标文件路径pdf
     * @param var5 是否需要转pdf  true  需要
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public String generatePdf(ConcurrentHashMap<String, Map<String,Object>> var1, String var2, String var3,String var4,boolean var5) throws Exception{
        AsposeWordsUtils.replaceItems(var2, var3, var1);
        String fileHash = null;
        // word转 pdf
        if(var5){
            Thread.sleep(1000);
            AsposeWordsUtils.doc2pdf(var3, var4);
            // 生成文件hash
            fileHash = SecureUtil.md5(new File(var4));
        }else{
            fileHash = SecureUtil.md5(new File(var3));
        }
        return fileHash;
    }

    /**
     * 删除文件中表格的第一行，将剩余内容保存到targetFile
     * @param sourcerFile
     * @param targetFile
     * @return
     * @throws Exception
     */
    public String generateNotarization(String sourcerFile, String targetFile) throws Exception{
        AsposeWordsUtils.deleteFirstRow(sourcerFile, targetFile);
        return SecureUtil.md5(new File(targetFile));
    }

    /**
     * pdf公证书加水印
     * @param sourcerFile
     * @param targetFile
     * @throws Exception
     */
    public String generateCertificate(String sourcerFile, String targetFile,Map<String,Object> map,Map<String,String> mergeMap) throws Exception{
//        ItextPdfUtils.replaceRow(sourcerFile, targetFile, map);
        PdfBoxGenerate.replaceRow(sourcerFile, targetFile, map,mergeMap);
        return SecureUtil.md5(new File(targetFile));
    }


    /**
     * 获取表格内容
     * @param sourcerFile
     * @param tableIndex
     * @param rowIndex
     * @param colIndex
     * @return
     */
    public String getText(String sourcerFile,int tableIndex,int rowIndex ,int colIndex) throws Exception{
        return AsposeWordsUtils.getText( sourcerFile, tableIndex, rowIndex , colIndex);
    }

    /**
     * 插入内容到某表格中
     * @param sourcerFile 源文件
     * @param targetFile 插入文本目标文件
     * @param tableIndex 表格
     * @param rowIndex 行
     * @param colIndex 列
     * @param path 保存路径
     * @throws Exception
     */
    public   void insertOtherNodeInTable(String sourcerFile,String targetFile,int tableIndex,int rowIndex ,int colIndex,String path) throws Exception{
        AsposeWordsUtils.insertOtherNodeInTable(sourcerFile,targetFile,tableIndex,rowIndex ,colIndex,path);
    }

    /**
     * word转pdf
     * @param var1  源文件
     * @param var2  目标文件
     */
    public  void doc2pdf(String var1,String var2){
        AsposeWordsUtils.doc2pdf(var1, var2);
    }

    /**
     * 文件多合一
     * @param var1 多文件
     * @param var2 生成路径
     */
    public  void addPdf(String[] var1,String var2) throws Exception{
//        AsposeWordsUtils.addPdf(var1,var2);//aspose未注册不可用
//        ItextPdfUtils.mergePdfFiles(var1,var2);//不能用itext
        PdfBoxGenerate.mergePdfFiles(var1,var2);
    }


    /**
     * 邮件合并文字替换单用方法
     * @param sourcerFile
     * @param targetFile
     * @param map
     * @throws Exception
     */
    public  void rangePlace(String sourcerFile,String targetFile,Map<String,String> map) throws Exception{
        AsposeWordsUtils.rangePlace(sourcerFile,targetFile,map);
    }


    /**
     * 验证是否有邮件合并
     * @param inputStream
     * @param map
     * @throws Exception
     */
    public static void rangeMilmerge(InputStream inputStream, Map<String,String> map) throws Exception{
        AsposeWordsUtils.rangeMilmerge(inputStream,map);
    }
}
