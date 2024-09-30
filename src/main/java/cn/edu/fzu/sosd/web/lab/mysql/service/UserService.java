package cn.edu.fzu.sosd.web.lab.mysql.service;

import cn.edu.fzu.sosd.web.lab.mysql.dto.UserDto;

import java.util.Date;
import java.util.List;

public interface UserService {

    UserDto save(UserDto userDto);

    UserDto banById(long id);

    UserDto getUserById(long id);

    List<UserDto> getAllUser();

    List<UserDto> searchUserByPrefix(String prefix);

    List<UserDto> getUserByRole(String role);

    List<UserDto> searchUserByPermission(String permission);

    List<UserDto> listUserByBirthdayInterval(Date start, Date end);
}
