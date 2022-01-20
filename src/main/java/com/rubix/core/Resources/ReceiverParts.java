package com.rubix.core.Resources;

import static com.rubix.Resources.Functions.PAYMENTS_PATH;
import static com.rubix.Resources.Functions.readFile;
import static com.rubix.Resources.Functions.writeToFile;

import java.io.File;
import java.io.IOException;

import com.rubix.Denominations.ReceiveTokenParts;
import com.rubix.Resources.Functions;
import com.rubix.core.Controllers.Basics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReceiverParts implements Runnable {
    @Override
    public void run() {
        while (true) {
            try {
                String result = ReceiveTokenParts.receive();
                JSONObject tokenResult = new JSONObject(result);
                if (tokenResult.getString("status").contains("Success")) {
                    String tokens = tokenResult.getString("tokens");

                    File partTokensFile = new File(PAYMENTS_PATH.concat("PartsToken.json"));
                    if (!partTokensFile.exists()) {
                        partTokensFile.createNewFile();
                        writeToFile(partTokensFile.toString(), "[]", false);
                    }

                    boolean existingFlag = false;
                    String bankFile = readFile(Basics.location + "PartsToken.json");
                    JSONArray bankArray = new JSONArray(bankFile);
                    for (int i = 0; i < bankArray.length(); i++) {
                        if (bankArray.getJSONObject(i).getString("tokenHash").equals(tokens))
                            existingFlag = true;
                    }
                    if (!existingFlag) {
                        JSONObject tokenObject = new JSONObject();
                        tokenObject.put("tokenHash", tokens);
                        bankArray.put(tokenObject);
                        Functions.writeToFile(Basics.location + "PartsToken.json", bankArray.toString(), false);
                    }

                }

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
