package com.mmall.filter;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.mmall.common.ApplicationContextHelper;
import com.mmall.common.JsonData;
import com.mmall.common.RequestHolder;
import com.mmall.model.SysUser;
import com.mmall.service.SysCoreService;
import com.mmall.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
@Slf4j
public class AclControFilter implements Filter {
    //用于存放不需要拦截的url
    private static Set<String> exclusionUrlSet=Sets.newConcurrentHashSet();
    private final static String noAuthUrl="/sys/user/noAuth.page";
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String exclusionUrl=filterConfig.getInitParameter("exclusionUrls");
        //trimResults:去掉前后的空格；omitEmptyStrings：去掉空字符串;
        List<String> exclusionUrlList= Splitter.on(",").trimResults().omitEmptyStrings().splitToList(exclusionUrl);
        exclusionUrlSet= Sets.newConcurrentHashSet(exclusionUrlList);
        exclusionUrlSet.add(noAuthUrl);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request= (HttpServletRequest) servletRequest;
        HttpServletResponse response= (HttpServletResponse) servletResponse;
        String servletPath=request.getServletPath();
        Map requestMap=request.getParameterMap();
        if(exclusionUrlSet.contains(servletPath)){
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }
        SysUser sysUser= RequestHolder.getCurrentUser();
        if(sysUser==null){
            log.info("someone visit {},but no login,parameter:{}",servletPath, JsonMapper.obj2String(requestMap));
            noAuth(request,response);
            return;
        }
        SysCoreService sysCoreService= ApplicationContextHelper.popBean(SysCoreService.class);
        if(!sysCoreService.hasUrlAcl(servletPath)){
            log.info("{} visit {}",JsonMapper.obj2String(sysUser),servletPath);
            noAuth(request,response);
            return;
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    private void noAuth(HttpServletRequest request,HttpServletResponse response) throws IOException {
        String servletPath=request.getServletPath();
        if(servletPath.endsWith(".json")){
            JsonData jsonData=JsonData.fail("没有访问权限");
            response.setHeader("Content-Type","application/json");
            response.getWriter().print(JsonMapper.obj2String(jsonData));
            return;
        }else{
            clientRedirect(noAuthUrl,response);
            return;
        }
    }
    private void clientRedirect(String url,HttpServletResponse response) throws IOException {
        response.setHeader("Content-Type","text/html");
        response.sendRedirect(url);
    }
    @Override
    public void destroy() {

    }
}
