package com.sanli.mallsystem.form;

import com.sanli.mallsystem.controller.CheckGroup.login;
import com.sanli.mallsystem.controller.CheckGroup.registry;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NotNull(groups = {registry.class} , message = "用户不能为空")
public class UserForm {
    private Integer id;

    @NotBlank
    @NotNull(groups = {registry.class , login.class} , message = "用户名不能为空")
    private String username;

    @NotBlank
    @NotNull(groups = {registry.class , login.class} , message = "密码不能为空")
    private String password;

    @NotNull(groups = {registry.class} , message = "邮箱不能为空")
    @Email(groups = {registry.class} , message = "邮箱非法")
    private String email;

    private String phone;

    private String question;

    private String answer;

    @NotNull(groups = {registry.class} , message = "角色类型不能为空")
    private Integer role;

    private Date createTime;

    private Date updateTime;

    public UserForm() {
    }

    public UserForm(String username, String password, String email, Integer role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }
}
