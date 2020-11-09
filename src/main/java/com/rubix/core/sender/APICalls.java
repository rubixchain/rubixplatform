package com.rubix.core.sender;

import com.rubix.Consensus.QuorumConsensus;
import com.rubix.Resources.Functions;
import com.rubix.Resources.IPFSNetwork;
import com.rubix.TokenTransfer.TokenReceiver;
import com.rubix.core.Fractionalisation.FractionChooser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import static RubixDID.DIDCreation.DIDimage.createDID;
import static com.rubix.Resources.APIHandler.*;
import static com.rubix.Resources.Functions.*;
import static com.rubix.core.sender.Details.setOS;

@RestController
public class APICalls {

    public static String location = "";

    @PostMapping("/create")
    public String Create(@RequestParam("image") MultipartFile imageFile, @RequestParam("data") String value) throws Exception {
        JSONObject result = createDID(value, imageFile.getInputStream());
        return result.toString();
    }

    @RequestMapping("/setup")
    public String SetUp() throws IOException, JSONException {
        File dataFolder = new File(setOS() + "config.json");
        if (!dataFolder.exists()) {
            JSONObject result = new JSONObject();
            result.put("Message", "User not registered, create your Decentralised Identity!");
            result.put("Status", "Failed");
            return result.toString();
        }
        launch();

        String osName = Functions.getOsName();
        if (osName.contains("Windows"))
            location = "C:\\Rubix\\PaymentsApp\\";
        else if (osName.contains("Mac"))
            location = "/Applications/Rubix/PaymentsApp/";
        else if (osName.contains("Linux"))
            location = "/home/" + getSystemUser() + "/Rubix/PaymentsApp/";

        QuorumConsensus quorumClass = new QuorumConsensus();
        Thread quorumThread = new Thread(quorumClass);
        quorumThread.start();

        while (true) {
            String result = TokenReceiver.receive();
            JSONObject tokenResult = new JSONObject(result);
            if (tokenResult.getString("status").contains("Success")) {
                JSONArray tokens = tokenResult.getJSONArray("tokens");
                JSONArray tokenHeader = tokenResult.getJSONArray("tokenHeader");

                for (int i = 0; i < tokens.length(); i++) {
                    String bank = tokenHeader.getString(i);
                    String bankFile = readFile(location + bank + ".json");
                    JSONArray bankArray = new JSONArray(bankFile);
                    JSONObject tokenObject = new JSONObject();
                    tokenObject.put("tokenHash", tokens.getString(i));
                    bankArray.put(tokenObject);
                    Functions.writeToFile(location + bank + ".json", bankArray.toString(), false);

                }
            }
        }
    }




    @PostMapping("/initiateTransaction")
    public String initiateTransaction(@RequestBody Details details) throws JSONException, IOException {
        File dataFolder = new File(setOS() + "config.json");
        if (!dataFolder.exists()) {
            JSONObject result = new JSONObject();
            result.put("Message", "User not registered, create your Decentralised Identity!");
            result.put("Status", "Failed");
            return result.toString();
        }
        String recDID = details.getReceiver();
        int tokenCount = details.getTokenCount();
        String comments = details.getComment();
        JSONArray tokens = FractionChooser.calculate(tokenCount);
        JSONObject objectSend = new JSONObject();
        objectSend.put("tokens", tokens);
        objectSend.put("recDID", recDID);
        objectSend.put("comments", comments);
        objectSend.put("tokenHeader", FractionChooser.tokenHeader);
        JSONObject resultObject = send(objectSend.toString());

        if (resultObject.getString("status").equals("Success")) {
            for (int i = 0; i < tokens.length(); i++) {
                Functions.updateJSON("remove", location + FractionChooser.tokenHeader.get(i).toString() + ".json", tokens.getString(i));
            }
        }

        return resultObject.toString();
    }

    @RequestMapping("/getAccountInfo")
    public String getAccountInfo() throws JSONException {
        File dataFolder = new File(setOS() + "config.json");
        if (!dataFolder.exists()) {
            JSONObject result = new JSONObject();
            result.put("Message", "User not registered, create your Decentralised Identity!");
            result.put("Status", "Failed");
            return result.toString();
        }
        return accountInformation();
    }

    @RequestMapping("/getBalance")
    public String getBalance() throws JSONException {
        File dataFolder = new File(setOS() + "config.json");
        if (!dataFolder.exists()) {
            JSONObject result = new JSONObject();
            result.put("Message", "User not registered, create your Decentralised Identity!");
            result.put("Status", "Failed");
            return result.toString();
        }
        JSONObject result = new JSONObject();
        result.put("Account Balance", accountBalance());
        return result.toString();
    }

    @PostMapping("/viewProofs")
    public String viewProofs(@RequestBody Details details) throws JSONException {
        File dataFolder = new File(setOS() + "config.json");
        if (!dataFolder.exists()) {
            JSONObject result = new JSONObject();
            result.put("Message", "User not registered, create your Decentralised Identity!");
            result.put("Status", "Failed");
            return result.toString();
        }
        String token = details.getToken();
        return proofChains(token);

    }

    @PostMapping("/getTxnDetails")
    public String getTxnDetails(@RequestBody Details details) throws JSONException {
        File dataFolder = new File(setOS() + "config.json");
        if (!dataFolder.exists()) {
            JSONObject result = new JSONObject();
            result.put("Message", "User not registered, create your Decentralised Identity!");
            result.put("Status", "Failed");
            return result.toString();
        }
        String txnId = details.getTransactionID();
        return transactionDetail(txnId);
    }

    @PostMapping("/getTxnByDate")
    public String getTxnByDate(@RequestBody Details details) throws JSONException {
        File dataFolder = new File(setOS() + "config.json");
        if (!dataFolder.exists()) {
            JSONObject result = new JSONObject();
            result.put("Message", "User not registered, create your Decentralised Identity!");
            result.put("Status", "Failed");
            return result.toString();
        }
        String s = details.getsDate();
        String e = details.geteDate();
        return transactionsByDate(s, e);
    }

    @PostMapping("/getTxnByCount")
    public String getTxnByCount(@RequestBody Details details) throws JSONException {
        File dataFolder = new File(setOS() + "config.json");
        if (!dataFolder.exists()) {
            JSONObject result = new JSONObject();
            result.put("Message", "User not registered, create your Decentralised Identity!");
            result.put("Status", "Failed");
            return result.toString();
        }
        int n = details.getTxnCount();
        return transactionsByCount(n);
    }




    @PostMapping("/getTxnByComment")
    public String getTxnByComment(@RequestBody Details details) throws JSONException {
        File dataFolder = new File(setOS() + "config.json");
        if (!dataFolder.exists()) {
            JSONObject result = new JSONObject();
            result.put("Message", "User not registered, create your Decentralised Identity!");
            result.put("Status", "Failed");
            return result.toString();
        }
        String comment = details.getComment();
        return transactionsByComment(comment);
    }



    @RequestMapping("/viewTokens")
    public String viewTokens() throws JSONException {
        File dataFolder = new File(setOS() + "config.json");
        JSONObject result = new JSONObject();

        if (!dataFolder.exists()) {
            result.put("Message", "User not registered, create your Decentralised Identity!");
            result.put("Status", "Failed");
            return result.toString();
        }

        File directoryPath = new File(TOKENS_PATH);
        String[] contents = directoryPath.list();

        JSONArray returnTokens = new JSONArray();
        for (String content : contents)
            returnTokens.put(content);
        return returnTokens.toString();

    }

    @RequestMapping("/p2pClose")
    public void p2pClose() {
        closeStreams();
    }


    @RequestMapping("/sync")
    public String sync() throws IOException, JSONException {
        File dataFolder = new File(setOS() + "config.json");
        if (!dataFolder.exists()) {
            JSONObject result = new JSONObject();
            result.put("Message", "User not registered, create your Decentralised Identity!");
            result.put("Status", "Failed");
            return result.toString();
        }
        return networkInfo();
    }

    @RequestMapping("/shutdown")
    public void shutdown() throws IOException, JSONException {
        IPFSNetwork.executeIPFSCommands("ipfs shutdown");
        System.exit(0);
    }


}

