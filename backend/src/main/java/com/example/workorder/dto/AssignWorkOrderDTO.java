package com.example.workorder.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AssignWorkOrderDTO {

    @NotBlank(message = "处理人不能为空")
    private String processor;
}
