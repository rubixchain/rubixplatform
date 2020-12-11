package com.rubix.core.Controllers;

import com.rubix.Resources.Functions;
import com.rubix.core.Fractionalisation.FractionChooser;
import com.rubix.core.Resources.RequestModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import static RubixDID.DIDCreation.DIDimage.createDID;
import static com.rubix.Resources.APIHandler.send;
import static com.rubix.Resources.Functions.*;
import static com.rubix.Resources.IntegrityCheck.didIntegrity;
import static com.rubix.Resources.IntegrityCheck.message;
import static com.rubix.core.Controllers.Basics.*;
import static com.rubix.core.Controllers.Basics.start;
import static com.rubix.core.Resources.CallerFunctions.*;

@RestController
public class Operations {

    @RequestMapping(value = "/initiateTransaction", method = RequestMethod.POST,
            produces = {"application/json", "application/xml"})
    public static String initiateTransaction(@RequestBody RequestModel requestModel) throws Exception {
        if (!mainDir())
            return checkRubixDir();
        if(!Basics.mutex)
            start();

        String recDID = requestModel.getReceiver();
        int tokenCount = requestModel.getTokenCount();
        String comments = requestModel.getComment();
        
        JSONArray tokens = FractionChooser.calculate(tokenCount);
        JSONObject objectSend = new JSONObject();
        objectSend.put("tokens", tokens);
        objectSend.put("recDID", recDID);
        objectSend.put("comments", comments);
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
