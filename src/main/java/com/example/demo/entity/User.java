package com.example.demo.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * user
 * @author 
 */
@Data
public class User implements Serializable {
    /**
     * 用户工号
     */
    private Integer id;

    /**
     * 密码
     */
    private String pwd;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 电话号码
     */
    private String phone;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Integer getId() {
        return id;
    }

    public String getPwd() {
        return pwd;
    }

    public String getRealName() {
        return realName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public Boolean getState() {
        return state;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 部门id
     */
    private Integer departmentId;

    /**
     * 用户状态
     */
    private Boolean state;

    private static final long serialVersionUID = 1L;
}