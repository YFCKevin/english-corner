package com.gurula.talkyo.snapshot.enums;

public enum SnapshotType {
    CHAT(1,"對話");

    private int value;
    private String label;
    private SnapshotType(int value,String label){
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
