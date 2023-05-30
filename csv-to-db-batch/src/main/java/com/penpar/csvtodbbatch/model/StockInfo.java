package com.penpar.csvtodbbatch.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "stock_info")
@IdClass(StockInfoId.class)
public class StockInfo implements Serializable {

    @Id
    @Column(name = "bas_dt")
    private String basDt;
    
    @Id
    @Column(name = "srtn_cd")
    private String srtnCd;

    @Column(name = "isin_cd")
    private String isinCd;

    @Column(name = "itms_nm")
    private String itmsNm;

    @Column(name = "mrkt_ctg")
    private String mrktCtg;

    @Column(name = "clpr")
    private Integer clpr;

    @Column(name = "vs")
    private Integer vs;

    @Column(name = "flt_rt")
    private Double fltRt;

    @Column(name = "mkp")
    private Integer mkp;

    @Column(name = "hipr")
    private Integer hipr;

    @Column(name = "lopr")
    private Integer lopr;

    @Column(name = "trqu")
    private Integer trqu;

    @Column(name = "tr_prc")
    private Long trPrc;

    @Column(name = "lstg_st_cnt")
    private Long lstgStCnt;

    @Column(name = "mrkt_tot_amt")
    private Long mrktTotAmt;

    @Override
    public String toString() {
        return "StockInfo [basDt=" + basDt + ", srtnCd=" + srtnCd + ", isinCd=" + isinCd + ", itmsNm=" + itmsNm
                + ", mrktCtg=" + mrktCtg + ", clpr=" + clpr + ", vs=" + vs + ", fltRt=" + fltRt + ", mkp=" + mkp
                + ", hipr=" + hipr + ", lopr=" + lopr + ", trqu=" + trqu + ", trPrc=" + trPrc + ", lstgStCnt="
                + lstgStCnt + ", mrktTotAmt=" + mrktTotAmt + "]";
    }

    
}
