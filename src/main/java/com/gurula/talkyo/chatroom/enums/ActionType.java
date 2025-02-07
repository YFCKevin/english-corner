package com.gurula.talkyo.chatroom.enums;

public enum ActionType {
    CREATE(1,"創建"),
    EDIT(2,"編輯"),
    LEGACY(3, "沿用");
    private int value;
    private String label;

    private ActionType(int value, String label){
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
