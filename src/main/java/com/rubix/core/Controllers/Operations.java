package com.rubix.core.Controllers;

import static RubixDID.DIDCreation.DIDimage.createDID;
import static com.rubix.Resources.APIHandler.send;
import static com.rubix.Resources.APIHandler.sendParts;
import static com.rubix.Resources.Functions.PAYMENTS_PATH;
import static com.rubix.Resources.Functions.dirPath;
import static com.rubix.Resources.Functions.readFile;
import static com.rubix.Resources.Functions.setDir;
import static com.rubix.core.Controllers.Basics.checkRubixDir;
import static com.rubix.core.Controllers.Basics.location;
import static com.rubix.core.Controllers.Basics.start;
import static com.rubix.core.Resources.CallerFunctions.createWorkingDirectory;
import static com.rubix.core.Resources.CallerFunctions.deleteFolder;
import static com.rubix.core.Resources.CallerFunctions.mainDir;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Base64;

import javax.imageio.ImageIO;

import com.rubix.Resources.APIHandler;
import com.rubix.Resources.Functions;
import com.rubix.core.Fractionalisation.FractionChooser;
import com.rubix.core.Resources.RequestModel;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.CEILING);

        String recDID = requestModel.getReceiver();
        double tokenCount = requestModel.getTokenCount();
        String comments = requestModel.getComment();
        int type = requestModel.getType();

        Number numberFormat = tokenCount;
        tokenCount = Double.parseDouble(df.format(numberFormat.doubleValue()));

        int intPart = (int) tokenCount;
        double decimal = tokenCount - intPart;
        System.out.println("Input Value: " + tokenCount);
        System.out.println("Integer Part: " + intPart);
        System.out.println("Decimal Part: " + (tokenCount - intPart));
        String[] div = String.valueOf(tokenCount).split("\\.");
        System.out.println("Number of decimals: " + div[1].length());

        if (div[1].length() > 3) {
            JSONObject result = new JSONObject();
            result.put("data", "Amount can have only 3 precisions maximum");
            result.put("message", "");
            result.put("status", "true");

            return result.toString();
        }

        double available = Functions.getBalance();
        if (tokenCount > available) {
            JSONObject result = new JSONObject();
            result.put("data", "Amount greater than available");
            result.put("message", "");
            result.put("status", "true");

            return result.toString();
        }

        if (intPart > 0) {
            File bankFile = new File(PAYMENTS_PATH.concat("BNK00.json"));
            if (bankFile.exists()) {
                String bankContent = readFile(PAYMENTS_PATH.concat("BNK00.json"));
                JSONArray bankArray = new JSONArray(bankContent);
                if (intPart > bankArray.length()) {
                    JSONObject result = new JSONObject();
                    result.put("data", intPart + " whole tokens not available. Please send in parts");
                    result.put("message", "");
                    result.put("status", "true");

                    return result.toString();
                }
            }
        } else {
            if (decimal > 0) {
                JSONObject objectSendParts = new JSONObject();
                objectSendParts.put("receiverDidIpfsHash", recDID);
                objectSendParts.put("type", type);
                objectSendParts.put("comment", comments);
                objectSendParts.put("amount", decimal);

                System.out.println("Starting Decimal Amount Transfer...");
                JSONObject resultObjectParts = sendParts(objectSendParts.toString());
                if (resultObjectParts.getString("status").equals("Success")) {
                    JSONObject result = new JSONObject();
                    JSONObject contentObject = new JSONObject();
                    contentObject.put("Parts response", resultObjectParts);
                    result.put("data", contentObject);
                    result.put("message", "");
                    result.put("status", "true");

                    return result.toString();
                } else {
                    JSONObject result = new JSONObject();
                    JSONObject contentObject = new JSONObject();
                    contentObject.put("Parts response", resultObjectParts);
                    result.put("data", contentObject);
                    result.put("message", "");
                    result.put("status", "true");
                    return result.toString();
                }
            } else {
                JSONObject result = new JSONObject();
                JSONObject contentObject = new JSONObject();
                result.put("data", contentObject);
                result.put("message", "");
                result.put("status", "true");
                return result.toString();
            }
        }

        if (!recDID.startsWith("Qm")) {
            String contactsTable = Functions.readFile(Functions.DATA_PATH + "Contacts.json");
            JSONArray contactsArray = new JSONArray(contactsTable);
            for (int i = 0; i < contactsArray.length(); ++i) {
                if (contactsArray.getJSONObject(i).getString("nickname").equals(recDID))
                    recDID = contactsArray.getJSONObject(i).getString("did");
            }
        }

        JSONArray tokens = FractionChooser.calculate(intPart);
        JSONObject objectSend = new JSONObject();
        objectSend.put("tokens", tokens);
        objectSend.put("receiverDidIpfsHash", recDID);
        objectSend.put("type", type);
        objectSend.put("comment", comments);
        objectSend.put("amount", tokenCount);
        objectSend.put("tokenHeader", FractionChooser.tokenHeader);

        System.out.println("Starting Whole Amount Transfer...");
        JSONObject resultObject = send(objectSend.toString());

        if (resultObject.getString("status").equals("Success")) {
            for (int i = 0; i < tokens.length(); i++) {
                Functions.updateJSON("remove", location + FractionChooser.tokenHeader.get(i).toString() + ".json",
                        tokens.getString(i));
            }
            System.out.println("Whole Amount Transfer Complete");

            if (decimal > 0) {
                JSONObject objectSendParts = new JSONObject();
                objectSendParts.put("receiverDidIpfsHash", recDID);
                objectSendParts.put("type", type);
                objectSendParts.put("comment", comments);
                objectSendParts.put("amount", decimal);

                System.out.println("Starting Decimal Amount Transfer...");
                JSONObject resultObjectParts = sendParts(objectSendParts.toString());
                if (resultObjectParts.getString("status").equals("Success")) {
                    JSONObject result = new JSONObject();
                    JSONObject contentObject = new JSONObject();
                    contentObject.put("Parts response", resultObjectParts);
                    contentObject.put("Whole response", resultObject);
                    result.put("data", contentObject);
                    result.put("message", "");
                    result.put("status", "true");

                    return result.toString();
                } else {
                    JSONObject result = new JSONObject();
                    JSONObject contentObject = new JSONObject();
                    contentObject.put("Parts response", resultObjectParts);
                    contentObject.put("Whole response", resultObject);
                    result.put("data", contentObject);
                    result.put("message", "");
                    result.put("status", "true");
                    return result.toString();
                }
            } else {
                JSONObject result = new JSONObject();
                JSONObject contentObject = new JSONObject();
                contentObject.put("response", resultObject);
                result.put("data", contentObject);
                result.put("message", "");
                result.put("status", "true");
                return result.toString();
            }
        } else {
            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("response", resultObject);
            result.put("data", contentObject);
            result.put("message", "");
            result.put("status", "true");
            return result.toString();
        }

    }

    @RequestMapping(value = "/mine", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
    public static String mine(int type) throws Exception {
        if (!mainDir())
            return checkRubixDir();
        if (!Basics.mutex)
            start();
        return APIHandler.create(type).toString();

    }

    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public String Create(@RequestParam("image") MultipartFile imageFile) throws Exception {
        setDir();
        File RubixFolder = new File(dirPath);
        if (RubixFolder.exists())
            deleteFolder(RubixFolder);
        JSONObject didResult = createDID(imageFile.getInputStream());
        if (didResult.getString("Status").contains("Success"))
            createWorkingDirectory();

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", didResult);
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    @RequestMapping(value = "/generate", method = RequestMethod.GET, produces = { "application/json",
            "application/xml" })
    public String generate() {
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
}
