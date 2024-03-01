package com.exchange.service;

public enum Currency {
    USD(840), EUR(978);

    private final int code;
    Currency(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
