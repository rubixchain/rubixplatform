package com.rubix.core.Resources;

import org.json.JSONArray;

public class RequestModel {
    private String peerid;
    private int value;
    private String receiver;
    private String sender;
    private JSONArray quorum;
    private double tokenCount;
    private double amount;
    private String transactionID;
    private String threadExt;
    private String comment;
    private String token;
    private int txnCount;
    private String sDate;
    private String eDate;
    private String groupId;
    private String did;
    private int startRange;
    private int endRange;
    private int type;

    public RequestModel() {
    }

    public String getGroupId() {
        return groupId;
    }

    public String getToken() {
        return token;
    }

    public String getThreadExt() {
        return threadExt;
    }

    public String getComment() {
        return comment;
    }

    public double getTokenCount() {
        return tokenCount;
    }

    public double getAmount() {
        return amount;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public int getTxnCount() {
        return txnCount;
    }

    public int getType() {
        return type;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSender() {
        return sender;
    }

    public String getDid() {
        return did;
    }

    public int getValue() {
        return value;
    }

    public JSONArray getQuorum() {
        return quorum;
    }

    public String getsDate() {
        return sDate;
    }

    public String geteDate() {
        return eDate;
    }

    public int getStartRange() {
        return startRange;
    }

    public int getEndRange() {
        return endRange;
    }

    public String getPeerid() {
        return peerid;
    }

}