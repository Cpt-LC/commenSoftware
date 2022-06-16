package com.lianzheng.core.pdf;



import com.aspose.words.*;
import com.aspose.words.net.System.Data.DataTable;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static java.lang.System.in;

@Log
@Service
public class AsposeWordsUtils {

    /**
     * 判断是否有授权文件 如果没有则会认为是试用版，转换的文件会有水印
     *
     * @return
     */
    public static boolean getLicense() {
        boolean result = false;
        try {
            InputStream is = AsposeWordsUtils.class.getClassLoader().getResourceAsStream("license-word.xml");
            License aposeLic = new License();
            aposeLic.setLicense(is);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * Word转PDF操作
     *
     * @param sourcerFile 源文件
     * @param targetFile  目标文件
     */
    public static void doc2pdf(String sourcerFile, String targetFile) {
        if (!getLicense()) {// 验证License 若不验证则转化出的pdf文档会有水印产生
            return;
        }
        try {
            long old = System.currentTimeMillis();
            File file = new File(targetFile);  //新建一个空白pdf文档
            FileOutputStream os = new FileOutputStream(file);
            Document doc = new Document(sourcerFile);                    //sourcerFile是将要被转化的word文档
            doc.save(os, SaveFormat.PDF);//全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF, EPUB, XPS, SWF 相互转换
            os.close();
            long now = System.currentTimeMillis();
            System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒");  //转化用时
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void rangePlace(String sourcerFile,String targetFile,Map<String,String> map) throws Exception{
        if (!getLicense()) {// 验证License 若不验证则转化出的pdf文档会有水印产生
            return;
        }
        Document doc = new Document(sourcerFile);
        DocumentBuilder builder = new DocumentBuilder(doc);
        for(Map.Entry<String,String> entry : map.entrySet()){
            if(builder.moveToMergeField(entry.getKey())) {
                builder.insertHtml(entry.getValue(), true);
            }else {
                throw new RuntimeException("丢失定位域:"+"«"+entry.getKey()+"»");
            }
        }

        doc.save(targetFile);
    }

    public static void replaceItems(String sourcerFile, String targetFile, ConcurrentHashMap<String, Map<String, Object>> hashMap) throws Exception {
        if (!getLicense()) {// 验证License 若不验证则转化出的pdf文档会有水印产生
            return;
        }
        Document doc = new Document(sourcerFile);
        DocumentBuilder builder = new DocumentBuilder(doc);
        Range range = doc.getRange();
        Enumeration keys = hashMap.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            Map<String, Object> object = hashMap.get(key);
            String type = object.get("type").toString();
            Object value = object.get("value");
            switch (type) {
                case "text":
                    String insert = null;
                    if(value!=null){
                        insert = value.toString();
                    }else {
                        insert ="";
                    }

                    //range.replace(key, insert.replace("\r\n", ""), true, true);
                    while (builder.moveToMergeField(key)) {
                        builder.insertHtml(insert, true);
                    }
                    break;
                case "img":
                    List<String> url = (List<String>) value;
                    if (url.size() == 0) {
                        log.info("图片地址为空");
                        break;
                    }
                    while (builder.moveToMergeField(key)) {
                        for(String item:url){
                            builder.insertImage(item,60,60);
                        }
                    }
//                    range.replace(Pattern.compile(key), new ReplaceAndInsertImage(url, 60, 60), false);
                    break;
                case "table":
                    DataTable table = new DataTable(key);
                    //获取到字段名
                    List<String> fieldName = (List<String>) object.get("fieldName");
                    List<Map<String, Object>> insertList = (List<Map<String, Object>>) value;
                    int size = fieldName.size();
                    int listSize = insertList.size();
                    if (listSize == 0 || size == 0) {
                        log.info("列表参数为空");
                        break;
                    }
                    //加入列
                    for (String string : fieldName) {
                        table.getColumns().add(string);
                    }
                    //动态构造参数
                    for (Map<String, Object> map : insertList) {
                        Object[] row = new Object[size];
                        //构造一条数据
                        for (int i = 0; i < size; i++) {
                            String columnName = table.getColumnName(i);
                            row[i] = map.get(columnName);
                        }
                        table.getRows().add(row);
                    }
                    doc.getMailMerge().executeWithRegions(table);
                    break;
                case "sign":
                    List<String> signUrl = (List<String>) value;
                    if (signUrl.size() == 0) {
                        log.info("图片地址为空");
                        break;
                    }
                    while (builder.moveToMergeField(key)) {
                        for(String item:signUrl){
                            builder.insertImage(item,70,65);
                        }
                    }
//                    range.replace(Pattern.compile(key), new ReplaceAndInsertImage(signUrl, 60, 60), false);
                    break;
                case "selfImg":
                    Map<String,Object> mapO =  (Map<String,Object>)value;
                    List<String> selfImgUrl = (List<String>)mapO.get("url");
                    Integer width = Integer.decode(mapO.get("width").toString());
                    Integer height = Integer.decode(mapO.get("height").toString());
                    if (selfImgUrl.size() == 0) {
                        log.info("图片地址为空");
                        break;
                    }
                    while (builder.moveToMergeField(key)) {
                        for(String item:selfImgUrl){
                            builder.insertImage(item,width,height);
                        }
                    }
//                    range.replace(Pattern.compile(key), new ReplaceAndInsertImage(signUrl, 60, 60), false);
                    break;
            }
        }
        doc.save(targetFile);
    }



    /**
     * 删除表格的第一行，基本是给拟稿纸生成公证书专用的
     * @param sourcerFile 源文件，即拟稿纸
     * @param targetFile  目标文件，即公证书
     * @throws Exception
     */
    public static void deleteFirstRow(String sourcerFile, String targetFile) throws Exception {
        if (!getLicense()) {// 验证License 若不验证则转化出的pdf文档会有水印产生
            return;
        }
        Document doc = new Document(sourcerFile);
        Table table = (Table)doc.getChild(NodeType.TABLE,0,true);
        table.getRows().removeAt(0);
        doc.save(targetFile);
    }


    /**
     * 公用获取表格文字
     * @param sourcerFile
     * @param tableIndex
     * @param rowIndex
     * @param colIndex
     * @return
     * @throws Exception
     */
    public static  String getText(String sourcerFile,int tableIndex,int rowIndex ,int colIndex) throws Exception{
        if (!getLicense()) {// 验证License 若不验证则转化出的pdf文档会有水印产生
            return "";
        }
        Document doc = new Document(sourcerFile);
        //得到文档中的第一个表格
        Table table = (Table)doc.getChild(NodeType.TABLE, tableIndex, true);
        //第一行第一列单元格
        Cell cell=table.getRows().get(rowIndex).getCells().get(colIndex);
        //单元格内容
        String cellContent=cell.toTxt().replaceAll("\n","<br>");

        return cellContent;
    }

    public static  void insertOtherNodeInTable(String sourcerFile,String targetFile,int tableIndex,int rowIndex ,int colIndex,String path) throws Exception{
        if (!getLicense()) {// 验证License 若不验证则转化出的pdf文档会有水印产生
            return;
        }
        Document docS = new Document(sourcerFile);
        Document docT = new Document(targetFile);
        Table table = (Table)docT.getChild(NodeType.TABLE, tableIndex, true);
        Cell cell=table.getRows().get(rowIndex).getCells().get(colIndex);
        //插入其他文档node的工具
        NodeImporter importer = new NodeImporter(docS, docT, ImportFormatMode.KEEP_SOURCE_FORMATTING);
        for(Paragraph item :docS.getFirstSection().getBody().getParagraphs()) {
            if(item.getText().trim().length()<1){
                continue;
            }
            item.getParagraphFormat().setLineSpacingRule(LineSpacingRule.MULTIPLE);
            item.getParagraphFormat().setLineSpacing(12);
            Node importNode = importer.importNode(item, true);
            cell.appendChild(importNode);
        }
        int num = docT.getPageCount();
//        double fontSize = 10;
//        if(num>1){
//            cell.removeAllChildren();
//            for(Paragraph item :docS.getFirstSection().getBody().getParagraphs()) {
//                if(item.getText().trim().length()<1){
//                    continue;
//                }
//                item.getParagraphFormat().setLineSpacingRule(LineSpacingRule.MULTIPLE);
//                item.getParagraphFormat().setLineSpacing(12);
//                item.getParagraphFormat().getStyle().getFont().setSize(fontSize);
//                Node importNode = importer.importNode(item, true);
//                cell.appendChild(importNode);
//            }
//            num = docT.getPageCount();
//            fontSize--;
//        }
        docT.save(path);
    }


//    /**
//     * pdf多合一(暂不可用)
//     * @param var1 文件数组
//     * @param var2
//     * @throws Exception
//     */
//    public static void addPdf(String[] var1,String var2) throws Exception{
//        if (!getLicense()) {// 验证License 若不验证则转化出的pdf文档会有水印产生
//            return;
//        }
//        PdfFileEditor pdfEditor = new PdfFileEditor();
//        pdfEditor.concatenate(var1, var2);
//    }



    public static void rangeMilmerge(InputStream inputStream,Map<String,String> map) throws Exception{
        if (!getLicense()) {// 验证License 若不验证则转化出的pdf文档会有水印产生
            return;
        }
        Document doc = new Document(inputStream);
        DocumentBuilder builder = new DocumentBuilder(doc);
        for(Map.Entry<String,String> entry : map.entrySet()){
            if(builder.moveToMergeField(entry.getKey())) {
            }else {
                throw new RuntimeException("丢失定位域:"+"«"+entry.getKey()+"»");
            }
        }
        inputStream.close();
    }

}

