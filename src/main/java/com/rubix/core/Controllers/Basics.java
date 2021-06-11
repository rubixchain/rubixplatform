package com.rubix.core.Controllers;

import com.rubix.Consensus.QuorumConsensus;
import com.rubix.Resources.IPFSNetwork;
import com.rubix.core.Resources.Receiver;
import io.ipfs.api.IPFS;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.io.*;

import static com.rubix.Resources.APIHandler.*;
import static com.rubix.Resources.Functions.*;
import static com.rubix.Resources.IPFSNetwork.executeIPFSCommands;
import static com.rubix.core.Resources.CallerFunctions.mainDir;

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


            QuorumConsensus alpha1 = new QuorumConsensus("alpha",QUORUM_PORT,0);
            Thread alpha1Thread = new Thread(alpha1);
            alpha1Thread.start();

            QuorumConsensus alpha2 = new QuorumConsensus("alpha",QUORUM_PORT+1,1);
            Thread alpha2Thread = new Thread(alpha2);
            alpha2Thread.start();

            QuorumConsensus alpha3 = new QuorumConsensus("alpha",QUORUM_PORT+2,2);
            Thread alpha3Thread = new Thread(alpha3);
            alpha3Thread.start();

            QuorumConsensus alpha4 = new QuorumConsensus("alpha",QUORUM_PORT+3,3);
            Thread alpha4Thread = new Thread(alpha4);
            alpha4Thread.start();

            QuorumConsensus alpha5 = new QuorumConsensus("alpha",QUORUM_PORT+4,4);
            Thread alpha5Thread = new Thread(alpha5);
            alpha5Thread.start();

            QuorumConsensus alpha6 = new QuorumConsensus("alpha",QUORUM_PORT+5,5);
            Thread alpha6Thread = new Thread(alpha6);
            alpha6Thread.start();

            QuorumConsensus alpha7 = new QuorumConsensus("alpha",QUORUM_PORT+6,6);
            Thread alpha7Thread = new Thread(alpha7);
            alpha7Thread.start();

            QuorumConsensus beta1 = new QuorumConsensus("beta",QUORUM_PORT+7,0);
            Thread beta1Thread = new Thread(beta1);
            beta1Thread.start();

            QuorumConsensus beta2 = new QuorumConsensus("beta",QUORUM_PORT+8,1);
            Thread beta2Thread = new Thread(beta2);
            beta2Thread.start();

            QuorumConsensus beta3 = new QuorumConsensus("beta",QUORUM_PORT+9,2);
            Thread beta3Thread = new Thread(beta3);
            beta3Thread.start();

            QuorumConsensus beta4 = new QuorumConsensus("beta",QUORUM_PORT+10,3);
            Thread beta4Thread = new Thread(beta4);
            beta4Thread.start();

            QuorumConsensus beta5 = new QuorumConsensus("beta",QUORUM_PORT+11,4);
            Thread beta5Thread = new Thread(beta5);
            beta5Thread.start();

            QuorumConsensus beta6 = new QuorumConsensus("beta",QUORUM_PORT+12,5);
            Thread beta6Thread = new Thread(beta6);
            beta6Thread.start();

            QuorumConsensus beta7 = new QuorumConsensus("beta",QUORUM_PORT+13,6);
            Thread beta7Thread = new Thread(beta7);
            beta7Thread.start();

            QuorumConsensus gamma1 = new QuorumConsensus("gamma",QUORUM_PORT+14,0);
            Thread gamma1Thread = new Thread(gamma1);
            gamma1Thread.start();

            QuorumConsensus gamma2 = new QuorumConsensus("gamma",QUORUM_PORT+15,1);
            Thread gamma2Thread = new Thread(gamma2);
            gamma2Thread.start();

            QuorumConsensus gamma3 = new QuorumConsensus("gamma",QUORUM_PORT+16,2);
            Thread gamma3Thread = new Thread(gamma3);
            gamma3Thread.start();

            QuorumConsensus gamma4 = new QuorumConsensus("gamma",QUORUM_PORT+17,3);
            Thread gamma4Thread = new Thread(gamma4);
            gamma4Thread.start();

            QuorumConsensus gamma5 = new QuorumConsensus("gamma",QUORUM_PORT+18,4);
            Thread gamma5Thread = new Thread(gamma5);
            gamma5Thread.start();

            QuorumConsensus gamma6 = new QuorumConsensus("gamma",QUORUM_PORT+19,5);
            Thread gamma6Thread = new Thread(gamma6);
            gamma6Thread.start();

            QuorumConsensus gamma7 = new QuorumConsensus("gamma",QUORUM_PORT+20,6);
            Thread gamma7Thread = new Thread(gamma7);
            gamma7Thread.start();


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
        IPFSNetwork.executeIPFSCommands("ipfs shutdown");
        System.exit(0);
        return "Shutting down";
    }
}

