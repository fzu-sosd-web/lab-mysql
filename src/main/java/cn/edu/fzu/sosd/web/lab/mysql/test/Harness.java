package cn.edu.fzu.sosd.web.lab.mysql.test;

import cn.edu.fzu.sosd.web.lab.mysql.dto.UserDto;
import cn.edu.fzu.sosd.web.lab.mysql.util.TimeUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.*;

public abstract class Harness {

    private final ExecutorService executor;

    public Harness() {
        this.executor = Executors.newSingleThreadExecutor();
    }

    public Object runTask(Callable<Object> task, int timeout, String testName) throws InterruptedException {
        System.out.println("任务[" + testName + "]开始执行，限时:" + timeout + "ms");
        Future<Object> future = executor.submit(task);
        try {
            // 获取结果，如果在指定时间内未完成会抛出 TimeoutException
            return future.get(timeout, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            System.out.println("任务[" + testName + "]超时！");
            return null;
        } catch (ExecutionException e) {
            System.out.println("任务[" + testName + "]执行时发生错误：" + e.getMessage());
            return null;
        } finally {
            future.cancel(true); // 取消任务
        }
    }

//    public boolean verify() {
//
//    }

    public UserDto mockRandomUser() throws ParseException {
        UserDto userDto = new UserDto();
        userDto.setUsername(RandomStringUtils.randomAlphanumeric(8));
        userDto.setPassword(RandomStringUtils.randomAlphanumeric(8));
        userDto.setAvatar(RandomStringUtils.randomAlphanumeric(128));
        Date randDate = DateUtils.addDays(TimeUtil.parseDate("2003-01-25"), RandomUtils.nextInt(0, 1000) - 500);
        userDto.setBirthday(randDate);
        userDto.setStatus(0);
        return userDto;
    }


}
