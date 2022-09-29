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

import static RubixDID.DIDCreation.DIDimage.*;
import static com.rubix.Resources.APIHandler.send;
import static com.rubix.Resources.Functions.*;
import static com.rubix.Mining.HashChain.*;
import static com.rubix.core.Controllers.Basics.*;
import static com.rubix.core.Resources.CallerFunctions.*;
import static com.rubix.Mining.HashChain.*;

@CrossOrigin(origins = "http://localhost:1898")
@RestController
public class Operations {

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

        System.out.println("Starting Whole Amount Transfer...");
        JSONObject wholeTransferResult = send(objectSend.toString());

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", wholeTransferResult);
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
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
            JSONObject didResult = createDID(imageFile.getInputStream());
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

    @RequestMapping(value = "/sign", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
    public String sign() throws IOException, JSONException, InterruptedException {
        if (!mainDir())
            checkRubixDir();
        if (!Basics.mutex)
            start();

        Functions.pathSet();
        String response = Functions.getSign();

        JSONObject resObj = new JSONObject(response);
        JSONObject result = new JSONObject();
        if (resObj.getString("status").equals("false")) {
            result.put("data", "");
            result.put("message", "Cold wallet Signature process failed");
            result.put("status", "false");
        } else {
            result.put("data", "");
            result.put("message", "Cold wallet Signature process Success");
            result.put("status", "true");
        }
        return result.toString();
    }

    @RequestMapping(value = "/createColdtWallet", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public String Create_Cold_Wallet(@RequestParam("did") String DID,
            @RequestParam("publicshare") String PublicShare)
            throws IOException, JSONException, InterruptedException {
        setDir();
        File RubixFolder = new File(dirPath);

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();

        String walletType = "COLDWALLET";


        if (RubixFolder.exists()) {
            contentObject.put("response", "Rubix Wallet already exists!");
        } else {
            // deleteFolder(RubixFolder);
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

    @RequestMapping(value = "/disableHotWallet", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public String enableColdWallet(@RequestParam("Response") String response) {
        JSONObject result = new JSONObject();
        String walletType ="COLDWALLET";

        String checkWalletType = checkWalleType();

        if(checkWalletType.equals("WALLET_TYPE_NOT_SET"))
        {
            result.put("data", "");
            result.put("message", "WALLET_TYPE not set");
            result.put("status", "false");
            return result.toString();
        }

        if(checkWalletType.equals("COLDWALLET"))
        {
            result.put("data", "");
            result.put("message", "WALLET_TYPE already set as COLDWALLET");
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

        if(response.equals("true"))
        {
            deletePvtShare();
        }

        result.put("data", "");
        result.put("message", "Cold Wallet enabled");
        result.put("status", "true");

        return result.toString();
    }

    @RequestMapping(value = "/exportShares", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public String exportShares() {
        JSONObject result = new JSONObject();
        String walletType = getWalletType();
        boolean checkShares = checkSharesPresent();

        if(walletType.equals("COLDWALLET") && checkSharesExported())
        {
            result.put("data", "");
            result.put("message", "Shares Already Exported");
            result.put("status", "false");

            return result.toString();
        }

        if(!walletType.equals("STANDARD") && !checkShares)
        {
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

}