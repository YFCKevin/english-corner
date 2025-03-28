package com.gurula.talkyo.chatroom.enums;

public enum ChatroomType {
    PROJECT(1,"學習計畫"),
    SITUATION(2,"情境"),
    FREE_TALK(3,"自由對話"),
    IMAGE(4,"圖片描述");

    private int value;
    private String label;
    private ChatroomType(int value, String label){
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
