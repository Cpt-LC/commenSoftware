//
//package com.lianzheng.core.pdf;
//
////import com.aspose.words.Document;
//import com.aspose.words.Range;
//import com.itextpdf.text.BaseColor;
//
//
//import com.itextpdf.text.Document;
//import com.itextpdf.text.Image;
//import com.itextpdf.text.pdf.*;
//
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//import java.util.regex.Pattern;
//
//import static com.itextpdf.text.pdf.collection.PdfCollectionField.SIZE;
//
//
//
//
//public class ItextPdfUtils {
//
//    /**
//     * 生成公证书专用方法
//     * @param sourcerFile 源文件，即公证书docx
//     * @param targetFile  目标文件，即公证书pdf
//     * @throws Exception
//     */
//    public static void replaceRow(String sourcerFile, String targetFile, Map<String,Object> map) throws Exception {
//
//        String fileNameBk = sourcerFile.substring(0,sourcerFile.lastIndexOf("."))+"bk"+".pdf";
//        AsposeWordsUtils.doc2pdf(sourcerFile,fileNameBk);
//
//
//        //获取附加水印定位
//
//        float[] positionSign= PdfKeywordFinder.getAddImagePositionXY(fileNameBk,"«签名»");
//        float[] positionStamp= PdfKeywordFinder.getAddImagePositionXY(fileNameBk,"«盖章»");
////        float[] positionCertificate= PdfKeywordFinder.getAddImagePositionXY(fileNameBk,"公 证 书");
//
//
//
//        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
//        //替换公证书文字
//        Map<String,String> mergeMap =new HashMap<>();
//        mergeMap.put("签名","&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp");
//        mergeMap.put("盖章","&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp");
//        mergeMap.put("落款时间",df.format(new Date()));
//        AsposeWordsUtils.rangePlace(sourcerFile,fileNameBk,mergeMap);
//
//
//
//        //插入签名
//        PdfReader pdfReader = new PdfReader(fileNameBk);
//        PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(targetFile));
//        Image image = Image.getInstance(map.get("imgSign").toString());
//        //Fixed Positioning
//        image.scaleAbsolute(85, 80);
//        //Scale to new height and new width of image
//        image.setAbsolutePosition(positionSign[1]-60, positionSign[2]-30);
//        PdfContentByte content = pdfStamper.getOverContent((int) positionSign[0]);
//        content.addImage(image);
//
//
//        //插入盖章
//        image = Image.getInstance(map.get("imgStamp").toString());
//        //Fixed Positioning
//        image.scaleAbsolute(115, 115);
//        //Scale to new height and new width of image
//        image.setAbsolutePosition(positionStamp[1]-110, positionStamp[2]-70);
//        content = pdfStamper.getOverContent((int) positionStamp[0]);
//        content.addImage(image);
//
//
//
////        //插入公证编号
////        BaseFont baseFont = BaseFont.createFont("/st.ttf",BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
////        content = pdfStamper.getUnderContent((int) positionCertificate[0]);
////        content.beginText(); // 插入文字信息
////        content.setFontAndSize(baseFont, 16);
////        BaseColor baseColor = new BaseColor(0, 0, 0);
////        content.setColorFill(baseColor);
////        content.setTextMatrix(positionCertificate[1]+20, positionCertificate[2]-70); // 设置文字在页面中的坐标
////        content.showText(map.get("certificateId").toString());
////        content.endText();
//
//        pdfStamper.close();
//        pdfReader.close();
//
//
//
//        //删除定位pdf
//        File file = new File(fileNameBk);
//        file.delete();
//    }
//
//
//    //多文件合并
//    public static boolean mergePdfFiles(String[] files, String newfile) {
//                 boolean retValue = false;
//                 Document document = null;
//                try {
//                         document = new Document(new PdfReader(files[0]).getPageSize(1));
//                         PdfCopy copy = new PdfCopy(document, new FileOutputStream(newfile));
//                         document.open();
//                        for (int i = 0; i < files.length; i++) {
//                                 PdfReader reader = new PdfReader(files[i]);
//                                 int n = reader.getNumberOfPages();
//                                 for (int j = 1; j <= n; j++) {
//                                         document.newPage();
//                                         PdfImportedPage page = copy.getImportedPage(reader, j);
//                                         copy.addPage(page);
//                                     }
//                             }
//                         retValue = true;
//                     } catch (Exception e) {
//                         e.printStackTrace();
//                     } finally {
//                         document.close();
//                     }
//                 return retValue;
//    }
//
//
//
//
//}
