package cn.edu.fzu.sosd.web.lab.mysql.service.impl;

import cn.edu.fzu.sosd.web.lab.mysql.dto.UserDto;
import cn.edu.fzu.sosd.web.lab.mysql.service.UserService;

import java.util.Date;
import java.util.List;

public class UserServiceImpl implements UserService {

    @Override
    public UserDto save(UserDto userDto) {
        return null;
    }

    @Override
    public UserDto banById(long id) {
        return null;
    }

    @Override
    public UserDto getUserById(long id) {
        return null;
    }

    @Override
    public List<UserDto> getAllUser() {
        return List.of();
    }

    @Override
    public List<UserDto> searchUserByPrefix(String prefix) {
        return List.of();
    }

    @Override
    public List<UserDto> getUserByRole(String role) {
        return List.of();
    }

    @Override
    public List<UserDto> listUserByBirthdayInterval(Date start, Date end) {
        return List.of();
    }

    @Override
    public void removeAll() {

    }

    @Override
    public int userCount() {
        return 0;
    }
}
