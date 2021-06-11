package com.rubix.core.Controllers;

import com.rubix.Resources.APIHandler;
import com.rubix.Resources.Functions;
import com.rubix.core.Fractionalisation.FractionChooser;
import com.rubix.core.Resources.RequestModel;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import static RubixDID.DIDCreation.DIDimage.createDID;
import static com.rubix.Resources.APIHandler.send;
import static com.rubix.Resources.Functions.*;
import static com.rubix.core.Controllers.Basics.*;
import static com.rubix.core.Controllers.Basics.start;
import static com.rubix.core.Resources.CallerFunctions.*;

@CrossOrigin(origins = "http://localhost:1898")
@RestController
public class Operations {
//    static  int count = 0;
//    public static void writeDataLineByLine(String data[])
//    {
//        File file = new File("data.csv");
//        try {
//            FileWriter outputfile = new FileWriter(file,true);
//            CSVWriter writer = new CSVWriter(outputfile);
//            writer.writeNext(data);
//            writer.close();
//        }
//        catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }

    @RequestMapping(value = "/initiateTransaction", method = RequestMethod.POST,
            produces = {"application/json", "application/xml"})
    public static String initiateTransaction(@RequestBody RequestModel requestModel) throws Exception {
//        Instant start = Instant.now();
        if (!mainDir())
            return checkRubixDir();
        if(!Basics.mutex)
            start();

        String recDID = requestModel.getReceiver();
        int tokenCount = requestModel.getTokenCount();
        String comments = requestModel.getComment();
        int type = requestModel.getType();

        if(!recDID.startsWith("Qm")){
            String contactsTable = Functions.readFile(Functions.DATA_PATH + "Contacts.json");
            JSONArray contactsArray = new JSONArray(contactsTable);
            for (int i = 0; i < contactsArray.length(); ++i) {
                if(contactsArray.getJSONObject(i).getString("nickname").equals(recDID))
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
                Functions.updateJSON("remove", location + FractionChooser.tokenHeader.get(i).toString() + ".json", tokens.getString(i));
            }
            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("response", resultObject);
            result.put("data", contentObject);
            result.put("message", "");
            result.put("status", "true");
//            Instant end = Instant.now();
//            Duration timeElapsed = Duration.between(start, end);
//            count++;
//            String[] data = {String.valueOf(count), String.valueOf((timeElapsed.getSeconds()))};
//            writeDataLineByLine(data);
            return result.toString();
        }else{
            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("response", resultObject);
            result.put("data", contentObject);
            result.put("message", "");
            result.put("status", "true");
            return result.toString();
        }

    }

    @RequestMapping(value = "/mine", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public static String mine() throws Exception {
        if (!mainDir())
            return checkRubixDir();
        if(!Basics.mutex)
            start();
        return APIHandler.create().toString();

    }


    @RequestMapping(value = "/create", method = RequestMethod.POST,
            produces = {"application/json", "application/xml"})
    public String Create(@RequestParam("image") MultipartFile imageFile, @RequestParam("data") String value) throws Exception {
        setDir();
        File RubixFolder = new File(dirPath);
        if(RubixFolder.exists())
            deleteFolder(RubixFolder);
        JSONObject didResult = createDID(value, imageFile.getInputStream());
        if(didResult.getString("Status").contains("Success"))
            createWorkingDirectory();

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", didResult);
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }
}
