package com.rubix.core.Resources;

import com.rubix.Resources.Functions;
import org.json.JSONException;

import java.util.logging.Logger;

import static com.rubix.Resources.APIHandler.addPublicData;
import static com.rubix.core.Controllers.Basics.repo;

public class Background implements Runnable {
    @Override
    public void run() {
        while (true) {

            try {
                Functions.tokenBank();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                Functions.clearParts();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            repo();

            addPublicData();

            try {
                Thread.sleep(600000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }
}
