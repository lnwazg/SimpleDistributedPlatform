package com.lnwazg.kit.document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfUtils
{
    
    public static void writePdfSimple(String filePath, String title, String... content)
    {
        writePdf(filePath, null, title, content);
    }
    
    public static void writePdf(String filePath, String author, String title, String... content)
    {
        try
        {
            File file = new File(filePath);
            if (!file.exists())
            {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            // step 1
            Document document = new Document();
            document.addLanguage("zh-cn");
            // step 2
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            // step 3
            document.open();
            // step 4
            if (StringUtils.isNotEmpty(author))
            {
                document.addAuthor(author);
            }
            document.addTitle(title);
            for (String con : content)
            {
                document.add(new Paragraph(con));
            }
            // step 5
            document.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (DocumentException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
}
