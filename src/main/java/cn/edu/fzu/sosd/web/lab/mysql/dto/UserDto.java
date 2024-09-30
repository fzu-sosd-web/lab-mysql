package cn.edu.fzu.sosd.web.lab.mysql.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private String avatar;
    private Integer status;
    private Date birthday;
    private Date updateTime;
    private List<String> roles;
    private List<String> permissions;


}
