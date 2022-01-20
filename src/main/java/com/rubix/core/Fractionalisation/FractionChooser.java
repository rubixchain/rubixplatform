package com.rubix.core.Fractionalisation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.rubix.Resources.Functions;
import com.rubix.core.Controllers.Basics;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FractionChooser {
    public static String output;
    public static JSONArray tokenHeader;

    public static Logger FractionChooserLogger = Logger.getLogger(FractionChooser.class);

    public static JSONArray calculate(int amount) {
        JSONArray tokensList = new JSONArray();
        tokenHeader = new JSONArray();
        JSONObject tknmap = new JSONObject();

        try {
            int i, j, index = 0, valueInt, size;
            JSONObject tempJsonObject;
            JSONArray tempJsonArray, mapList;
            String type, lists;
            LinkedHashMap map = new LinkedHashMap();
            LinkedHashMap usedmap = new LinkedHashMap();
            List<JSONArray> bnk = new ArrayList<>();
            if (amount < 1) {
                FractionChooserLogger.warn("Invalid Transaction Amount");
                output = "Please make a valid transaction";
                return tokensList;
            }

            mapList = new JSONArray(Functions.readFile(Basics.location + "TokenMap.json"));

            for (i = 0; i < mapList.length(); i++) {
                tempJsonObject = mapList.getJSONObject(i);
                type = tempJsonObject.getString("type");
                valueInt = tempJsonObject.getInt("value");
                tknmap.put(String.valueOf(valueInt), type);
                lists = Functions.readFile(Basics.location + type + ".json");
                tempJsonArray = new JSONArray(lists);
                bnk.add(i, tempJsonArray);
                size = tempJsonArray.length();
                map.put(valueInt, size);
                usedmap.put(valueInt, 0);

            }

            List<Integer> keyList = new ArrayList<Integer>(map.keySet());
            for (i = map.size() - 1; i > 0; i--) {
                if (keyList.get(i) <= amount) {
                    index = i;
                    break;
                }
            }

            while (amount != 0) {
                valueInt = keyList.get(index);
                if ((int) map.get(valueInt) > 0 && valueInt <= amount) {
                    amount -= valueInt;
                    int temp = (int) usedmap.get(valueInt);
                    int temp1 = (int) map.get(valueInt);
                    usedmap.put(valueInt, ++temp);
                    map.put(valueInt, --temp1);
                } else if (index != 0)
                    index--;
                else {
                    FractionChooserLogger.warn("Insufficient Amount in the Wallet. Required " + amount + " currency");
                    output = "Balance not sufficient, need " + amount + " more currency";
                    return tokensList;
                }

                if (valueInt > amount && index != 0)
                    index--;
            }

            for (i = 0; i < keyList.size(); i++) {
                for (j = 0; j < (int) usedmap.get(keyList.get(i)); j++) {
                    tokensList.put(bnk.get(i).getJSONObject(j).getString("tokenHash"));
                    tokenHeader.put(tknmap.get(String.valueOf(keyList.get(i))));
                }
            }
        } catch (JSONException e) {
            FractionChooserLogger.error("JSON Exception Occurred", e);
            e.printStackTrace();
        }
        FractionChooserLogger.debug("Tokens chosen to be sent: " + tokensList);

        return tokensList;

    }
}
