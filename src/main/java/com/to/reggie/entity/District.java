package com.to.reggie.entity;

import lombok.Data;

//省市区 信息类
@Data
public class District {
    private Integer id;
    private String parent;
    private String code;
    private String name;
}
