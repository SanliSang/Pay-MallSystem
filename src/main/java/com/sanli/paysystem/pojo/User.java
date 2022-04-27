package com.sanli.paysystem.pojo;

import com.sanli.paysystem.controller.CheckGroup.login;
import com.sanli.paysystem.controller.CheckGroup.registry;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@NotNull(groups = {registry.class} , message = "用户不能为空")
public class User {
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

    public User() {
    }

    public User(String username, String password, String email, Integer role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}