package com.rubix.core.sender;

import org.json.JSONArray;

import static com.rubix.Resources.Functions.getOsName;
import static com.rubix.Resources.Functions.getSystemUser;

public class Details {
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



    public Details(){}

    public String getGroupId(){return groupId;}
    public String getToken(){return token;}
    public String getThreadExt(){ return threadExt;}
    public String getComment(){return comment;}
    public int getTokenCount(){ return tokenCount; }
    public String getTransactionID(){return transactionID;}
    public int getTxnCount(){return txnCount;}
    public String getReceiver(){
        return receiver;
    }
    public String getSender(){
        return sender;
    }
    public String getDid(){return did; }
    public int getValue(){ return value;}
    public JSONArray getQuorum(){
        return quorum;
    }
    public String getsDate(){ return sDate;}
    public String geteDate() { return eDate;}

    public static String setOS(){
        String osName = getOsName();
        String pathDir = "";
        if(osName.contains("Windows"))
            pathDir = "C:\\Rubix\\";
        else if(osName.contains("Mac"))
            pathDir = "/Applications/Rubix/";
        else if(osName.contains("Linux"))
            pathDir = "/home/" + getSystemUser() + "/Rubix/";

        return pathDir;
    }



}