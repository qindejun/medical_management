package com.medical.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.medical.entity.Employee;
import com.medical.service.EmployeeService;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class JwtTokenUtils {

    private static EmployeeService staticEmployeeService;
    private static final Logger log = LoggerFactory.getLogger(JwtTokenUtils.class);

    @Resource
    private EmployeeService employeeService;

    @PostConstruct
    public void setUserService() {
        staticEmployeeService = employeeService;
    }
    /*
     *   生成Token
     */
    public static String generalToken(String userId, String password){

        return JWT.create().withAudience(userId)
                .withExpiresAt(DateUtil.offsetHour(new Date(),10))
                .sign(Algorithm.HMAC256(password));
    }

    /*
     *   获取当前的用户信息
     * */
    public static Employee getCurrentUser() {
        String token = null;
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            token = request.getHeader("token");
            if (StrUtil.isBlank(token)) {
                token = request.getParameter("token");
            }
            if (StrUtil.isBlank(token)) {
                log.error("获取当前登录的Token失败,token: {}", token);
                return null;
            }
            //解析Token,获取用户id
            String userId = Jwts.parser().parseClaimsJws(token).getBody().getAudience();
            return staticEmployeeService.findById(Integer.valueOf(userId));
        } catch (Exception e) {
            log.error("获取当前登录的管理员信息失败,token: {}", token, e);
            return null;
        }
    }


}
