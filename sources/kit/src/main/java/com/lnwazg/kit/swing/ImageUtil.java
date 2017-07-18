package com.lnwazg.kit.swing;

import java.awt.Toolkit;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;

/**
 * 图片常用的工具类
 * @author Administrator
 * @version 2016年2月10日
 */
public class ImageUtil
{
    public static void main(String[] args)
    {
        Image img = getImageFromUrl("http://oimagea6.ydstatic.com/image?url=http://en.wikipedia.org/wiki/File:WhiteCat.jpg&product=PICDICT_EDIT");
        System.out.println("img.width=" + img.getWidth() + " img.hight=" + img.getHeight());
        
        System.out.println(
            getAutoZoomHeightByWidth("http://oimagea6.ydstatic.com/image?url=http://en.wikipedia.org/wiki/File:WhiteCat.jpg&product=PICDICT_EDIT", 120));
            
        System.out.println(
            getAutoZoomHeightByWidth("http://oimagea6.ydstatic.com/image?url=http://en.wikipedia.org/wiki/File:WhiteCat.jpg&product=PICDICT_EDIT", 200));
            
        System.out.println(
            getAutoZoomWidthByHeight("http://oimagea6.ydstatic.com/image?url=http://en.wikipedia.org/wiki/File:WhiteCat.jpg&product=PICDICT_EDIT", 80));
            
        System.out.println(
            getAutoZoonImageHtmlByWidth("http://oimagea6.ydstatic.com/image?url=http://en.wikipedia.org/wiki/File:WhiteCat.jpg&product=PICDICT_EDIT", 120));
        System.out.println(getAutoZoonImageHtmlByWidth(
            "http://oimageb1.ydstatic.com/image?url=http://mydrupalsite.co.uk/JJLJ/sites/default/files/images/computer_pig_logo-cartoon-pig.jpg&product=PICDICT_EDIT",
            120));
    }
    
    /**
     * 根据宽度或者自动缩放后的自适应高度<br>
     * 适用于swing的jeditpane绘制图片
     * @author Administrator
     * @param url
     * @param width
     * @return
     */
    public static String getAutoZoonImageHtmlByWidth(String url, int width, int border)
    {
        String result = String.format("<p align=\"left\"><img width=\"%s\" height=\"%s\" border=\"%s\" src=\"%s\"></p>",
            width,
            getAutoZoomHeightByWidth(url, width),
            border,
            url);
        return result;
    }
    
    /**
     * 根据宽度或者自动缩放后的自适应高度<br>
     * 适用于swing的jeditpane绘制图片<br>
     * 默认边框大小为1
     * @author Administrator
     * @param url
     * @param width
     * @return
     */
    public static String getAutoZoonImageHtmlByWidth(String url, int width)
    {
        return getAutoZoonImageHtmlByWidth(url, width, 0);
    }
    
    public static String getAutoZoonImageHtmlByHeight(String url, int height)
    {
        return getAutoZoonImageHtmlByHeight(url, height, 0);
    }
    
    public static String getAutoZoonImageHtmlByHeight(String url, int height, int border)
    {
        String result = String.format("<p align=\"left\"><img width=\"%s\" height=\"%s\" border=\"%s\" src=\"%s\"></p>",
            getAutoZoomWidthByHeight(url, height),
            height,
            border,
            url);
        return result;
    }
    
    /**
     * 根据限定的最大尺寸进行缩放
     * @author Administrator
     * @param imgUrl
     * @param imageMaxDimen
     * @return
     */
    public static String getAutoZoonImageHtmlByMaxDimen(String imgUrl, int imageMaxDimen)
    {
        return getAutoZoonImageHtmlByMaxDimen(imgUrl, imageMaxDimen, 0);
    }
    
    public static String getAutoZoonImageHtmlByMaxDimen(String imgUrl, int imageMaxDimen, int border)
    {
        String ret = null;
        Image img = getImageFromUrl(imgUrl);
        if (img == null)
        {
            return null;
        }
        else
        {
            float w = img.getWidth();
            float h = img.getHeight();
            if (w >= h)
            {
                //宽度较大,以宽度作为基准来自动缩放
                ret = String.format("<p align=\"left\"><img width=\"%s\" height=\"%s\" border=\"%s\" src=\"%s\"></p>",
                    imageMaxDimen,
                    (int)(h * imageMaxDimen / w),
                    border,
                    imgUrl);
            }
            else
            {
                //高度较大
                ret = String.format("<p align=\"left\"><img width=\"%s\" height=\"%s\" border=\"%s\" src=\"%s\"></p>",
                    (int)(w * imageMaxDimen / h),
                    imageMaxDimen,
                    border,
                    imgUrl);
            }
        }
        return ret;
    }
    
    /**
     * 获取自动缩放后的宽度
     * @author Administrator
     * @param url
     * @param height
     * @return
     */
    public static int getAutoZoomWidthByHeight(String url, int height)
    {
        Image image = getImageFromUrl(url);
        if (image == null)
        {
            return 0;
        }
        else
        {
            float w = image.getWidth();
            float h = image.getHeight();
            return (int)(w * height / h);
        }
    }
    
    /**
     * 获取自动缩放后的高度
     * @author Administrator
     * @param url
     * @param width
     * @return
     */
    public static int getAutoZoomHeightByWidth(String url, int width)
    {
        Image image = getImageFromUrl(url);
        if (image == null)
        {
            return 0;
        }
        else
        {
            float w = image.getWidth();
            float h = image.getHeight();
            return (int)(h * width / w);
        }
    }
    
    /**
     * 获取远程的图片对象
     * @author Administrator
     * @param url
     * @return
     */
    public static Image getImageFromUrl(String url)
    {
        Image img = null;
        try
        {
            img = Image.getInstance(new URL(url));
        }
        catch (BadElementException e)
        {
            e.printStackTrace();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return img;
    }
    
    /**
     * 根据相对Url生成ImageIcon
     * 考虑到兼容导出jar包，因此只能采用classloader getResource()的加载模式
     * 并且这种方式将无法读取jar包中的文件夹，因此所有需要读取的文件都必须指名道姓，而不是通过遍历文件夹的方式进行加载<br>
     * 可用预编译的手段，获取到所有的待读取文件的名称的列表，生成到配置文件中，这样，jar包里面的文件列表也可以完美获取到！
     * @param relativeUrl
     * @return
     */
    public static Icon getIcon(String relativeUrl)
    {
        return new ImageIcon(ImageUtil.class.getClassLoader().getResource(relativeUrl));
    }
    
    /**
     * 获得图标对象，并指定宽高度
     * @author Administrator
     * @param relativeUrl
     * @param width
     * @param height
     * @return
     */
    public static Icon getIcon(String relativeUrl, int width, int height)
    {
        ImageIcon imageIcon = new ImageIcon(ImageUtil.class.getClassLoader().getResource(relativeUrl));
        return new ImageIcon(imageIcon.getImage().getScaledInstance(width, height, java.awt.Image.SCALE_DEFAULT));
    }
    
    public static java.awt.Image image(String relativeUrl)
    {
        return Toolkit.getDefaultToolkit().createImage(ImageUtil.class.getClassLoader().getResource(relativeUrl));
    }
}
