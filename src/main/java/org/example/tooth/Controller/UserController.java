package org.example.tooth.Controller;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.example.tooth.Service.UserService;
import org.example.tooth.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Data
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/user/register")
    public R register(@RequestBody JSONObject jsonObject) {

        return R.ok().put("msg", "注册成功");
    }
}
