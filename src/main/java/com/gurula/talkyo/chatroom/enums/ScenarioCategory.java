package com.gurula.talkyo.chatroom.enums;

public enum ScenarioCategory {
    WORKPLACE(1,"職場");
    private int value;
    private String label;
    private ScenarioCategory(int value,String label){
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

