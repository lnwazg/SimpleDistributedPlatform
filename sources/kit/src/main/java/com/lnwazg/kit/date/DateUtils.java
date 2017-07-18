package com.lnwazg.kit.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.lnwazg.kit.log.Logs;

/**
 * 时间工具类
 * 
 * @author lnwazg
 * @version [版本号, 2011-12-17]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class DateUtils
{
    public static final String DEFAULT_DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * 文件名-日期时间格式化<br>
     * 例如：20170702220512
     */
    public static final String FILE_DATE_TIME_FORMAT_PATTERN = "yyyyMMddHHmmss";
    
    public static final String DEFAULT_DATE_TIME_HHmm_FORMAT_PATTERN = "yyyy-MM-dd HH:mm";
    
    public static final String DEFAULT_DATE_TIME_HH_FORMAT_PATTERN = "yyyy-MM-dd HH";
    
    public static final String DEFAULT_DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    
    public static final String DEFAULT_DATE_FORMAT_PATTERN2 = "yyyyMMdd";
    
    public static final String DEFAULT_TIME_FORMAT_PATTERN = "HH:mm:ss";
    
    public static final String DEFAULT_TIME_HHmm_FORMAT_PATTERN = "HH:mm";
    
    /**
     * 是否是日期格式
     * @author nan.li
     * @param dateStr
     * @return
     */
    public static boolean isDate(String dateStr)
    {
        return parseDate(dateStr, DEFAULT_DATE_FORMAT_PATTERN) != null;
    }
    
    public static void main(String[] args)
    {
        System.out.println(isDate("2017-03-14"));
        System.out.println(isDate("2017-03- "));
        System.out.println(isDate("2017-03-14  "));
    }
    
    /**
     * 获取指定格式的时间字符串
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String getFormattedTimeStr(String pattern)
    {
        return getFormattedTimeStr(pattern, new Date());
    }
    
    /**
     * 获取指定格式的时间字符串 格式化指定日期
     * 
     * @param pattern
     * @param date
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String getFormattedTimeStr(String pattern, Date date)
    {
        return new SimpleDateFormat(pattern).format(date);
    }
    
    /**
     * 获取当前日期，例如：20150410
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String getFormattedDateStr()
    {
        return getFormattedTimeStr(DEFAULT_DATE_FORMAT_PATTERN2);
    }
    
    /**
     * 获取当前日期时间，例如：2015-04-10 12:10:13
     * @author Administrator
     * @return
     */
    public static String getCurStandardDateTimeStr()
    {
        return getFormattedTimeStr(DEFAULT_DATE_TIME_FORMAT_PATTERN);
    }
    
    /**
     * 适配文件名格式的时间戳<br>
     * 例如：20170702220512
     * @author nan.li
     * @return
     */
    public static String getCurFileNameDateTimeStr()
    {
        return getFormattedTimeStr(FILE_DATE_TIME_FORMAT_PATTERN);
    }
    
    /**
     * 获取当前日期，例如：2015-04-10
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String getSimpleCurrentDate()
    {
        return getFormattedTimeStr(DEFAULT_DATE_FORMAT_PATTERN);
    }
    
    /**
     * 根据指定的日期格式，将参数日期字符串转换成Date对象
     * 
     * @author nan.li
     * @param dateTimeStr
     * @param pattern
     * @return
     */
    public static Date parseDate(String dateTimeStr, String pattern)
    {
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        try
        {
            Date date = dateFormat.parse(dateTimeStr);
            return date;
        }
        catch (ParseException e)
        {
            return null;
        }
    }
    
    /**
     * 计算时间差
     * @author Administrator
     * @param planEnd
     * @param planBegin
     * @param timeUnit
     * @return
     */
    public static long timeDiff(Calendar planEnd, Calendar planBegin, TimeUnit timeUnit)
    {
        long timeDiff = 0L;
        long diffMills = planEnd.getTime().getTime() - planBegin.getTime().getTime();
        switch (timeUnit)
        {
            case DAYS:
                timeDiff = diffMills / (1000 * 60 * 60 * 24);
                break;
            case HOURS:
                timeDiff = diffMills / (1000 * 60 * 60);
                break;
            case MINUTES:
                timeDiff = diffMills / (1000 * 60);
                break;
            case SECONDS:
                timeDiff = diffMills / (1000);
                break;
            default:
                break;
        }
        timeDiff = Math.abs(timeDiff);
        return timeDiff;
    }
    
    /**
     * 获取当前时间的Calendar对象
     * @author nan.li
     * @return
     */
    public static Calendar getCurrentCalendar()
    {
        return Calendar.getInstance();
    }
    
    public static Date getCurrentDate()
    {
        return new Date();
    }
    
    /**
     * 时间计算，增加指定的字段指定的数量
     * @author nan.li
     * @param date
     * @param amount
     * @param field
     * @return
     */
    public static Date add(Date date, int amount, int field)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, amount);
        return calendar.getTime();
    }
    
    /**
     * 获取当前时间的所有信息
     * @author Administrator
     * @return
     */
    public static CalendarDesc getCurrentCalendarDesc()
    {
        CalendarDesc calendarDesc = new CalendarDesc();
        Calendar calendar = getCurrentCalendar();
        
        // 显示年份  
        int year = calendar.get(Calendar.YEAR);
        //        System.out.println("year is = " + String.valueOf(year));
        //        
        // 显示月份 (从0开始, 实际显示要加一)  
        int month = calendar.get(Calendar.MONTH);
        //        System.out.println("nth is = " + (month + 1));
        
        // 本周几  
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        //        System.out.println("week is = " + week);
        
        // 今年的第 N 天  
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        //        System.out.println("DAY_OF_YEAR is = " + DAY_OF_YEAR);
        
        // 本月第 N 天  
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        //        System.out.println("DAY_OF_MONTH = " + String.valueOf(DAY_OF_MONTH));
        
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        
        //属性设置
        calendarDesc.setYear(year)
            .setMonth(month)
            .setWeek(week)
            .setDayOfYear(dayOfYear)
            .setDayOfMonth(dayOfMonth)
            .setHourOfDay(hourOfDay)
            .setMinute(minute)
            .setSecond(second)
            .setCalendar(calendar);
        return calendarDesc;
    }
    
    /**
     * Calendar的描述信息
     * @author nan.li
     * @version 2016年8月18日
     */
    public static class CalendarDesc
    {
        private Calendar calendar;
        
        private int year;
        
        private int month;
        
        private int week;
        
        private int dayOfYear;
        
        private int dayOfMonth;
        
        private int hourOfDay;
        
        private int minute;
        
        private int second;
        
        public Calendar getCalendar()
        {
            return calendar;
        }
        
        public CalendarDesc setCalendar(Calendar calendar)
        {
            this.calendar = calendar;
            return this;
        }
        
        public int getYear()
        {
            return year;
        }
        
        public CalendarDesc setYear(int year)
        {
            this.year = year;
            return this;
        }
        
        public int getMonth()
        {
            return month;
        }
        
        public CalendarDesc setMonth(int month)
        {
            this.month = month;
            return this;
        }
        
        public int getWeek()
        {
            return week;
        }
        
        public CalendarDesc setWeek(int week)
        {
            this.week = week;
            return this;
        }
        
        public int getDayOfYear()
        {
            return dayOfYear;
        }
        
        public CalendarDesc setDayOfYear(int dayOfYear)
        {
            this.dayOfYear = dayOfYear;
            return this;
        }
        
        public int getDayOfMonth()
        {
            return dayOfMonth;
        }
        
        public CalendarDesc setDayOfMonth(int dayOfMonth)
        {
            this.dayOfMonth = dayOfMonth;
            return this;
        }
        
        public int getHourOfDay()
        {
            return hourOfDay;
        }
        
        public CalendarDesc setHourOfDay(int hourOfDay)
        {
            this.hourOfDay = hourOfDay;
            return this;
        }
        
        public int getMinute()
        {
            return minute;
        }
        
        public CalendarDesc setMinute(int minute)
        {
            this.minute = minute;
            return this;
        }
        
        public int getSecond()
        {
            return second;
        }
        
        public CalendarDesc setSecond(int second)
        {
            this.second = second;
            return this;
        }
        
        /**
         * 是否符合其中的一个时分
         * @author Administrator
         * @param hourMinute
         * @return
         */
        public boolean matchHourMinutes(String... hourMinute)
        {
            if (hourMinute.length > 0)
            {
                for (String hm : hourMinute)
                {
                    if (hm.length() == 5 && hm.indexOf(":") == 2)
                    {
                        String hourStr = hm.substring(0, 2);
                        String minuteStr = hm.substring(3);
                        if (Integer.valueOf(hourStr) == this.hourOfDay && Integer.valueOf(minuteStr) == this.minute)
                        {
                            return true;
                        }
                    }
                    else
                    {
                        Logs.e(String.format("参数\"%s\"格式非法！无法进行有效验证", hm));
                        return false;
                    }
                }
            }
            return false;
        }
        
        /**
         * 是否符合时间间隔区间
         * 例如09:00~23:00 每隔半个小时
         * @author nan.li
         * @param hourMinuteBegin
         * @param hourMinuteEnd
         * @param interval
         * @return
         */
        public boolean matchHourMinutesRange(String hourMinuteBegin, String hourMinuteEnd, int interval)
        {
            if (hourMinuteBegin.length() == 5 && hourMinuteBegin.indexOf(":") == 2 && hourMinuteEnd.length() == 5 && hourMinuteEnd.indexOf(":") == 2)
            {
                int hourBegin = Integer.valueOf(hourMinuteBegin.substring(0, 2));
                int minuteBegin = Integer.valueOf(hourMinuteBegin.substring(3));
                int hourEnd = Integer.valueOf(hourMinuteEnd.substring(0, 2));
                int minuteEnd = Integer.valueOf(hourMinuteEnd.substring(3));
                List<Integer> validMinutes = new ArrayList<>();
                for (int i = 0; i < 60; i += interval)
                {
                    validMinutes.add(i);
                }
                //如果开始时间和结束时间的小时数相同
                if (hourBegin == hourEnd)
                {
                    //必须在那个区间内才行
                    if (this.hourOfDay == hourBegin && validMinutes.contains(this.minute) && this.minute >= minuteBegin && this.minute <= minuteEnd)
                    {
                        return true;
                    }
                }
                else
                {
                    //开始时间与结束时间的小时数不同
                    if ((this.hourOfDay == hourBegin && validMinutes.contains(this.minute) && this.minute >= minuteBegin)
                        || (this.hourOfDay > hourBegin && this.hourOfDay < hourEnd && validMinutes.contains(this.minute))
                        || (this.hourOfDay == hourEnd && validMinutes.contains(this.minute) && this.minute <= minuteEnd))
                    {
                        return true;
                    }
                }
            }
            else
            {
                Logs.e(String.format("参数\"%s %s\"格式非法！无法进行有效验证", hourMinuteBegin, hourMinuteEnd));
                return false;
            }
            return false;
        }
    }
    
}
