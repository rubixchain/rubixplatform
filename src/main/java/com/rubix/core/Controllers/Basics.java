package com.rubix.core.Controllers;

import com.rubix.Consensus.QuorumConsensus;
import com.rubix.Ping.PingCheck;
import com.rubix.Resources.Functions;
import com.rubix.Resources.IPFSNetwork;
import com.rubix.core.Resources.Background;
import com.rubix.core.Resources.QuorumPingReceiveThread;
import com.rubix.core.Resources.Receiver;
import com.rubix.core.Resources.ReceiverPingReceive;
import io.ipfs.api.IPFS;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.rubix.Constants.IPFSConstants.bootstrap;

import java.io.*;

import static com.rubix.Resources.APIHandler.*;
import static com.rubix.Resources.Functions.*;
import static com.rubix.Resources.IPFSNetwork.executeIPFSCommandsResponse;
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

            ReceiverPingReceive receiverPingReceive = new ReceiverPingReceive();
            Thread receiverPingThread = new Thread(receiverPingReceive);
            receiverPingThread.start();

            QuorumPingReceiveThread quorumPingReceiveThread = new QuorumPingReceiveThread();
            Thread quorumPingThread = new Thread(quorumPingReceiveThread);
            quorumPingThread.start();

            tokenBank();

            System.out.println(repo());

            addPublicData();
            pathSet();

            String PART_TOKEN_CHAIN_PATH = TOKENCHAIN_PATH.concat("PARTS/");
            String PART_TOKEN_PATH = TOKENS_PATH.concat("PARTS/");
            File partFolder = new File(PART_TOKEN_PATH);
            if (!partFolder.exists())
                partFolder.mkdir();
            partFolder = new File(PART_TOKEN_CHAIN_PATH);
            if (!partFolder.exists())
                partFolder.mkdir();
            File partTokensFile = new File(PAYMENTS_PATH.concat("PartsToken.json"));
            if (!partTokensFile.exists()) {
                partTokensFile.createNewFile();
                writeToFile(partTokensFile.toString(), "[]", false);
            }

            String STAKE_PATH = WALLET_DATA_PATH.concat("Stake/");
            File stakeFolder = new File(STAKE_PATH);
            if (!stakeFolder.exists())
              stakeFolder.mkdir();

            Background background = new Background();
            Thread backThread = new Thread(background);
            backThread.start();

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

    @RequestMapping(value = "/bootstrap", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
    public String getBootstrap() throws IOException, JSONException {


        String command = bootstrap + "list";

        // String response = executeIPFSCommandsResponse(command);
        boolean configMatching = true;

        JSONObject result = new JSONObject();
        result.put("response", "Bootstrap List");
        // result.put("message", "Bootstrap added: " + bootstrapId);
        result.put("message", BOOTSTRAPS.toString().replace(",", "") // remove the commas
                .replace("[", "") // remove the right bracket
                .replace("]", "") // remove the left bracket
                .trim());
        result.put("ipfs-config-sync", configMatching);
        return result.toString();
    }

    @RequestMapping(value = "/bootstrap", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public String addBootstrap(@RequestParam("id") String bootstrapId) throws JSONException, IOException {

        String command = "ipfs bootstrap add " + bootstrapId;

        String response = executeIPFSCommandsResponse(command);

        String configPath = dirPath.concat("config.json");
        String configFileContent = readFile(configPath);
        JSONArray pathsArray = new JSONArray(configFileContent);

        BOOTSTRAPS = pathsArray.getJSONArray(5);
        BOOTSTRAPS.put(bootstrapId);
        writeToFile(configPath, pathsArray.toString(), false);

        JSONObject result = new JSONObject();
        result.put("response", "Bootstrap Node Added");
        // result.put("message", "Bootstrap added: " + bootstrapId);
        result.put("message", response);
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/bootstrap", method = RequestMethod.DELETE, produces = { "application/json",
            "application/xml" })
    public String removeBootstrap(@RequestParam("id") String bootstrapId) throws JSONException, IOException {

        String command = "ipfs bootstrap rm " + bootstrapId;

        String response = executeIPFSCommandsResponse(command);

        String configPath = dirPath.concat("config.json");
        String configFileContent = readFile(configPath);
        JSONArray pathsArray = new JSONArray(configFileContent);
        BOOTSTRAPS = pathsArray.getJSONArray(5);

        for(int i = 0; i < BOOTSTRAPS.length(); i++){
            if(BOOTSTRAPS.getString(i).equals(bootstrapId)){
                pathsArray.getJSONArray(5).remove(i);
                break;
            }
        }
        writeToFile(configPath, pathsArray.toString(), false);

        JSONObject result = new JSONObject();
        result.put("response", "Bootstrap Node Removed");
        // result.put("message", "Bootstrap added: " + bootstrapId);
        result.put("message", response);
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

    @RequestMapping(value = "/repo", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public static String repo() {
        IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/" + IPFS_PORT);
        IPFSNetwork.repo(ipfs);
        return "Garbage Collected";
    }
    @RequestMapping(value = "/tokenParts", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public static Double tokenParts(@RequestParam("token") String tokenHash) {
        return Functions.partTokenBalance(tokenHash);

    }

    @RequestMapping(value = "/validateReceiver", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public String validateReceiver(@RequestParam("receiverDID") String receiverDID) throws IOException{
        System.out.println(receiverDID);
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        String receiverPeerId = getValues(DATA_PATH + "DataTable.json", "peerid", "didHash", receiverDID);
        boolean sanityCheck = sanityCheck(receiverPeerId, ipfs, SEND_PORT+10);
        if(!sanityCheck){
            contentObject.put("response", sanityMessage);
            result.put("data",contentObject);
            result.put("status", "Failed");
            result.put("message", "");
            System.out.println(sanityMessage);
            return result.toString();
        }
        if(getValues(DATA_PATH + "DataTable.json", "didHash", "didHash", receiverDID)=="") {
            sync();
            if(getValues(DATA_PATH + "DataTable.json", receiverDID, "didHash", receiverDID)=="") {
                contentObject.put("response", "Invalid "+receiverDID);
                result.put("data",contentObject);
                result.put("message", "Invalid "+receiverDID);
                result.put("status", "true");
            }

        }
        else {
            contentObject.put("response", receiverDID+" is valid");
            result.put("data",contentObject);
            result.put("message", receiverDID+" is valid");
            result.put("status", "true");
        }
        System.out.println(result.toString());
        return result.toString();
    }

}

