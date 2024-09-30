package cn.edu.fzu.sosd.web.lab.mysql.util;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Date;

public class TimeUtil {

    public static Date parseDate(String date) throws ParseException {
        return DateUtils.parseDate(date, "yyyy-MM-dd");
    }

}
