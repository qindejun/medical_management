package com.medical.controller;


import com.medical.common.CaptureConfig;
import com.medical.entity.AuthCode;
import com.medical.entity.Employee;
import com.medical.entity.Login;
import com.medical.entity.Result;
import com.medical.service.LoginService;
import com.medical.utils.JwtTokenUtils;
import com.wf.captcha.utils.CaptchaUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;

@Slf4j
@RestController
@RequestMapping
@CrossOrigin
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public Result Login(@RequestBody Employee employee, @RequestParam String key ,HttpServletRequest request) throws NoSuchAlgorithmException {
        log.info("收到的验证码为:{}",employee.getVerCode());
        if(!employee.getVerCode().toLowerCase().equals(CaptureConfig.CAPTURE_MAP.get(key))){
            CaptchaUtil.clear(request);
            return Result.error("验证码不正确!");
        }
        Employee emp = loginService.Login(employee);
        CaptureConfig.CAPTURE_MAP.remove(key);
        return Result.success(emp);
    }

    @PostMapping("/login/authcode")
    public Result LoginAuthCode(@RequestBody AuthCode authCode) {
        AuthCode code = loginService.selectRole(authCode);
        return Result.success(code);
    }

    @PostMapping("/login/register")
    public Result Register(@RequestBody Employee employee){
        Employee emp = loginService.Register(employee);
        return Result.success(emp);
    }

}
