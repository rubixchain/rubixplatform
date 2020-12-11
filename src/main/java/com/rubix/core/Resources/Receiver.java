package com.rubix.core.Resources;

import com.rubix.Resources.Functions;
import com.rubix.TokenTransfer.TokenReceiver;
import com.rubix.core.Controllers.Basics;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
                    JSONArray tokenHeader = tokenResult.getJSONArray("tokenHeader");

                    for (int i = 0; i < tokens.length(); i++) {
                        String bank = tokenHeader.getString(i);
                        String bankFile = readFile(Basics.location + bank + ".json");
                        JSONArray bankArray = new JSONArray(bankFile);
                        JSONObject tokenObject = new JSONObject();
                        tokenObject.put("tokenHash", tokens.getString(i));
                        bankArray.put(tokenObject);
                        Functions.writeToFile(Basics.location + bank + ".json", bankArray.toString(), false);

                    }
                }

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
