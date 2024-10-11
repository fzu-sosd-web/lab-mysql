package cn.edu.fzu.sosd.web.lab.mysql.test;

import cn.edu.fzu.sosd.web.lab.mysql.dto.UserDto;
import cn.edu.fzu.sosd.web.lab.mysql.util.TimeUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.*;

public abstract class Harness {

    private final ExecutorService executor;
    private final Logger log;

    public Harness(Logger log) {
        this.log = log;
        this.executor = Executors.newSingleThreadExecutor();
    }

    public Object runTask(Callable<Object> task, int timeout, String taskName) throws InterruptedException {
        log.info("run task {} with timeout {}", taskName, timeout);
        Future<Object> future = executor.submit(task);
        try {
            // 获取结果，如果在指定时间内未完成会抛出 TimeoutException
            return future.get(timeout, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.error("run task {} timeout", taskName, e);
            return null;
        } catch (ExecutionException e) {
            log.error("run task {} error", taskName, e);
            return null;
        } finally {
            future.cancel(true); // 取消任务
        }
    }

    public boolean infoMatch(UserDto actual, UserDto expected) {
        if (actual == null) {
            log.warn("actual is null");
            return false;
        }
        if (expected == null) {
            log.warn("expected is null");
        }
        return actual.getUsername().equals(expected.getUsername()) &&
                actual.getPassword().equals(expected.getPassword()) &&
                actual.getAvatar().equals(expected.getAvatar()) &&
                actual.getBirthday().equals(expected.getBirthday()) &&
                actual.getStatus() == expected.getStatus() &&
                actual.roles().equals(expected.roles());
    }

    public boolean infoMatch(List<UserDto> actual, List<UserDto> expected) {
        if (actual.size() != expected.size()) {
            return false;
        }
        Comparator<UserDto> comp = new Comparator<UserDto>() {
            @Override
            public int compare(UserDto o1, UserDto o2) {
                return (int) (o1.getId() - o2.getId());
            }
        };
        Collections.sort(actual, comp);
        Collections.sort(expected, comp);
        for (int i = 0; i < actual.size(); i++) {
            UserDto actualDto = actual.get(i);
            UserDto expectedDto = expected.get(i);
            if (false == infoMatch(actualDto, expectedDto)) {
                return false;
            }
        }
        return true;
    }

    public UserDto mockInput() {
        UserDto userDto = new UserDto();
        userDto.setUsername(RandomStringUtils.randomNumeric(8));
        userDto.setPassword(RandomStringUtils.randomNumeric(8));
        userDto.setAvatar(RandomStringUtils.randomAlphanumeric(128));
        Date randDate = DateUtils.addDays(TimeUtil.parseDate("2003-01-25"), RandomUtils.nextInt(0, 1000) - 500);
        userDto.setBirthday(randDate);
        userDto.setStatus(0);
        userDto.setRoles(new ArrayList<>());
        userDto.getRoles().add("mock-user");
        return userDto;
    }

    public List<UserDto> mockInputList(int size) {
        List<UserDto> inputList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            inputList.add(mockInput());
        }
        return inputList;
    }


}
