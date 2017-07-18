package test.lnwazg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.lnwazg.kit.testframework.TestFramework;
import com.lnwazg.kit.testframework.anno.TestCase;

/**
 * 单词
 * @author nan.li
 * @version 2016年4月14日
 */
public class WordTest
{
    //    @TestIt
    void loadWords()
    {
        try
        {
            List<String> trueLines = new ArrayList<>();
            List<String> lines = FileUtils.readLines(new File("c:\\1.txt"), "utf-8");
            for (String line : lines)
            {
                if (StringUtils.isNotEmpty(line))
                {
                    if (line.startsWith("n.") || line.startsWith("adv.") || line.startsWith("adj.") || line.startsWith("v."))
                    {
                        continue;
                    }
                    line = line.trim();
                    line = line.substring(0, line.indexOf(" "));
                    trueLines.add(line);
                    System.out.println(line);
                }
            }
            StringBuilder sb = new StringBuilder();
            for (String s : trueLines)
            {
                sb.append(s).append("\n");
            }
            FileUtils.writeStringToFile(new File("c:\\2.txt"), sb.toString());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    @TestCase
    void changeFileName()
        throws Exception
    {
        String dir = "C:\\Windows\\GoogleTranslate\\image";
        File dirFile = new File(dir);
        File[] files = dirFile.listFiles();
        for (File f : files)
        {
            String oldName = f.getName();
            String newName = oldName.toLowerCase();
            if (!oldName.equals(newName))
            {
                System.out.println("change " + f.getName());
                //                FileUtils.moveFile(f, new File(f.getParentFile().getAbsolutePath(), newName));
            }
        }
        System.out.println("OK!");
    }
    
    public static void main(String[] args)
    {
        TestFramework.loadTest(WordTest.class);
    }
}
