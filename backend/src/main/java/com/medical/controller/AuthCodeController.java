package com.medical.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.medical.entity.AuthCode;
import com.medical.entity.Device;
import com.medical.entity.Params;
import com.medical.entity.Result;
import com.medical.service.AuthCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/authcode")
@CrossOrigin
public class AuthCodeController {

    @Autowired
    private AuthCodeService authCodeService;

    @GetMapping
    public Result findAll(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "5") Integer pageSize){
        log.info("参数为,{},{}",pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        List<AuthCode> list = authCodeService.findAll();
        PageInfo<AuthCode> pageInfo = new PageInfo<>(list);
        return Result.success(pageInfo);
    }

    @GetMapping("/search")
    public Result findAll(Params params){
        log.info("参数为,{}",params);
        PageInfo<AuthCode> pageInfo = authCodeService.search(params);
        return Result.success(pageInfo);
    }

    @PutMapping("/update")
    public Result update(@RequestBody Params params) {
        log.info("修改参数为,{}", params);
        authCodeService.update(params);
        // 立即查询最新数据
        PageInfo<AuthCode> pageInfo = authCodeService.search(new Params());
        log.info("更新后查询数据: {}", pageInfo.getList());
        return Result.success(pageInfo);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Integer role){
        log.info("新增的角色为,{}",role);
        authCodeService.add(role);
        return Result.success();
    }

    @DeleteMapping("/{codeId}")
    public Result deleteById(@PathVariable Integer codeId){
        log.info("要删除的授权码Id为,{}",codeId);
        authCodeService.deleteById(codeId);
        return Result.success();
    }

    @PutMapping("/batch")
    public Result batchDelete(@RequestBody ArrayList<Integer> ids){
        log.info("批量删除,接收到的id为,{}",ids);
        for(Integer id : ids ){
            authCodeService.deleteById(id);
        }
        return Result.success();
    }
}
