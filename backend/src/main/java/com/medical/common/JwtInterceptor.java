package com.medical.common;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.medical.entity.Employee;
import com.medical.exception.CustomException;
import com.medical.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(JwtInterceptor.class);

    @Resource
    private EmployeeService employeeService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 放行 OPTIONS 请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
            response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "token,Content-Type");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setStatus(HttpServletResponse.SC_OK);
            return false; // 预检请求不继续处理
        }
        //1.获取token
        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)) {
            token = request.getParameter("token");
        }
        //2.开始执行认证
        if (StrUtil.isBlank(token)) {
            throw new CustomException("无token,请重新登录");
        }
        //获取token的userId
        String userId;
        Employee employee;
        try {
            userId = JWT.decode(token).getAudience().get(0);
            //根据token的userId查询数据库
            employee = employeeService.findById(Integer.valueOf(userId));

        } catch (Exception e) {
            String errMsg = "token验证失败,请重新登录!";
            log.error(errMsg + ",token = " + token, e);
            throw new CustomException(errMsg);
        }
        if (employee == null) {
            throw new CustomException("用户不存在,请重新登录!");
        }
        try {
            //验证token
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(employee.getPassword())).build();
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e){
            throw new CustomException("token验证失败,请重新登录!");
        }
        log.info("token验证成功,允许放行");
        return true;
    }

}
