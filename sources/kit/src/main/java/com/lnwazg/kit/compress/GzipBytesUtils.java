package com.lnwazg.kit.compress;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * gzip字节压缩以及解压缩工具类
 * 
 * @author  Administrator
 * @version  [版本号, 2012-9-30]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class GzipBytesUtils
{
    /**
    * Logger for this class
    */
    private static final Log logger = LogFactory.getLog(GzipBytesUtils.class);
    
    /** 
     * 解压缩gzip字节数组
     * @param gzipBytes
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static byte[] unzip(byte[] gzipBytes)
    {
        byte[] result = null;
        GzipCompressorInputStream zis = null;
        ByteArrayOutputStream bos = null;
        try
        {
            zis = new GzipCompressorInputStream(new BufferedInputStream(new ByteArrayInputStream(gzipBytes)));
            bos = new ByteArrayOutputStream();
            IOUtils.copy(zis, bos);
            bos.close();
            result = bos.toByteArray();
        }
        catch (Exception e)
        {
            logger.error(e);
        }
        finally
        {
            IOUtils.closeQuietly(bos);
            IOUtils.closeQuietly(zis);
        }
        return result;
    }
    
    /** 
     * 压缩字节数组
     * @param bytes
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static byte[] zip(byte[] bytes)
    {
        byte[] result = null;
        GzipCompressorOutputStream zos = null;
        ByteArrayOutputStream bos = null;
        try
        {
            bos = new ByteArrayOutputStream();
            zos = new GzipCompressorOutputStream(new BufferedOutputStream(bos));
            IOUtils.write(bytes, zos);
            zos.close();
            result = bos.toByteArray();
        }
        catch (Exception e)
        {
            logger.error(e);
        }
        finally
        {
            IOUtils.closeQuietly(zos);
            IOUtils.closeQuietly(bos);
        }
        return result;
    }
    
    public static void main(String[] args)
        throws IOException
    {
        //        File file = new File("c:\\111.gif");
        //        File fileTo = new File("c:\\111.gif.gz");
        //        File fileUnzip = new File("c:\\222.gif");
        //        
        //        //file->zip
        //        byte[] original = FileUtils.readFileToByteArray(file);
        //        byte[] zipped = zip(original);
        //        FileUtils.writeByteArrayToFile(fileTo, zipped);
        //        
        //        //zip->file
        //        byte[] unzipped = unzip(zipped);
        //        FileUtils.writeByteArrayToFile(fileUnzip, unzipped);
        
        File file = new File("c:\\1.pdf");
        File fileTo = new File("c:\\1.pdf.gz");
        File fileUnzip = new File("c:\\2.pdf");
        
        //file->zip
        byte[] original = FileUtils.readFileToByteArray(file);
        byte[] zipped = zip(original);
        FileUtils.writeByteArrayToFile(fileTo, zipped);
        
        //zip->file
        byte[] unzipped = unzip(zipped);
        FileUtils.writeByteArrayToFile(fileUnzip, unzipped);
    }
}
