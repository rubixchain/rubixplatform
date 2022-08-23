package com.rubix.core.NFTResources;

import com.rubix.NFT.NftSeller;
import static com.rubix.Resources.Functions.*;
import com.rubix.core.Controllers.Basics;

import org.json.JSONArray;
import org.json.JSONObject;


public class NFTReceiver implements Runnable {

    @Override
    public void run() {
        while (true) {
            try {
                /* String resultStr=Buyer.receive();
                JSONObject resulObject = new JSONObject(resultStr);
                if (resulObject.getString("status").contains("Success")) {
                    String nftToken = resulObject.getString("tokens");
                }  */

                NftSeller.receive();
                /* JSONObject tokenResult = new JSONObject(result);
                if (tokenResult.getString("status").contains("Success")) {
                    JSONArray tokens = tokenResult.getJSONArray("tokens");

                    for (int i = 0; i < tokens.length(); i++) {
                        String bankFile = readFile(Basics.location + "BNK00.json");
                        JSONArray bankArray = new JSONArray(bankFile);
                        JSONObject tokenObject = new JSONObject();
                        tokenObject.put("tokenHash", tokens.getString(i));
                        bankArray.put(tokenObject);
                        writeToFile(Basics.location + "BNK00.json", bankArray.toString(), false);

                    }
                } */
                
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
}
}
