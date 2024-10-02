package cn.edu.fzu.sosd.web.lab.mysql.util;

import lombok.SneakyThrows;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Date;

public class TimeUtil {

    @SneakyThrows
    public static Date parseDate(String date) {
        return DateUtils.parseDate(date, "yyyy-MM-dd");
    }

}
