package com.rubix.core.Controllers;

import static com.rubix.Resources.APIHandler.accountInformation;
import static com.rubix.Resources.APIHandler.contacts;
import static com.rubix.Resources.APIHandler.creditsInfo;
import static com.rubix.Resources.APIHandler.onlinePeersCount;
import static com.rubix.Resources.APIHandler.peersOnlineStatus;
import static com.rubix.Resources.APIHandler.txnPerDay;
import static com.rubix.Resources.Functions.DATA_PATH;
import static com.rubix.Resources.Functions.TOKENS_PATH;
import static com.rubix.Resources.Functions.WALLET_DATA_PATH;
import static com.rubix.Resources.Functions.checkTokenPartBalance;
import static com.rubix.Resources.Functions.pathSet;
import static com.rubix.Resources.Functions.readFile;
import static com.rubix.Resources.Functions.writeToFile;
import static com.rubix.core.Controllers.Basics.checkRubixDir;
import static com.rubix.core.Controllers.Basics.mutex;
import static com.rubix.core.Controllers.Basics.start;
import static com.rubix.core.Resources.CallerFunctions.getBalance;
import static com.rubix.core.Resources.CallerFunctions.mainDir;

import java.io.File;
import java.io.IOException;

import com.rubix.Resources.Functions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:1898")
@RestController
public class Wallet {

    @RequestMapping(value = "/getAccountInfo", method = RequestMethod.GET, produces = { "application/json",
            "application/xml" })
    public String getAccountInfo() throws JSONException, IOException {
        if (!mainDir())
            return checkRubixDir();
        if (!mutex)
            start();
        JSONArray accountInfo = accountInformation();
        JSONObject accountObject = accountInfo.getJSONObject(0);
        accountObject.put("balance", getBalance());
        accountObject.put("credits", creditsInfo());

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", accountObject);
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/getDashboard", method = RequestMethod.GET, produces = { "application/json",
            "application/xml" })
    public String getDashboard() throws JSONException, IOException, InterruptedException {
        if (!mainDir())
            return checkRubixDir();
        if (!mutex)
            start();

        JSONArray contactsObject = contacts();
        int contactsCount = contactsObject.length();
        System.out.println(contactsCount);

        JSONArray accountInfo = accountInformation();
        JSONObject accountObject = accountInfo.getJSONObject(0);
        System.out.println(accountObject);

        JSONArray dateTxn = txnPerDay();
        JSONObject dateTxnObject = dateTxn.getJSONObject(0);
        System.out.println(dateTxnObject);

        // To display the Mine Count of the wallet - Reading from
        // QuorumSignedTransactions
        String content = readFile(WALLET_DATA_PATH.concat("QuorumSignedTransactions.json"));
        JSONArray contentArray = new JSONArray(content);
        JSONArray finalArray = new JSONArray();
        for (int j = 0; j < contentArray.length(); j++) {
            if (contentArray.getJSONObject(j).has("minestatus")) {
                if (!contentArray.getJSONObject(j).getBoolean("minestatus"))
                    finalArray.put(contentArray.getJSONObject(j));
            } else
                finalArray.put(contentArray.getJSONObject(j));

        }
        System.out.println(finalArray);

        int totalTxn = accountObject.getInt("senderTxn") + accountObject.getInt("receiverTxn");
        accountObject.put("totalTxn", totalTxn);
        accountObject.put("onlinePeers", onlinePeersCount());
        accountObject.put("contactsCount", contactsCount);
        accountObject.put("transactionsPerDay", dateTxnObject);
        accountObject.put("balance", getBalance());
        accountObject.put("proofCredits", finalArray.length());

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", accountObject);
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/getOnlinePeers", method = RequestMethod.GET, produces = { "application/json",
            "application/xml" })
    public String getOnlinePeers() throws IOException, JSONException, InterruptedException {
        if (!mainDir())
            return checkRubixDir();

        if (!mutex)
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

    @RequestMapping(value = "/getContactsList", method = RequestMethod.GET, produces = { "application/json",
            "application/xml" })
    public String getContactsList() throws JSONException, IOException {
        if (!mainDir())
            return checkRubixDir();
        if (!mutex)
            start();

        String contactsTable = Functions.readFile(Functions.DATA_PATH + "Contacts.json");
        JSONArray contactsArray = new JSONArray(contactsTable);

        String DIDFile = readFile(DATA_PATH + "DID.json");
        JSONArray didArray = new JSONArray(DIDFile);
        String myDID = didArray.getJSONObject(0).getString("didHash");
        JSONArray finalArray = new JSONArray();

        for (int i = 0; i < contactsArray.length(); i++) {
            if (!(contactsArray.getJSONObject(i).getString("did").equals(myDID)))
                finalArray.put(contactsArray.getJSONObject(i));
        }

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", finalArray);
        contentObject.put("count", finalArray.length());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/getNetworkNodes", method = RequestMethod.GET, produces = { "application/json",
            "application/xml" })
    public String getNetworkNodes() throws JSONException, IOException, InterruptedException {
        if (!mainDir())
            return checkRubixDir();
        if (!mutex)
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

    @RequestMapping(value = "/viewTokens", method = RequestMethod.GET, produces = { "application/json",
            "application/xml" })
    public String viewTokens() throws JSONException, IOException {
        if (!mainDir())
            return checkRubixDir();
        if (!mutex)
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

    @RequestMapping(value = "/checkPartBalance", method = RequestMethod.GET, produces = { "application/json",
            "application/xml" })
    public String checkPartBalance(@RequestParam("token") String token) throws JSONException, IOException {
        if (!mainDir())
            return checkRubixDir();
        if (!mutex)
            start();

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", checkTokenPartBalance(token));
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();

    }

    @RequestMapping(value = "/addNickName", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public static String addNickName(@RequestParam("did") String did, @RequestParam("nickname") String nickname)
            throws JSONException, IOException, InterruptedException {
        if (!mainDir())
            return checkRubixDir();
        if (!mutex)
            start();

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        pathSet();
        String contactsFile = readFile(DATA_PATH + "Contacts.json");
        JSONArray contactsArray = new JSONArray(contactsFile);

        for (int i = 0; i < contactsArray.length(); i++) {
            if (contactsArray.getJSONObject(i).getString("did").equals(did)) {
                contentObject.put("response", "DID already assigned with same/another NickName");
                contentObject.put("did", did);
                contentObject.put("nickname", contactsArray.getJSONObject(i).getString("nickname"));
                result.put("data", contentObject);
                result.put("message", "");
                result.put("status", "true");
                return result.toString();
            }
        }
        for (int i = 0; i < contactsArray.length(); i++) {
            if (contactsArray.getJSONObject(i).getString("nickname").equals(nickname)) {
                contentObject.put("response", "Nickname already assigned to same/another DID");
                contentObject.put("did", nickname);
                contentObject.put("nickname", contactsArray.getJSONObject(i).getString("did"));
                result.put("data", contentObject);
                result.put("message", "");
                result.put("status", "true");
                return result.toString();
            }
        }

        JSONObject contactObject = new JSONObject();
        contactObject.put("did", did);
        contactObject.put("nickname", nickname);
        contactsArray.put(contactObject);

        writeToFile(DATA_PATH + "Contacts.json", contactsArray.toString(), false);
        contentObject.put("response", "Added");

        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();

    }
}
