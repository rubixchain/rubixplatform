package com.rubix.core.Resources;

import static com.rubix.Resources.APIHandler.addPublicData;
import static com.rubix.core.Controllers.Basics.repo;

import com.rubix.Resources.Functions;

import org.json.JSONException;

public class Background implements Runnable {
    @Override
    public void run() {
        while (true) {

            try {
                Functions.tokenBank();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            repo();

            addPublicData();

            System.out.println("Background Checks Executed");
            try {
                Thread.sleep(600000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
