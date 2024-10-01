package cn.edu.fzu.sosd.web.lab.mysql.sanity;

import cn.edu.fzu.sosd.web.lab.mysql.dto.UserDto;
import cn.edu.fzu.sosd.web.lab.mysql.service.UserService;
import cn.edu.fzu.sosd.web.lab.mysql.service.impl.UserServiceImpl;
import cn.edu.fzu.sosd.web.lab.mysql.test.Harness;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SanityTest extends Harness {

    private final Logger log;
    private final UserService userService;

    public SanityTest(Logger log, UserService userService) {
        super(log);
        this.log = log;
        this.userService = userService;
    }

    public void process() throws ParseException {
        log.info(">>> Sanity test start >>>");
        userService.removeAll();

        // Step 1
        verifySave();

        // Step 2
        verifyPrefixSearch();

        // Step 3
        verifyPageAndOrder();

        // Step 5: Get users by role
        log.info("Getting users by role: {}", "admin");
        List<UserDto> usersByRole = userService.getUserByRole("admin");
        assert usersByRole != null : "Users by role should not be null";
        log.info("Users with role 'admin': {}", usersByRole);

        // Step 7: Get users by birthday interval
        Date startDate = new Date(0); // Unix epoch time
        Date endDate = new Date(); // Current date
        log.info("Getting users with birthdays between {} and {}", startDate, endDate);
        List<UserDto> usersByBirthdayInterval = userService.listUserByBirthdayInterval(startDate, endDate);
        assert usersByBirthdayInterval != null : "Users by birthday interval should not be null";
        log.info("Users with birthdays in interval: {}", usersByBirthdayInterval);

        // Validate birthday interval
        for (UserDto user : usersByBirthdayInterval) {
            assert user.getBirthday().after(startDate) && user.getBirthday().before(endDate) :
                    "User birthday " + user.getBirthday() + " is not in the interval";
        }

        // Step 8: Ban the created user by ID

        log.info(">>> Pass all sanity tests, Congrats! >>>");
    }

    void verifySave() {
        clear();
        log.info("Test: verify save to db and generate unique key.");
        UserDto input = mockInput();
        UserDto saved = userService.save(input);
        UserDto shadow = userService.save(input);
        boolean match = infoMatch(saved, input);
        if (false == match) {
            log.error("FAIL: save failed, input: {}, saved: {}", input, saved);
            System.exit(-1);
        }
        long key = saved.getId();
        if (key == 0L || shadow.getId() != key + 1) {
            log.error("FAIL: auto-increment primary key generate failed");
            System.exit(-1);
        }
        UserDto got = userService.getUserById(key);
        match = infoMatch(got, saved);
        if (false == match) {
            log.error("FAIL: failed to locate user by key:{}", key);
            System.exit(-1);
        }
        final String updateName = "ywj";
        saved.setUsername(updateName);
        userService.save(saved);
        got = userService.getUserById(key);
        match = infoMatch(got, saved);
        if (false == match) {
            log.error("FAIL: failed to update user info, should be:{}, but:{}", updateName, got.getUsername());
            System.exit(-1);
        }

        log.info("PASS");
    }

    void verifyPrefixSearch() {
        clear();
        log.info("Test: verify prefix search.");
        List<UserDto> users = new ArrayList<>();
        final String prefix = "sosd";
        final int size = 20;
        int prefixUserCount = 0;
        List<UserDto> inputList = mockInputList(size);
        for (UserDto input : inputList) {
            if (input.getUsername().hashCode() % 2 == 0) {
                input.setUsername(prefix + input.getUsername());
                prefixUserCount++;
            }
            UserDto saved = userService.save(input);
            users.add(saved);
        }

        List<UserDto> searched = userService.searchUserByPrefix(prefix);
        if (searched.size() != prefixUserCount) {
            log.error("FAIL: searched user count {} does not match size {}", searched.size(), size);
            System.exit(-1);
        }

        log.info("PASS");
    }

    void verifyPageAndOrder() {
        clear();
        log.info("Test: verify page and order.");
        final int num = 100;
        final int pageSize = 10;
        List<UserDto> users = new ArrayList<>();
        List<UserDto> inputList = mockInputList(num);
        for (UserDto input : inputList) {
            UserDto saved = userService.save(input);
            users.add(saved);
        }

        List<UserDto> page0 = userService.getAllUserByPage(0, 10);

        if (page0.size() != pageSize) {
            log.error("FAIL: page failed");
            System.exit(-1);
        }

        UserDto chosen = users.get(RandomUtils.nextInt(0, num));
        chosen.setPassword("123456");
        userService.save(chosen);

        List<UserDto> page1 = userService.getAllUserByPage(0, 10);
        if (infoMatch(page1.get(0), chosen) == false) {
            log.error("FAIL: order failed");
            System.exit(-1);
        }

        log.info("PASS");
    }

    void clear() {
        log.info("Must Done: clear all user.");
        userService.removeAll();
        int userCount = userService.userCount();
        if (userCount != 0) {
            log.error("FATAL: user count: {}, clear unsuccessful", userCount);
            System.exit(-1);
        }
        log.info("Done");
    }


    public static void main(String[] args) throws ParseException {
        new SanityTest(LoggerFactory.getLogger(SanityTest.class), new UserServiceImpl()).process();
    }

}
