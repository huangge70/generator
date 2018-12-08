package com.mmall.param;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
@Getter
@Setter
public class TestVo {
    @NotNull
    private String msg;
    @NotNull(message = "id不能为空")
    @Max(value = 10,message = "id不能大于10")
    @Min(value = 0,message = "id至少要大于等于0")
    private Integer id;
}
