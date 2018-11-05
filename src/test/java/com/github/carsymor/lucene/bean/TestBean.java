package com.github.carsymor.lucene.bean;

import java.sql.Timestamp;
import java.util.Date;

public class TestBean {
    private String attr1;
    private int attr2;
    private Date attr3;
    private long attr4;
    private Timestamp attr5;

    public String getAttr1() {
        return attr1;
    }

    public void setAttr1(String attr1) {
        this.attr1 = attr1;
    }

    public int getAttr2() {
        return attr2;
    }

    public void setAttr2(int attr2) {
        this.attr2 = attr2;
    }

    public Date getAttr3() {
        return attr3;
    }

    public void setAttr3(Date attr3) {
        this.attr3 = attr3;
    }

    public long getAttr4() {
        return attr4;
    }

    public void setAttr4(long attr4) {
        this.attr4 = attr4;
    }

    public Timestamp getAttr5() {
        return attr5;
    }

    public void setAttr5(Timestamp attr5) {
        this.attr5 = attr5;
    }
}
