package com.rubix.core.Resources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;

import static com.rubix.Resources.Functions.*;
import static com.rubix.Resources.Functions.readFile;
import static com.rubix.core.Controllers.Basics.checkRubixDir;
import static com.rubix.core.Controllers.Basics.location;

public class CallerFunctions {

    public static boolean mainDir() throws JSONException, IOException {
        String folderStatus = checkRubixDir();
        JSONObject folder = new JSONObject(folderStatus);
        return folder.getString("status").contains("true");
    }

    public static void createWorkingDirectory() throws IOException, JSONException {
        pathSet();
        setDir();
        location = dirPath + "PaymentsApp/";
        File workingDirectory = new File(location);
        workingDirectory.mkdir();
        JSONArray array = new JSONArray();
        File bank00File = new File(location + "BNK00.json");
        File bank01File = new File(location + "BNK01.json");
        File bank10File = new File(location + "BNK10.json");
        File bank11File = new File(location + "BNK11.json");
        FileWriter bank00writer = new FileWriter(bank00File, false);
        bank00writer.write(array.toString());
        bank00writer.close();
        FileWriter bank01writer = new FileWriter(bank01File, false);
        bank01writer.write(array.toString());
        bank01writer.close();
        FileWriter bank10writer = new FileWriter(bank10File, false);
        bank10writer.write(array.toString());
        bank10writer.close();
        FileWriter bank11writer = new FileWriter(bank11File, false);
        bank11writer.write(array.toString());
        bank11writer.close();

        JSONArray tokenMapArray = new JSONArray();
        JSONObject bank00Object = new JSONObject();
        bank00Object.put("type", "BNK00");
        bank00Object.put("value", 1);

        JSONObject bank01Object = new JSONObject();
        bank01Object.put("type", "BNK01");
        bank01Object.put("value", 10);

        JSONObject bank10Object = new JSONObject();
        bank10Object.put("type", "BNK10");
        bank10Object.put("value", 100);

        JSONObject bank11Object = new JSONObject();
        bank11Object.put("type", "BNK11");
        bank11Object.put("value", 1000);

        tokenMapArray.put(bank00Object);
        tokenMapArray.put(bank01Object);
        tokenMapArray.put(bank10Object);
        tokenMapArray.put(bank11Object);

        File tokenMapFile = new File(location + "TokenMap.json");
        FileWriter tokenWriter = new FileWriter(tokenMapFile, false);
        tokenWriter.write(tokenMapArray.toString());
        tokenWriter.close();


        File contactsFile = new File(DATA_PATH + "Contacts.json");
        if(!contactsFile.exists())
            contactsFile.createNewFile();
        writeToFile(DATA_PATH + "Contacts.json", new JSONArray().toString(), false);
    }

    public static void deleteFolder(File file){
        for (File subFile : file.listFiles()) {
            if(subFile.isDirectory()) {
                deleteFolder(subFile);
            } else {
                subFile.delete();
            }
        }
        file.delete();
    }

    public static int getBalance() throws JSONException {
        int balance = 0;
        String tokenMapFile = readFile(location + "TokenMap.json");
        JSONArray tokenMapArray = new JSONArray(tokenMapFile);

        for (int i = 0; i < tokenMapArray.length(); i++) {
            String bankFile = readFile(location + tokenMapArray.getJSONObject(i).getString("type") + ".json");
            JSONArray bankArray = new JSONArray(bankFile);
            int tokenCount = bankArray.length();
            int value = tokenCount * tokenMapArray.getJSONObject(i).getInt("value");
            balance = balance + value;
        }

        return balance;
    }

    public static int getNftCount() throws JSONException {
        int nftCount = 0;
        try {
            DirectoryStream<Path> nftTokenFolderStream = Files.newDirectoryStream(Paths.get(NFT_TOKENS_PATH, new String[0]));
            for (Path path : nftTokenFolderStream)
                nftCount++;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nftCount;
    }
}
