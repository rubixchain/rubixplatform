package com.rubix.core.Controllers;

import static com.rubix.Resources.APIHandler.creditsInfo;
import static com.rubix.Resources.APIHandler.transactionDetails;
import static com.rubix.Resources.APIHandler.transactionsByComment;
import static com.rubix.Resources.APIHandler.transactionsByCount;
import static com.rubix.Resources.APIHandler.transactionsByDID;
import static com.rubix.Resources.APIHandler.transactionsByDate;
import static com.rubix.Resources.APIHandler.transactionsByRange;
import static com.rubix.Resources.Functions.mutex;
import static com.rubix.Resources.IntegrityCheck.dateIntegrity;
import static com.rubix.Resources.IntegrityCheck.didIntegrity;
import static com.rubix.Resources.IntegrityCheck.message;
import static com.rubix.Resources.IntegrityCheck.rangeIntegrity;
import static com.rubix.Resources.IntegrityCheck.txnIdIntegrity;
import static com.rubix.core.Controllers.Basics.checkRubixDir;
import static com.rubix.core.Controllers.Basics.start;
import static com.rubix.core.Resources.CallerFunctions.mainDir;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.rubix.core.Resources.RequestModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:1898")
@RestController
public class Transactions {

    @RequestMapping(value = "/getTxnDetails", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public String getTxnDetails(@RequestBody RequestModel requestModel) throws JSONException, IOException {
        if (!mainDir())
            return checkRubixDir();
        if (!mutex)
            start();
        String txnId = requestModel.getTransactionID();
        if (!txnIdIntegrity(txnId)) {
            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("message", message);
            result.put("data", contentObject);
            result.put("message", "");
            result.put("status", "false");
            result.put("error_code", 1311);
            return result.toString();
        }

        if (transactionDetails(txnId).length() == 0) {
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

    @RequestMapping(value = "/getTxnByDate", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public String getTxnByDate(@RequestBody RequestModel requestModel)
            throws JSONException, IOException, ParseException {
        if (!mainDir())
            return checkRubixDir();
        if (!mutex)
            start();

        String s = requestModel.getsDate();
        String e = requestModel.geteDate();

        String strDateFormat = "yyyy-MM-dd"; // Date format is Specified
        SimpleDateFormat objSDF = new SimpleDateFormat(strDateFormat);
        Date date1 = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy").parse(s);
        Date date2 = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy").parse(e);
        String start = objSDF.format(date1);
        String end = objSDF.format(date2);

        if (!dateIntegrity(start, end)) {
            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("response", message);
            result.put("data", contentObject);
            result.put("message", "");
            result.put("status", "false");
            result.put("error_code", 1311);
            return result.toString();
        }

        if (transactionsByDate(s, e).length() == 0) {
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

    @RequestMapping(value = "/getTxnByComment", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public String getTxnByComment(@RequestBody RequestModel requestModel) throws JSONException, IOException {
        if (!mainDir())
            return checkRubixDir();
        if (!mutex)
            start();

        String comment = requestModel.getComment();

        if (transactionsByComment(comment).length() == 0) {
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

    @RequestMapping(value = "/getTxnByCount", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public String getTxnByCount(@RequestBody RequestModel requestModel) throws JSONException, IOException {
        if (!mainDir())
            return checkRubixDir();
        if (!mutex)
            start();

        int n = requestModel.getTxnCount();
        if (n < 1) {
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

    @RequestMapping(value = "/getTxnByDID", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public String getTxnByDID(@RequestBody RequestModel requestModel) throws JSONException, IOException {
        if (!mainDir())
            return checkRubixDir();
        if (!mutex)
            start();
        String did = requestModel.getDid();
        if (!didIntegrity(did)) {
            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("response", message);
            result.put("data", contentObject);
            result.put("message", "");
            result.put("status", "false");
            result.put("error_code", 1311);
            return result.toString();
        }

        if (transactionsByDID(did).length() == 0) {
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

    @RequestMapping(value = "/getTxnByRange", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public String getTxnByRange(@RequestBody RequestModel requestModel) throws JSONException, IOException {
        if (!mainDir())
            return checkRubixDir();
        if (!mutex)
            start();
        int start = requestModel.getStartRange();
        int end = requestModel.getEndRange();
        if (!rangeIntegrity(start, end)) {
            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("response", message);
            result.put("data", contentObject);
            result.put("message", "");
            result.put("status", "false");
            result.put("error_code", 1311);
            return result.toString();
        }

        if (transactionsByRange(start, end).length() == 0) {
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

    private String noTxnError() {
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("message", "No transactions found!");
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "false");
        result.put("error_code", 1311);
        return result.toString();
    }

    // New API - To display total number of credits, Spent credits, Unspent Credits
    // and total no of transactions
    @RequestMapping(value = "/getTransactionHeader", method = RequestMethod.GET, produces = { "application/json",
            "application/xml" })
    public String getTransactionHeader() throws JSONException, IOException {
        if (!mainDir())
            return checkRubixDir();
        if (!mutex)
            start();

        JSONObject result = new JSONObject();
        result.put("data", creditsInfo());
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }
}
