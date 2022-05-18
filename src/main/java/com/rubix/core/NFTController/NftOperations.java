package com.rubix.core.NFTController;

import static com.rubix.KeyPairGen.RsaKeyGen.*;

import static com.rubix.core.Controllers.Basics.checkRubixDir;

import static com.rubix.core.Controllers.Basics.start;

import static com.rubix.core.Resources.CallerFunctions.mainDir;

import static com.rubix.NFTResources.NFTAPIHandler.*;
import static com.rubix.NFTResources.EnableNft.*;
import static com.rubix.NFTResources.NFTFunctions.*;
import static com.rubix.Resources.IPFSNetwork.*;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.rubix.Resources.Functions.*;
import static com.rubix.Resources.IntegrityCheck.*;

import com.rubix.NFTResources.NFTAPIHandler;
import com.rubix.core.Controllers.Basics;
import com.rubix.core.NFTResources.NftRequestModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:1898")
@RestController
public class NftOperations{
    @RequestMapping(value = { "/initiateNftTransfer" }, method = { RequestMethod.POST }, produces = {
            "application/json", "application/xml" })
    public static String initiateNftTransaction(@RequestBody NftRequestModel requestModel) throws Exception {

        String sellerDid, nftTokenIpfsHash, comments, sellerPubKeyIpfsHash, saleContractIpfsHash, buyerPubKeyIpfsHash="",
                pvtKey, pvtKeyPass;
        int type;
        double tokenCount;
        if (!mainDir())
            return checkRubixDir();
        if (!Basics.mutex)
            start();

        JSONObject objectSend = new JSONObject();
        JSONObject result = new JSONObject();
        String buyerPeerID = getPeerID(DATA_PATH + "DID.json");
        String buyerDID = getValues(DATA_PATH + "DataTable.json", "didHash", "peerid", buyerPeerID);
        int p2pFlag = requestModel.getP2pFlag();
        /* if (p2pFlag != 0 || p2pFlag != 1) {
            result.put("data", "");
            result.put("message", "p2pFlag value shouldbe either 0 0r 1");
            result.put("status", "false");

            return result.toString();
        }
 */
        if (requestModel.getSellerDid().isBlank()) {
            result.put("data", "");
            result.put("message", "Seller DID cannot be Empty");
            result.put("status", "false");

            return result.toString();
        }

        if (requestModel.getNftToken().isBlank()) {
            result.put("data", "");
            result.put("message", "NFT TOken value cannot be Empty");
            result.put("status", "false");

            return result.toString();
        }

        /* if (requestModel.getType() != 1 || requestModel.getType() != 2) {
            result.put("data", "");
            result.put("message", "Wrong value of type for quorum selction. Choose either 1 or 2");
            result.put("status", "false");

            return result.toString();
        } */

        sellerDid = requestModel.getSellerDid();
        nftTokenIpfsHash = requestModel.getNftToken();
        type = requestModel.getType();
        comments = requestModel.getComment();

        objectSend.put("nftToken", nftTokenIpfsHash);
        objectSend.put("buyerDidIpfsHash", buyerDID);
        objectSend.put("sellerDidIpfsHash", sellerDid);
        objectSend.put("type", type);
        objectSend.put("comment", comments);
        objectSend.put("p2pFlag", p2pFlag);
        objectSend.put("userHash", requestModel.getUserHash());

        if (p2pFlag == 0) {
            if (requestModel.getSellerPubKeyIpfsHash().isBlank()) {
                result.put("data", "");
                result.put("message", "Seller Public Key Ipfs Hash cannot be Empty");
                result.put("status", "false");

                return result.toString();
            }

            if (requestModel.getBuyerPubKeyIpfsHash().isBlank()) {
                result.put("data", "");
                result.put("message", "Buyer Public Key Ipfs Hash cannot be Empty");
                result.put("status", "false");

                return result.toString();
            }

            if (requestModel.getSaleContractIpfsHash().isBlank()) {
                result.put("data", "");
                result.put("message", "Sale Contract Ipfs Hash cannot be Empty");
                result.put("status", "false");

                return result.toString();
            }

            if (requestModel.getPvtKey().isBlank()) {
                result.put("data", "");
                result.put("message", "Private Key cannot be Empty");
                result.put("status", "false");

                return result.toString();
            }

            if (requestModel.getPvtKeyPass().isBlank()) {
                result.put("data", "");
                result.put("message", "Private Key password cannot be Empty");
                result.put("status", "false");

                return result.toString();
            }

            sellerPubKeyIpfsHash = requestModel.getSellerPubKeyIpfsHash();
            buyerPubKeyIpfsHash = requestModel.getBuyerPubKeyIpfsHash();
            saleContractIpfsHash = requestModel.getSaleContractIpfsHash();
            pvtKey = requestModel.getPvtKey();
            pvtKeyPass = requestModel.getPvtKeyPass();

            tokenCount = requestModel.getAmount();
            int intPart = (int) tokenCount;
            double decimal = tokenCount - intPart;
            decimal = formatAmount(decimal);
            System.out.println("Input Value: " + tokenCount);
            System.out.println("Integer Part: " + intPart);
            System.out.println("Decimal Part: " + decimal);

            String[] div = String.valueOf(tokenCount).split("\\.");
            System.out.println("Number of decimals: " + div[1].length());

            if (div[1].length() > 3) {
                System.out.println("Amount can have only 3 precisions maximum");
                JSONObject resultObject = new JSONObject();
                resultObject.put("did", "");
                resultObject.put("tid", "null");
                resultObject.put("status", "Failed");
                resultObject.put("message", "Amount can have only 3 precisions maximum");

                JSONObject contentObject = new JSONObject();
                contentObject.put("response", resultObject);
                result.put("data", contentObject);
                result.put("message", "");
                result.put("status", "false");
                return result.toString();

            }

            Double available = getBalance();
            if (tokenCount > available) {
                System.out.println("Amount greater than available");
                JSONObject resultObject = new JSONObject();
                resultObject.put("did", "");
                resultObject.put("tid", "null");
                resultObject.put("status", "Failed");
                resultObject.put("message", "Amount greater than available");

                JSONObject contentObject = new JSONObject();
                contentObject.put("response", resultObject);
                result.put("data", contentObject);
                result.put("message", "");
                result.put("status", "false");
                return result.toString();
            }

            objectSend.put("sellerPubKeyIpfsHash", sellerPubKeyIpfsHash);
            objectSend.put("saleContractIpfsHash", saleContractIpfsHash);
            objectSend.put("buyerPubKeyIpfsHash", buyerPubKeyIpfsHash);
            objectSend.put("buyerPvtKey", pvtKey);
            objectSend.put("buyerPvtKeyPass", pvtKeyPass);
            objectSend.put("amount", tokenCount);
        } else {
            objectSend.put(buyerPubKeyIpfsHash, getPubKeyIpfsHash());
        }

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
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", resultObject);
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "false");
        return result.toString();

    }

    @RequestMapping(value = "/generateRac", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public static String generateRac(@RequestBody NftRequestModel requestModel) {
        String result = null;
        JSONObject data = new JSONObject();

        try {
            if (!mainDir())
                return checkRubixDir();
            if (!Basics.mutex)
                start();

            String creatorPeerID = getPeerID(DATA_PATH + "DID.json");
            String creatorDid = getValues(DATA_PATH + "DataTable.json", "didHash", "peerid", creatorPeerID);
            JSONObject creatorInput = new JSONObject(requestModel.getCreatorInput());
            if (requestModel.getCreatorPubKeyIpfsHash().length() != 0
                    && requestModel.getCreatorPubKeyIpfsHash() != null) {
                creatorInput.put("creatorPubKeyIpfsHash", requestModel.getCreatorPubKeyIpfsHash());
                data.put("creatorPubKeyIpfsHash", requestModel.getCreatorPubKeyIpfsHash());
            } else {
                String creatorPubKeyIpfsHash = getPubKeyIpfsHash();
                creatorInput.put("creatorPubKeyIpfsHash", creatorPubKeyIpfsHash);
                data.put("creatorPubKeyIpfsHash", creatorPubKeyIpfsHash);
            }
            data.put("racType", requestModel.getType());
            data.put("creatorDid", creatorDid);
            data.put("totalSupply", requestModel.getTotalSupply());
            data.put("contentHash", requestModel.getContentHash());
            data.put("creatorInput", creatorInput.toString());
            if (requestModel.getUrl().length() != 0 && requestModel.getUrl() != null) {
                data.put("url", requestModel.getUrl());
            }

            if (requestModel.getPvtKey().length() != 0 && requestModel.getPvtKey() != null) {
                data.put("pvtKeyStr", requestModel.getPvtKey());
            }

            data.put("pvtKeyPass", requestModel.getPvtKeyPass());

            result = generateRacToken(data.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @RequestMapping(value = "/generateRsaKeys", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public static String generateRsaKeys(@RequestBody NftRequestModel nftRequestModel) {

        JSONObject response = new JSONObject();
        if (nftRequestModel.getPvtKeyPass().isBlank()) {
            response.put("data", "");
            response.put("message", "Private Key password cannot be Empty");
            response.put("status", "false");

            return response.toString();
        }

        if (nftRequestModel.getReturnKey()!=0 || nftRequestModel.getReturnKey()!=1) {
            response.put("data", "");
            response.put("message", "Return Key flag should be either 0 or 1");
            response.put("status", "false");

            return response.toString();
        }


        String password = nftRequestModel.getPvtKeyPass();
        int returnKey = nftRequestModel.getReturnKey();
        

        try {
            if (!mainDir()) {
                return checkDirectory();
            }

            boolean keyFileCheck = checkKeyFiles();
            if (!keyFileCheck) {
                System.out.println("private & public key not generated");
                System.out.println("generating key files");
            }

            if (returnKey == 0) {
                generateKeyPair(password);
                keyFileCheck = checkKeyFiles();
                if (!keyFileCheck) {
                    response.put("message", "Key Files not generated");
                    response.put("status", "false");
                } else {
                    response.put("message", "Key Files generated and stored in Rubix/DATA folder");
                    response.put("status", "true");
                }
            }
            if (returnKey == 1) {
                String res = genAndRetKey(password);
                response.put("message", "Key Files Generated");
                response.put("status", "true");
                response.put("content", res);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return response.toString();
    }

    @RequestMapping(value = "/enableNFT", method = RequestMethod.GET, produces = { "application/json",
            "application/xml" })
    public static String enableNFT() {

        try {
            if (!mainDir()) {
                return checkDirectory();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        nftPathSet();
        // enableNft();
        JSONObject response = new JSONObject();
        boolean nftWalletcheck = checkWalletCompatibiltiy();
        if (nftWalletcheck) {
            System.out.println("NFT Wallet not Enabled.");
            System.out.println("Starting to enable NFT wallet");
            enableNft();
        }
        nftWalletcheck = checkWalletCompatibiltiy();
        if (nftWalletcheck) {
            response.put("message", "NFT wallet not enabled");
            response.put("status", "false");
            return response.toString();
        }

        response.put("message", "NFT wallet Enabled");
        response.put("status", "true");
        return response.toString();
    }

    @RequestMapping(value = "/createNftSaleContract", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public static String nftSaleContract(@RequestBody NftRequestModel nftRequestModel) {
        JSONObject response = new JSONObject();

        if (nftRequestModel.getSellerDid().isBlank()) {
            response.put("contractIpfsHash", "");            
            response.put("message", "Seller DID cannot be Empty");
            response.put("status", "false");

            return response.toString();
        }

        if(nftRequestModel.getNftToken().isBlank())
        {
            response.put("contractIpfsHash", "");            
            response.put("message", "NFT TOken value cannot be Empty");
            response.put("status", "false");

            return response.toString();
        }

        if(nftRequestModel.getPvtKeyPass().isBlank())
        {
            response.put("contractIpfsHash", "");            
            response.put("message", "Private Key password cannot be Empty");
            response.put("status", "false");

            return response.toString();
        }
        String sellerDid = nftRequestModel.getSellerDid();
        String nftToken = nftRequestModel.getNftToken();
        Double rbtAmunt = nftRequestModel.getAmount();
        String sellerPvtKeyPass = nftRequestModel.getPvtKeyPass();
        String sellerPvtKey;
        JSONObject data = new JSONObject();

        if (nftRequestModel.getPvtKey().length() != 0 && nftRequestModel.getPvtKey() != null) {
            sellerPvtKey = nftRequestModel.getPvtKey();
            data.put("sellerPvtKey", sellerPvtKey);
        }
        else{
            response.put("contractIpfsHash", "");            
            response.put("message", "Private Key cannot be Empty");
            response.put("status", "false");

            return response.toString();
        }

        data.put("sellerDID", sellerDid);
        data.put("nftToken", nftToken);
        data.put("rbtAmount", rbtAmunt);
        data.put("sellerPvtKeyPass", sellerPvtKeyPass);

        String result = createNftSaleContract(data.toString());

        JSONObject responseObj = new JSONObject(result);


        if (responseObj.getString("status").equals("Failed")) {
            response.put("message", responseObj.getString("message"));
            response.put("status", "false");
            response.put("contractIpfsHash", "");
        } else {
            response.put("message", "Sale Contract Generated");
            response.put("status", "true");
            response.put("contractIpfsHash", responseObj.getString("saleContractIpfsHash"));
        }
        return response.toString();
    }

    @RequestMapping(value = "/getNftTxnDetails", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public String getNftTxnDetails(@RequestBody NftRequestModel nftRequestModel) {

        try {
            if (!mainDir())
                return checkRubixDir();
            if (!mutex)
                start();
        } catch (IOException e) {
            // TODO: handle exception
        }

        String txnId = nftRequestModel.getTransactionID();
        if (!txnIdIntegrity(txnId)) {
            JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("message", message);
            result.put("data", contentObject);
            result.put("message", "");
            result.put("status", "false");
            result.put("error_code", 1311);
            return result.toString();
        }

        if (nftTransactionDetails(txnId).length() == 0) {
            return noTxnError();
        }

        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("response", nftTransactionDetails(txnId));
        contentObject.put("count", nftTransactionDetails(txnId).length());
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "true");
        return result.toString();
    }

    private String noTxnError() {
        JSONObject result = new JSONObject();
        JSONObject contentObject = new JSONObject();
        contentObject.put("message", "No transactions found!");
        result.put("data", contentObject);
        result.put("message", "");
        result.put("status", "false");
        result.put("error_code", 1311);
        return result.toString();
    }

    @RequestMapping(value = "/getNftTxnByDate", method = RequestMethod.POST, produces = { "application/json",
            "application/xml" })
    public String getNftTxnByDate(@RequestBody NftRequestModel nftRequestModel) {
        try {
            if (!mainDir())
                return checkRubixDir();
            if (!mutex)
                start();
        } catch (IOException e) {
            // TODO: handle exception
        }

        JSONObject result = new JSONObject();
        String s = nftRequestModel.getsDate();
        String e = nftRequestModel.geteDate();

        try {
            String strDateFormat = "yyyy-MM-dd"; // Date format is Specified
            SimpleDateFormat objSDF = new SimpleDateFormat(strDateFormat);
            Date date1 = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy").parse(s);
            Date date2 = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy").parse(e);
            String start = objSDF.format(date1);
            String end = objSDF.format(date2);

            if (!dateIntegrity(start, end)) {
                // JSONObject result = new JSONObject();
                JSONObject contentObject = new JSONObject();
                contentObject.put("response", message);
                result.put("data", contentObject);
                result.put("message", "");
                result.put("status", "false");
                result.put("error_code", 1311);
                return result.toString();
            }

            if (nftTransactionsByDate(s, e).length() == 0) {
                return noTxnError();
            }

            // JSONObject result = new JSONObject();
            JSONObject contentObject = new JSONObject();
            contentObject.put("response", nftTransactionsByDate(s, e));
            contentObject.put("count", nftTransactionsByDate(s, e).length());
            result.put("data", contentObject);
            result.put("message", "");
            result.put("status", "true");
        } catch (ParseException ex) {
            // TODO: handle exception
        }
        return result.toString();

    }

}