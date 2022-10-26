package com.rubix.core.NFTController;

import java.util.Map;

public class NftRequestModel {
    private int type;
    private String buyerDid;
    private String sellerDid;
    private double amount;
    private String nftToken;
    private int racType;
    private String creatorDid;
    private long totalSupply;
    private String racComment;
    private String contentHash;
    private String url;
    private String pvtKeyPass;
    private String pvtKey;
    private String creatorPubKeyIpfsHash;
    private String sellerPubKeyIpfsHash;
    private String buyerPubKeyIpfsHash;
    private String saleContractIpfsHash;
    private String comment;
    private int returnKey;
    private Map<String, Object> creatorInput;
    private String transactionID;
    private String sDate;
    private String eDate;
    private int startRange;
    private int endRange;
    private String did;
    private int p2pFlag;
    private String userHash;
    private String keyType;

    public NftRequestModel() {
    }

    public String getTransactionID() {
        return transactionID;
    }

    public int getType() {
        return this.type;
    }

    public String getDid() {
        return did;
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

    public String getBuyerDid() {
        return this.buyerDid;
    }

    public String getSellerDid() {
        return this.sellerDid;
    }

    public double getAmount() {
        return this.amount;
    }

    public String getNftToken() {
        return this.nftToken;
    }

    public int getRacType() {
        return this.racType;
    }

    public String getCreatorDid() {
        return this.creatorDid;
    }

    public long getTotalSupply() {
        return this.totalSupply;
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

    public String getPvtKeyPass() {
        return this.pvtKeyPass;
    }

    public String getPvtKey() {
        return this.pvtKey;
    }

    public String getSellerPubKeyIpfsHash() {
        return this.sellerPubKeyIpfsHash;
    }

    public String getBuyerPubKeyIpfsHash() {
        return this.buyerPubKeyIpfsHash;
    }

    public String getSaleContractIpfsHash() {
        return this.saleContractIpfsHash;
    }

    public String getCreatorPubKeyIpfsHash() {
        return this.creatorPubKeyIpfsHash;
    }

    public String getComment() {
        return this.comment;
    }

    public int getReturnKey() {
        return this.returnKey;
    }

    public Map<String, Object> getCreatorInput() {
        return this.creatorInput;
    }

    public int getP2pFlag() {
        return this.p2pFlag;
    }

    public String getUserHash() {
        return this.userHash;
    }

    public String getKeyType() {
        return this.keyType;
    }
}
