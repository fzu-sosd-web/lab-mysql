package cn.edu.fzu.sosd.web.lab.mysql.sanity;

import cn.edu.fzu.sosd.web.lab.mysql.dto.UserDto;
import cn.edu.fzu.sosd.web.lab.mysql.service.UserService;
import cn.edu.fzu.sosd.web.lab.mysql.service.impl.UserServiceImpl;
import cn.edu.fzu.sosd.web.lab.mysql.test.Harness;
import cn.edu.fzu.sosd.web.lab.mysql.util.TimeUtil;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.*;

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

        // Step 1
        verifySave();

        // Step 2
        verifyPrefixSearch();

        // Step 3
        verifyPageAndOrder();

        // Step 4
        verifyRole();

        // Step 5
        verifyInterval();

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
        final int num = 1000;
        final int pageSize = 10;
        List<UserDto> users = new ArrayList<>();
        List<UserDto> inputList = mockInputList(num);
        for (UserDto input : inputList) {
            UserDto saved = userService.save(input);
            users.add(saved);
        }

        List<UserDto> page0 = userService.getAllUserByPageOrderByUpdateTime(0, 10);

        if (page0.size() != pageSize) {
            log.error("FAIL: page failed");
            System.exit(-1);
        }

        UserDto chosen = users.get(RandomUtils.nextInt(0, num));
        chosen.setPassword("123456");
        userService.save(chosen);

        List<UserDto> page1 = userService.getAllUserByPageOrderByUpdateTime(0, 10);
        if (infoMatch(page1.get(0), chosen) == false) {
            log.error("FAIL: order failed");
            System.exit(-1);
        }

        log.info("PASS");
    }

    void verifyRole() {
        clear();
        log.info("Test: verify role.");
        List<UserDto> users = new ArrayList<>();
        Map<String, List<UserDto>> roleUserMapper = new HashMap<>();
        List<String> allRoles = List.of("member", "admin", "teacher", "eval-member");
        allRoles.forEach(role -> {
            roleUserMapper.put(role, new ArrayList<>());
        });
        final int num = 100;
        List<UserDto> inputList = mockInputList(num);
        for (UserDto input : inputList) {
            List<String> roles = new ArrayList<>();
            if (RandomUtils.secure().randomBoolean()) {
                roles.add("eval-member");  // 50% chance of being "eval-member"
            } else {
                roles.add("member");
                roles.add(RandomUtils.secure().randomBoolean() ? "admin" : "teacher");
            }

            UserDto saved = userService.save(input);
            users.add(saved);
            for (String role : roles) {
                roleUserMapper.get(role).add(saved);
            }
        }

        roleUserMapper.forEach((k, v) -> {
            List<UserDto> byRole = userService.getUserByRole(k);
            if (false == infoMatch(byRole, roleUserMapper.get(k))) {
                log.error("FAIL: wrong answer of finding role by key:{}", k);
                System.exit(-1);
            }
        });

        // 1. choose a random eval-member user
        UserDto chosen = roleUserMapper.get("eval-member").get(0);
        // 2. adjust user role to member, delete eval-member
        chosen = userService.removeRole(chosen.getId(), "eval-member");
        chosen = userService.appendRole(chosen.getId(), "member");

        if (chosen.getRoles().contains("eval-member") || chosen.getRoles().contains("member") == false) {
            log.error("FAIL: update roles failed:{}", chosen.getId());
            System.exit(-1);
        }
        // 3. update in-memory user list
        long influenceId = chosen.getId();
        roleUserMapper.get("eval-member").removeIf(user -> user.getId() == influenceId);
        roleUserMapper.get("member").add(chosen);

        roleUserMapper.forEach((k, v) -> {
            List<UserDto> byRole = userService.getUserByRole(k);
            if (false == infoMatch(byRole, roleUserMapper.get(k))) {
                log.error("FAIL: wrong answer of finding role by key:{} (after update role)", k);
                System.exit(-1);
            }
        });

        log.info("PASS");
    }

    void verifyInterval() {
        clear();
        log.info("Test: test interval select.");
        final int num = 1000;
        final Date mid = TimeUtil.parseDate("2003-01-25");
        final int delta = RandomUtils.secure().randomInt(0, 500);
        final Date start = DateUtils.addDays(mid, -delta);
        final Date end = DateUtils.addDays(mid, delta);
        List<UserDto> users = new ArrayList<>();
        List<UserDto> inputList = mockInputList(num);
        List<UserDto> inInterval = new ArrayList<>();
        for (UserDto input : inputList) {
            UserDto saved = userService.save(input);
            users.add(saved);
            if (saved.getBirthday().after(start) && saved.getBirthday().before(end)) {
                inInterval.add(saved);
            }
        }

        List<UserDto> byBirthdayInterval = userService.listUserByBirthdayInterval(start, end);
        if (false == infoMatch(byBirthdayInterval, inInterval)) {
            log.error("FAIL: wrong answer of finding interval");
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
