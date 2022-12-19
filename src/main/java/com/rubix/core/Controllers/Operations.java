package com.rubix.core.Controllers;

import com.rubix.Resources.APIHandler;
import com.rubix.Resources.Functions;
import com.rubix.TokenTransfer.TokenSender;
import com.rubix.TokenTransfer.TransferPledge.*;
import com.rubix.core.Resources.RequestModel;

import RubixDID.DIDCreation.DIDimage;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import java.lang.InterruptedException;

import static RubixDID.DIDCreation.DIDimage.createDID;
import static RubixDID.DIDCreation.DIDimage.*;
import static RubixDID.HelperFunctions.Functions.checkSharesGenerated;
import static com.rubix.Resources.APIHandler.send;
import static com.rubix.Resources.Functions.*;
import static com.rubix.Mining.HashChain.*;
import static com.rubix.core.Controllers.Basics.*;
import static com.rubix.core.Resources.CallerFunctions.*;
import static com.rubix.Mining.HashChain.*;

@CrossOrigin(origins = "http://localhost:1898")
@RestController
public class Operations {

    
    @RequestMapping(value="/transactionFinality",method = RequestMethod.POST, produces = { "application/json",
    "application/xml" })
    public static String transactionFinality(@RequestBody String requestModel) throws Exception {

        if (!mainDir())
            return checkRubixDir();
        if (!Basics.mutex)
            start();
        System.out.println(requestModel);

        JSONObject jsonObject = new JSONObject(requestModel);

        System.out.println(jsonObject.toString());

        JSONObject wholeTransferResult = APIHandler.sendB(jsonObject);

        String status = 
        wholeTransferResult.has("status") && wholeTransferResult.getString("status").equals("Failed") ? "false" : "true";

    

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", wholeTransferResult);
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", status);
        return result.toString();

    }

    @RequestMapping(value = "/newHotWallet", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public String newHotWallet(@RequestBody RequestModel requestModel)
            throws IOException, JSONException, InterruptedException {
        setDir();
        File RubixFolder = new File(dirPath);

        String DID = requestModel.getDidString();
        String PublicShare = requestModel.getPublicShareString();
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();

        int walletType = 2;

        if (RubixFolder.exists()) {
            contentObject.put("response", "Rubix Wallet already exists!");
        } else {
            // deleteFolder(RubixFolder);
            JSONObject didResult = DIDimage.setupDID(DID, PublicShare, walletType);
            if (didResult.getString("Status").contains("Success"))
                createWorkingDirectory();

            start();

            contentObject.put("response", didResult);
        }
        APIHandler.networkInfo();
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/initiateTransaction", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public static String initiateTransaction(@RequestBody RequestModel requestModel) throws Exception {
        if (!mainDir())
            return checkRubixDir();
        if (!Basics.mutex)
            start();

        String recDID = requestModel.getReceiver();
        double tokenCount = requestModel.getTokenCount();
        String comments = requestModel.getComment();
        int type = requestModel.getType();
        String pvtKeyPass = requestModel.getPvtKeyPass();

        // If user forgets to input the private key password in the curl request.
        if (pvtKeyPass == null) {
            System.out.println("Please include your private key password in the transaction request");
            JSONObject resultObject = new JSONObject();
            resultObject.put("did", "");
            resultObject.put("tid", "null");
            resultObject.put("status", "Failed");
            resultObject.put("message",
                    "Your private Key password must be provided. If you haven't generated keys, use /generateEcDSAKeys and then proceed to perform token transfer");
            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("response", resultObject);
            result.put("data", contentObject);
            result.put("message", "");
            result.put("status", "true");
            return result.toString();
        }

        int intPart = (int) tokenCount;
        double decimal = tokenCount - intPart;
        decimal = formatAmount(decimal);
        System.out.println("Input Value: " + tokenCount);
        System.out.println("Integer Part: " + intPart);
        System.out.println("Decimal Part: " + decimal);

        String[] div = String.valueOf(tokenCount).split("\\.");
        System.out.println("Number of decimals: " + div[1].length());

        if (div[1].length() > 3) {
            System.out.println("Amount can have only 3 precisions maximum");
            JSONObject resultObject = new JSONObject();
            resultObject.put("did", "");
            resultObject.put("tid", "null");
            resultObject.put("status", "Failed");
            resultObject.put("message", "Amount can have only 3 precisions maximum");

            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("response", resultObject);
            result.put("data", contentObject);
            result.put("message", "");
            return result.toString();

        }

        Double available = Functions.getBalance();
        if (tokenCount > available) {
            System.out.println("Amount greater than available");
            JSONObject resultObject = new JSONObject();
            resultObject.put("did", "");
            resultObject.put("tid", "null");
            resultObject.put("status", "Failed");
            resultObject.put("message", "Amount greater than available");

            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("response", resultObject);
            result.put("data", contentObject);
            result.put("message", "");
            return result.toString();
        }

        JSONObject objectSend = new JSONObject();
        objectSend.put("receiverDidIpfsHash", recDID);
        objectSend.put("type", type);
        objectSend.put("comment", comments);
        objectSend.put("amount", tokenCount);
        objectSend.put("pvtKeyPass", pvtKeyPass);

        System.out.println("Starting Whole Amount Transfer...");
        JSONObject wholeTransferResult = send(objectSend.toString());

        String status = 
        wholeTransferResult.has("status") && wholeTransferResult.getString("status").equals("Failed") ? "false" : "true";

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", wholeTransferResult);
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", status);
        return result.toString();

    }

    @RequestMapping(value = "/mine", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
    public static String mine(int type) throws Exception {
        if (!mainDir())
            return checkRubixDir();
        if (!Basics.mutex)
            start();
        if (type == 0) {
            type = 1;
        }
        return APIHandler.create(type).toString();

    }

    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public String Create(@RequestParam("image") MultipartFile imageFile)
            throws IOException, JSONException, InterruptedException {
        setDir();

        File RubixFolder = new File(dirPath);
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();

        if (RubixFolder.exists()) {
            contentObject.put("response", "Rubix Wallet already exists!");
        } else {
            // deleteFolder(RubixFolder);
            JSONObject didResult = DIDimage.createDID(imageFile.getInputStream());
            if (didResult.getString("Status").contains("Success"))
                createWorkingDirectory();

            start();

            contentObject.put("response", didResult);
        }
        APIHandler.networkInfo();
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/hashchain", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public String Create(@RequestParam("tid") String tid, @RequestParam("DIDs") String[] DIDs,
            @RequestParam("matchRule") int matchRule) throws IOException, JSONException, InterruptedException {

        String hash = newHashChain(tid, DIDs, matchRule);

        JSONObject result = new JSONObject();
        result.put("data", hash);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/generate", method = RequestMethod.GET, produces = { "application/json",
            "application/xml" })
    public String generate() throws JSONException {
        int width = 256;
        int height = 256;
        String src = null;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        // File f;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int a = (int) (Math.random() * 256); // alpha
                int r = (int) (Math.random() * 256); // red
                int g = (int) (Math.random() * 256); // green
                int b = (int) (Math.random() * 256); // blue

                int p = (a << 24) | (r << 16) | (g << 8) | b; // pixel
                img.setRGB(x, y, p);
            }
        }
        try {
            ByteArrayOutputStream f = new ByteArrayOutputStream();
            ImageIO.write(img, "png", f);
            byte[] bytes = f.toByteArray();
            String base64bytes = Base64.getEncoder().encodeToString(bytes);
            src = "data:image/png;base64," + base64bytes;
            System.out.println(src);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", src);
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/ownerIdentity", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public String ownerIdentity(@RequestParam("tokens") JSONArray tokensArray)
            throws IOException, JSONException, InterruptedException {
        if (!mainDir())
            return checkRubixDir();
        if (!Basics.mutex)
            start();

        Functions.pathSet();
        String didFile = readFile(DATA_PATH.concat("DID.json"));
        JSONArray didArray = new JSONArray(didFile);
        String did = didArray.getJSONObject(0).getString("didHash");
        Functions.ownerIdentity(tokensArray, did);
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", "Successfully Updated");
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/commitBlock", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public static String commitBlock(@RequestBody RequestModel requestModel) throws Exception {
        if (!mainDir())
            return checkRubixDir();
        if (!Basics.mutex)
            start();
        System.out.println(requestModel.toString());
        String blockHash = requestModel.getBlockHash();
        String comments = requestModel.getComment();
        int type = requestModel.getType();
        String pvtKeyPass = requestModel.getPvtKeyPass();

        // If user forgets to input the private key password in the curl request.
        if (pvtKeyPass == null) {
            System.out.println("Please include your private key password in the transaction request");
            JSONObject resultObject = new JSONObject();
            resultObject.put("did", "");
            resultObject.put("tid", "null");
            resultObject.put("status", "Failed");
            resultObject.put("message",
                    "Your private Key password must be provided. If you haven't generated keys, use /generateEcDSAKeys and then proceed to perform token transfer");
            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("response", resultObject);
            result.put("data", contentObject);
            result.put("message", "");
            result.put("status", "true");
            return result.toString();
        }

        // System.out.println("Opertaions - blockHash " + blockHash + " comments " +
        // comments + " type " + type);

        JSONObject objectSend = new JSONObject();
        objectSend.put("blockHash", blockHash);
        objectSend.put("type", type);
        objectSend.put("comment", comments);
        objectSend.put("pvtKeyPass", pvtKeyPass);

        // System.out.println("Opertaions - objectsend is " + objectSend.toString());

        // System.out.println("Opertaions - Starting to commit block");
        // System.out.println("Opertaions - ObjectSend " + objectSend.toString());
        JSONObject commitBlockObject = APIHandler.commit(objectSend.toString());

        // System.out.println("Opertaions -block commit object is " +
        // commitBlockObject.toString());
        // System.out.println("Block commit status is "+
        // commitBlockObject.getString("status").toLowerCase());

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", commitBlockObject);
        // System.out.println("Opertaions - commitBlockObject " +
        // commitBlockObject.toString());
        // System.out.println("Opertaions - contentObject " + contentObject.toString());
        result.put("data", contentObject);
        // result.put("message", "");
        result.put("status", "true");
        // System.out.println("result " + result.toString());

        return result.toString();

    }

    @RequestMapping(value = "/unpledgeToken", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public static String unpledgeToken(@RequestBody RequestModel requestModel) throws Exception {
        if (!mainDir())
            return checkRubixDir();
        if (!Basics.mutex)
            start();

        // String trnxId = requestModel.getTransactionID();
        // String hashMatch = requestModel.gethashMatchString();
        // int powLevel = requestModel.getpowLevel();
        String tokenList = requestModel.getTokenList();

        // System.out.println(powLevel);
        // System.out.println(requestModel.getpowLevel());

        // List<String> hashMatchList = Arrays.asList(hashMatch);
        List<String> tokenListList = Arrays.asList(tokenList);

        boolean pledgeStatus = APIHandler.proofGeneration(tokenListList);

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("Pledge status", pledgeStatus);
        result.put("data", contentObject);
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/verifyPoW", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public static String verifyPoW(@RequestBody RequestModel requestModel) throws Exception {
        if (!mainDir())
            return checkRubixDir();
        if (!Basics.mutex)
            start();

        String tokenList = requestModel.getTokenList();
        String receiver = requestModel.getReceiver();
        String transactionID = requestModel.getTransactionID();

        boolean powVerified = Unpledge.verifyProof(tokenList, receiver, transactionID);

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("Release status", powVerified);
        result.put("data", contentObject);
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/newColdWallet", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public String createColdWallet(@RequestParam("image") MultipartFile imageFile,
            @RequestParam("passPhrase") String passKey)
            throws IOException, JSONException, InterruptedException {
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        if (checkSharesGenerated()) {
            result.put("data", "");
            result.put("message", "Shares Already Generated");
            result.put("status", "true");
            return result.toString();
        }

        JSONObject response = setUpColdWallet(imageFile.getInputStream(), passKey);
        if (!response.getString("Status").equals("Success")) {
            result.put("data", "");
            result.put("message", "Shares Not Generated");
            result.put("status", "false");
            return result.toString();
        }

        contentObject.put("response", response);
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();

    }

    @RequestMapping(value = { "/signChallenge" }, method = { RequestMethod.POST }, produces = { "application/json",
            "application/xml" })
    public static String signChallenge(@RequestBody RequestModel requestModel) throws Exception {
        if (!mainDir())
            return checkRubixDir();
        if (!Basics.mutex)
            Basics.start();
        String tid = requestModel.getTransactionID();
        boolean status = signChallengePayload(tid);
        JSONObject result = new JSONObject();
        String message = status ? "Challenge Payload signed" : "Challenge Payload file not found/ not signed";
        result.put("message", message);
        result.put("data", "");
        result.put("status", status);
        return result.toString();
  }

}