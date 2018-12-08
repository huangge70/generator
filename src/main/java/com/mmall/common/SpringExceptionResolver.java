package com.mmall.common;

import com.mmall.exception.PermissionException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Slf4j
public class SpringExceptionResolver implements HandlerExceptionResolver {
    private Logger log=LoggerFactory.getLogger(SpringExceptionResolver.class);
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) {
        String url=request.getRequestURL().toString();
        ModelAndView mv;
        String defaultMsg="System error";
        //请求json数据的请求全部以.json结尾，请求页面的请求全部以.page结尾
        if(url.endsWith(".json")){
            if(e instanceof PermissionException){
              JsonData result=JsonData.fail(e.getMessage()) ;
              mv=new ModelAndView("jsonView",result.toMap());
            }else{
                log.error("unknowen json exception url:"+url,e);
                JsonData result=JsonData.fail(defaultMsg) ;
                mv=new ModelAndView("jsonView",result.toMap());
            }
        }else if(url.endsWith(".page")){
            log.error("unknowen page exception url:"+url,e);
            JsonData result=JsonData.fail(defaultMsg);
            mv=new ModelAndView("exception",result.toMap());
        }else{
            log.error("unknowen exception url:"+url,e);
            JsonData result=JsonData.fail(defaultMsg);
            mv=new ModelAndView("exception",result.toMap());
        }
        return null;
    }
}
