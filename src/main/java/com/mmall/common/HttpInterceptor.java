package com.mmall.common;

import com.mmall.util.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class HttpInterceptor extends HandlerInterceptorAdapter {
    private static Logger log= LoggerFactory.getLogger(HttpInterceptor.class);
    private static final String START_TIME="requestStartTime";
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url=request.getRequestURI().toString();
        Map parameterMap=request.getParameterMap();
        log.info("request start:url:{},param:{}",url, JsonMapper.obj2String(parameterMap));
        long start=System.currentTimeMillis();
        request.setAttribute(START_TIME,start);
        return true;
    }

    @Override
    //正常处理请求的时候调用
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String url=request.getRequestURI().toString();
        Map parameterMap=request.getParameterMap();
        long start= (long) request.getAttribute(START_TIME);
        long end=System.currentTimeMillis();
        log.info("request finish:url:{},param:{}",url, JsonMapper.obj2String(parameterMap));
    }

    @Override
    //无论什么情况都会调用
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String url=request.getRequestURI().toString();
        Map parameterMap=request.getParameterMap();
        log.info("url:{},param:{}",url, JsonMapper.obj2String(parameterMap));

        removeThreadLocalInfo();
    }

    public void removeThreadLocalInfo(){
        RequestHolder.remove();
    }
}
