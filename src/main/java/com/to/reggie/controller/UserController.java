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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController extends BaseController{

    @Autowired
    private IUserService iUserService;

    @Autowired
    private RedisTemplate redisTemplate;

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
        //session.setAttribute("code", code);

        //redis缓存优化
        redisTemplate.opsForValue().set("code", code, 5, TimeUnit.MINUTES);

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

        //Object codeInSession = session.getAttribute("code");

        //从redis缓存中获取验证码
        Object codeInSession = redisTemplate.opsForValue().get("code");

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
            session.setAttribute("userId", temp.getId());

            //登录成功删除缓存
            redisTemplate.delete("code");

            return R.success(temp);
        }
        throw new PasswordNotMatchException("用户名或验证码错误，登录失败");
    }

    /**
     * 登出
     * @param session
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpSession session) {
        session.removeAttribute("userId");
        return R.success("登出成功");
    }

}
