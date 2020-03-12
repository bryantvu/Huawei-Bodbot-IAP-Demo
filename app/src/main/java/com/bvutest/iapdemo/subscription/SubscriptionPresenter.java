/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.bvutest.iapdemo.subscription;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.bvutest.iapdemo.callback.ProductInfoCallback;
import com.bvutest.iapdemo.callback.PurchaseIntentResultCallback;
import com.bvutest.iapdemo.callback.QueryPurchasesCallback;
import com.bvutest.iapdemo.common.ExceptionHandle;
import com.bvutest.iapdemo.common.IapRequestHelper;
import com.huawei.hms.iap.Iap;
import com.huawei.hms.iap.IapClient;
import com.huawei.hms.iap.entity.OrderStatusCode;
import com.huawei.hms.iap.entity.OwnedPurchasesResult;
import com.huawei.hms.iap.entity.ProductInfo;
import com.huawei.hms.iap.entity.ProductInfoResult;
import com.huawei.hms.iap.entity.PurchaseIntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionPresenter implements SubscriptionContract.Presenter {

    private static final String TAG = "IapPresenter";

//    private CordovaInterface view;

    private SubscriptionContract.View view;

//    public CallbackContext callbackContextPublic;

//    HuaweiBillingPlugin huaweiBillingPlugin = this;

    private OwnedPurchasesResult cacheOwnedPurchasesResult;

    public SubscriptionPresenter(SubscriptionContract.View view) {
//        setView(viewDummy);
        Log.d(TAG,"SubscriptionPresenter");
        setView(view);
    }

    @Override
    public void setView(SubscriptionContract.View view) {
        if (null == view) {
            throw new NullPointerException("can not set null view");
        }
        this.view = view;
    }

//    @Override
//    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
//        super.initialize(cordova, webView);
//        this.cordova = cordova;
//        this.view = cordova;
//    }
//
//    @Override
//    public void setView(com.example.iapdemo.subscription.SubscriptionContract.View view) {
//        this.view = this.cordova;
////        huaweiBillingPlugin.
////        if (null == view) {
////            throw new NullPointerException("can not set null view");
////        }
////        this.view = view;
//    }


//    @Override
//    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) {
//        Log.d(TAG, "executing on android");
//
////        this.callbackContextPublic = callbackContext;
//
//        if ("init".equals(action)) {
//
//            ArrayList<String> list = new ArrayList<String>();
//            if (args != null) {
//                int len = args.length();
//                for (int i=0;i<len;i++){
//                    try {
//                        list.add(args.get(i).toString());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            load(list);
//
//        } else if ("subscribe".equals(action)) {
//
//            try {
//                buy(args.getString(0));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        } else if ("restorePurchases".equals(action)) {
//
//            refreshSubscription();
//
//        }

//    } else if ("buy".equals(action)) {
//      try {
//        subscriptionPresenter.buy(args.getString(0));
//      } catch (JSONException e) {
//        e.printStackTrace();
//      }
//    } else if ("consumePurchase".equals(action)) {
//      return consumePurchase(args, callbackContext);
//    } else if ("getSkuDetails".equals(action)) {
////      return getSkuDetails(args, callbackContext);

//        return false;
//    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        super.onActivityResult(requestCode, resultCode, intent);
//    }

    //    @Override
    public void load(List<String> productIds) {
        queryProducts(productIds);
        refreshSubscription();
    }

    //    @Override
    public void refreshSubscription() {
        querySubscriptions(status -> {
            if(status){
                try {
                    JSONArray response = new JSONArray();
                    JSONObject detailsJson = new JSONObject();
                    detailsJson.put("orderId", "");
                    detailsJson.put("packageName", "");
                    detailsJson.put("productId", "huaweipurchase");
                    detailsJson.put("purchaseTime", "");
                    detailsJson.put("purchaseState", "");
                    detailsJson.put("purchaseToken", "");
                    detailsJson.put("signature", "");
                    detailsJson.put("type", "");
                    detailsJson.put("currency", "");
                    detailsJson.put("receipt", "");
                    response.put(detailsJson);
                    Log.d(TAG, "refreshSubscription >> "+response);
//                        callbackContextPublic.success(response);
                } catch (JSONException e) {
//                        callbackContextPublic.error(e.getMessage());
                }
            }else{
//                    callbackContextPublic.error("Purchase succeeded but success handler failed");
            }
        });
    }

    private void queryProducts(List<String> productIds) {
        Log.e(TAG, "queryProducts: "+productIds.toString());
        IapRequestHelper.obtainProductInfo(Iap.getIapClient(view.getActivity()), productIds, IapClient.PriceType.IN_APP_SUBSCRIPTION, new ProductInfoCallback() {
            @Override
            public void onSuccess(final ProductInfoResult result) {
                if (null == result) {
                    Log.e(TAG, "ProductInfoResult is null");
                    return;
                }

                List<ProductInfo> productInfos = result.getProductInfoList();

                JSONArray response = new JSONArray();
                productInfos.forEach((skuDetails) ->{
                    if (skuDetails != null) {
                        JSONObject detailsJson = new JSONObject();
                        try {
                            detailsJson.put("productId", skuDetails.getProductId());
                            detailsJson.put("title", skuDetails.getProductName());
                            detailsJson.put("description", skuDetails.getProductDesc());
                            //detailsJson.put("currency", skuDetails.getPriceCurrencyCode());
                            detailsJson.put("price", skuDetails.getPrice());
                            detailsJson.put("type", skuDetails.getPriceType());
                            response.put(detailsJson);
                            Log.d(TAG, "queryProducts >> "+response);
                        } catch (JSONException e) {
//                            callbackContextPublic.error(e.getMessage());
                        }
                    }
                });
//                callbackContextPublic.success(response);
//                view.showProducts(productInfos);
            }

            @Override
            public void onFail(Exception e) {
                int errorCode = ExceptionHandle.handle(view.getActivity(), e);
                if (ExceptionHandle.SOLVED != errorCode) {
                    Log.e(TAG, "unknown error");
                }else{
                    Log.e(TAG, "errorCode: "+errorCode);
                }
//                view.showProducts(null);
            }
        });
    }



    private void querySubscriptions(final SubscriptionContract.ResultCallback<Boolean> callback) {
        IapRequestHelper.obtainOwnedPurchases(Iap.getIapClient(view.getActivity()), IapClient.PriceType.IN_APP_SUBSCRIPTION, new QueryPurchasesCallback() {
            @Override
            public void onSuccess(OwnedPurchasesResult result) {
                cacheOwnedPurchasesResult = result;
                callback.onResult(true);
            }

            @Override
            public void onFail(Exception e) {
                Log.e(TAG, "querySubscriptions exception", e);
                ExceptionHandle.handle(view.getActivity(), e);
                callback.onResult(false);
            }
        });
    }

    //    @Override
    public void buy(final String productId) {

        // clear local cache
        cacheOwnedPurchasesResult = null;
        IapClient mClient = Iap.getIapClient(view.getActivity());
        IapRequestHelper.createPurchaseIntent(mClient, productId, IapClient.PriceType.IN_APP_SUBSCRIPTION, new PurchaseIntentResultCallback() {
            @Override
            public void onSuccess(PurchaseIntentResult result) {
                if (result == null) {
                    Log.e(TAG, "PurchaseIntentResult is null");
                    return;
                }

                // you should pull up the page to complete the payment process
                try {
                    JSONObject pluginResponse = new JSONObject();
                    pluginResponse.put("paymentData", result.getPaymentData());
//                    callbackContextPublic.success(pluginResponse);
                } catch (JSONException e) {
//                    callbackContextPublic.error("Purchase succeeded but success handler failed");
                }
            }

            @Override
            public void onFail(Exception e) {
                int errorCode = ExceptionHandle.handle(view.getActivity(), e);
                if (ExceptionHandle.SOLVED != errorCode) {
                    Log.w(TAG, "createPurchaseIntent, returnCode: " + errorCode);

                    if (OrderStatusCode.ORDER_PRODUCT_OWNED == errorCode) {
                        Log.w(TAG, "already own this product");
//                        callbackContextPublic.success("already own this product");
                    } else {
//                        callbackContextPublic.error("unknown error");
                    }
                }
            }
        });
    }

    //    @Override
    public void showSubscription(String productId) {
        IapRequestHelper.showSubscription(view.getActivity(), "com.iapdemo.huawei", "101510967", productId);
    }

    //    @Override
    public void shouldOfferService(final String productId, final SubscriptionContract.ResultCallback<Boolean> callback) {
        if (null == callback || TextUtils.isEmpty(productId)) {
            Log.e(TAG, "ResultCallback is null or productId is empty");
            return;
        }

        if (null != cacheOwnedPurchasesResult) {
            Log.i(TAG, "using cache data");
            boolean shouldOffer = SubscriptionUtils.shouldOfferService(cacheOwnedPurchasesResult, productId);
            callback.onResult(shouldOffer);
        } else {
            querySubscriptions(result -> {
                boolean shouldOffer = SubscriptionUtils.shouldOfferService(cacheOwnedPurchasesResult, productId);
                callback.onResult(shouldOffer);
            });
        }
    }

}
