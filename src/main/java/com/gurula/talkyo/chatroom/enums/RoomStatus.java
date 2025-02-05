package com.gurula.talkyo.chatroom.enums;

public enum RoomStatus {
    ACTIVE(1,"進行中"), CLOSED(2,"已關閉");

    private int value;
    private String label;
    private RoomStatus(int value,String label){
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
