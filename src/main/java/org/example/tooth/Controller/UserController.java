package org.example.tooth.Controller;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.Data;
import org.example.tooth.Entity.UserEntity;
import org.example.tooth.Service.UserService;
import org.example.tooth.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "用户注册", description = "提交用户名与密码进行注册")
    @ApiResponses({
            @ApiResponse(responseCode = "0", description = "返回注册结果",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = R.class)))
    })
    @PostMapping("/user/register")
    public R register(@Valid @RequestBody UserEntity req) {
        int ok = userService.register(req.getUserName(), req.getPassword());

        if (ok == 1) {
            return R.ok("注册成功");
        }
        else if(ok == 2){
            return R.ok("注册失败，用户名已存在");
        }
        return R.ok().put("msg", "注册失败");
    }

    @Operation(summary = "用户登录",description = "用户通过用户名和密码进行登录")
    @ApiResponses({
            @ApiResponse(responseCode = "0", description = "返回登录结果",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = R.class)))
    })
    @PostMapping("/user/login")
    public R login(@Valid @RequestBody UserEntity req) {
        if (userService.login(req)) {
            return R.ok("登陆成功");
        }
        return R.error("登陆失败，请检查用户名或密码是否正确");
    }

    @Operation(summary = "用户登出",description = "用户登出")
    @ApiResponses({
            @ApiResponse(responseCode = "0", description = "返回登出结果",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = R.class)))
    })
    @GetMapping("/user/logout/{userId}")
    public R logout(@PathVariable String userId) {
        return R.ok("登出成功");
    }
}
