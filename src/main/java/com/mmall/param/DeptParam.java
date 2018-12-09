package com.mmall.param;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class DeptParam {
    private Integer id;

    @NotNull(message = "部门名称不能为空")
    @Length(max=15,min=2,message = "部门名称需要在2-15个字之间")
    private String name;

    //默认值为0
    private Integer parentId=0;

    @NotNull(message = "展示顺序不能为空")
    private Integer seq;

    @Length(max = 150,message = "备注长度不能超过150字")
    private String remark;
}
