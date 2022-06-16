package com.lianzheng.core.pdf;

import com.aspose.words.*;


import java.util.List;

public class ReplaceAndInsertImage implements IReplacingCallback{
    public List<String> url;
    public int width;
    public int height;
    public ReplaceAndInsertImage(){
    }
    public ReplaceAndInsertImage(List<String> url,int width,int height){
        this.url = url;
        this.width=width;
        this.height=height;
    }
    @Override
    public int replacing(ReplacingArgs e) throws Exception {
        //Get current node
        Node node = e.getMatchNode();
        //Get current document
        Document document =  (Document) node.getDocument();
        DocumentBuilder builder = new DocumentBuilder(document);
        //Move the cursor to the specified node
        builder.moveTo(node);
        //Insert picture
        for(String item:url){
            builder.insertImage(item,width,height);
        }
        return ReplaceAction.REPLACE;
    }
}
