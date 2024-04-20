package com.example.laboratorio3_iot.dto;

public class NumeroPrimo {
    private int number;
    private int order;

    public NumeroPrimo(int number, int order) {
        this.number = number;
        this.order = order;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
