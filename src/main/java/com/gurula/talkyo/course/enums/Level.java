package com.gurula.talkyo.course.enums;

public enum Level {
    EASY(1,"簡單"), MEDIUM(2,"中等"),DIFFICULT(3,"困難");
    private int value;
    private String label;
    private Level(int value,String label){
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
