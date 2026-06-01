package cn.example.tenant.demo.usr.controller.org.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrgListRO {
    private Long id;
    private String orgName;
    private String orgShortName;
    private String orgRemark;
    private Integer orgStatus;

    /** Jackson 对 {@code getRAddTime()} 默认会序列化成 {@code raddTime}，需显式指定 */
    @JsonProperty("rAddTime")
    private LocalDateTime rAddTime;
}
