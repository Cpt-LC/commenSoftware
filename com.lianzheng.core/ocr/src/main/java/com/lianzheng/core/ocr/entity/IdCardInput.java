package com.lianzheng.core.ocr.entity;

/**
 * @Description: TODO
 * @author: 何江雁
 * @date: 2022年02月17日 17:50
 */
public class IdCardInput extends OcrInput {
    private byte[] backImage; //身份证人像面
    private byte[] frontImage;//身份证国徽面

    public void setBackImage(byte[] backImage){
        this.backImage = backImage;
    }

    /* @Description: 身份证人像面
    * */
    public byte[] getBackImage(){
        return this.backImage;
    }

    public void setFrontImage(byte[] frontImage){
        this.frontImage = frontImage;
    }


    /* @Description: 身份证国徽面
     * */
    public byte[] getFrontImage(){
        return this.frontImage;
    }
}
