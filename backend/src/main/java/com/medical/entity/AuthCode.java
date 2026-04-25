package com.medical.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthCode {
    private Integer codeId;
    private String authCode;
    private String name;
    private String position;
    private String department;
    private Integer role;
    private Byte status;
    private Integer usedBy;
    private LocalDateTime usedAt;
}
