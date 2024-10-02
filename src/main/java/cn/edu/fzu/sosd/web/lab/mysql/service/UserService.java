package cn.edu.fzu.sosd.web.lab.mysql.service;

import cn.edu.fzu.sosd.web.lab.mysql.dto.UserDto;

import java.util.Date;
import java.util.List;

public interface UserService {

    UserDto save(UserDto userDto);

    UserDto banById(long id);

    UserDto getUserById(long id);

    /**
     * 分页返回所有User数据
     * 需要根据updateTime降序返回（新更新的数据排在前面）。
     * @param page 第几页，从0开始计数
     * @param size 每页有多少条数据
     * @return 该页的UserDto列表
     */
    List<UserDto> getAllUserByPageOrderByUpdateTime(int page, int size);

    List<UserDto> searchUserByPrefix(String prefix);

    UserDto appendRole(long id, String role);

    UserDto removeRole(long id, String role);

    List<UserDto> getUserByRole(String role);

    /**
     * 是开区间 () 不包含端点
     */
    List<UserDto> listUserByBirthdayInterval(Date start, Date end);

    void removeAll();

    int userCount();
}
