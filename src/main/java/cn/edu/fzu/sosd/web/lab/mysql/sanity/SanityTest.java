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
        log.info("Sanity test start");

        // Step 1: Create a new user
        UserDto newUser = mockRandomUser();
        log.info("Creating new user: {}", newUser);
        UserDto savedUser = userService.save(newUser);
        log.info("Saved user: {}", savedUser);

        // Step 2: Fetch user by ID
        long userId = savedUser.getId();
        log.info("Fetching user by ID: {}", userId);
        UserDto fetchedUser = userService.getUserById(userId);
        log.info("Fetched user: {}", fetchedUser);

        // Step 3: Search user by prefix
        log.info("Searching users by prefix: {}", "test");
        List<UserDto> usersByPrefix = userService.searchUserByPrefix("test");
        log.info("Users found with prefix 'test': {}", usersByPrefix);

        // Step 4: Get all users
        log.info("Getting all users");
        List<UserDto> allUsers = userService.getAllUser();
        log.info("All users: {}", allUsers);

        // Step 5: Get users by role
        log.info("Getting users by role: {}", "admin");
        List<UserDto> usersByRole = userService.getUserByRole("admin");
        log.info("Users with role 'admin': {}", usersByRole);

        // Step 6: Search users by permission
        log.info("Searching users by permission: {}", "READ");
        List<UserDto> usersByPermission = userService.searchUserByPermission("READ");
        log.info("Users with permission 'READ': {}", usersByPermission);

        // Step 7: Get users by birthday interval
        Date startDate = new Date(0); // Unix epoch time
        Date endDate = new Date(); // Current date
        log.info("Getting users with birthdays between {} and {}", startDate, endDate);
        List<UserDto> usersByBirthdayInterval = userService.listUserByBirthdayInterval(startDate, endDate);
        log.info("Users with birthdays in interval: {}", usersByBirthdayInterval);

        // Step 8: Ban the created user by ID
        log.info("Banning user by ID: {}", userId);
        UserDto bannedUser = userService.banById(userId);
        log.info("Banned user: {}", bannedUser);

        log.info("Sanity test end");
    }

    public static void main(String[] args) throws ParseException {
        new SanityTest(LoggerFactory.getLogger(SanityTest.class), new UserServiceImpl()).process();
    }

}
