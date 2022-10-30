package com.rubix.core.Controllers;

import static com.rubix.Constants.IPFSConstants.bootstrap;
import static com.rubix.NFTResources.NFTFunctions.checkForQuorumKeyPassword;
import static com.rubix.Resources.APIHandler.addPublicData;
import static com.rubix.Resources.APIHandler.closeStreams;
import static com.rubix.Resources.APIHandler.ipfs;
import static com.rubix.Resources.APIHandler.networkInfo;
import static com.rubix.Resources.Functions.BOOTSTRAPS;
import static com.rubix.Resources.Functions.DATA_PATH;
import static com.rubix.Resources.Functions.DATUM_CHAIN_PATH;
import static com.rubix.Resources.Functions.IPFS_PORT;
import static com.rubix.Resources.Functions.PAYMENTS_PATH;
import static com.rubix.Resources.Functions.QUORUM_PORT;
import static com.rubix.Resources.Functions.SEND_PORT;
import static com.rubix.Resources.Functions.TOKENCHAIN_PATH;
import static com.rubix.Resources.Functions.TOKENS_PATH;
import static com.rubix.Resources.Functions.WALLET_DATA_PATH;
import static com.rubix.Resources.Functions.checkDirectory;
import static com.rubix.Resources.Functions.dirPath;
import static com.rubix.Resources.Functions.getValues;
import static com.rubix.Resources.Functions.launch;
import static com.rubix.Resources.Functions.pathSet;
import static com.rubix.Resources.Functions.readFile;
import static com.rubix.Resources.Functions.sanityCheck;
import static com.rubix.Resources.Functions.sanityMessage;
import static com.rubix.Resources.Functions.tokenBank;
import static com.rubix.Resources.Functions.writeToFile;
import static com.rubix.Resources.IPFSNetwork.executeIPFSCommandsResponse;
import static com.rubix.core.Resources.CallerFunctions.mainDir;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rubix.Consensus.QuorumConsensus;
import com.rubix.Datum.Dependency;
import com.rubix.Resources.Functions;
import com.rubix.Resources.IPFSNetwork;
import com.rubix.TokenTransfer.TransferPledge.Pledger;
import com.rubix.core.NFTResources.NFTReceiver;
import com.rubix.core.Resources.QuorumPingReceiveThread;
import com.rubix.core.Resources.Receiver;
import com.rubix.core.Resources.ReceiverPingReceive;
import com.rubix.core.Resources.RequestModel;

import io.ipfs.api.IPFS;

@CrossOrigin(origins = "http://localhost:1898")
@RestController
public class Basics {
    public static String location = "";
    public static boolean mutex = false;
    public static boolean quorumStatus = false;
    public static Logger BasicsLogger = Logger.getLogger(Basics.class);

    @RequestMapping(value = "/start", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
    public static String start() throws JSONException, IOException {
        if (mutex) {
            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("response", "Already Setup");
            result.put("data", contentObject);
            result.put("message", "");
            result.put("status", "true");
            return result.toString();
        }
        if (mainDir()) {
        	
            mutex = true;
            launch();
            Dependency.checkDatumPath();
        	Dependency.checkDatumFolder();
            pathSet();

            Receiver receiver = new Receiver();
            Thread receiverThread = new Thread(receiver);
            receiverThread.start();

            ReceiverPingReceive receiverPingReceive = new ReceiverPingReceive();
            Thread receiverPingThread = new Thread(receiverPingReceive);
            receiverPingThread.start();

            NFTReceiver nftReceiver = new NFTReceiver();
            Thread nftReceiverThread = new Thread(nftReceiver);
            nftReceiverThread.start();

            Pledger pledger = new Pledger();
            Thread pledgerThread = new Thread(pledger);
            pledgerThread.start();

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
            if (!stakeFolder.exists()) {
                stakeFolder.mkdir();
            }

            String datumFolderPath = DATUM_CHAIN_PATH;
            File datumFolder = new File(datumFolderPath);
            if (!datumFolder.exists()) {
                datumFolder.mkdir();
            }
            File datumCommitChain = new File(datumFolderPath.concat("/datumCommitChain.json"));
            if (!datumCommitChain.exists()) {
                datumCommitChain.createNewFile();
                writeToFile(datumCommitChain.toString(), "[]", false);
            }
            File datumCommitToken = new File(datumFolderPath.concat("/dataToken.json"));
            if (!datumCommitToken.exists()) {
                datumCommitToken.createNewFile();
                writeToFile(datumCommitToken.toString(), "[]", false);
            }
            File datumCommitHistory = new File(datumFolderPath.concat("/datumCommitHistory.json"));
            if (!datumCommitHistory.exists()) {
                datumCommitHistory.createNewFile();
                writeToFile(datumCommitHistory.toString(), "[]", false);

            }

            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("response", "Setup Complete");
            result.put("data", contentObject);
            result.put("message", "");
            result.put("status", "true");
            return result.toString();
        } else {
            return checkRubixDir();
        }
    }

    @RequestMapping(value = "/startQuorumService", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public static String startQuorumService(@RequestBody RequestModel requestModel)
            throws IOException, JSONException, InterruptedException {

        if (!mainDir())
            return checkRubixDir();
        if (!mutex)
            start();

        if (quorumStatus == false) {

            String quorumKeyPass = requestModel.getPvtKeyPass();

            // check for wrong pvt key password entered
            boolean checkFlag = checkForQuorumKeyPassword(quorumKeyPass);

            if (checkFlag == false) {
                BasicsLogger.debug(
                        "\n Response code : 400. Incorrect password for quorum private key. Please use the correct password and re-run the service.\n");
                JSONObject resultObject = new JSONObject();
                // resultObject.put("did", "");
                // resultObject.put("tid", "null");
                resultObject.put("status", "Failed");
                resultObject.put("message",
                        "Incorrect password for quorum private key. Please use the correct password and re-run the service.");

                JSONObject result = new JSONObject();
                JSONObject contentObject = new JSONObject();
                contentObject.put("response", resultObject);
                result.put("data", contentObject);
                // result.put("message", "");
                // result.put("status", "true");
                result.put("response code", 400);
                return result.toString();
            }

            QuorumConsensus alpha1 = new QuorumConsensus("alpha", QUORUM_PORT, quorumKeyPass);
            Thread alpha1Thread = new Thread(alpha1);
            alpha1Thread.start();

            QuorumConsensus beta1 = new QuorumConsensus("beta", QUORUM_PORT + 1, quorumKeyPass);
            Thread beta1Thread = new Thread(beta1);
            beta1Thread.start();

            QuorumConsensus gamma1 = new QuorumConsensus("gamma", QUORUM_PORT + 2, quorumKeyPass);
            Thread gamma1Thread = new Thread(gamma1);
            gamma1Thread.start();

            QuorumPingReceiveThread quorumPingReceiveThread = new QuorumPingReceiveThread();
            Thread quorumPingThread = new Thread(quorumPingReceiveThread);
            quorumPingThread.start();

            quorumStatus = true;

            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("response", "Quorum service successfully started.");
            result.put("data", contentObject);
            result.put("message", "");
            result.put("status", "true");
            result.put("response code", 200);
            return result.toString();
        } else {

            BasicsLogger.debug(
                    "\nResponse code : 409. Quorum Service is already running. In case you want to re-initiate, re-initiate Rubix jar and run the service again.\n");
            JSONObject resultObject = new JSONObject();
            // resultObject.put("did", "");
            // resultObject.put("tid", "null");
            resultObject.put("status", "Failed");
            resultObject.put("message",
                    "Quorum Service is already running. In case you want to re-initiate, re-initiate Rubix jar and run the service again.");

            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("response", resultObject);
            result.put("data", contentObject);
            // result.put("message", "");
            // result.put("status", "false");
            result.put("response code", 409);
            return result.toString();

        }

    }

    @RequestMapping(value = "/check", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
    public static String checkRubixDir() throws JSONException, IOException {
        String rubixFolders = checkDirectory();
        JSONObject folderStatus = new JSONObject(rubixFolders);
        if (!folderStatus.getString("status").contains("Success")) {
            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("response", folderStatus);
            result.put("data", contentObject);
            result.put("message", "");
            result.put("status", "false");
            return result.toString();
        }

        File contactsFile = new File(DATA_PATH + "Contacts.json");
        if (!contactsFile.exists()) {
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

        if (!bnk00file.exists() || !bnk01file.exists() || !bnk10file.exists() || !bnk11file.exists()
                || !tokenMapFile.exists()) {
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

    @RequestMapping(value = "/sync", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
    public String sync() throws IOException, JSONException, InterruptedException {
        if (!mainDir())
            return checkRubixDir();
        if (!mutex)
            start();
        networkInfo();

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", "Network Nodes Synced");
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    /*
     * An alternate solution has been implemented for generating Hash... So
     * commented this
     * 
     * @RequestMapping(value = "/generateHashtable", method = RequestMethod.GET,
     * produces = {"application/json", "application/xml"})
     * public static String generateHashtable() throws IOException, JSONException {
     * System.out.println("generateHashtable request received");
     * System.out.println("mainDir "+mainDir()+" mutex is "+mutex);
     * JSONArray result = new JSONArray();
     * System.out.println("Received generateHashtable request");
     * try {
     * result.put(tokenHashTableGeneration());
     * } catch (JSONException e) {
     * // TODO Auto-generated catch block
     * e.printStackTrace();
     * } catch (InterruptedException e) {
     * // TODO Auto-generated catch block
     * e.printStackTrace();
     * }
     * 
     * return result.toString();
     * }
     * 
     * public static void generateHashtableBG() throws JSONException, IOException {
     * String str = generateHashtable();
     * }
     */
    @RequestMapping(value = "/bootstrap", method = RequestMethod.GET, produces = { "application/json",
            "application/xml" })
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

        for (int i = 0; i < BOOTSTRAPS.length(); i++) {
            if (BOOTSTRAPS.getString(i).equals(bootstrapId)) {
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

    @RequestMapping(value = "/p2pClose", method = RequestMethod.GET, produces = { "application/json",
            "application/xml" })
    public String p2pClose() throws JSONException, IOException, InterruptedException {
        if (!mutex)
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

    @RequestMapping(value = "/shutdown", method = RequestMethod.GET, produces = { "application/json",
            "application/xml" })
    public String shutdown() {
        IPFSNetwork.executeIPFSCommands("ipfs shutdown");
        System.exit(0);
        return "Shutting down";
    }

    @RequestMapping(value = "/repo", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
    public static String repo() {
        IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/" + IPFS_PORT);
        IPFSNetwork.repo(ipfs);
        return "Garbage Collected";
    }

    @RequestMapping(value = "/tokenParts", method = RequestMethod.GET, produces = { "application/json",
            "application/xml" })
    public static Double tokenParts(@RequestParam("token") String tokenHash) {
        return Functions.partTokenBalance(tokenHash);

    }

    @RequestMapping(value = "/checkDatum", method = RequestMethod.GET, produces = { "application/json",
            "application/xml" })
    public static void checkDatum() throws IOException {
        System.out.println("checkDatum initated");
        Dependency.checkDatumFolder();

    }

    @RequestMapping(value = "/validateReceiver", method = RequestMethod.GET, produces = { "application/json",
            "application/xml" })
    public String validateReceiver(@RequestParam("receiverDID") String receiverDID)
            throws IOException, JSONException, InterruptedException {
        System.out.println(receiverDID);
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        String receiverPeerId = getValues(DATA_PATH + "DataTable.json", "peerid", "didHash", receiverDID);
        boolean sanityCheck = sanityCheck("Receiver", receiverPeerId, ipfs, SEND_PORT + 10);
        if (!sanityCheck) {
            contentObject.put("response", sanityMessage);
            result.put("data", contentObject);
            result.put("status", "Failed");
            result.put("message", "");
            System.out.println(sanityMessage);
            return result.toString();
        }
        if (getValues(DATA_PATH + "DataTable.json", "didHash", "didHash", receiverDID) == "") {
            sync();
            if (getValues(DATA_PATH + "DataTable.json", receiverDID, "didHash", receiverDID) == "") {
                contentObject.put("response", "Invalid " + receiverDID);
                result.put("data", contentObject);
                result.put("message", "Invalid " + receiverDID);
                result.put("status", "true");
            }

        } else {
            contentObject.put("response", receiverDID + " is valid");
            result.put("data", contentObject);
            result.put("message", receiverDID + " is valid");
            result.put("status", "true");
        }
        System.out.println(result.toString());
        return result.toString();
    }

}
