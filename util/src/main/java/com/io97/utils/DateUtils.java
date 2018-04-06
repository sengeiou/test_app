package com.io97.utils;

import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    private final SimpleDateFormat format;
    public DateUtils(SimpleDateFormat format) {
        this.format = format;
    }
    public SimpleDateFormat getFormat() {
        return format;
    }
    //紧凑型日期格式，也就是纯数字类型yyyyMMdd
    public static final DateUtils COMPAT = new DateUtils(new SimpleDateFormat("yyyyMMdd"));
    public static final DateUtils COMMON = new DateUtils(new SimpleDateFormat("yyyy-MM-dd"));
    public static final DateUtils COMMON_FULL = new DateUtils(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    public static Date fromMysqlTimestamp(int timeInSeconds) {
		if (timeInSeconds <= 0)
			return null;
		else
			return new Date(1000L * timeInSeconds);
	}

	public static int nowInSeconds() {
		return (int) (Calendar.getInstance().getTimeInMillis() / 1000L);
	}

    public static long nowInMillis() {
        return Calendar.getInstance().getTimeInMillis();
    }

	public static String format(Date date, String pattern) {
		return DateFormatUtils.format(date, pattern);
	}

	public static int fromGoodsDate() {
		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DAY_OF_MONTH, -1);
		String tmp = DateUtils.format(yesterday.getTime(), "yyyyMMdd");
		Integer tmp1 = Integer.parseInt(tmp);
		return tmp1;
	}

	// 将html5 转换为时间戳
	public static int dateParseTimestamp(String dateString)
			throws ParseException {

		dateString = dateString.replaceAll("T", " ");
		Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateString);
		int timestamp = (int) (date.getTime() / 1000);// JAVA的时间戳长度是13位
		return timestamp;
	}

    public static int dateParseIntTime(String dateString) throws ParseException{
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateString);
        int timestamp = (int) (date.getTime() / 1000);
        return timestamp;
    }
    public static int dateParseIntDate(String dateString) throws ParseException{
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
        int timestamp = (int) (date.getTime() / 1000);
        return timestamp;
    }

    /**
     * 转换指定格式时间字符 为秒数
     * @param dateString
     * @param format
     * @return
     * @throws java.text.ParseException
     */
    public static int dateParseTimestamp(String dateString, String format) {
        try {

            Date date = new SimpleDateFormat(format).parse(dateString);
            int timestamp = (int) (date.getTime() / 1000);
            return timestamp;
        } catch (Exception ex) {
            return nowInSeconds();
        }

    }

	// 将时间戳转成 yyyy-MM-dd'T'HH:mm:ss格式"
	public static String timeStampParseHtml5Date(int timeStamp) {
		if (timeStamp <= 0) {
			return "";
		} else {
			Date date = fromMysqlTimestamp(timeStamp);
			return DateUtils.format(date, "yyyy-MM-dd'T'HH:mm:ss");
		}
	}

	// 将时间戳转成 yyyy-MM-dd'T'HH:mm:ss格式"
	public static String timeStampParseDateStr(int timeStamp) {
		if (timeStamp <= 0) {
			return "";
		} else {
			Date date = fromMysqlTimestamp(timeStamp);
			return DateUtils.format(date, "yyyy-MM-dd HH:mm");
		}
	}

	// 将时间戳转成yyyyMMdd格式
	public static String timeStampParseDate(int timeStamp) {
		if (timeStamp <= 0) {
			return null;
		} else {
			Date date = fromMysqlTimestamp(timeStamp);
			return DateUtils.format(date, "yyyyMMdd");
		}

	}
    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }


    /**
     * 指定格式转换
     * @param timeStamp
     * @param format
     * @return
     */
    public static String timeStampParseDate(int timeStamp,String format) {
        if (timeStamp <= 0) {
            return null;
        } else {
            Date date = fromMysqlTimestamp(timeStamp);
            return DateUtils.format(date, format);
        }

    }


    /**
    *将时间戳转成MM/dd格式
    */
    public static String timeStampParseString(int timeStamp) {
        if (timeStamp <= 0) {
            return null;
        } else {
            Date date = fromMysqlTimestamp(timeStamp);
            return DateUtils.format(date, "MM/dd");
        }
    }

    /**
     *将时间戳转成yyyy.MM.dd格式
     */
    public static String timeStampParsePhoto(int timeStamp) {
        if (timeStamp <= 0) {
            return null;
        } else {
            Date date = fromMysqlTimestamp(timeStamp);
            return DateUtils.format(date, "yyyy.MM.dd");
        }
    }

    // 将时间戳转成yyyy-MM-dd格式
    public static String timeComment(int timeStamp) {
        if (timeStamp <= 0) {
            return null;
        } else {
            Date date = fromMysqlTimestamp(timeStamp);
            return DateUtils.format(date, "yyyy-MM-dd");
        }
    }

    //将时间戳转换成yymm
    public static String timeStampParseLink(int timeStamp) {
        if (timeStamp <= 0) {
            return null;
        } else {
            Date date = fromMysqlTimestamp(timeStamp);
            return DateUtils.format(date, "yyyyMMdd");
        }
    }

    // 将时间戳转成MM-dd格式
    public static String timeNewsString(int timeStamp) {
        if (timeStamp <= 0) {
            return null;
        } else {
            Date date = fromMysqlTimestamp(timeStamp);
            return DateUtils.format(date, "MM-dd");
        }
    }

	public static String timeAbstract(int datetime){
		Calendar firstTimeOfToday = Calendar.getInstance();
		firstTimeOfToday.set(Calendar.HOUR_OF_DAY, 0);
		firstTimeOfToday.set(Calendar.MINUTE, 0);
		firstTimeOfToday.set(Calendar.SECOND, 0);
		int timeToToday = (int)(firstTimeOfToday.getTimeInMillis() / 1000);
		firstTimeOfToday.roll(Calendar.DATE, false);
		int timeToYestoday = (int)(firstTimeOfToday.getTimeInMillis() / 1000);
		int timeToNow = DateUtils.nowInSeconds() - datetime;

		if(datetime < timeToYestoday){
			Date date = DateUtils.fromMysqlTimestamp(datetime);
			if(date != null)
				return DateUtils.format(date,"yyyy年MM月dd日");
		}
		else if(datetime >= timeToYestoday && datetime < timeToToday)
			return "昨天";
		else{
		//60秒内
			if(timeToNow < 60)
				return "刚刚";
			else if(timeToNow >= 60 && timeToNow < 60*60)
				return (timeToNow / 60) + "分钟前";
			else if(timeToNow >= 60*60 && timeToNow < 24*60*60)
				return (timeToNow / (60*60)) + "小时前";
		}
		return "";
	}


    public static String timeAbstracts(int datetime){
        Calendar firstTimeOfToday = Calendar.getInstance();
        firstTimeOfToday.set(Calendar.HOUR_OF_DAY, 0);
        firstTimeOfToday.set(Calendar.MINUTE, 0);
        firstTimeOfToday.set(Calendar.SECOND, 0);
        int timeToToday = (int)(firstTimeOfToday.getTimeInMillis() / 1000);
        firstTimeOfToday.roll(Calendar.DATE, false);
        int timeToYestoday = (int)(firstTimeOfToday.getTimeInMillis() / 1000);
        int timeToNow = DateUtils.nowInSeconds() - datetime;

        if(datetime < timeToYestoday){
            Date date = DateUtils.fromMysqlTimestamp(datetime);
            if(date != null)
                return DateUtils.format(date,"MM月dd日");
        }
        else if(datetime >= timeToYestoday && datetime < timeToToday)
            return "昨天";
        else{
            //60秒内
            if(timeToNow < 60)
                return "刚刚";
            else if(timeToNow >= 60 && timeToNow < 60*60)
                return (timeToNow / 60) + "分钟前";
            else if(timeToNow >= 60*60 && timeToNow < 24*60*60)
                return (timeToNow / (60*60)) + "小时前";
        }
        return "";
    }

    public static String timeStampParseStr(int timeStamp) {
        if (timeStamp <= 0) {
            return "";
        } else {
            Date date = fromMysqlTimestamp(timeStamp);
            return DateUtils.format(date, "yyyy-MM-dd HH:mm:ss");
        }
    }

    /**
     * 把时间转换成字符串
     * @param dateString
     * @return
     * @throws ParseException
     */
    public static int dateParseIntTimeNew(String dateString) throws ParseException{
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
        int timestamp = (int) (date.getTime() / 1000);
        return timestamp;
    }

    /**
     * 吧当前时间去掉符号装换成数字(ex:2016-07-04 => 20160704)
     * @return
     */
    public static int timeStampParseInt(){
        SimpleDateFormat df =new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String timestamp =df.format(date);
        String[] dateStr=timestamp.split("-");
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < dateStr.length; i++){
            sb.append(dateStr[i]);
        }
        Integer newStr = Integer.parseInt(sb.toString());
        return newStr;
    }

    /**
     * 日期获取字符串
     */
    public static String getDateText(Date date ,String format){
        return new SimpleDateFormat(format).format(date);
    }
    /**
     * 日期获取字符串
     */
    public String getDateText(Date date){
        return getFormat().format(date);
    }
    /**
     * 字符串获取日期
     * @throws ParseException
     */
    public Date getTextDate(String text) throws ParseException{
        return getFormat().parse(text);
    }

    /**
     * 功能描述：返回小时
     * @param date 日期
     * @return 返回小时
     */
    public static int getHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

}
