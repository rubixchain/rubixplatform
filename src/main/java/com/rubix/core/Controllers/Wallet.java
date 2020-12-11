package com.rubix.core.Controllers;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;

import static com.rubix.Resources.APIHandler.*;
import static com.rubix.Resources.Functions.*;
import static com.rubix.Resources.Functions.dirPath;
import static com.rubix.core.Controllers.Basics.mutex;
import static com.rubix.core.Controllers.Basics.*;
import static com.rubix.core.Resources.CallerFunctions.getBalance;
import static com.rubix.core.Resources.CallerFunctions.mainDir;

@RestController
public class Wallet {

    @RequestMapping(value = "/getAccountInfo", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public String getAccountInfo() throws JSONException {
        if (!mainDir())
            return checkRubixDir();
        if(!mutex)
            start();
        JSONArray accountInfo = accountInformation();
        JSONObject accountObject = accountInfo.getJSONObject(0);
        accountObject.put("balance", getBalance());

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", accountObject);
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/getDashboard", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public String getDashboard() throws JSONException, IOException, InterruptedException {
        if (!mainDir())
            return checkRubixDir();
        if(!mutex)
            start();

        JSONArray contactsObject = contacts();
        int contactsCount = contactsObject.length();

        JSONArray accountInfo = accountInformation();
        JSONObject accountObject = accountInfo.getJSONObject(0);

        JSONArray dateTxn = txnPerDay();
        JSONObject dateTxnObject = dateTxn.getJSONObject(0);


        int totalTxn = accountObject.getInt("senderTxn") + accountObject.getInt("receiverTxn");
        accountObject.put("totalTxn", totalTxn);
        accountObject.put("onlinePeers", onlinePeersCount());
        accountObject.put("contactsCount", contactsCount);
        accountObject.put("transactionsPerDay", dateTxnObject);
        accountObject.put("balance", getBalance());


        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", accountObject);
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/getOnlinePeers", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public String getOnlinePeers() throws IOException, JSONException, InterruptedException {
        if (!mainDir())
            return checkRubixDir();

        if(!mutex)
            start();

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", peersOnlineStatus());
        contentObject.put("count", peersOnlineStatus().length());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/getContactsList", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public String getContactsList() throws JSONException {
        if (!mainDir())
            return checkRubixDir();
        if(!mutex)
            start();

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", contacts());
        contentObject.put("count", contacts().length());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/viewTokens", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public String viewTokens() throws JSONException {
        if (!mainDir())
            return checkRubixDir();
        if(!mutex)
            start();

        File directoryPath = new File(TOKENS_PATH);
        String[] contents = directoryPath.list();

        JSONArray returnTokens = new JSONArray();
        for (String content : contents)
            returnTokens.put(content);

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", returnTokens);
        contentObject.put("count", returnTokens.length());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();

    }
}
