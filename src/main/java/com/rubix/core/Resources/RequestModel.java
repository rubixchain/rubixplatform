package com.rubix.core.Resources;

import org.json.JSONArray;

import static com.rubix.Resources.Functions.getOsName;
import static com.rubix.Resources.Functions.getSystemUser;

public class RequestModel {
    private String peerid;
    private int value;
    private String receiver;
    private String sender;
    private JSONArray quorum;
    private int tokenCount;
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
    private String buyer;
    private int amount;
    private String nftToken;
    private int ractype;
    private String creatorDid;
    private long totalSupply;
    private long tokenNo;
    private String racComment;
    private String contentHash;
    private String url;
    private String pvtKey;

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

    public int getTokenCount() {
        return tokenCount;
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

    public String getBuyer() {
        return this.buyer;
    }

    public int getAmount() {
        return this.amount;
    }

    public String getNftToken() {
        return this.nftToken;
    }

    public int getRactype() {
        return this.ractype;
    }

    public String getCreatorDid() {
        return this.creatorDid;
    }

    public long getTotalSupply() {
        return this.totalSupply;
    }

    public long getTokenNo() {
        return this.tokenNo;
    }

    public String getRacComment() {
        return this.racComment;
    }

    public String getContentHash() {
        return this.contentHash;
    }

    public String getUrl() {
        return this.url;
    }

    public String getPvtKey() {
        return this.pvtKey;
    }

}