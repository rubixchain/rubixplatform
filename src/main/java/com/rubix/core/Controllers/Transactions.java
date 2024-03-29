package com.rubix.core.Controllers;

import com.rubix.core.Resources.RequestModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

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
    public String getTxnDetails(@RequestBody RequestModel requestModel) throws JSONException, IOException, InterruptedException {
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

        JSONArray transactionDetails = transactionDetails(txnId);
        if(transactionDetails.length()==0) {
            return noTxnError();
        }

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", transactionDetails);
        contentObject.put("count", transactionDetails.length());
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

        JSONArray transactionsByDate = transactionsByDate(s, e);
        if(transactionsByDate.length()==0) {
            return noTxnError();
        }


        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", transactionsByDate);
        contentObject.put("count", transactionsByDate.length());
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
        
        JSONArray transactionsByComment = transactionsByComment(comment);

        if(transactionsByComment.length()==0) {
            return noTxnError();
        }


        JSONObject contentObject = new JSONObject();
        JSONObject result = new JSONObject();
        contentObject.put("response", transactionsByComment);
        contentObject.put("count", transactionsByComment.length());
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
        
        JSONArray transactionsByCount = transactionsByCount(n);
        contentObject.put("response", transactionsByCount);
        contentObject.put("count", transactionsByCount.length());
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

        JSONArray transactionsByDID = transactionsByDID(did);
        if(transactionsByDID.length()==0) {
            return noTxnError();
        }

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", transactionsByDID);
        contentObject.put("count", transactionsByDID.length());
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

        JSONArray transactionsByRange = transactionsByRange(start, end);
        if(transactionsByRange.length()==0) {
            return noTxnError();
        }

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", transactionsByRange);
        contentObject.put("count", transactionsByRange.length());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    private String noTxnError() throws JSONException {
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
    public String getTransactionHeader() throws JSONException, IOException {
        if (!mainDir())
            return checkRubixDir();
        if(!mutex)
            start();

        JSONObject result = new JSONObject();
        result.put("data", creditsInfo());
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }
}
