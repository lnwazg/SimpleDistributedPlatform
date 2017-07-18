package com.lnwazg.kit.reflect;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import com.lnwazg.kit.list.Lists;
import com.lnwazg.kit.log.Logs;

/**
 * 类的工具包
 * @author nan.li
 * @version 2016年5月13日
 */
public class ClassKit
{
    /** 
     * 取得某个接口下所有实现这个接口的类 
     * 
     */
    public static List<Class<?>> getAllClassByInterface(Class<?> c)
    {
        List<Class<?>> returnClassList = null;
        if (c.isInterface())
        {
            // 获取当前的包名  
            String packageName = c.getPackage().getName();
            // 获取当前包下以及子包下所以的类  
            List<Class<?>> allClass = getClasses(packageName);
            if (allClass != null)
            {
                returnClassList = new ArrayList<Class<?>>();
                for (Class<?> clazz : allClass)
                {
                    // 判断是否是同一个接口  
                    if (c.isAssignableFrom(clazz))
                    {
                        // 本身不加入进去  
                        if (!c.equals(clazz))
                        {
                            returnClassList.add(clazz);
                        }
                    }
                }
            }
        }
        return returnClassList;
    }
    
    /**
     * 取得某一类所在包的所有类名 不含迭代 
     * @author Administrator
     * @param classLocation
     * @param packageName
     * @return
     */
    public static String[] getPackageAllClassName(String classLocation, String packageName)
    {
        //将packageName分解  
        String[] packagePathSplit = packageName.split("[.]");
        String realClassLocation = classLocation;
        int packageLength = packagePathSplit.length;
        for (int i = 0; i < packageLength; i++)
        {
            realClassLocation = realClassLocation + File.separator + packagePathSplit[i];
        }
        File packeageDir = new File(realClassLocation);
        if (packeageDir.isDirectory())
        {
            String[] allClassName = packeageDir.list();
            return allClassName;
        }
        return null;
    }
    
    /** 
     * 从包package中获取所有的Class 
     * @param pack 
     * @return 
     */
    public static List<Class<?>> getClasses(String packageName)
    {
        //第一个class类的集合  
        List<Class<?>> classes = new ArrayList<Class<?>>();
        //是否循环迭代  
        boolean recursive = true;
        //获取包的名字 并进行替换  
        String packageDirName = packageName.replace('.', '/');
        //定义一个枚举的集合 并进行循环来处理这个目录下的things  
        Enumeration<URL> dirs;
        try
        {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            //循环迭代下去  
            while (dirs.hasMoreElements())
            {
                //获取下一个元素  
                URL url = dirs.nextElement();
                //得到协议的名称  
                String protocol = url.getProtocol();
                //如果是以文件的形式保存在服务器上  
                if ("file".equals(protocol))
                {
                    //获取包的物理路径  
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    //以文件的方式扫描整个包下的文件 并添加到集合中  
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                }
                else if ("jar".equals(protocol))
                {
                    //如果是jar包文件   
                    //定义一个JarFile  
                    JarFile jar;
                    try
                    {
                        //获取jar  
                        jar = ((JarURLConnection)url.openConnection()).getJarFile();
                        //从此jar包 得到一个枚举类  
                        Enumeration<JarEntry> entries = jar.entries();
                        //同样的进行循环迭代  
                        while (entries.hasMoreElements())
                        {
                            //获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件  
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            //如果是以/开头的  
                            if (name.charAt(0) == '/')
                            {
                                //获取后面的字符串  
                                name = name.substring(1);
                            }
                            //如果前半部分和定义的包名相同  
                            if (name.startsWith(packageDirName))
                            {
                                int idx = name.lastIndexOf('/');
                                //如果以"/"结尾 是一个包  
                                if (idx != -1)
                                {
                                    //获取包名 把"/"替换成"."  
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                //如果可以迭代下去 并且是一个包  
                                if ((idx != -1) || recursive)
                                {
                                    //如果是一个.class文件 而且不是目录  
                                    if (name.endsWith(".class") && !entry.isDirectory())
                                    {
                                        //去掉后面的".class" 获取真正的类名  
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try
                                        {
                                            //添加到classes  
                                            classes.add(Class.forName(packageName + '.' + className));
                                        }
                                        catch (ClassNotFoundException e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return classes;
    }
    
    /** 
     * 以文件的形式来获取包下的所有Class 
     * @param packageName 
     * @param packagePath 
     * @param recursive 
     * @param classes 
     */
    public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, List<Class<?>> classes)
    {
        //获取此包的目录 建立一个File  
        File dir = new File(packagePath);
        //如果不存在或者 也不是目录就直接返回  
        if (!dir.exists() || !dir.isDirectory())
        {
            return;
        }
        //如果存在 就获取包下的所有文件 包括目录  
        File[] dirfiles = dir.listFiles(new FileFilter()
        {
            //自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)  
            public boolean accept(File file)
            {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        //循环所有文件  
        for (File file : dirfiles)
        {
            //如果是目录 则继续扫描  
            if (file.isDirectory())
            {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
            }
            else
            {
                //如果是java类文件 去掉后面的.class 只留下类名  
                String className = file.getName().substring(0, file.getName().length() - 6);
                try
                {
                    //添加到集合中去  
                    classes.add(Class.forName(packageName + '.' + className));
                }
                catch (ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * 检查某个字符串是否是一个合法的java类的全路径
     * @author nan.li
     * @param jobName
     * @return
     */
    public static boolean isValidClass(String paramClazz)
    {
        try
        {
            Class.forName(paramClazz);
            return true;
        }
        catch (ClassNotFoundException e)
        {
            return false;
        }
    }
    
    /**
     * 获取字段名称列表
     * @author nan.li
     * @param entity
     * @return
     */
    public static Collection<String> getFields(Object entity)
    {
        return getFields(entity.getClass());
    }
    
    public static Collection<String> getFields(Class<?> entity)
    {
        Collection<String> ret = new ArrayList<>();
        Field[] fields = entity.getDeclaredFields();
        for (Field field : fields)
        {
            ret.add(field.getName());
        }
        return ret;
    }
    
    /**
     * 获取批量插入的参数
     * @author nan.li
     * @param cols
     * @param entities 
     * @return
     */
    public static Collection<Collection<?>> getBatchArgs(Collection<String> cols, List<?> entities)
    {
        Collection<Collection<?>> args = new ArrayList<>();
        for (Object entity : entities)
        {
            Class<?> entityClass = entity.getClass();
            //实体类的值列表
            Collection<Object> values = new ArrayList<>();
            for (String fieldName : cols)
            {
                try
                {
                    Field field = entityClass.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    values.add(field.get(entity));
                }
                catch (NoSuchFieldException e)
                {
                    e.printStackTrace();
                }
                catch (SecurityException e)
                {
                    e.printStackTrace();
                }
                catch (IllegalArgumentException e)
                {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }
            args.add(values);
        }
        return args;
    }
    
    /**
     * 字段的缓存表
     */
    static Map<Class<?>, Map<String, Field>> fieldMap = new HashMap<>();
    
    /**
     * 上次访问的字段的高速缓存
     */
    static Triple<Class<?>, String, Field> lastFieldTriple;
    
    /**
     * 缓存是否需要刷新
     */
    static boolean lastFieldTripleNeedOverride;
    
    /**
     * 方法的缓存表
     */
    static Map<Class<?>, Map<String, Method>> methodMap = new HashMap<>();
    
    /**
     * 上次访问的方法的高速缓存
     */
    static Triple<Class<?>, String, Method> lastMethodTriple;
    
    /**
     * 缓存是否需要刷新
     */
    static boolean lastMethodTripleNeedOverride;
    
    /**
     * 从缓存中获取对应的field对象
     * @author nan.li
     * @param object
     * @param fieldName
     * @return
     */
    private static Field getFieldFromCache(Object object, String fieldName)
    {
        lastFieldTripleNeedOverride = true;
        if (object == null)
        {
            return null;
        }
        if (lastFieldTriple != null && object.getClass() == lastFieldTriple.getLeft() && fieldName.equals(lastFieldTriple.getMiddle()))
        {
            if (lastFieldTriple.getRight() != null)
            {
                //既然和上次的相同，那么就没必要刷新了
                lastFieldTripleNeedOverride = false;
                return lastFieldTriple.getRight();
            }
        }
        Map<String, Field> nameFieldMap = fieldMap.get(object.getClass());
        if (nameFieldMap != null)
        {
            return nameFieldMap.get(fieldName);
        }
        return null;
    }
    
    /**
     * 将Field对象设置到缓存中
     * @author nan.li
     * @param object
     * @param fieldName
     * @param foundField
     */
    private static void setFieldToCache(Object object, String fieldName, Field foundField)
    {
        if (object == null)
        {
            return;
        }
        Map<String, Field> nameFieldMap = fieldMap.get(object.getClass());
        if (nameFieldMap == null)
        {
            nameFieldMap = new HashMap<>();
        }
        nameFieldMap.put(fieldName, foundField);
        fieldMap.put(object.getClass(), nameFieldMap);
    }
    
    /**
     * 从缓存中取出方法对象 
     * @author nan.li
     * @param object
     * @param fieldName
     * @return
     */
    private static Method getMethodFromCache(Object object, String methodName)
    {
        lastMethodTripleNeedOverride = true;
        if (object == null)
        {
            return null;
        }
        if (lastMethodTriple != null && object.getClass() == lastMethodTriple.getLeft() && methodName.equals(lastMethodTriple.getMiddle()))
        {
            if (lastMethodTriple.getRight() != null)
            {
                //既然和上次的相同，那么就没必要刷新了
                lastMethodTripleNeedOverride = false;
                return lastMethodTriple.getRight();
            }
        }
        Map<String, Method> nameMethodMap = methodMap.get(object.getClass());
        if (nameMethodMap != null)
        {
            return nameMethodMap.get(methodName);
        }
        return null;
    }
    
    /**
     * 将方法对象设置到缓存中
     * @author nan.li
     * @param object
     * @param methodName
     * @param foundMethod
     */
    private static void setMethodToCache(Object object, String methodName, Method foundMethod)
    {
        if (object == null)
        {
            return;
        }
        Map<String, Method> nameMethodMap = methodMap.get(object.getClass());
        if (nameMethodMap == null)
        {
            nameMethodMap = new HashMap<>();
        }
        nameMethodMap.put(methodName, foundMethod);
        methodMap.put(object.getClass(), nameMethodMap);
    }
    
    /**
     * 获取任意对象的任意字段值<br>
     * 可以是私有字段<br>
     * 如果当前对象中没有找到，则会一直向上查找直到Object<br>
     * 优化后的方法速度不逊色于ReflectASM，得益于高速缓存的应用。最重要的是，这个方法可以自由访问到私有成员
     * @author nan.li
     * @param object
     * @param fieldName
     * @param paramClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getField(Object object, String fieldName, Class<T> paramClass)
    {
        Field foundField = getFieldFromCache(object, fieldName);
        //跑100w次
        //使用缓存前耗时：147  150  153
        //使用缓存后耗时：35   36   37  
        //内存缓存对性能的提升巨大！使用时间缩减到原有的20%，提升了5倍的性能！
        if (foundField == null)
        {
            Class<?> clazz = object.getClass();
            for (; clazz != Object.class; clazz = clazz.getSuperclass())
            {
                try
                {
                    foundField = clazz.getDeclaredField(fieldName);
                    //如果不报错，那么肯定就是已经获取到了，那么接下来直接break即可。
                    //否则，报错了，肯定就是获取不到了，那么循环需要继续执行
                    //所以，只要能执行到此处，就说明肯定已经获取成功了
                    foundField.setAccessible(true);//让该字段可访问
                    setFieldToCache(object, fieldName, foundField);
                    break;
                }
                catch (Exception e)
                {
                }
            }
        }
        if (foundField != null)
        {
            if (lastFieldTripleNeedOverride)
            {
                lastFieldTriple = new ImmutableTriple<Class<?>, String, Field>(object.getClass(), fieldName, foundField);
            }
            try
            {
                return (T)foundField.get(object);
            }
            catch (IllegalArgumentException e)
            {
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Logs.w("无法找到字段：" + fieldName);
        }
        return null;
    }
    
    /**
     * 设置任意对象的字段值
     * @author Administrator
     * @param object
     * @param fieldName
     * @param fieldValue
     */
    public static void setField(Object object, String fieldName, Object fieldValue)
    {
        Field foundField = getFieldFromCache(object, fieldName);
        //跑100w次
        //使用缓存前耗时：147  150  153
        //使用缓存后耗时：35   36   37  
        //内存缓存对性能的提升巨大！使用时间缩减到原有的20%，提升了5倍的性能！
        if (foundField == null)
        {
            Class<?> clazz = object.getClass();
            for (; clazz != Object.class; clazz = clazz.getSuperclass())
            {
                try
                {
                    foundField = clazz.getDeclaredField(fieldName);
                    //如果不报错，那么肯定就是已经获取到了，那么接下来直接break即可。
                    //否则，报错了，肯定就是获取不到了，那么循环需要继续执行
                    //所以，只要能执行到此处，就说明肯定已经获取成功了
                    foundField.setAccessible(true);//让该字段可访问
                    setFieldToCache(object, fieldName, foundField);
                    break;
                }
                catch (Exception e)
                {
                }
            }
        }
        if (foundField != null)
        {
            if (lastFieldTripleNeedOverride)
            {
                lastFieldTriple = new ImmutableTriple<Class<?>, String, Field>(object.getClass(), fieldName, foundField);
            }
            try
            {
                foundField.set(object, fieldValue);
            }
            catch (IllegalArgumentException e)
            {
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Logs.w("无法找到字段：" + fieldName);
        }
    }
    
    /**
     * 调用任意类的无参数方法<br>
     * 得益于高速缓存，因此性能逆天
     * @author nan.li
     * @param object
     * @param methodName
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Object object, String methodName)
    {
        Method foundMethod = getMethodFromCache(object, methodName);
        if (foundMethod == null)
        {
            Class<?> clazz = object.getClass();
            for (; clazz != Object.class; clazz = clazz.getSuperclass())
            {
                try
                {
                    foundMethod = clazz.getDeclaredMethod(methodName);
                    foundMethod.setAccessible(true);//让该字段可访问
                    setMethodToCache(object, methodName, foundMethod);
                    break;
                }
                catch (Exception e)
                {
                }
            }
        }
        if (foundMethod != null)
        {
            if (lastMethodTripleNeedOverride)
            {
                lastMethodTriple = new ImmutableTriple<Class<?>, String, Method>(object.getClass(), methodName, foundMethod);
            }
            try
            {
                return (T)foundMethod.invoke(object);
            }
            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Logs.w("无法找到方法：" + methodName);
        }
        return null;
    }
    
    /**
     * 获取该类的所有声明了的字段
     * @author nan.li
     * @param entity
     * @return
     */
    public static Field[] getAllDeclaredFields(Object entity)
    {
        return getAllDeclaredFields(entity.getClass());
    }
    
    /**
     * 获取该类的所有声明了的字段
     * @author nan.li
     * @param entity
     * @return
     */
    public static Field[] getAllDeclaredFields(Class<?> clazz)
    {
        if (clazz == lastClass)
        {
            return lastFields;
        }
        Class<?> originalClass = clazz;
        //优先从缓存中获取
        Field[] fields = classAllDeclaredFieldsMap.get(clazz);
        if (fields == null)
        {
            List<Field> fieldsList = new LinkedList<>();
            for (; clazz != Object.class; clazz = clazz.getSuperclass())
            {
                fields = clazz.getDeclaredFields();
                fieldsList.addAll(Lists.asList(fields));
            }
            //全部遍历完毕
            fields = fieldsList.toArray(fields);
            //并最终加入到缓存中
            classAllDeclaredFieldsMap.put(originalClass, fields);
        }
        //记录下上次访问记录
        lastClass = originalClass;
        lastFields = fields;
        return fields;
    }
    
    /**
     * 上次访问的类
     */
    static Class<?> lastClass;
    
    /**
     * 上次的计算结果
     */
    static Field[] lastFields;
    
    /**
     * 类的所有字段（包括继承过来的）的缓存表
     */
    static Map<Class<?>, Field[]> classAllDeclaredFieldsMap = new HashMap<>();
    
    private static class NULL
    {
    }
    
    /**
     * Get an array of types for an array of objects
     * 
     * @see Object#getClass()
     */
    private static Class<?>[] types(Object... values)
    {
        if (values == null)
        {
            return new Class[0];
        }
        Class<?>[] result = new Class[values.length];
        for (int i = 0; i < values.length; i++)
        {
            Object value = values[i];
            result[i] = value == null ? NULL.class : value.getClass();
        }
        return result;
    }
    
    /**
     * 调用任意类的有参数方法
     * @author nan.li
     * @param object
     * @param methodName
     * @param args
     * @return
     */
    public static <T> T invokeMethod(Object object, String methodName, Object... args)
    {
        return invokeMethod(object, methodName, types(args), args);
    }
    
    /**
     * 调用任意类的有参数方法
     * @author nan.li
     * @param object
     * @param methodName
     * @param parameterTypes
     * @param args
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Object object, String methodName, Class<?>[] parameterTypes, Object... args)
    {
        //1.优先精确查找
        Method method = null;
        Class<?> clazz = object.getClass();
        for (; clazz != Object.class; clazz = clazz.getSuperclass())
        {
            try
            {
                method = clazz.getDeclaredMethod(methodName, parameterTypes);
                method.setAccessible(true);
                break;
            }
            catch (Exception e)
            {
            }
        }
        //2. 精确查找方法失败，开启模糊匹配
        if (method == null)
        {
            Logs.i("精确查找方法失败，开启模糊匹配...");
            try
            {
                method = similarMethod(clazz, methodName, parameterTypes);
            }
            catch (NoSuchMethodException e)
            {
                e.printStackTrace();
            }
        }
        if (method != null)
        {
            try
            {
                return (T)method.invoke(object, args);
            }
            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Logs.w("无法找到方法：" + methodName);
        }
        return null;
    }
    
    /**
     * Searches a method with a similar signature as desired using
     * {@link #isSimilarSignature(java.lang.reflect.Method, String, Class[])}.
     * <p>
     * First public methods are searched in the class hierarchy, then private
     * methods on the declaring class. If a method could be found, it is
     * returned, otherwise a {@code NoSuchMethodException} is thrown.
     */
    private static Method similarMethod(Class<?> type, String name, Class<?>[] types)
        throws NoSuchMethodException
    {
        // first priority: find a public method with a "similar" signature in class hierarchy
        // similar interpreted in when primitive argument types are converted to their wrappers
        for (Method method : type.getMethods())
        {
            if (isSimilarSignature(method, name, types))
            {
                return method;
            }
        }
        // second priority: find a non-public method with a "similar" signature on declaring class
        do
        {
            for (Method method : type.getDeclaredMethods())
            {
                if (isSimilarSignature(method, name, types))
                {
                    return method;
                }
            }
            type = type.getSuperclass();
        } while (type != null);
        throw new NoSuchMethodException("No similar method " + name + " with params " + Arrays.toString(types) + " could be found on type " + type + ".");
    }
    
    /**
     * Determines if a method has a "similar" signature, especially if wrapping
     * primitive argument types would result in an exactly matching signature.
     */
    private static boolean isSimilarSignature(Method possiblyMatchingMethod, String desiredMethodName, Class<?>[] desiredParamTypes)
    {
        return possiblyMatchingMethod.getName().equals(desiredMethodName) && match(possiblyMatchingMethod.getParameterTypes(), desiredParamTypes);
    }
    
    /**
     * Check whether two arrays of types match, converting primitive types to
     * their corresponding wrappers.
     */
    private static boolean match(Class<?>[] declaredTypes, Class<?>[] actualTypes)
    {
        if (declaredTypes.length == actualTypes.length)
        {
            for (int i = 0; i < actualTypes.length; i++)
            {
                if (actualTypes[i] == NULL.class)
                    continue;
                    
                if (wrapper(declaredTypes[i]).isAssignableFrom(wrapper(actualTypes[i])))
                    continue;
                    
                return false;
            }
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * Get a wrapper type for a primitive type, or the argument type itself, if
     * it is not a primitive type.
     */
    public static Class<?> wrapper(Class<?> type)
    {
        if (type == null)
        {
            return null;
        }
        else if (type.isPrimitive())
        {
            if (boolean.class == type)
            {
                return Boolean.class;
            }
            else if (int.class == type)
            {
                return Integer.class;
            }
            else if (long.class == type)
            {
                return Long.class;
            }
            else if (short.class == type)
            {
                return Short.class;
            }
            else if (byte.class == type)
            {
                return Byte.class;
            }
            else if (double.class == type)
            {
                return Double.class;
            }
            else if (float.class == type)
            {
                return Float.class;
            }
            else if (char.class == type)
            {
                return Character.class;
            }
            else if (void.class == type)
            {
                return Void.class;
            }
        }
        return type;
    }
    
    static Map<String, Class<?>> classNameMap = new HashMap<>();
    
    /**
     * 加载类的更好的方式<br>
     * 优先从缓存中加载
     * @author nan.li
     * @param className
     * @return
     */
    public static Class<?> forName(String className)
    {
        if (classNameMap.containsKey(className))
        {
            return classNameMap.get(className);
        }
        try
        {
            Class<?> clazz = Class.forName(className);
            classNameMap.put(className, clazz);
            return clazz;
        }
        catch (ClassNotFoundException e)
        {
            return null;
        }
    }
    
    /**
     * 实例化的更好方式
     * @author nan.li
     * @param className
     * @return
     */
    public static Object newInstance(String className)
    {
        Class<?> clazz = ClassKit.forName(className);
        Validate.notNull(clazz, "无法加载类 %s", className);
        try
        {
            Object object = clazz.newInstance();
            return object;
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
            Logs.e("调用" + className + "的默认构造方法失败！");
            return null;
        }
    }
    
    /**
     * 生成一个指定clazz的实例对象
     * @author nan.li
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<T> clazz)
    {
        Object t = null;
        if (clazz == int.class || clazz == Integer.class)
        {
            t = (int)0;
        }
        else if (clazz == byte.class || clazz == Byte.class)
        {
            t = (byte)0;
        }
        else if (clazz == short.class || clazz == Short.class)
        {
            t = (short)0;
        }
        else if (clazz == long.class || clazz == Long.class)
        {
            t = (long)0;
        }
        else if (clazz == float.class || clazz == Float.class)
        {
            t = (float)0f;
        }
        else if (clazz == double.class || clazz == Double.class)
        {
            t = (double)0;
        }
        else if (clazz == char.class || clazz == Character.class)
        {
            t = 'a';
        }
        else if (clazz == boolean.class || clazz == Boolean.class)
        {
            t = false;
        }
        else
        {
            try
            {
                t = clazz.newInstance();
            }
            catch (InstantiationException | IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
        return (T)t;
    }
    
    /**
     * 将原生类型转换成包装类
     * @author nan.li
     * @param clazz
     * @return
     */
    public static Class<?> transferPrimitiveTypeToWrappedType(Class<?> clazz)
    {
        if (clazz == int.class)
        {
            return Integer.class;
        }
        else if (clazz == byte.class)
        {
            return Byte.class;
        }
        else if (clazz == short.class)
        {
            return Short.class;
        }
        else if (clazz == long.class)
        {
            return Long.class;
        }
        else if (clazz == float.class)
        {
            return Float.class;
        }
        else if (clazz == double.class)
        {
            return Double.class;
        }
        else if (clazz == char.class)
        {
            return Character.class;
        }
        else if (clazz == boolean.class)
        {
            return Boolean.class;
        }
        else
        {
            return clazz;
        }
    }
    
    /**
     * 实例化的更好方式<br>
     * 可以调用带参数的构造函数
     * @author nan.li
     * @param className
     * @param parameterTypes
     * @param initargs
     * @return
     */
    public static Object newInstance(String className, Class<?>[] parameterTypes, Object... initargs)
    {
        Class<?> clazz = ClassKit.forName(className);
        Validate.notNull(clazz, "无法加载类 %s", className);
        try
        {
            Constructor<?> constructor = clazz.getConstructor(parameterTypes);
            return constructor.newInstance(initargs);
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
}