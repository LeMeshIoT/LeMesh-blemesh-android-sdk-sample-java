package com.example.leshidemo1.ui.bean;

/**
 * 设备dps集合
 */
public class DpsBean {
    private int id;
    private int type;
    private String name;
    private String value;
    public DpsBean(int id, int type, String name,String value) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
