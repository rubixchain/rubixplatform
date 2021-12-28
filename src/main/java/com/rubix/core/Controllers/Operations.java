package com.rubix.core.Controllers;

import static RubixDID.DIDCreation.DIDimage.createDID;
import static com.rubix.Resources.APIHandler.send;
import static com.rubix.Resources.Functions.dirPath;
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
import java.util.Base64;

import static RubixDID.DIDCreation.DIDimage.createDID;
import static com.rubix.Resources.APIHandler.*;
import static com.rubix.Resources.Functions.*;
import static com.rubix.core.Controllers.Basics.*;
import static com.rubix.core.Controllers.Basics.start;
import static com.rubix.core.Resources.CallerFunctions.*;
import static com.rubix.core.Resources.NFTReceiver.*;
import static com.rubix.core.Resources.Receiver.*;

import javax.imageio.ImageIO;

import com.rubix.Resources.APIHandler;
import com.rubix.Resources.Functions;
import com.rubix.core.Fractionalisation.FractionChooser;
import com.rubix.core.Resources.RequestModel;

import org.json.JSONArray;
import org.json.JSONException;
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
    // static int count = 0;
    // public static void writeDataLineByLine(String data[])
    // {
    // File file = new File("data.csv");
    // try {
    // FileWriter outputfile = new FileWriter(file,true);
    // CSVWriter writer = new CSVWriter(outputfile);
    // writer.writeNext(data);
    // writer.close();
    // }
    // catch (IOException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }

    @RequestMapping(value = "/initiateTransaction", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public static String initiateTransaction(@RequestBody RequestModel requestModel) throws Exception {
        // Instant start = Instant.now();
        if (!mainDir())
            return checkRubixDir();
        if (!Basics.mutex)
            start();

        String recDID = requestModel.getReceiver();
        int tokenCount = requestModel.getTokenCount();
        String comments = requestModel.getComment();
        int type = requestModel.getType();

        if (!recDID.startsWith("Qm")) {
            String contactsTable = Functions.readFile(Functions.DATA_PATH + "Contacts.json");
            JSONArray contactsArray = new JSONArray(contactsTable);
            for (int i = 0; i < contactsArray.length(); ++i) {
                if (contactsArray.getJSONObject(i).getString("nickname").equals(recDID))
                    recDID = contactsArray.getJSONObject(i).getString("did");
            }
        }
        JSONArray tokens = FractionChooser.calculate(tokenCount);
        JSONObject objectSend = new JSONObject();
        objectSend.put("tokens", tokens);
        objectSend.put("receiverDidIpfsHash", recDID);
        objectSend.put("type", type);
        objectSend.put("comment", comments);
        objectSend.put("amount", tokenCount);
        objectSend.put("tokenHeader", FractionChooser.tokenHeader);

        JSONObject resultObject = send(objectSend.toString());

        if (resultObject.getString("status").equals("Success")) {
            for (int i = 0; i < tokens.length(); i++) {
                Functions.updateJSON("remove", location + FractionChooser.tokenHeader.get(i).toString() + ".json",
                        tokens.getString(i));
            }
            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("response", resultObject);
            result.put("data", contentObject);
            result.put("message", "");
            result.put("status", "true");
            // Instant end = Instant.now();
            // Duration timeElapsed = Duration.between(start, end);
            // count++;
            // String[] data = {String.valueOf(count),
            // String.valueOf((timeElapsed.getSeconds()))};
            // writeDataLineByLine(data);
            return result.toString();
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


    @RequestMapping(value = {"/initiateNftTransaction"}, method = {RequestMethod.POST}, produces = {"application/json", "application/xml"})
    public static String initiateNftTransaction(@RequestBody RequestModel requestModel) throws Exception {
        if (!mainDir())
            return checkRubixDir();
        if (!Basics.mutex)
            start();
        String buyerDID = requestModel.getBuyer();
        String nftTokenIpfsHash = requestModel.getNftToken();
        int amount = requestModel.getAmount();
        String comments = requestModel.getComment();
        int type = requestModel.getType();
        if (!buyerDID.startsWith("Qm")) {
            String contactsTable =readFile(DATA_PATH + "Contacts.json");
            JSONArray contactsArray = new JSONArray(contactsTable);
            for (int i = 0; i < contactsArray.length(); i++) {
                if (contactsArray.getJSONObject(i).getString("nickname").equals(buyerDID))
                    buyerDID = contactsArray.getJSONObject(i).getString("did");
            }
        }
        JSONObject objectSend = new JSONObject();
        objectSend.put("nftToken", nftTokenIpfsHash);
        objectSend.put("buyerDidIpfsHash", buyerDID);
        objectSend.put("type", type);
        objectSend.put("comment", comments);
        objectSend.put("amount", amount);
        JSONObject resultObject = sendNft(objectSend.toString());
        if (resultObject.getString("status").equals("Success")) {
            JSONObject jSONObject1 = new JSONObject();
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("response", resultObject);
            jSONObject1.put("data", jSONObject2);
            jSONObject1.put("message", "");
            jSONObject1.put("status", "true");
            return jSONObject1.toString();
        }
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", resultObject);
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
        return APIHandler.create(type).toString();

    }

    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public String Create(@RequestParam("image") MultipartFile imageFile, @RequestParam("data") String value)
            throws Exception {
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
}
