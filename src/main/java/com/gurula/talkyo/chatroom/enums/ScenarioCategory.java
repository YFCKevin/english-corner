package com.gurula.talkyo.chatroom.enums;

public enum ScenarioCategory {
    WORKPLACE(1,"職場"),
    FRIENDSHIP(2,"友誼"),
    SHOPPING(3,"購物"),
    TRANSPORTATION(4,"交通"),
    TRAVEL(5,"旅遊");

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

