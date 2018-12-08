package com.mmall.controller;

import com.mmall.common.JsonData;
import com.mmall.exception.PermissionException;
import com.mmall.param.TestVo;
import com.mmall.util.BeanValidator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/test")
@Slf4j
public class TestController {
    private Logger log= LoggerFactory.getLogger(TestController.class);
    @RequestMapping("/hello.json")
    @ResponseBody
    public JsonData hello(){
        log.info("hello");
        return JsonData.success("hello,permission");
    }

    @RequestMapping("/validate.json")
    @ResponseBody
    public JsonData validate(TestVo vo){
        log.info("validate");
        try {
            Map<String,String> map= BeanValidator.validateObject(vo);
            if(map!=null&&map.entrySet().size()>0){
                for(Map.Entry<String,String> entry:map.entrySet()){
                    log.info("{}->{}",entry.getKey(),entry.getValue());
                    System.out.println(entry.getKey()+","+entry.getValue());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonData.success("test validate");
    }
}
