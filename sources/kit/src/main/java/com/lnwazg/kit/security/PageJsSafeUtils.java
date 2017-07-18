package com.lnwazg.kit.security;

import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lnwazg.kit.servlet.ReqUtils;

/**
 * 页面的JS的安全性检测工具类
 * @author nan.li
 * @version 2016年7月5日
 */
public class PageJsSafeUtils
{
    /**
     * Logger for this class
     */
    private static final Log logger = LogFactory.getLog(PageJsSafeUtils.class);
    
    /**
     * 检查当前页面请求的安全性，安全则返回true，否则返回false<br>
     * 所谓的是否安全，是指页面参数中是否包含一些非法的js可执行脚本<br>
     * filter the invalid params! if valid, return true else return false!<br>
     * 使用方法：
     * {@code
     *      在jsp页面doctype.jsp顶部加入以下代码（如果不安全则自动转到首页）：
            <%
                response.setHeader("Cache-Control","no-cache"); 
                response.setHeader("Pragma","no-cache"); 
                response.setDateHeader("Expires",0); 
                String contextPath = request.getContextPath();
                String port = request.getServerPort()==80 ? "":(":" + request.getServerPort()) ;//如果是80端口，则默认省略掉
                String basePath = request.getScheme() + "://" + request.getServerName() + port + contextPath + "/";
                //检测参数的安全性
                boolean secure = Utils.checkSecurity(request);
                if(!secure){
                    response.sendRedirect(basePath);
                }
            %>
     * }
     * @author nan.li
     * @return
     */
    public static boolean checkSecurity(HttpServletRequest request)
    {
        Map<String, String> paramMap = ReqUtils.getParamMap(request);
        for (Map.Entry<String, String> entry : paramMap.entrySet())
        {
            String original = entry.getValue();
            String value = entry.getValue();// 获取参数的值
            if (value != null)
            {
                // String
                // NOTE: It's highly recommended to use the ESAPI library and
                // uncomment the following line to
                // avoid encoded attacks.
                // value = ESAPI.encoder().canonicalize(value);
                // Avoid null characters
                // value = value.replaceAll(" ", ""); //add by linan 2015-12-11
                // 放开了这个过于严厉的参数限制
                
                // Avoid anything between script tags
                Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
                value = scriptPattern.matcher(value).replaceAll("");
                // Avoid anything in a
                // src="http://www.yihaomen.com/article/java/..." type of
                // e­xpression
                scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
                value = scriptPattern.matcher(value).replaceAll("");
                scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
                value = scriptPattern.matcher(value).replaceAll("");
                // Remove any lonesome </script> tag
                scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
                value = scriptPattern.matcher(value).replaceAll("");
                // Remove any lonesome <script ...> tag
                scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
                value = scriptPattern.matcher(value).replaceAll("");
                // Avoid eval(...) e­xpressions
                scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
                value = scriptPattern.matcher(value).replaceAll("");
                // Avoid e­xpression(...) e­xpressions
                scriptPattern = Pattern.compile("e­xpression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
                value = scriptPattern.matcher(value).replaceAll("");
                // Avoid javascript:... e­xpressions
                scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
                value = scriptPattern.matcher(value).replaceAll("");
                // Avoid vbscript:... e­xpressions
                scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
                value = scriptPattern.matcher(value).replaceAll("");
                // Avoid onload= e­xpressions
                scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
                value = scriptPattern.matcher(value).replaceAll("");
                
                scriptPattern = Pattern.compile("';.*?(.*?)//", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);// 对诸如
                                                                                                                               // ';document.write(11111111111)//
                                                                                                                               // 之类的代码植入进行强过滤！
                value = scriptPattern.matcher(value).replaceAll("");
                
                scriptPattern = Pattern.compile("'.*?;.*?(.*?);.*?'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);// 对诸如
                                                                                                                                     // ';;;;;;;;;;document.write(11111111111);;;;;;;;'
                                                                                                                                     // 之类的代码植入进行强过滤！
                value = scriptPattern.matcher(value).replaceAll("");
                
                // finally, we will check that !
                value = StringUtils.replace(value, "alert(", "");// 对非法的alert调用企图进行强过滤
                                                                 // 2015-12-11
                                                                 // by linan
                value = StringUtils.replace(value, "document.cookie", "");// 对cookie的非法企图进行强过滤
                                                                          // 2015-12-11
                                                                          // by
                                                                          // linan
                value = StringUtils.replace(value, "'", "");// 对非法字符进行强过滤
                                                            // 2015-12-11 by
                                                            // linan
                value = StringUtils.replace(value, "\"", "");// 对非法字符进行强过滤
                                                             // 2015-12-11 by
                                                             // linan
                                                             // value = StringUtils.replace(value, "\\", "");//对非法字符进行强过滤
                                                             // 2015-12-11 by linan
                                                             // value = StringUtils.replace(value, "&", "");//对非法字符进行强过滤
                                                             // 2015-12-11 by linan
                value = StringUtils.replace(value, "<", "");// 对非法字符进行强过滤
                                                            // 2015-12-11 by
                                                            // linan
                value = StringUtils.replace(value, ">", "");// 对非法字符进行强过滤
                                                            // 2015-12-11 by
                                                            // linan
                                                            
                // System.out.println(String.format("开始检测页面非法参数:\n【Original：】%s\n【filtered：】%s\n\n",
                // original, value));
                // decodeURIComponent('http://my.haiziwang.com:8080/front_insurance/jsp/bxtkgz.jsp?item_id=38%27%3balert(1)%2f%2f')
                // http://my.haiziwang.com:8080/front_insurance/jsp/bxtkgz.jsp?item_id=38';alert(1)//
                // 开始检测页面非法参数:
                // 【Original：】 38';alert(1)//
                // 【filtered：】 38';alert(1)//
                // "http://test.baoxian.haiziwang.com:8080/front_insurance/jsp/bxtkgz.jsp?item_id=38%27;;;;;document.write(11111111111)//"
                // "http://test.baoxian.haiziwang.com:8080/front_insurance/jsp/bxtkgz.jsp?item_id=38';;;;;document.write(11111111111)//"
                // http://test.baoxian.haiziwang.com:8080/front_insurance/jsp/bxtkgz.jsp?item_id=38%27;;;;;document.write(11111111111)//
                // http://my.haiziwang.com:8080/front_insurance/jsp/bxtkgz.jsp?item_id=38%27;;;;;document.write(11111111111)//
                // http://my.haiziwang.com:8080/front_insurance/jsp/bxtkgz.jsp?item_id=38%27;prompt(11111111111)//
                // http://my.haiziwang.com:8080/front_insurance/jsp/bxtkgz.jsp?item_id=38';alert(1);'
                // http://my.haiziwang.com:8080/front_insurance/jsp/bxtkgz.jsp?item_id=38';confirm(1);'
                // http://my.haiziwang.com:8080/front_insurance/jsp/bxtkgz.jsp?item_id=38';;;;;;;;;;;;;confirm(1);;;;;;;;;;;;;;;'
                // http://my.haiziwang.com:8080/front_insurance/jsp/bxtkgz.jsp?item_id=38'
                // ;confirm(1); '
                
                if (!original.equals(value))
                {
                    System.out.println(String.format("LiNan's Security Util 在【 %s】检测到页面请求参数含有非法注入脚本，并已将其自动杀死！！！  \n【原有的参数】  %s\n【过滤后参数】  %s\n\n", new Date().toLocaleString(), original, value));
                    return false;
                }
            }
        }
        return true;
    }
    
    //    /**
    //     * 从session中获取用户信息
    //     *
    //     * @param session
    //     * @return
    //     */
    //    public static UserVO getSessionUser(HttpSession session)
    //    {
    //        return (UserVO)session.getAttribute(Constant.SYS_LOGIN_INFO);
    //    }
    //    
    //    /**
    //     * 从request中获取用户信息
    //     *
    //     * @param request
    //     * @return
    //     */
    //    public static UserVO getSessionUser(HttpServletRequest request)
    //    {
    //        return getSessionUser(request.getSession());
    //    }
}
