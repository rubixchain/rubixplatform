package com.rubix.core.Controllers;

import com.rubix.core.Resources.RequestModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import static com.rubix.Resources.APIHandler.*;
import static com.rubix.Resources.Functions.mutex;
import static com.rubix.Resources.IntegrityCheck.*;
import static com.rubix.core.Controllers.Basics.checkRubixDir;
import static com.rubix.core.Controllers.Basics.start;
import static com.rubix.core.Resources.CallerFunctions.mainDir;

@RestController
public class Transactions {

    @RequestMapping(value = "/getTxnDetails", method = RequestMethod.POST,
            produces = {"application/json", "application/xml"})
    public String getTxnDetails(@RequestBody RequestModel requestModel) throws JSONException {
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

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", transactionDetails(txnId));
        contentObject.put("count", transactionDetails(txnId).length());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/getTxnByDate", method = RequestMethod.POST,
            produces = {"application/json", "application/xml"})
    public String getTxnByDate(@RequestBody RequestModel requestModel) throws JSONException {
        if (!mainDir())
            return checkRubixDir();
        if(!mutex)
            start();


        String s = requestModel.getsDate();
        String e = requestModel.geteDate();
        System.out.println(s + " " + e);

        if(!dateIntegrity(s, e)){
            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("response", message);
            result.put("data", contentObject);
            result.put("message", "");
            result.put("status", "false");
            result.put("error_code", 1311);
            return result.toString();
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


    @RequestMapping(value = "/getTxnByComment", method = RequestMethod.POST,
            produces = {"application/json", "application/xml"})
    public String getTxnByComment(@RequestBody RequestModel requestModel) throws JSONException {
        if (!mainDir())
            return checkRubixDir();
        if(!mutex)
            start();

        String comment = requestModel.getComment();
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", transactionsByComment(comment));
        contentObject.put("count", transactionsByComment(comment).length());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/getTxnByCount", method = RequestMethod.POST,
            produces = {"application/json", "application/xml"})
    public String getTxnByCount(@RequestBody RequestModel requestModel) throws JSONException {
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

    @RequestMapping(value = "/getTxnByDID", method = RequestMethod.POST,
            produces = {"application/json", "application/xml"})
    public String getTxnByDID(@RequestBody RequestModel requestModel) throws JSONException {
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
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", transactionsByDID(did));
        contentObject.put("count", transactionsByDID(did).length());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/getTxnByRange", method = RequestMethod.POST,
            produces = {"application/json", "application/xml"})
    public String getTxnByRange(@RequestBody RequestModel requestModel) throws JSONException {
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
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", transactionsByRange(start, end));
        contentObject.put("count", transactionsByRange(start, end).length());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }
}
