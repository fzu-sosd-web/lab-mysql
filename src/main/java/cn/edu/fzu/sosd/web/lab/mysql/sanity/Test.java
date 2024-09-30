package cn.edu.fzu.sosd.web.lab.mysql.sanity;

import cn.edu.fzu.sosd.web.lab.mysql.dto.UserDto;
import cn.edu.fzu.sosd.web.lab.mysql.service.UserService;
import cn.edu.fzu.sosd.web.lab.mysql.service.impl.NoopUserService;
import cn.edu.fzu.sosd.web.lab.mysql.test.Harness;

import java.text.ParseException;

public class Test extends Harness {

    private final UserService userService;

    public Test(UserService userService) {
        super();
        this.userService = userService;
    }

    public void process() throws ParseException {
        UserDto userDto = mockRandomUser();

        System.out.println(userDto);
    }

    public static void main(String[] args) throws ParseException {
        new Test(new NoopUserService()).process();
    }

}
