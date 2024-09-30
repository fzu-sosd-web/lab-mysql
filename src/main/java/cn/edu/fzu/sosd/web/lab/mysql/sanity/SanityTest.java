package cn.edu.fzu.sosd.web.lab.mysql.sanity;

import cn.edu.fzu.sosd.web.lab.mysql.dto.UserDto;
import cn.edu.fzu.sosd.web.lab.mysql.service.UserService;
import cn.edu.fzu.sosd.web.lab.mysql.service.impl.UserServiceImpl;
import cn.edu.fzu.sosd.web.lab.mysql.test.Harness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;

public class SanityTest extends Harness {

    private final Logger log;

    private final UserService userService;

    public SanityTest(Logger log, UserService userService) {
        super(log);
        this.log = log;
        this.userService = userService;
    }

    public void process() throws ParseException {

        UserDto userDto = mockRandomUser();
        log.info("userDto:{}", userDto);

    }

    public static void main(String[] args) throws ParseException {
        new SanityTest(LoggerFactory.getLogger(SanityTest.class), new UserServiceImpl()).process();
    }

}
