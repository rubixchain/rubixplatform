package com.rubix.core.Controllers;

import com.rubix.Consensus.QuorumConsensus;
import com.rubix.Resources.IPFSNetwork;
import com.rubix.core.Resources.Receiver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.io.*;

import static com.rubix.Resources.APIHandler.*;
import static com.rubix.Resources.Functions.*;
import static com.rubix.core.Resources.CallerFunctions.mainDir;


@RestController
public class Basics {

    public static String location = "";
    public static boolean mutex = false;

    @RequestMapping(value = "/start", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public static String start() throws JSONException {
        if(mutex){
            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("response", "Already Setup");
            result.put("data", contentObject);
            result.put("message", "");
            result.put("status", "true");
            return result.toString();
        }
        if(mainDir()){
            mutex = true;
            launch();
            pathSet();
            QuorumConsensus quorumClass = new QuorumConsensus();
            Thread quorumThread = new Thread(quorumClass);
            quorumThread.start();

            Receiver receiver = new Receiver();
            Thread receiverThread = new Thread(receiver);
            receiverThread.start();

            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("response", "Setup Complete");
            result.put("data", contentObject);
            result.put("message", "");
            result.put("status", "true");
            return result.toString();
        }
        else{
            return checkRubixDir();
        }
    }

    @RequestMapping(value = "/check", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public static String checkRubixDir() throws JSONException {
        String rubixFolders = checkDirectory();
        JSONObject folderStatus = new JSONObject(rubixFolders);
        if(!folderStatus.getString("status").contains("Success")){
            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("response", folderStatus);
            result.put("data", contentObject);
            result.put("message", "");
            result.put("status", "false");
            return result.toString();
        }

        location = dirPath + "PaymentsApp/";
        File workingDir = new File(location);
        if (!workingDir.exists()) {
            workingDir.delete();

            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("response", "User's Wallet Missing!");
            result.put("data", contentObject);
            result.put("message", "");
            result.put("status", "false");
            result.put("error_code", 403);
            return result.toString();
        }
        File bnk00file = new File(location + "BNK00.json");
        File bnk01file = new File(location + "BNK01.json");
        File bnk10file = new File(location + "BNK10.json");
        File bnk11file = new File(location + "BNK11.json");
        File tokenMapFile = new File(location + "TokenMap.json");

        if (!bnk00file.exists() || !bnk01file.exists() || !bnk10file.exists() || !bnk11file.exists() || !tokenMapFile.exists()) {
            workingDir.delete();

            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("response", "Missing Banks");
            result.put("data", contentObject);
            result.put("message", "");
            result.put("status", "false");
            result.put("error_code", 403);
            return result.toString();
        }
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", "User is Registered");
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/sync", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public String sync() throws IOException, JSONException {
        if (!mainDir())
            return checkRubixDir();
        if(!mutex)
            start();
         networkInfo();

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", "Network Nodes Synced");
        result.put("data",contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/p2pClose", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public String p2pClose() throws JSONException {
        if(!mutex)
            start();
        closeStreams();
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", "All Streams Closed");
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/shutdown", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public String shutdown() {
        IPFSNetwork.executeIPFSCommands("ipfs shutdown");
        System.exit(0);
        return "Shutting down";
    }
}

