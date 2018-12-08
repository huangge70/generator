package com.mmall.param;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
@Getter
@Setter
public class TestVo {
    @NotNull
    private String msg;
    @NotNull
    private Integer id;
}
