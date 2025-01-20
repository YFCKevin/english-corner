package com.gurula.talkyo.course.enums;

public enum SentenceLevel {
    BASIC(1,"基礎"), ADVANCED(2,"進階");
    private int value;
    private String label;
    private SentenceLevel(int value,String label){
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
