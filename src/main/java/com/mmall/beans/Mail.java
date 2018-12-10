package com.mmall.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@ToString
@Builder
//无参构造函数
@NoArgsConstructor
//全参的构造函数
@AllArgsConstructor
public class Mail {

    //邮件主题
    private String subject;
    //邮件信息
    private String message;
    //收件人，可能有多个
    private Set<String> receivers;
}
