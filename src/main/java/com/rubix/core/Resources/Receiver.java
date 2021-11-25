package com.rubix.core.Resources;

import com.rubix.Resources.Functions;
import com.rubix.TokenTransfer.TokenReceiver;
import com.rubix.core.Controllers.Basics;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;

import static com.rubix.Resources.Functions.readFile;

public class Receiver implements Runnable {
    @Override
    public void run() {
        while (true) {
            try {
                String result = TokenReceiver.receive();
                JSONObject tokenResult = new JSONObject(result);
                if (tokenResult.getString("status").contains("Success")) {
                    JSONArray tokens = tokenResult.getJSONArray("tokens");

                    for (int i = 0; i < tokens.length(); i++) {
                        String bankFile = readFile(Basics.location + "BNK00.json");
                        JSONArray bankArray = new JSONArray(bankFile);
                        JSONObject tokenObject = new JSONObject();
                        tokenObject.put("tokenHash", tokens.getString(i));
                        bankArray.put(tokenObject);
                        Functions.writeToFile(Basics.location + "BNK00.json", bankArray.toString(), false);

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
