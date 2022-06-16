package com.lianzheng.h5.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifDirectoryBase;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

;

public class RotateImage {
    public static void main(String[] args) throws Exception {
        String filePath = "/Users/daishuming/fileUpload/00e3934a-b538-49a7-861d-e4897307fd4e.png";
        String newFilePath = "/Users/daishuming/fileUpload/1.png";
        rotateImg(filePath, newFilePath);
    }

    public static boolean rotateImg(String filePath, String newFilePath) {
        try {
            File file = new File(filePath);
            //测试发现文件大于7Mb以上时会出现读取速率很慢，找时间再改改；
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            Directory directory = metadata.getFirstDirectoryOfType(ExifDirectoryBase.class);
            int orientation = 0;
            // Exif信息中有保存方向,把信息复制到缩略图
            // 原图片的方向信息
            if (directory != null && directory.containsTag(ExifDirectoryBase.TAG_ORIENTATION)) {
                orientation = directory.getInt(ExifDirectoryBase.TAG_ORIENTATION);
                System.out.println(orientation);
            }
            int angle = 0;
            if (6 == orientation) {
                //6旋转90
                angle = 90;
            } else if (3 == orientation) {
                //3旋转180
                angle = 180;
            } else if (8 == orientation) {
                //8旋转90
                angle = 270;
            }
            System.out.println(angle);
            BufferedImage src = ImageIO.read(file);
            BufferedImage des = RotateImage.Rotate(src, angle);
            String filename = file.getName();
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
            ImageIO.write(des, ext, new File(newFilePath));
            return true;
        } catch (IOException | MetadataException | ImageProcessingException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static BufferedImage Rotate(Image src, int angel) {
        int srcWidth = src.getWidth(null);
        int srcHeight = src.getHeight(null);
        // calculate the new image size
        Rectangle rect_des = CalcRotatedSize(new Rectangle(new Dimension(
                srcWidth, srcHeight)), angel);

        BufferedImage res = null;
        res = new BufferedImage(rect_des.width, rect_des.height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = res.createGraphics();
        // transform
        g2.translate((rect_des.width - srcWidth) / 2,
                (rect_des.height - srcHeight) / 2);
        g2.rotate(Math.toRadians(angel), srcWidth / 2, srcHeight / 2);

        g2.drawImage(src, null, null);
        return res;
    }

    public static Rectangle CalcRotatedSize(Rectangle src, int angel) {
        // if angel is greater than 90 degree, we need to do some conversion
        if (angel >= 90) {
            if (angel / 90 % 2 == 1) {
                int temp = src.height;
                src.height = src.width;
                src.width = temp;
            }
            angel = angel % 90;
        }

        double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
        double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
        double angel_alpha = (Math.PI - Math.toRadians(angel)) / 2;
        double angel_dalta_width = Math.atan((double) src.height / src.width);
        double angel_dalta_height = Math.atan((double) src.width / src.height);

        int len_dalta_width = (int) (len * Math.cos(Math.PI - angel_alpha
                - angel_dalta_width));
        int len_dalta_height = (int) (len * Math.cos(Math.PI - angel_alpha
                - angel_dalta_height));
        int des_width = src.width + len_dalta_width * 2;
        int des_height = src.height + len_dalta_height * 2;
        return new Rectangle(new Dimension(des_width, des_height));
    }

}
