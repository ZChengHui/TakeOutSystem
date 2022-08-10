package com.to.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.to.reggie.common.R;
import com.to.reggie.common.SMSUtils;
import com.to.reggie.common.ValidateCodeUtils;
import com.to.reggie.entity.User;
import com.to.reggie.service.IUserService;
import com.to.reggie.service.ex.PasswordNotMatchException;
import com.to.reggie.service.ex.PhoneNotFoundException;
import com.to.reggie.service.ex.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    public IUserService iUserService;

    /**
     * 发送验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        //获取手机号
        String phone = user.getPhone();
        if (phone == null) {
            throw new PhoneNotFoundException("手机号不存在");
        }
        //生成四位验证码
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
        log.info("code={}",code);
        //发送短信
        //SMSUtils.sendMessage("阿里云短信测试","SMS_154950909", phone, code);
        //保存验证码
        session.setAttribute("code", code);
        return R.success("手机验证码已发送");
    }

    /**
     * 用户登录
     * @return
     */
    @PostMapping("/login")//巧用Map封装JSON数据
    public R<User> login(@RequestBody Map map, HttpSession session) {
        //log.info(map.toString());
        //获取手机号和验证码
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        Object codeInSession = session.getAttribute("code");
        //与session中数据比较
        if (codeInSession != null && codeInSession.equals(code)) {
            //比对成功
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User temp = iUserService.getOne(queryWrapper);
            //如果是新用户
            if (temp == null) {
                //自动注册
                temp = new User();
                temp.setPhone(phone);
                iUserService.save(temp);
            }
            session.setAttribute("userPhone", temp.getPhone());
            return R.success(temp);
        }
        throw new PasswordNotMatchException("用户名或验证码错误，登录失败");
    }

}
