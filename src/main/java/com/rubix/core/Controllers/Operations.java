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
public class Operations {

    @RequestMapping(value = "/initiateTransaction", method = RequestMethod.POST,
            produces = {"application/json", "application/xml"})
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

    @RequestMapping(value = "/mine", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
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
 
    @RequestMapping(value = "/create", method = RequestMethod.POST,
            produces = {"application/json", "application/xml"})
    public String Create(@RequestParam("image") MultipartFile imageFile) throws IOException, JSONException, InterruptedException {
        setDir();
        File RubixFolder = new File(dirPath);
        
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        
        if (RubixFolder.exists()) {
        	 contentObject.put("response", "Rubix Wallet already exists!");        	
        }else {
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


    @RequestMapping(value = "/hashchain", method = RequestMethod.POST,
            produces = {"application/json", "application/xml"})
    public String Create(@RequestParam("tid") String tid, @RequestParam("DIDs") String[] DIDs, @RequestParam("matchRule") int matchRule) throws IOException, JSONException, InterruptedException {

        String hash = newHashChain(tid, DIDs, matchRule);

        JSONObject result = new JSONObject();
        result.put("data", hash);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/generate", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public String generate() throws JSONException {
        int width = 256;
        int height = 256;
        String src = null;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        //File f;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int a = (int) (Math.random() * 256); //alpha
                int r = (int) (Math.random() * 256); //red
                int g = (int) (Math.random() * 256); //green
                int b = (int) (Math.random() * 256); //blue

                int p = (a << 24) | (r << 16) | (g << 8) | b; //pixel
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

    @RequestMapping(value = "/ownerIdentity", method = RequestMethod.POST,
            produces = {"application/json", "application/xml"})
    public String ownerIdentity(@RequestParam("tokens") JSONArray tokensArray) throws IOException, JSONException, InterruptedException {
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

}