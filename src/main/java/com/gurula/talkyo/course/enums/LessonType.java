package com.gurula.talkyo.course.enums;

public enum LessonType {
    NOT_STARTED(1,"尚未開始"),
    IN_PROGRESS(2,"進行中"),
    COMPLETED(3,"已完課");

    private int value;
    private String label;
    private LessonType(int value,String label){
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

