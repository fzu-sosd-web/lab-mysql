package cn.edu.fzu.sosd.web.lab.mysql.sanity;

import cn.edu.fzu.sosd.web.lab.mysql.dto.UserDto;
import cn.edu.fzu.sosd.web.lab.mysql.service.UserService;
import cn.edu.fzu.sosd.web.lab.mysql.service.impl.UserServiceImpl;
import cn.edu.fzu.sosd.web.lab.mysql.test.Harness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SanityTest extends Harness {

    private final Logger log;
    private final UserService userService;

    private List<UserDto> users;
    private Map<String, List<UserDto>> roleUserMap;

    public SanityTest(Logger log, UserService userService) {
        super(log);
        this.log = log;
        this.userService = userService;
    }

    public void process() throws ParseException {
        log.info(">>> Sanity test start >>>");
        userService.removeAll();

        // Step 1:
        verifySave();

        // Step 3: Search user by prefix
        log.info("Searching users by prefix: {}", "test");
        List<UserDto> usersByPrefix = userService.searchUserByPrefix("test");
        assert usersByPrefix != null : "Users by prefix should not be null";
        assert usersByPrefix.size() > 0 : "Users found with prefix 'test' should be greater than 0";
        log.info("Users found with prefix 'test': {}", usersByPrefix);

        // Step 4: Get all users
        log.info("Getting all users");
        List<UserDto> allUsers = userService.getAllUser();
        assert allUsers != null : "All users should not be null";
        assert allUsers.size() > 0 : "All users should be greater than 0";
        log.info("All users: {}", allUsers);

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
        log.info("Banning user by ID: {}", userId);
        UserDto bannedUser = userService.banById(userId);
        assert bannedUser != null : "Banned user should not be null";
        assert bannedUser.isBanned() : "Banned user should be marked as banned";
        log.info("Banned user: {}", bannedUser);

        log.info("Sanity test end");
    }

    void verifySave() {
        clear();

        log.info("Test: verify save to db and generate unique key.");
        UserDto input = mockInput();
        UserDto saved = userService.save(input);
        boolean match = infoMatch(saved, input);
        if (false == match) {
            log.error("FAIL: save failed, input: {}, saved: {}", input, saved);
            System.exit(-1);
        }
        long key = input.getId();
        if (key == 0L) {
            log.error("FAIL: primary generate failed");
            System.exit(-1);
        }
        UserDto got = userService.getUserById(key);
        match = infoMatch(got, saved);
        if (false == match) {
            log.error("FAIL: failed to locate user by key:{}", key);
            System.exit(-1);
        }
        String updateName = "ywj";
        saved.setUsername(updateName);
        userService.save(saved);
        got = userService.getUserById(key);
        match = infoMatch(got, saved);
        if (false == match) {
            log.error("FAIL: failed to update user info, should be:{}, but:{}", saved.getUsername(), got.getUsername());
            System.exit(-1);
        }

        log.info("PASS");
    }

    void verifyPrefixSearch() {
        log.info("Test: verify prefix search.");

    }

    void clear() {
        log.info("Must Done: clear all user.");
        userService.removeAll();
        int userCount = userService.userCount();
        if (userCount != 0) {
            log.error("FAIL: user count: {}, clear unsuccessful", userCount);
            System.exit(-1);
        }
        log.info("Done.");
    }


    public static void main(String[] args) throws ParseException {
        new SanityTest(LoggerFactory.getLogger(SanityTest.class), new UserServiceImpl()).process();
    }

}
