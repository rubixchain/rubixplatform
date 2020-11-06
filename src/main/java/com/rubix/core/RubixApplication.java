package com.rubix.core;

import com.rubix.Consensus.QuorumConsensus;
import com.rubix.Resources.Functions;
import com.rubix.Resources.IPFSNetwork;
import com.rubix.TokenTransfer.TokenReceiver;
import com.rubix.core.sender.APICalls;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;

import static com.rubix.Resources.Functions.*;
import static com.rubix.Resources.Functions.readFile;

@SpringBootApplication
public class RubixApplication {


	public static void main(String[] args) throws IOException, JSONException {

		System.setProperty("server.port", String.valueOf(1898));
		SpringApplication.run(RubixApplication.class, args);


	}

}
