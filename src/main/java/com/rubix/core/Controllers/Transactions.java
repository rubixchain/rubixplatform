package com.rubix.core.Controllers;

import com.rubix.Resources.IntegrityCheck;
import com.rubix.core.Resources.RequestModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import netscape.javascript.JSException;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

import static com.rubix.Resources.APIHandler.*;

import static com.rubix.Resources.Functions.*;
import static com.rubix.Resources.IntegrityCheck.*;
import static com.rubix.core.Controllers.Basics.checkRubixDir;
import static com.rubix.core.Controllers.Basics.start;
import static com.rubix.core.Resources.CallerFunctions.mainDir;

@CrossOrigin(origins = "http://localhost:1898")
@RestController
public class Transactions {

    @RequestMapping(value = "/getTxnDetails", method = RequestMethod.POST,
            produces = {"application/json", "application/xml"})
    public String getTxnDetails(@RequestBody RequestModel requestModel) throws JSONException, IOException {
        if (!mainDir())
            return checkRubixDir();
        if(!mutex)
            start();
        String txnId = requestModel.getTransactionID();
        if(!txnIdIntegrity(txnId)){
            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("message", message);
            result.put("data", contentObject);
            result.put("message", "");
            result.put("status", "false");
            result.put("error_code", 1311);
            return result.toString();
        }

        if(transactionDetails(txnId).length()==0) {
            return noTxnError();
        }

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", transactionDetails(txnId));
        contentObject.put("count", transactionDetails(txnId).length());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = {"/getNftTxnDetails"}, method = {RequestMethod.POST}, produces = {"application/json", "application/xml"})
    public String getNftTxnDetails(@RequestBody RequestModel requestModel) throws JSONException, IOException {
        if (!mainDir())
            return checkRubixDir();
        if (!mutex)
            start();
        String txnId = requestModel.getTransactionID();
        if (!IntegrityCheck.txnIdIntegrity(txnId)) {
            JSONObject jSONObject1 = new JSONObject();
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("message", IntegrityCheck.message);
            jSONObject1.put("data", jSONObject2);
            jSONObject1.put("message", "");
            jSONObject1.put("status", "false");
            jSONObject1.put("error_code", 1311);
            return jSONObject1.toString();
        }
        if (nftTransactionDetails(txnId).has("Message"))
            return noTxnError();
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", nftTransactionDetails(txnId));
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/getTxnByDate", method = RequestMethod.POST,
            produces = {"application/json", "application/xml"})
    public String getTxnByDate(@RequestBody RequestModel requestModel) throws JSONException, IOException, ParseException {
        if (!mainDir())
            return checkRubixDir();
        if(!mutex)
            start();


        String s = requestModel.getsDate();
        String e = requestModel.geteDate();

        String strDateFormat = "yyyy-MM-dd"; //Date format is Specified
        SimpleDateFormat objSDF = new SimpleDateFormat(strDateFormat);
        Date date1=new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy").parse(s);
        Date date2=new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy").parse(e);
        String start = objSDF.format(date1);
        String end = objSDF.format(date2);

        if(!dateIntegrity(start, end)){
            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("response", message);
            result.put("data", contentObject);
            result.put("message", "");
            result.put("status", "false");
            result.put("error_code", 1311);
            return result.toString();
        }

        if(transactionsByDate(s, e).length()==0) {
            return noTxnError();
        }


        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", transactionsByDate(s, e));
        contentObject.put("count", transactionsByDate(s, e).length());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();

    }


    @RequestMapping(value = {"/getNftTxnByDate"}, method = {RequestMethod.POST}, produces = {"application/json", "application/xml"})
    public String getNftTxnByDate(@RequestBody RequestModel requestModel) throws JSONException, IOException, ParseException {
        if (!mainDir())
            return checkRubixDir();
        if (!mutex)
            start();
        String s = requestModel.getsDate();
        String e = requestModel.geteDate();
        String strDateFormat = "yyyy-MM-dd";
        SimpleDateFormat objSDF = new SimpleDateFormat(strDateFormat);
        Date date1 = (new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy")).parse(s);
        Date date2 = (new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy")).parse(e);
        String start = objSDF.format(date1);
        String end = objSDF.format(date2);
        if (!dateIntegrity(start, end)) {
            JSONObject jSONObject1 = new JSONObject();
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("response", message);
            jSONObject1.put("data", jSONObject2);
            jSONObject1.put("message", "");
            jSONObject1.put("status", "false");
            jSONObject1.put("error_code", 1311);
            return jSONObject1.toString();
        }
        JSONArray nftTransactions = new JSONArray();
        nftTransactions = nftTransactionsByDate(s, e);
        if (nftTransactions.length() == 0)
            return noTxnError();
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", nftTransactions);
        contentObject.put("count", nftTransactions.length());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }


    @RequestMapping(value = "/getTxnByComment", method = RequestMethod.POST,
            produces = {"application/json", "application/xml"})
    public String getTxnByComment(@RequestBody RequestModel requestModel) throws JSONException, IOException {
        if (!mainDir())
            return checkRubixDir();
        if(!mutex)
            start();

        String comment = requestModel.getComment();

        if(transactionsByComment(comment).length()==0) {
            return noTxnError();
        }


        JSONObject contentObject = new JSONObject();
        JSONObject result = new JSONObject();
        contentObject.put("response", transactionsByComment(comment));
        contentObject.put("count", transactionsByComment(comment).length());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = {"/getNftTxnByComment"}, method = {RequestMethod.POST}, produces = {"application/json", "application/xml"})
    public String getNftTxnByComment(@RequestBody RequestModel requestModel) throws JSONException, IOException {
        if (!mainDir())
            return checkRubixDir();
        if (!mutex)
            start();
        String comment = requestModel.getComment();
        if (nftTransactionsByComment(comment).length() == 0)
            return noTxnError();
        JSONObject contentObject = new JSONObject();
        JSONObject result = new JSONObject();
        contentObject.put("response", nftTransactionsByComment(comment));
        contentObject.put("count", nftTransactionsByComment(comment).length());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }


    @RequestMapping(value = "/getTxnByCount", method = RequestMethod.POST,
            produces = {"application/json", "application/xml"})
    public String getTxnByCount(@RequestBody RequestModel requestModel) throws JSONException, IOException {
        if (!mainDir())
            return checkRubixDir();
        if(!mutex)
            start();

        int n = requestModel.getTxnCount();
        if(n < 1){
            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("response", "Call Bounds Less Than 1");
            result.put("data", contentObject);
            result.put("message", "");
            result.put("status", "false");
            result.put("error_code", 1311);
            return result.toString();
        }

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", transactionsByCount(n));
        contentObject.put("count", transactionsByCount(n).length());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();

    }

    @RequestMapping(value = {"/getNftTxnByCount"}, method = {RequestMethod.POST}, produces = {"application/json", "application/xml"})
    public String getNftTxnByCount(@RequestBody RequestModel requestModel) throws JSONException, IOException {
        if (!mainDir())
            return checkRubixDir();
        if (!mutex)
            start();
        int n = requestModel.getTxnCount();
        if (n < 1) {
            JSONObject jSONObject1 = new JSONObject();
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("response", "Call Bounds Less Than 1");
            jSONObject1.put("data", jSONObject2);
            jSONObject1.put("message", "");
            jSONObject1.put("status", "false");
            jSONObject1.put("error_code", 1311);
            return jSONObject1.toString();
        }
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", nftTransactionsByCount(n));
        contentObject.put("count", nftTransactionsByCount(n).length());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }


    @RequestMapping(value = "/getTxnByDID", method = RequestMethod.POST,
            produces = {"application/json", "application/xml"})
    public String getTxnByDID(@RequestBody RequestModel requestModel) throws JSONException, IOException {
        if (!mainDir())
            return checkRubixDir();
        if(!mutex)
            start();
        String did = requestModel.getDid();
        if(!didIntegrity(did)){
            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("response", message);
            result.put("data", contentObject);
            result.put("message", "");
            result.put("status", "false");
            result.put("error_code", 1311);
            return result.toString();
        }

        if(transactionsByDID(did).length()==0) {
            return noTxnError();
        }

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", transactionsByDID(did));
        contentObject.put("count", transactionsByDID(did).length());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = {"/getNftTxnByDID"}, method = {RequestMethod.POST}, produces = {"application/json", "application/xml"})
    public String getNftTxnByDID(@RequestBody RequestModel requestModel) throws JSONException, IOException {
        if (!mainDir())
            return checkRubixDir();
        if (!mutex)
            start();
        String did = requestModel.getDid();
        if (!didIntegrity(did)) {
            JSONObject jSONObject1 = new JSONObject();
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("response", IntegrityCheck.message);
            jSONObject1.put("data", jSONObject2);
            jSONObject1.put("message", "");
            jSONObject1.put("status", "false");
            jSONObject1.put("error_code", 1311);
            return jSONObject1.toString();
        }
        if (transactionsByDID(did).length() == 0)
            return noTxnError();
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", nftTransactionsByDID(did));
        contentObject.put("count", nftTransactionsByDID(did).length());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }


    @RequestMapping(value = "/getTxnByRange", method = RequestMethod.POST,
            produces = {"application/json", "application/xml"})
    public String getTxnByRange(@RequestBody RequestModel requestModel) throws JSONException, IOException {
        if (!mainDir())
            return checkRubixDir();
        if(!mutex)
            start();
        int start = requestModel.getStartRange();
        int end = requestModel.getEndRange();
        if(!rangeIntegrity(start, end)){
            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("response", message);
            result.put("data", contentObject);
            result.put("message", "");
            result.put("status", "false");
            result.put("error_code", 1311);
            return result.toString();
        }

        if(transactionsByRange(start, end).length()==0) {
            return noTxnError();
        }

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", transactionsByRange(start, end));
        contentObject.put("count", transactionsByRange(start, end).length());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = {"/getNftTxnByRange"}, method = {RequestMethod.POST}, produces = {"application/json", "application/xml"})
    public String getNftTxnByRange(@RequestBody RequestModel requestModel) throws JSONException, IOException {
        if (!mainDir())
            return checkRubixDir();
        if (!mutex)
            start();
        int start = requestModel.getStartRange();
        int end = requestModel.getEndRange();
        if (!rangeIntegrity(start, end)) {
            JSONObject jSONObject1 = new JSONObject();
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("response", IntegrityCheck.message);
            jSONObject1.put("data", jSONObject2);
            jSONObject1.put("message", "");
            jSONObject1.put("status", "false");
            jSONObject1.put("error_code", 1311);
            return jSONObject1.toString();
        }
        if (nftTransactionsByRange(start, end).length() == 0)
            return noTxnError();
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", nftTransactionsByRange(start, end));
        contentObject.put("count", nftTransactionsByRange(start, end).length());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    private String noTxnError() throws JSONException{
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("message", "No transactions found!");
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "false");
        result.put("error_code", 1311);
        return result.toString();
    }

    //New API - To display total number of credits, Spent credits, Unspent Credits and total no of transactions
    @RequestMapping(value = "/getTransactionHeader", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public String getTransactionHeader() throws JSONException, IOException, InterruptedException {
        if (!mainDir())
            return checkRubixDir();
        if(!mutex)
            start();

        String txnPath = WALLET_DATA_PATH.concat("TransactionHistory.json");
        String quorumPath = WALLET_DATA_PATH.concat("QuorumSignedTransactions.json");

        File txnFile = new File(txnPath);
        File quorumFile = new File(quorumPath);

        int txnCount = 0;
        if(txnFile.exists()){
            String transactionFile = readFile(WALLET_DATA_PATH.concat("TransactionHistory.json"));
            JSONArray txnArray = new JSONArray(transactionFile);
            txnCount = txnArray.length();

        }
        int maxCredits = 0;
        int spentCredits = 0;
        int unspentCredits = 0;
        if(quorumFile.exists()){
            String qFile = readFile(WALLET_DATA_PATH.concat("QuorumSignedTransactions.json"));
            JSONArray qArray = new JSONArray(qFile);
            maxCredits = qArray.length();
            for(int i = 0; i < qArray.length(); i++){
                if(qArray.getJSONObject(i).getBoolean("minestatus"))
                    spentCredits++;
                else
                    unspentCredits++;
            }
        }

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("txnCount", txnCount);
        contentObject.put("maxCredits", maxCredits);
        contentObject.put("spentCredits", spentCredits);
        contentObject.put("unspentCredits", unspentCredits);
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }
}
