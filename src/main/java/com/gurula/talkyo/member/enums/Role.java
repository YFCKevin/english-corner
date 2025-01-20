package com.gurula.talkyo.member.enums;

public enum Role {
    ADMIN(1,"管理員"),
    STUDENT(2,"學員");

    private int value;
    private String label;
    private Role(int value,String label){
        this.value = value;
        this.label = label;
    }
    public int getValue() {
        return value;
    }
    private void setValue(int value) {
        this.value = value;
    }
    public String getLabel() {
        return label;
    }
    private void setLabel(String label) {
        this.label = label;
    }
}