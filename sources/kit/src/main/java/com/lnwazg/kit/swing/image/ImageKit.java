package com.lnwazg.kit.swing.image;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;

/**
 * 图片处理工具<br>
 * 可应用于验证码破解等等
 * @author nan.li
 * @version 2016年7月28日
 */
public class ImageKit
{
    /**
     * 圆角的默认长度
     */
    private static final int DEFAULT_ARC_LENGTH = 12;
    
    static String lastString = "";
    
    static int maxValue = 765;
    
    /**
     * 采样个数
     */
    static int DEFAULT_SLICE_NUM = 15;
    
    /**
     * 图片拍照识别<br>
     * 识别任意的图片，生成10张照片，供肉眼识别<br>
     * 从上到下生成
     * @author nan.li
     * @param imageIcon
     */
    public static void takePhotos(String srcFile)
    {
        takePhotos(srcFile, DEFAULT_SLICE_NUM);
    }
    
    public static void takePhotos(String srcFile, int sliceNum)
    {
        try
        {
            BufferedImage img = ImageIO.read(new File(srcFile));
            int width = img.getWidth();
            int height = img.getHeight();
            BufferedImage imgOut = new BufferedImage(width, height * sliceNum, BufferedImage.TYPE_INT_RGB);
            for (int i = 0; i < sliceNum; i++)
            {
                //逐像素扫描
                for (int x = 0; x < width; x++)
                {
                    for (int y = 0; y < height; ++y)
                    {
                        //从左至右，从上至下，逐个像素扫描。最终转为黑白图
                        if (isWhite(img.getRGB(x, y), i, sliceNum))
                        {
                            //找到纯白的，将其设置为白色
                            imgOut.setRGB(x, (y + i * height), Color.WHITE.getRGB());
                        }
                        else
                        {
                            //剩余的，将其全部渲染为黑色
                            imgOut.setRGB(x, (y + i * height), Color.BLACK.getRGB());
                        }
                    }
                }
            }
            int pointIndex = srcFile.lastIndexOf(".");
            String outFile = srcFile.substring(0, pointIndex) + "_out" + srcFile.substring(pointIndex);//输出的文件，重命名之，并加入out以表示是输出的图像
            ImageIO.write(imgOut, "png", new File(outFile));
            System.out.println("OK!");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public static boolean isWhite(int colorInt)
    {
        Color color = new Color(colorInt);
        String thisStr = String.format("red %s green %s blue %s T %s", color.getRed(), color.getGreen(), color.getBlue(), color.getTransparency());
        if (thisStr.equals(lastString))
        {
            System.out.print(thisStr);
        }
        else
        {
            System.out.print(">>>" + thisStr);
            lastString = thisStr;
        }
        if (color.getRed() + color.getGreen() + color.getBlue() > 400)
        {
            System.out.println("");
            return true;
        }
        System.out.println("    false");
        return false;
    }
    
    /**
     * 判断该像素点是否是白色
     * @author nan.li
     * @param colorInt
     * @param picIndex
     * @return
     */
    public static boolean isWhite(int colorInt, int picIndex, int sliceNum)
    {
        Color color = new Color(colorInt);
        int eachPiece = maxValue / sliceNum;//分片后的单位值
        if (color.getRed() + color.getGreen() + color.getBlue() > ((picIndex) * eachPiece))
        {
            return true;
        }
        return false;
    }
    
    /**
     * 移除背景色
     * @author nan.li
     * @param picFile
     * @return
     * @throws IOException
     */
    public static BufferedImage removeBackGround(String picFile)
        throws IOException
    {
        BufferedImage img = ImageIO.read(new File(picFile));
        int width = img.getWidth();
        int height = img.getHeight();
        //逐像素扫描
        for (int x = 0; x < width; x++)
        {
            System.out.println(String.format("x: %s", x));
            for (int y = 0; y < height; ++y)
            {
                //从左至右，从上至下，逐个像素扫描
                if (isWhite(img.getRGB(x, y)))
                {
                    //找到纯白的，将其设置为白色
                    img.setRGB(x, y, Color.WHITE.getRGB());
                }
                else
                {
                    //剩余的，将其全部渲染为黑色
                    img.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }
        return img;
    }
    
    public static void main(String[] args)
    {
        //        takePhotos("c:\\11.png");
        ImageKit.takePhotos("c:\\111.png");
        //        System.out.println(String.format("white: %s %s %s %s %s ", Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue(), Color.WHITE.getTransparency(), Color.WHITE.getRGB()));
        //        System.out.println(String.format("black: %s %s %s %s %s ", Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue(), Color.BLACK.getTransparency(), Color.BLACK.getRGB()));
        //        white: 255 255 255 1  -1 
        //        black: 0   0   0   1  -16777216 
        //        try
        //        {
        //            //                        JOptionPane.showMessageDialog(null, new ImageIcon(removeBackGround("c:\\11.png")));
        //            //            JOptionPane.showMessageDialog(null, new ImageIcon(removeBackGround("c:\\img2.jpg")));
        //            //            takePhoto("c:\\11.png");
        //            //            takePhoto("c:\\img.jpg");
        //            //            takePhoto("c:\\img2.jpg");
        //            //            takePhoto("c:\\img3.png");
        //            //            takePhoto("c:\\1.jpg");
        //            //            takePhoto("c:\\2.jpg");
        //            //            System.out.println("OK");
        //        }
        //        catch (HeadlessException e)
        //        {
        //            e.printStackTrace();
        //        }
        //        catch (IOException e)
        //        {
        //            e.printStackTrace();
        //        }
    }
    
    public static void drawString(Graphics2D g, int x, int y, int w, int h, String str, Font font, Color foreColor)
    {
        //默认居中绘制
        drawString(g, x, y, w, h, str, font, foreColor, Align.CENTER);
    }
    
    /**
     * 在指定区域绘制字符串
     * @author nan.li
     * @param i
     * @param j
     * @param graphWidth
     * @param graphTitleHeight
     * @param title
     * @param titleFont
     * @param foreColor
     */
    public static void drawString(Graphics2D g, int x, int y, int w, int h, String str, Font font, Color foreColor, Align align)
    {
        //        Logs.d(String.format("drawString %s %s %s %s", x, y, w, h));
        
        if (StringUtils.isEmpty(str))
        {
            return;
        }
        
        //绘制前景
        g.setColor(foreColor);
        g.setFont(font);
        
        //字符串的宽度
        int strWidth = g.getFontMetrics().stringWidth(str);
        int strHeight = g.getFontMetrics().getHeight();
        
        //字体抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        //绘制字符串
        float finalX = 0;
        float finalY = y + (float)(h / 2 * 1.3);
        switch (align)
        {
            case CENTER:
                finalX = x + (w / 2 - strWidth / 2);
                break;
            case LEFT:
                finalX = x;
                break;
            case RIGHT:
                finalX = x + (w - strWidth);
                break;
            default:
                break;
        }
//        int delta = 5;//偏移量，防止贴到外框上
//        g.drawString(str, finalX + delta, finalY);
        g.drawString(str, finalX, finalY);
    }

    /**
     * 绘制矩形
     * @author nan.li
     * @param g
     * @param x
     * @param y
     * @param w
     * @param h
     * @param margin
     * @param foreColor
     */
    public static void drawRect(Graphics g, int x, int y, int w, int h, int margin, Color foreColor)
    {
        //        Logs.d(String.format("drawRect %s %s %s %s", x, y, w, h));
        g.setColor(foreColor);
        
        //画圆角矩形，就不会那么生硬
        g.drawRect(x + margin, y + margin, w - 1 - 2 * margin, h - 1 - 2 * margin);
    }
    
    /**
     * 画圆角矩形
     * @author nan.li
     * @param g
     * @param x
     * @param y
     * @param w
     * @param h
     * @param margin
     * @param foreColor
     */
    public static void drawRoundRect(Graphics g, int x, int y, int w, int h, int margin, Color foreColor)
    {
        drawRoundRect(g, x, y, w, h, margin, foreColor, DEFAULT_ARC_LENGTH, DEFAULT_ARC_LENGTH);
    }
    
    public static void fillRoundRect(Graphics g, int x, int y, int w, int h, int margin, Color foreColor)
    {
        fillRoundRect(g, x, y, w, h, margin, foreColor, DEFAULT_ARC_LENGTH, DEFAULT_ARC_LENGTH);
    }
    
    public static void fillRoundRect(Graphics g, int x, int y, int w, int h, int margin, Color foreColor, int arcWidth, int arcHeight)
    {
        g.setColor(foreColor);
        //填充圆角矩形
        g.fillRoundRect(x + margin, y + margin, w - 1 - 2 * margin, h - 1 - 2 * margin, arcWidth, arcHeight);
    }
    
    /**
     * 画圆角矩形
     * @author nan.li
     * @param g
     * @param x
     * @param y
     * @param w
     * @param h
     * @param margin
     * @param foreColor
     * @param arcWidth
     * @param arcHeight
     */
    public static void drawRoundRect(Graphics g, int x, int y, int w, int h, int margin, Color foreColor, int arcWidth, int arcHeight)
    {
        //        Logs.d(String.format("drawRoundRect %s %s %s %s", x, y, w, h));
        g.setColor(foreColor);
        
        //画圆角矩形，就不会那么生硬
        g.drawRoundRect(x + margin, y + margin, w - 1 - 2 * margin, h - 1 - 2 * margin, arcWidth, arcHeight);
    }
    
}
