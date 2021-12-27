package com.rubix.core.Controllers;

import static com.rubix.Resources.APIHandler.*;
import static com.rubix.Resources.Functions.*;
import static com.rubix.core.Resources.CallerFunctions.mainDir;

import java.io.File;
import java.io.IOException;

import com.rubix.Consensus.QuorumConsensus;
import com.rubix.LevelDb.DataBase;
import com.rubix.Resources.IPFSNetwork;
import com.rubix.core.RubixApplication;
import com.rubix.core.Resources.Receiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.ipfs.api.IPFS;

@CrossOrigin(origins = "http://localhost:1898")
@RestController
public class Basics {

    public static String location = "";
    public static boolean mutex = false;

    @RequestMapping(value = "/start", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public static String start() throws JSONException, IOException {
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

            QuorumConsensus alpha1 = new QuorumConsensus("alpha",QUORUM_PORT);
            Thread alpha1Thread = new Thread(alpha1);
            alpha1Thread.start();

            QuorumConsensus beta1 = new QuorumConsensus("beta",QUORUM_PORT+1);
            Thread beta1Thread = new Thread(beta1);
            beta1Thread.start();

            QuorumConsensus gamma1 = new QuorumConsensus("gamma",QUORUM_PORT+2);
            Thread gamma1Thread = new Thread(gamma1);
            gamma1Thread.start();

            Receiver receiver = new Receiver();
            Thread receiverThread = new Thread(receiver);
            receiverThread.start();

            /* DataBase.createOrOpenDB();
            DataBase.pushTxnFiletoDB(); */

            System.out.println(repo());

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


    @RequestMapping(value = "/putTxnHisToDB", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public static String putTxnHisToDB()
    {
        String folders=checkDirectory();
        JSONObject folderStatus= new JSONObject(folders);
        if(!folderStatus.getString("status").contains("Success"))
        {
            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("response", folderStatus);
            result.put("data", contentObject);
            result.put("message", "");
            result.put("status", "false");
            return result.toString();
        }

        DataBase.pushTxnFiletoDB();
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", "Data from JSON file moved to transactionHistory and essentialShare DB");
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();


    }

    @RequestMapping(value = "/putQstToDB", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public static String putQstToDB()
    {
        String folders=checkDirectory();
        JSONObject folderStatus= new JSONObject(folders);
        if(!folderStatus.getString("status").contains("Success"))
        {
            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("response", folderStatus);
            result.put("data", contentObject);
            result.put("message", "");
            result.put("status", "false");
            return result.toString();
        }

        DataBase.pushQstDatatoDB();
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", "Data from JSON file moved to quorumSignedTransaction and quorumSign DB");
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();


    }

    @RequestMapping(value = "/check", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public static String checkRubixDir() throws JSONException, IOException {
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

        File contactsFile = new File(DATA_PATH + "Contacts.json");
        if(!contactsFile.exists()) {
            contactsFile.createNewFile();
            writeToFile(DATA_PATH + "Contacts.json", new JSONArray().toString(), false);
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
    public static String sync() throws IOException, JSONException {
        if (!mainDir())
            return checkRubixDir();
        if(!mutex)
            start();
         networkInfo();

        if(DataBase.checkTransactionHistoryDBEmpty()==0)
        {
            DataBase.pushTxnFiletoDB();
        }
        if(DataBase.checkQuorumSignedTxnDBEmpty()==0)
        {
            DataBase.pushQstDatatoDB();
        }

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", "Network Nodes Synced");
        result.put("data",contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    /* @RequestMapping(value = "/restart", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
    public void restart() {
        RubixApplication.restart();
    } */

    @RequestMapping(value = "/p2pClose", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public String p2pClose() throws JSONException, IOException {
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
        DataBase.closeDB();
        IPFSNetwork.executeIPFSCommands("ipfs shutdown");
        System.exit(0);
        return "Shutting down";
    }

    @RequestMapping(value = "/repo", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public static String repo() {
        IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/" + IPFS_PORT);
        IPFSNetwork.repo(ipfs);
        return "Garbage Collected";
    }
}

