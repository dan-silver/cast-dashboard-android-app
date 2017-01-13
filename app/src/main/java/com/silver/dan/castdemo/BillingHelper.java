package com.silver.dan.castdemo;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.RemoteException;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * Created by dan on 1/12/17.
 */

public class BillingHelper {
    public static int UPGRADE_RETURN_CODE = 20002;

    private static Activity activity;
    public static boolean hasPurchased = false;

    public static void fetchUpgradedStatus(final IInAppBillingService mService, final SimpleCallback<Boolean> callback) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                ArrayList<String> skuList = new ArrayList<> ();
                skuList.add("cast_dashboard_pro");
                Bundle querySkus = new Bundle();
                querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

                try {
                    Bundle activeSubs = mService.getPurchases(3, activity.getPackageName(), "subs", null);



                    int response = activeSubs.getInt("RESPONSE_CODE");
                    if (response == 0) {

                        ArrayList<String> ownedSkus =
                                activeSubs.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                        ArrayList<String>  purchaseDataList =
                                activeSubs.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                        ArrayList<String>  signatureList =
                                activeSubs.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");

                        if (purchaseDataList == null) {
                            callback.onError(null);
                            return;
                        }

                        // list empty when PRO not purchased
                        hasPurchased = purchaseDataList.size() != 0;
                        callback.onComplete(hasPurchased);

                    }


                } catch (RemoteException e) {
                    e.printStackTrace();
                    callback.onError(e);
                }

            }
        });

        t.start();


    }

    public static void init(Activity activity) {
        BillingHelper.activity = activity;
    }

    public static void purchaseUpgrade(final IInAppBillingService mService) {
        Thread t = new Thread(new Runnable() {
            public void run() {

                Bundle buyIntentBundle;
                try {
                    buyIntentBundle = mService.getBuyIntent(3, activity.getPackageName(),
                            "cast_dashboard_pro", "subs", UUID.randomUUID().toString());

                    PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                    if (pendingIntent == null) {
                        return;
                    }

                    if (buyIntentBundle.getInt("RESPONSE_CODE") == 0) {
                        // Start purchase flow (this brings up the Google Play UI).
                        // Result will be delivered through onActivityResult().
                        activity.startIntentSenderForResult(pendingIntent.getIntentSender(), UPGRADE_RETURN_CODE, new Intent(),
                                0, 0, 0);
                    }

                } catch (RemoteException | IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }

            }
        });
        t.start();
    }

    public static boolean extractHasPurchased(int resultCode, Intent intent) {
        String purchaseData = intent.getStringExtra("INAPP_PURCHASE_DATA");

        if (resultCode == RESULT_OK) {
            try {
                JSONObject jo = new JSONObject(purchaseData);
//                    String sku = jo.getString("productId");
                if (jo.getInt("purchaseState") == 0)
                    return true;
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;

    }
}
