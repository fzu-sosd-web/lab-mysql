package cn.edu.fzu.sosd.web.lab.mysql.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    // id should be auto-increment
    private long id;
    private String username;
    private String password;
    private String avatar;
    private Integer status;
    private Date birthday;
    private Date updateTime;
    private List<String> roles;

    public String roles() {
        StringBuilder sb = new StringBuilder();
        Collections.sort(roles);
        for (String role : roles) {
            sb.append(role).append(",");
        }
        return sb.toString();
    }

    public boolean isBanned() {
        return status == -1;
    }

}
