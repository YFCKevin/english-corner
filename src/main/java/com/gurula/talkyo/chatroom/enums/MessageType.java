package com.gurula.talkyo.chatroom.enums;

public enum MessageType {
    AUDIO(1,"語音"),
    TEXT(2,"文字"),
    IMAGE(3,"圖片");

    private int value;
    private String label;
    private MessageType(int value,String label){
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
