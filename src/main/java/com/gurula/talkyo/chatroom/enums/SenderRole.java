package com.gurula.talkyo.chatroom.enums;

public enum SenderRole {
    HUMAN(1,"人類"), AI(2,"人工智慧");
    private int value;
    private String label;
    private SenderRole(int value,String label){
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
