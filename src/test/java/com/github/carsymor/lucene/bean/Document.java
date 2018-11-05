package com.github.carsymor.lucene.bean;

import java.util.Date;

public class Document {
    private long pk;
    private String recordName;
    private String userName;
    private String account;
    private String fileName;
    private String fileContent;
    private Date checkTime;
    private boolean uploaded;
    private String reportId;
    private int matchTime;

    public long getPk() {
        return pk;
    }

    public void setPk(long pk) {
        this.pk = pk;
    }

    public String getRecordName() {
        return recordName;
    }

    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
    }

    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public int getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(int matchTime) {
        this.matchTime = matchTime;
    }

    @Override
    public String toString() {
        return "Document{" +
                "pk=" + pk +
                ", recordName='" + recordName + '\'' +
                ", userName='" + userName + '\'' +
                ", account='" + account + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileContent='" + fileContent + '\'' +
                ", checkTime=" + checkTime +
                ", uploaded=" + uploaded +
                ", reportId='" + reportId + '\'' +
                ", matchTime=" + matchTime +
                '}';
    }
}
