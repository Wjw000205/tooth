package org.example.tooth.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.example.tooth.DTO.UserDTO;
import org.example.tooth.Entity.UserEntity;
import org.example.tooth.Service.UserService;
import org.example.tooth.common.utils.R;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
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
    @PostMapping("/register")
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
    @PostMapping("/login")
    public R login(@Valid @RequestBody UserDTO req) {
        int userId = userService.login(req);
        if (userId != 0) {
            return R.ok("登陆成功").put("userId", userId);
        }
        return R.error("登陆失败，请检查用户名或密码是否正确");
    }

    /**
     * 登出功能感觉没啥用啊，前端页面跳转就可以了，这边放一个接口用于后续实现
     * @param userId
     * @return
     */
    @Operation(summary = "用户登出",description = "用户登出")
    @ApiResponses({
            @ApiResponse(responseCode = "0", description = "返回登出结果",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = R.class)))
    })
    @GetMapping("/logout/{userId}")
    public R logout(@PathVariable String userId) {
        return R.ok("登出成功");
    }
}
