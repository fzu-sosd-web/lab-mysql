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

        // Step 1:
        if (verifySave() == false) {
            log.info(">>> Sanity test end >>>");
            return;
        }

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

        // Step 6: Search users by permission
        log.info("Searching users by permission: {}", "READ");
        List<UserDto> usersByPermission = userService.searchUserByPermission("READ");
        assert usersByPermission != null : "Users by permission should not be null";
        log.info("Users with permission 'READ': {}", usersByPermission);

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

    public boolean verifySave() {
        log.info("Test: verify save to db and generate unique key.");
        UserDto input = mockInput();
        UserDto saved = userService.save(input);
        boolean match = infoMatch(saved, input);
        if (false == match) {
            log.error("FAIL: save failed, input: {}, saved: {}", input, saved);
            return false;
        }
        long key = input.getId();
        if (key == 0L) {
            log.error("FAIL: primary generate failed");
            return false;
        }
        UserDto userById = userService.getUserById(key);
        match = infoMatch(userById, saved);
        if (false == match) {
            log.error("FAIL: failed to locate user by key:{}", key);
            return false;
        }
        log.info("PASS");
        return true;
    }


    public static void main(String[] args) throws ParseException {
        new SanityTest(LoggerFactory.getLogger(SanityTest.class), new UserServiceImpl()).process();
    }

}
