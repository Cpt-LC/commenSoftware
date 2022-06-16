package com.lianzheng.core.pdf.pdfbox;

import com.lianzheng.core.pdf.AsposeWordsUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PdfBoxGenerate {

    public static void replaceRow(String sourcerFile, String targetFile, Map<String,Object> map ,Map<String,String> mergeMap) throws Exception {

        String fileNameBk = sourcerFile.substring(0,sourcerFile.lastIndexOf("."))+"bk"+".pdf";
        AsposeWordsUtils.doc2pdf(sourcerFile,fileNameBk);

        //获取坐标
        PdfBoxKeyWordPosition pdf = new PdfBoxKeyWordPosition("«签名»", fileNameBk);
        float[] positionSign = pdf.getCoordinate().get(0);
        pdf = new PdfBoxKeyWordPosition("«盖章»", fileNameBk);
        float[] positionStamp = pdf.getCoordinate().get(0);


        AsposeWordsUtils.rangePlace(sourcerFile,fileNameBk,mergeMap);



        File file = new File(fileNameBk);
        PDDocument doc = PDDocument.load(file);

        //插入盖章
        PDImageXObject pdImage = PDImageXObject.createFromFile(map.get("imgStamp").toString(), doc);
        PDPage page = doc.getPage((int)positionStamp[2]-1);
        float x = positionStamp[0];
        float y = positionStamp[1];
        PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true);
        contentStream.drawImage(pdImage, x-95, y,115,115);
        contentStream.close();



        //插入签名
        pdImage = PDImageXObject.createFromFile(map.get("imgSign").toString(), doc);
        page = doc.getPage((int)positionSign[2]-1);
        x = positionSign[0];
        y = positionSign[1];
        contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true);
        contentStream.drawImage(pdImage, x-50, y+30,85,80);

        contentStream.close();


        doc.save(targetFile);
        doc.close();

        //删除定位pdf
        file = new File(fileNameBk);
        file.delete();
    }


    public static boolean mergePdfFiles(String[] files, String newfile) throws Exception {

        //Instantiating PDFMergerUtility class
        PDFMergerUtility PDFmerger = new PDFMergerUtility();
        //Setting the destination file
        PDFmerger.setDestinationFileName(newfile);

        for(String item:files){
            File file = new File(item);
            PDDocument doc = PDDocument.load(file);
            //adding the source files
            PDFmerger.addSource(file);
            doc.close();
        }

        //Merging the two documents
        PDFmerger.mergeDocuments();


        return true;
    }

}
