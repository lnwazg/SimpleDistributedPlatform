package com.lnwazg.kit.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.internet.MimeUtility;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * excel导出工具类<br>
 * 使用方法：<br>
 * 直接调用exportExcel2007()的几个方法即可，代码示例参考注释代码<br>
 * 
 * 该方法已经经过优化为低内存占用的API，可以放心调用并导出（注意，对象创建后仅能支持导出一次，然后自动销毁！）：<br>
 * 之前导出均用HSSFWorkbook，后来发现超出65536条后系统就报错，经过网上查询资料得出，XSSFWorkbook可以导出<br>
 * 然后程序又报OutOfMemoryError：Java heap space;内存溢出错误，又改用：<br>
 * Workbook writeWB = new SXSSFWorkbook(); <br>
 * Sheet writeSheet = writeWB.createSheet();<br>
 * 经测试导出15W、20W条数据都正常运行；<br>
 * 
 * HSSF是POI工程对Excel 97(-2007)文件操作的纯Java实现
 * XSSF是POI工程对Excel 2007 OOXML (.xlsx)文件操作的纯Java实现
 * 从POI 3.8版本开始，提供了一种基于XSSF的低内存占用的API----SXSSF 
 * 
 * SXSSF与XSSF的对比 
 * 在一个时间点上，只可以访问一定数量的数据
 * 不再支持Sheet.clone()
 * 不再支持公式的求值 
 * 
 * @author nan.li
 * @version 2016年2月19日
 */
public class ExcelUtils
{
    /**
     * Logger for this class
     */
    private static final Log logger = LogFactory.getLog(ExcelUtils.class);
    
    static int EXPORT_MAX_ROWS_LIMIT = 1000000;// excel2007 单sheet最大支持1048576行
    
    //    /**
    //     * 导出数据到excel中<br>
    //     * 
    //     * @author nan.li
    //     */
    //    public void exportDataToExcel() {
    //        try {
    //            Map<String, String> paramMap = Utils.getParamMap(this.getReq());
    //            // 根据Id作为标记，优先从缓存中查询
    //            // 提供强制刷新功能，便于实时调试
    //            // 如果非强制刷新，则优先从缓存中获取。获取不到，才再查询一次！
    //            // 强制刷新的标记：参数r非空
    //            if (StringUtils.isEmpty(paramMap.get("r"))) {
    //                // 无r标记，非强制刷新，则优先从缓存中获取
    //                String id = paramMap.get("id");
    //                if (EXPORT_DATA_CACHE.containsKey(id)) {
    //                    String dataName = EXPORT_DATA_CACHE.get(id);
    //                    if (StringUtils.isNotEmpty(dataName)) {
    //                        String filePath = StartUpServlet.PROJECT_EXCEL_EXPORT_DIR + id + "/" + dataName;
    //                        ExcelUtils.writeFileToPage(this.getRes(), filePath, dataName,
    //                                this.getReq().getHeader("user-agent"));
    //                        // SXSSFWorkbook xssfWorkbook = excelData.getWorkbook();
    //                        // ExcelUtils.exportExcel2007ByData(this.getRes(),
    //                        // xssfWorkbook, dataName,
    //                        // this.getReq().getHeader("user-agent"));
    //                        return;
    //                    }
    //                }
    //            }
    //            // 先查询出待查询的sql语句，并base64解码
    //            Map<String, Object> map = service.queryExportDataMap(paramMap);
    //            if (map != null) {
    //                String sqlBase64Value = ObjectUtils.toString(map.get("sql_content"));
    //                if (StringUtils.isNotEmpty(sqlBase64Value)) {
    //                    String sql = Base64Utils.decode(sqlBase64Value);
    //                    if (StringUtils.isNotEmpty(sql)) {
    //                        String sql_name = ObjectUtils.toString(map.get("sql_name"));
    //                        // 然后，将解码后的sql语句做出参数，查询出结果集。根据结果集中的列名称以及结果集，导出为excel，用流的形式输出到页面
    //                        Map<String, String> sqlParamMap = new HashMap<String, String>();
    //                        sqlParamMap.put("sql", sql);
    //                        List<Map<String, Object>> dataList = service.queryDataListBySql(sqlParamMap);
    //                        // 计算出缓存
    //                        SXSSFWorkbook workbook = ExcelUtils.calcTimestampCache(dataList, sql_name);
    //                        // 将缓存override到内存中
    //                        String id = paramMap.get("id");
    //                        String dataName = sql_name;
    //                        ExportDataController.EXPORT_DATA_CACHE.put(id, dataName);// 现在缓存的内容就是一个String文件名
    //                        // ExcelUtils.exportExcel2007ByData(this.getRes(),
    //                        // excelDataCache.getWorkbook(),
    //                        // excelDataCache.getFileName(),
    //                        // this.getReq().getHeader("user-agent"));
    //                        // 将文件写入到本地
    //                        String filePath = StartUpServlet.PROJECT_EXCEL_EXPORT_DIR + id + "/" + dataName;
    //                        ExcelUtils.writeDataToFile(filePath, dataName, workbook);
    //                        // 写入完毕后开始下载
    //                        ExcelUtils.writeFileToPage(this.getRes(), filePath, dataName,
    //                                this.getReq().getHeader("user-agent"));
    //                        // ExcelUtils.exportExcel2007(this.getRes(), dataList,
    //                        // sql_name,
    //                        // this.getReq().getHeader("user-agent"));
    //                        // 只有这一条路径可以成功，成功后，就直接return
    //                        return;
    //                    }
    //                }
    //            }
    //        } catch (Exception e) {
    //            logger.error("导出excel异常！", e);
    //            e.printStackTrace();
    //            return;
    //        }
    //        printExportExcelEmptyResult(this.getRes());
    //    }
    
    //    /**
    //     * 导出excel，无数据可导出的时候，输出空
    //     * 
    //     * @author nan.li
    //     * @param res
    //     */
    //    private void printExportExcelEmptyResult(HttpServletResponse res) {
    //        PrintWriter out;
    //        try {
    //            res.setCharacterEncoding("UTF-8");
    //            out = res.getWriter();
    //            out.println("<html>");// 输出的内容要放在body中
    //            out.println("<body>");
    //            out.println("<div><h3><i>查询结果集为空，无法导出excel！</i></h3></div>");
    //            out.println("</body>");
    //            out.println("</html>");
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //        }
    //    }
    
    /**
     * 导出为excel2007的格式
     * 
     * @author nan.li
     * @param res
     * @param dataList
     */
    public static void exportExcel2007(HttpServletResponse res, List<Map<String, Object>> dataList)
    {
        exportExcel2007(res, dataList, null);
    }
    
    /**
     * 导出为excel2007的格式<br>
     * 指定文件名
     * @author nan.li
     * @param res
     * @param dataList
     * @param dataName
     */
    public static void exportExcel2007(HttpServletResponse res, List<Map<String, Object>> dataList, String dataName)
    {
        exportExcel2007(res, dataList, dataName, null);
    }
    
    /**
     * 导出为excel2007的格式<br>
     * 指定文件名、指定浏览器类型，以适配不同浏览器的文件名解析规则
     * 
     * @author nan.li
     * @param res
     * @param dataList
     * @param dataName
     * @param userAgent
     */
    public static void exportExcel2007(HttpServletResponse res, List<Map<String, Object>> dataList, String dataName, String userAgent)
    {
        if (dataList == null || dataList.size() == 0)
        {
            logger.warn("dataList is empty! Quit export excel!");
            PrintWriter out;
            try
            {
                res.setCharacterEncoding("UTF-8");
                out = res.getWriter();
                out.println("<html>");// 输出的内容要放在body中
                out.println("<body>");
                out.println("<div><h3><i>查询结果集为空，无法导出excel！</i></h3></div>");
                out.println("</body>");
                out.println("</html>");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return;
        }
        ServletOutputStream os = null;
        try
        {
            // 输出流
            os = res.getOutputStream();
            res.setCharacterEncoding("UTF-8");
            setContentDisposition(res, dataName, userAgent);
            // 工作区
            SXSSFWorkbook wb = new SXSSFWorkbook();// keep 100 rows in
                                                   // memory, exceeding
                                                   // rows will be flushed
                                                   // to disk
                                                   // 创建第一个sheet
            SXSSFSheet sheet = wb.createSheet(StringUtils.isEmpty(dataName) ? "导出结果Sheet" : dataName);
            // 生成第一行
            String[] titles = getTitles(dataList);
            // 生成标题行
            generateTitleRow(sheet, titles, getCellStyle("标题样式", wb));
            // 生成内容行
            generateContentRows(sheet, titles, dataList);
            // 写文件
            wb.write(os);
            wb.close();
            // 关闭输出流
            os.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            IOUtils.closeQuietly(os);
        }
    }
    
    /**
     * 根据已经存好的数据，导出excel流
     * 
     * @author nan.li
     * @param res
     * @param sxssfWorkbook
     * @param dataName
     * @param header
     */
    public static void exportExcel2007ByData(HttpServletResponse res, SXSSFWorkbook sxssfWorkbook, String dataName, String userAgent)
    {
        if (sxssfWorkbook == null)
        {
            logger.warn("sxssfWorkbook is empty! Quit export excel!");
            PrintWriter out;
            try
            {
                res.setCharacterEncoding("UTF-8");
                out = res.getWriter();
                out.println("<html>");// 输出的内容要放在body中
                out.println("<body>");
                out.println("<div><h3><i>查询结果集为空，无法导出excel！</i></h3></div>");
                out.println("</body>");
                out.println("</html>");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return;
        }
        ServletOutputStream os = null;
        try
        {
            // 输出流
            os = res.getOutputStream();
            res.setCharacterEncoding("UTF-8");
            setContentDisposition(res, dataName, userAgent);
            // 写文件
            sxssfWorkbook.write(os);
            // xssfWorkbook.close();//此处不可以关闭！因为同一份对象可能要作为缓存输出多次！
            // 关闭输出流
            os.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            IOUtils.closeQuietly(os);
        }
    }
    
    /**
     * 将workbook的对象写入到文件中
     * 
     * @author nan.li
     * @param filePath
     * @param dataName
     * @param workbook
     * @param header
     */
    public static void writeDataToFile(String filePath, String dataName, SXSSFWorkbook workbook)
    {
        File targetFile = new File(filePath);
        if (StringUtils.isEmpty(filePath))
        {
            logger.warn("filePath is empty! Quit writeDataToFile!");
            return;
        }
        FileOutputStream fo = null;
        try
        {
            targetFile.getParentFile().mkdirs();
            fo = new FileOutputStream(targetFile);
            workbook.write(fo);
            IOUtils.closeQuietly(fo);
            workbook.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
        }
    }
    
    /**
     * 将文件数据输出到页面
     * 
     * @author nan.li
     * @param res
     * @param filePath
     * @param dataName
     * @param header
     */
    public static void writeFileToPage(HttpServletResponse res, String filePath, String dataName, String userAgent)
    {
        File srcFile = new File(filePath);
        if (StringUtils.isEmpty(filePath) || !srcFile.exists())
        {
            logger.warn("filePath is empty OR file is not exists! Quit export excel!");
            PrintWriter out;
            try
            {
                res.setCharacterEncoding("UTF-8");
                out = res.getWriter();
                out.println("<html>");// 输出的内容要放在body中
                out.println("<body>");
                out.println("<div><h3><i>查询结果集为空，无法导出excel！</i></h3></div>");
                out.println("</body>");
                out.println("</html>");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return;
        }
        ServletOutputStream os = null;
        FileInputStream fi = null;
        try
        {
            // 输出流
            os = res.getOutputStream();
            res.setCharacterEncoding("UTF-8");
            setContentDisposition(res, dataName, userAgent);
            // 写文件
            fi = new FileInputStream(srcFile);
            IOUtils.copy(fi, os);
            os.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(fi);
        }
    }
    
    /**
     * 计算出一个缓存的快照
     * 
     * @author nan.li
     * @param dataList
     * @param sql_name
     */
    public static SXSSFWorkbook calcTimestampCache(List<Map<String, Object>> dataList, String dataName)
    {
        SXSSFWorkbook wb = null;
        if (dataList == null || dataList.size() == 0)
        {
            logger.warn("dataList is empty! Quit calcTimestampCache!");
            return wb;
        }
        // 工作区
        wb = new SXSSFWorkbook();
        // 创建第一个sheet
        SXSSFSheet sheet = wb.createSheet(StringUtils.isEmpty(dataName) ? "导出结果Sheet" : dataName);
        // 生成第一行
        String[] titles = getTitles(dataList);
        // 生成标题行
        generateTitleRow(sheet, titles, getCellStyle("标题样式", wb));
        // 生成内容行
        generateContentRows(sheet, titles, dataList);
        return wb;
    }
    
    /**
     * 适配不同浏览器的下载文件头
     * 
     * @author nan.li
     * @param res
     * @param fileName
     * @param userAgent
     */
    private static void setContentDisposition(HttpServletResponse res, String fileName, String userAgent)
    {
        try
        {
            if (StringUtils.isEmpty(fileName))
            {
                fileName = "exportExcel.xlsx";
            }
            String newFileName = URLEncoder.encode(fileName, "UTF8");
            // 如果没有UA，则默认使用IE的方式进行编码，因为毕竟IE还是占多数的
            String rtn = "filename=\"" + newFileName + "\"";
            if (StringUtils.isNotEmpty(userAgent))
            {
                userAgent = userAgent.toLowerCase();
                // IE浏览器，只能采用URLEncoder编码
                if (userAgent.indexOf("msie") != -1)
                {
                    rtn = "filename=\"" + newFileName + "\"";
                }
                // Opera浏览器只能采用filename*
                else if (userAgent.indexOf("opera") != -1)
                {
                    rtn = "filename*=UTF-8''" + newFileName;
                }
                // Safari浏览器，只能采用ISO编码的中文输出
                else if (userAgent.indexOf("safari") != -1)
                {
                    rtn = "filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO8859-1") + "\"";
                }
                // Chrome浏览器，只能采用MimeUtility编码或ISO编码的中文输出
                else if (userAgent.indexOf("applewebkit") != -1)
                {
                    newFileName = MimeUtility.encodeText(fileName, "UTF8", "B");
                    rtn = "filename=\"" + newFileName + "\"";
                }
                // FireFox浏览器，可以使用MimeUtility或filename*或ISO编码的中文输出
                else if (userAgent.indexOf("mozilla") != -1)
                {
                    rtn = "filename*=UTF-8''" + newFileName;
                }
            }
            res.setHeader("content-disposition", String.format("attachment;%s", rtn));
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            res.setHeader("content-disposition", "attachment;filename=exportExcel.xlsx");
        }
    }
    
    /**
     * 根据名称去获得cell的样式
     * 
     * @author nan.li
     * @param name
     * @param sxssfWorkbook
     * @return
     */
    private static CellStyle getCellStyle(String name, SXSSFWorkbook sxssfWorkbook)
    {
        if ("标题样式".equals(name))
        {
            CellStyle btStyle = sxssfWorkbook.createCellStyle();// 新建样式对象
            Font btFont = sxssfWorkbook.createFont();// 创建字体对象
            btFont.setFontName("宋体"); // 字体
            btFont.setBold(true);// 是否加粗
            // btFont.setFontHeightInPoints((short) 15);// 字体大小
            btStyle.setFont(btFont);
            // btStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);// 水平居中
            // btStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);//
            // 垂直居中
            return btStyle;
        }
        else if ("卷次样式".equals(name))
        {
            CellStyle juanciStyle = sxssfWorkbook.createCellStyle();// 新建样式对象
            Font juanciFont = sxssfWorkbook.createFont();// 创建字体对象
            juanciFont.setFontName("宋体"); // 字体
            juanciFont.setBold(true);// 是否加粗
            juanciFont.setFontHeightInPoints((short)13);// 字体大小
            // juanciFont.setColor(new
            // XSSFColor(ColorUtils.hexStr2Color("#7030A0")));// 字体颜色
            juanciStyle.setFont(juanciFont);
            return juanciStyle;
        }
        else if ("卷样式".equals(name))
        {
            CellStyle juanStyle = sxssfWorkbook.createCellStyle();// 新建样式对象
            Font juanFont = sxssfWorkbook.createFont();// 创建字体对象
            juanFont.setFontName("宋体"); // 字体
            juanFont.setBold(true);// 是否加粗
            juanFont.setFontHeightInPoints((short)12);// 字体大小
            // juanFont.setColor(new
            // XSSFColor(ColorUtils.hexStr2Color("#008000")));// 字体颜色
            juanStyle.setFont(juanFont);
            return juanStyle;
        }
        else if ("篇样式".equals(name))
        {
            CellStyle pianStyle = sxssfWorkbook.createCellStyle();// 新建样式对象
            Font pianFont = sxssfWorkbook.createFont();// 创建字体对象
            pianFont.setFontName("宋体"); // 字体
            pianFont.setBold(true);// 是否加粗
            pianFont.setFontHeightInPoints((short)11);// 字体大小
            // pianFont.setColor(new
            // XSSFColor(ColorUtils.hexStr2Color("#92D050")));// 字体颜色
            pianStyle.setFont(pianFont);
            return pianStyle;
        }
        else if ("章样式".equals(name))
        {
            CellStyle zhangStyle = sxssfWorkbook.createCellStyle();// 新建样式对象
            Font zhangFont = sxssfWorkbook.createFont();// 创建字体对象
            zhangFont.setFontName("宋体"); // 字体
            zhangFont.setBold(true);// 是否加粗
            zhangFont.setFontHeightInPoints((short)10);// 字体大小
            // zhangFont.setColor(new
            // XSSFColor(ColorUtils.hexStr2Color("#0000FF")));// 字体颜色
            zhangStyle.setFont(zhangFont);
            return zhangStyle;
        }
        else if ("节样式".equals(name))
        {
            CellStyle jieStyle = sxssfWorkbook.createCellStyle();// 新建样式对象
            Font jieFont = sxssfWorkbook.createFont();// 创建字体对象
            jieFont.setFontName("宋体"); // 字体
            jieFont.setBold(true);// 是否加粗
            jieFont.setFontHeightInPoints((short)9);// 字体大小
            // jieFont.setColor(new
            // XSSFColor(ColorUtils.hexStr2Color("#66FFFF")));// 字体颜色
            jieStyle.setFont(jieFont);
            return jieStyle;
        }
        else if ("小节样式".equals(name))
        {
            CellStyle xiaojieStyle = sxssfWorkbook.createCellStyle();// 新建样式对象
            Font xiaojieFont = sxssfWorkbook.createFont();// 创建字体对象
            xiaojieFont.setFontName("宋体"); // 字体
            xiaojieFont.setBold(true);// 是否加粗
            xiaojieFont.setFontHeightInPoints((short)8);// 字体大小
            // xiaojieFont.setColor(new
            // XSSFColor(ColorUtils.hexStr2Color("#C2AFEB")));// 字体颜色
            xiaojieStyle.setFont(xiaojieFont);
            return xiaojieStyle;
        }
        else if ("校注样式".equals(name))
        {
            CellStyle jiaoazhuStyle = sxssfWorkbook.createCellStyle();// 新建样式对象
            Font jiaozhuFont = sxssfWorkbook.createFont();// 创建字体对象
            jiaozhuFont.setFontName("宋体"); // 字体
            jiaozhuFont.setBold(true);// 是否加粗
            jiaozhuFont.setFontHeightInPoints((short)8);// 字体大小
            // jiaozhuFont.setColor(new
            // XSSFColor(ColorUtils.hexStr2Color("#0000FF")));// 字体颜色
            jiaoazhuStyle.setFont(jiaozhuFont);
            return jiaoazhuStyle;
        }
        return null;
    }
    
    /**
     * 生成标题行
     * 
     * @author nan.li
     * @param sheet
     * @param titles
     * @param object
     */
    private static void generateTitleRow(SXSSFSheet sheet, String[] titles, CellStyle cellStyle)
    {
        SXSSFRow row = sheet.createRow(0);
        for (int i = 0; i < titles.length; i++)
        {
            SXSSFCell cell = row.createCell(i);
            cell.setCellStyle(cellStyle);// 设置元素的风格
            cell.setCellValue(titles[i]);
        }
    }
    
    /**
     * 生成内容行
     * 
     * @author nan.li
     * @param sheet
     * @param titles
     * @param dataList
     */
    private static void generateContentRows(SXSSFSheet sheet, String[] titles, List<Map<String, Object>> dataList)
    {
        if (dataList.size() > EXPORT_MAX_ROWS_LIMIT)
        {
            dataList = dataList.subList(0, EXPORT_MAX_ROWS_LIMIT);
        }
        int size = dataList.size();
        for (int i = 0; i < size; i++)
        {
            Map<String, Object> rowDataMap = dataList.get(i);
            SXSSFRow row = sheet.createRow(i + 1);// 跳过第一行
            for (int j = 0; j < titles.length; j++)
            {
                row.createCell(j).setCellValue(ObjectUtils.toString(rowDataMap.get(titles[j])));
            }
        }
    }
    
    /**
     * 获取数据表的标题栏名称列表，该列表是有顺序的
     * 
     * @author nan.li
     * @param dataList
     * @return
     */
    private static String[] getTitles(List<Map<String, Object>> dataList)
    {
        // 原来的逻辑是直接取第一条，但是这样很可能是不完整的！
        // mybatis丢失列的问题解决空值映射问题解决(可能要hack mybatis的启动配置才行了！) null转换成“”问题解决
        // 但是配置文件没办法直接修改，因此只好采用dirty fix：取出前100条（不够则取全部）作为参考样本！那么也能解决问题！！！
        Map<String, Object> map = dataList.get(0);
        Set<String> set = map.keySet();
        // 现在采用全新的fix方法：mybatis的类patch。即，利用java虚拟机类加载机制，优先加载用户的类，即可达到patch的效果！
        // 那么，现在的set就是完整的数据集了！（包含了null值的数据头）
        
        // 最终用于确定列名称列表的set
        // Set<String> set = new HashSet<String>();// 初始化为空
        // List<Map<String, Object>> testList = null;// 样本列表（样本数量取100个，基本可以满足要求）
        // if (dataList.size() > 100) {
        // testList = dataList.subList(0, 100);
        // } else {
        // testList = dataList;
        // }
        // List<Map<String, Object>> testList = dataList;//
        // 为了保证数据的全面性，现在不考虑性能了，而是要考虑到所有的情况
        // 取出样本中最全的那个set
        // for (Map<String, Object> map : testList) {
        // // 如果发现某一条更全，就将该更全的override原有的
        // if (map.keySet().size() > set.size()) {
        // set = map.keySet();
        // System.out.println(set.size());
        // }
        // }
        // List<String> titleList = new ArrayList<>();
        // for (Map<String, Object> map : testList) {
        // for(String key : map.keySet()) {
        // if(!titleList.contains(key)) {
        // titleList.add(key);
        // }
        // }
        // }
        String[] ret = new String[set.size()];
        int i = 0;
        for (String str : set)
        {
            ret[i++] = str;
        }
        return ret;
    }
}
