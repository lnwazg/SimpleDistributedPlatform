package com.lnwazg.kit.controllerpattern;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.lnwazg.kit.list.Lists;
import com.lnwazg.kit.log.Logs;
import com.lnwazg.kit.reflect.ClassKit;

/**
 * Controller管理器
 * @author nan.li
 * @version 2017年3月29日
 */
public abstract class AbstractControllerManager
{
    /**
     * 默认的控制器搜索包，可自定义
     */
    //    private String controllerSearchPackage = "com.lnwazg.mqctrl";
    private String controllerSearchPackage;
    
    private Map<String, Object> controllerNameObjectMap;
    
    /**
     * 构造函数 <br>
     * 初始化的时候，必须传入要扫描的包
     * @param controllerSearchPackage
     */
    public AbstractControllerManager(String controllerSearchPackage)
    {
        this.controllerSearchPackage = controllerSearchPackage;
        initControllerNameObjectMap();
    }
    
    /**
     * 初始化映射表
     * @author nan.li
     */
    private void initControllerNameObjectMap()
    {
        List<Class<?>> cList = ClassKit.getClasses(controllerSearchPackage);
        controllerNameObjectMap = new HashMap<String, Object>();
        if (Lists.isNotEmpty(cList))
        {
            try
            {
                for (Class<?> clazz : cList)
                {
                    String classShortName = clazz.getSimpleName().toLowerCase();//默认值
                    if (clazz.isAnnotationPresent(Controller.class))
                    {
                        Controller controller = clazz.getAnnotation(Controller.class);
                        String value = controller.value();//   /news
                        if (StringUtils.isNotEmpty(value))
                        {
                            classShortName = value;
                            //自动去除掉斜杠
                            if (classShortName.indexOf("/") != -1)
                            {
                                classShortName = StringUtils.replace(classShortName, "/", "");
                            }
                        }
                    }
                    controllerNameObjectMap.put(classShortName, clazz.newInstance());
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 调用指定类的指定方法，传入指定的paramMap参数
     * @author nan.li
     * @param classShortName
     * @param methodName
     * @param paramMap
     */
    public void invoke(String classShortName, String methodName, Map<String, Object> paramMap)
    {
        Object object = controllerNameObjectMap.get(classShortName);
        if (object == null)
        {
            Logs.w("无法找到对象实例:" + classShortName + ", 将退出执行该controller！");
            return;
        }
        
        //设置字段(注入paramMap参数)
        //定制的注入一些参数
        fillParamsCustom(object, paramMap);
        
        //调用方法
        ClassKit.invokeMethod(object, methodName);
    }
    
    /**
     * 定制的注入方法，可以注入需要的各种参数到Controller实例中，由具体的ControllerManager去实现
     * @author nan.li
     * @param object
     * @param paramMap
     */
    public abstract void fillParamsCustom(Object object, Map<String, Object> paramMap);
    //        ClassKit.setField(object, "request", paramMap);
    //        ClassKit.setField(object, "paramMap", paramMap);
    //        ClassKit.setField(object, "from", paramMap.get("from"));
}
