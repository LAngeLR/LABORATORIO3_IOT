package com.example.laboratorio3_iot.dto;

import java.io.Serializable;

public class Ratings implements Serializable {
    private String Source;
    private String Value;

    public String getSource() {
        return Source;
    }

    public void setSource(String source) {
        Source = source;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }
}
