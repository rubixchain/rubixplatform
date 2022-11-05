package com.rubix.core.Controllers;

import com.rubix.Resources.APIHandler;
import com.rubix.Resources.Functions;
import com.rubix.core.Resources.RequestModel;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import java.lang.InterruptedException;

import static RubixDID.DIDCreation.DIDimage.createDID;
import static com.rubix.Resources.APIHandler.send;
import static com.rubix.Resources.Functions.*;
import static com.rubix.Mining.HashChain.*;
import static com.rubix.core.Controllers.Basics.*;
import static com.rubix.core.Resources.CallerFunctions.*;
import static com.rubix.Mining.HashChain.*;

@CrossOrigin(origins = "http://localhost:1898")
@RestController
public class NCWalletOperations {

    @RequestMapping(value = "/requestTransactionPayload", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public static String requestTransactionPayload(@RequestBody RequestModel requestModel) throws Exception {
        if (!mainDir())
            return checkRubixDir();
        if (!Basics.mutex)
            start();

        String recDID = requestModel.getReceiver();
        double tokenCount = requestModel.getTokenCount();
        String comments = requestModel.getComment();
        int type = requestModel.getType();

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
        objectSend.put("operation", "PreProcess");

        System.out.println("Starting Whole Amount Transfer...");
        JSONObject response = send(objectSend.toString());

        JSONObject result = new JSONObject();
        if (response.getString("status").equals("Failed")) {
            result.put("data", response);
            result.put("message", "");
            result.put("status", "false");
            return result.toString();
        }

        result.put("data", response);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/exportShares", method = RequestMethod.GET, produces = { "application/json",
            "application/xml" })
    public String exportShares(@RequestParam("authToken") String authToken) {
        JSONObject result = new JSONObject();
        int walletType = getWalletType();
        boolean checkShares = checkSharesPresent();

        if (!authToken.equals(Functions.IdentityToken)) {
            result.put("data", "");
            result.put("message", "Error. authToken Supplied does not match Identity Token of node session.");
            result.put("status", "false");

            return result.toString();
        }

        if (walletType == 2 && checkSharesExported()) {
            result.put("data", "");
            result.put("message", "Shares Already Exported");
            result.put("status", "false");

            return result.toString();
        }

        if (walletType != 1 && !checkShares) {
            result.put("data", "");
            result.put("message", "Error");
            result.put("status", "false");

            return result.toString();
        }

        String shareStr = exportShareImages();

        if (shareStr.isBlank()) {
            result.put("data", "");
            result.put("message", "Shares not exported");
            result.put("status", "false");

            return result.toString();
        }

        result.put("data", shareStr);
        result.put("message", "Shares exported");
        result.put("status", "true");

        return result.toString();
    }

    @RequestMapping(value = "/disableStandardWallet", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public String enableColdWallet(@RequestParam("status") String status, @RequestParam("authToken") String authToken,
            @RequestParam("challengeSign") String challengeSign) {
        JSONObject result = new JSONObject();
        int walletType = 2;

        int checkWalletType = getWalletType();

        if (!authToken.equals(Functions.IdentityToken)) {
            result.put("data", "");
            result.put("message", "Error. authToken Supplied does not match Identity Token of node session.");
            result.put("status", "false");

            return result.toString();
        }

        if (!status.equals("true")) {
            result.put("data", "");
            result.put("message", "Share Export and save not sucessful");
            result.put("status", "false");
            return result.toString();
        }

        if (checkWalletType == 0) {
            result.put("data", "");
            result.put("message", "WALLET_TYPE not set");
            result.put("status", "false");
            return result.toString();
        }

        if (checkWalletType == 2) {
            result.put("data", "");
            result.put("message", "WALLET_TYPE already set as COLDWALLET");
            result.put("status", "false");
            return result.toString();
        }

        boolean challengeCheck = verifyChallengeString(challengeSign);

        if (!challengeCheck) {
            result.put("data", "");
            result.put("message", "Error. challengeSignature not verified");
            result.put("status", "false");

            return result.toString();
        }

        boolean enableWalletCheck = setWalletType(walletType);
        if (!enableWalletCheck) {
            result.put("data", "");
            result.put("message", "Cold Wallet not enabled");
            result.put("status", "false");
            return result.toString();
        }

        if (challengeCheck) {
            deletePvtShare();
        }

        result.put("data", "");
        result.put("message", "Cold Wallet enabled");
        result.put("status", "true");

        return result.toString();
    }

    @RequestMapping(value = "/newHotWallet", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public String newHotWallet(@RequestParam("DID") MultipartFile DID,
            @RequestParam("PublicShare") MultipartFile PublicShare) {

        int walletType = 3;

        setDir();
        File RubixFolder = new File(dirPath);
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();

        try {
            if (RubixFolder.exists()) {
                contentObject.put("response", "Rubix Wallet already exists!");
            } else {
                // deleteFolder(RubixFolder);
                JSONObject didResult = setupHotWalletFolders(DID.getInputStream(), PublicShare.getInputStream(),
                        walletType);
                if (didResult.getString("Status").contains("Success"))
                    createWorkingDirectory();

                start();

                contentObject.put("response", didResult);
            }

            APIHandler.networkInfo();
        } catch (IOException e) {

        }

        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/newFexrHotWallet", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public String Create_Cold_Wallet(@RequestBody RequestModel requestModel)
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
            JSONObject didResult = setupDID(DID, PublicShare, walletType);
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

    @RequestMapping(value = "/generateSecretShares", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public String generateSecretShares(@RequestParam("image") MultipartFile imageFile,
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

        JSONObject response = createSecretImages(imageFile.getInputStream(), passKey);
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


    @RequestMapping(value = "/sendPositions", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public String sendPositions(@RequestBody RequestModel requestModel)
            throws IOException, JSONException, InterruptedException {
        JSONObject result = new JSONObject();

        String positions = requestModel.getPvtPositions();

        if(positions.isBlank())
        {
            JSONObject resultObject = new JSONObject();
                result.put("data", "");
                result.put("message", "Positions Array value is empty");
                result.put("status", "false");
                return result.toString();
        }

        boolean response = setPvtPositions();
        result.put("data", "");
        result.put("message", "Positions Array set Successsfully");
        result.put("status", "true");
        return result.toString();
    }

}
