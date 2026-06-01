package cn.example.tenant.demo.usr.controller.person.dto;

import lombok.Data;

@Data
public class PersonListRO {
    private Long id;
    private Long orgId;
    private String realName;
    private String mobile;
    private String email;
    private Integer personStatus;
}
