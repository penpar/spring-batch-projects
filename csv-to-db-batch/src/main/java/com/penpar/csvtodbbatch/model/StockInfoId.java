package com.penpar.csvtodbbatch.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class StockInfoId implements Serializable {
    private String basDt;
    private String srtnCd;
}