package com.penpar.apitocsvbatch.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {
    // private String basDt;
    // private String srtnCd;
    // private String isinCd;
    // private String mrktCtg;
    // private String itmsNm;
    // private String crno;
    // private String corpNm;

    private String basDt;
    private String srtnCd;
    private String isinCd;
    private String itmsNm;
    private String mrktCtg;
    private int clpr;
    private int vs;
    private double fltRt;
    private int mkp;
    private int hipr;
    private int lopr;
    private int trqu;
    private long trPrc;
    private long lstgStCnt;
    private long mrktTotAmt;
    
    public Item() {
    }

    public Item(String basDt, String srtnCd, String isinCd, String itmsNm, String mrktCtg, int clpr, int vs,
            double fltRt, int mkp, int hipr, int lopr, int trqu, long trPrc, long lstgStCnt, long mrktTotAmt) {
        this.basDt = basDt;
        this.srtnCd = srtnCd;
        this.isinCd = isinCd;
        this.itmsNm = itmsNm;
        this.mrktCtg = mrktCtg;
        this.clpr = clpr;
        this.vs = vs;
        this.fltRt = fltRt;
        this.mkp = mkp;
        this.hipr = hipr;
        this.lopr = lopr;
        this.trqu = trqu;
        this.trPrc = trPrc;
        this.lstgStCnt = lstgStCnt;
        this.mrktTotAmt = mrktTotAmt;
    }

}