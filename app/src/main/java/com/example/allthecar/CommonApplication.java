package com.example.allthecar;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.example.allthecar.Activity.MainActivity;
import com.example.allthecar.Item.ChargingCarItem;
import com.example.allthecar.Item.ParkingItem;
import com.example.allthecar.Item.RentalCarItem;
import com.example.allthecar.Item.RepairShopItem;
import com.example.allthecar.Item.RestAreaItem;
import com.example.allthecar.Item.WashCarItem;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;

public class CommonApplication extends Application {
//    private static Context mContext;
//
//    public CommonApplication() {
//        super();
//        mContext = this;
//    }
//
//    public static Context getContext() {
//        return mContext;
//    }

    private static CommonApplication commonApplication;

    public static final String serviceKey = "ikzGO3i87SLsKkhnQmtu%2FSss5IFZkCaHkIqW63DcaOrhH4CRAxEQ09Z7JfQi66eBtdoJFE3yvStcu6x%2FeV1%2FTg%3D%3D";

    public String address1;
    public String address2;

    public int tabPositionNum = 0;

    public static final int PARKING = 0;
    public static final int WASHCAR = 1;
    public static final int RESTAREA = 2;
    public static final int REPAIRSHOP = 3;
    public static final int CHARGINGCAR = 4;
    public static final int RENTALCAR = 5;

//    public enum TabPosition {
//        PARKING(0), WASHCAR(1), RESTAREA(2), REPAIRSHOP(3), CHARGINGCAR(4), RENTALCAR(5);
//
//        private int value;
//        TabPosition (int value) {
//            this.value = value;
//        }
//        public int getValue() {
//            return value;
//        }
//    };
//    TabPosition tabPosition;

    public ArrayList<ParkingItem> parkingItemArrayList = new ArrayList<ParkingItem>();
    public ArrayList<WashCarItem> washCarItemArrayList = new ArrayList<WashCarItem>();
    public ArrayList<RestAreaItem> restAreaItemArrayList = new ArrayList<RestAreaItem>();
    public ArrayList<RepairShopItem> repairShopItemArrayList = new ArrayList<RepairShopItem>();
    public ArrayList<ChargingCarItem> chargingCarItemArrayList = new ArrayList<ChargingCarItem>();
    public ArrayList<RentalCarItem> rentalCarItemArrayList = new ArrayList<RentalCarItem>();

    @Override
    public void onCreate() {
        super.onCreate();
        commonApplication = this;
    }

    public static CommonApplication getInstance() {
        return commonApplication;
    }

    public String getTitleWithPosition() {

        String title = "";
        Log.i(">>tapPos title: ", String.valueOf(tabPositionNum));
        switch (tabPositionNum) {

            case PARKING:
                title = "주차장";
                break;

            case WASHCAR:
                title = "세차장";
                break;

            case RESTAREA:
                title = "휴게소";
                break;

            case REPAIRSHOP:
                title = "정비소";
                break;

            case CHARGINGCAR:
                title = "전기차 충전";
                break;

            case RENTALCAR:
                title = "렌터카";
                break;
        }

        return title;
    }

    public void getOpenData() throws UnsupportedEncodingException {

        HashMap<String, String> params = new HashMap<>();

        params.put("ServiceKey", serviceKey);
        params.put("type", "json");
        String addrQueryLong = URLEncoder.encode(address1 + " " + address2, "utf-8");
//        String addrQueryShort = URLEncoder.encode(address1, "utf-8");
        Log.i(">>addressQuery: ", addrQueryLong);

        String baseUrl = "http://api.data.go.kr/openapi/prkplce-info-std";
        String fullUrl = baseUrl;

        if ((tabPositionNum != RESTAREA) && !addrQueryLong.isEmpty()) {
            params.put("insttNm", addrQueryLong);
        }

//        tabPosition.value = tabPositionNum;

        switch (tabPositionNum) {

            case PARKING:
                baseUrl = "http://api.data.go.kr/openapi/prkplce-info-std";
                fullUrl = addParams(baseUrl, params);
                parkingItemArrayList.clear();
                setParkingArrayData(fullUrl);
                break;

            case WASHCAR:
                baseUrl = "http://api.data.go.kr/openapi/carwsh-std";
                fullUrl = addParams(baseUrl, params);
                washCarItemArrayList.clear();
                setWashCarArrayData(fullUrl);
                break;

            case RESTAREA:
                baseUrl = "http://api.data.go.kr/openapi/restarea-std";
                fullUrl = addParams(baseUrl, params);
                restAreaItemArrayList.clear();
                setRestAreaArrayData(fullUrl);
                break;

            case REPAIRSHOP:
                baseUrl = "http://api.data.go.kr/openapi/crrpbsns-std";
                params.put("bsnSttus", "01");
                fullUrl = addParams(baseUrl, params);
                repairShopItemArrayList.clear();
                setRepairShopArrayData(fullUrl);
                break;

            case CHARGINGCAR:
                baseUrl = "http://api.data.go.kr/openapi/elcty-car-chrstn-std";
                fullUrl = addParams(baseUrl, params);
                chargingCarItemArrayList.clear();
                setChargingCarArrayData(fullUrl);
                break;

            case RENTALCAR:
                baseUrl = "http://api.data.go.kr/openapi/rntcarentrps-std";
                fullUrl = addParams(baseUrl, params);
                rentalCarItemArrayList.clear();
                setRentalCarArrayData(fullUrl);
                break;
        }
    }

    private String addParams(String url, HashMap<String, String> mapParam) {
        StringBuilder stringBuilder = new StringBuilder(url+"?");

        if(mapParam != null){
            for ( String key : mapParam.keySet() ) {
                stringBuilder.append(key+"=");
                stringBuilder.append(mapParam.get(key)+"&");
            }
        }
        return stringBuilder.toString();
    }

    private Boolean checkHttpUrlConnected (String url) throws IOException {

        URL checkUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) checkUrl.openConnection();

        connection.setRequestMethod("GET"); // URL 요청에 대한 메소드 설정 : POST.
        connection.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
        connection.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {

            callFireBase();
            return false;

        } else {
            return true;
        }
    }

    private void callFireBase() {

    }

    public ParkingItem getParkingItemData(int position) {

        ParkingItem parkingItem = new ParkingItem();

        if (position >= parkingItemArrayList.size()) {
            return parkingItem;
        }

        parkingItem.setPrkplceNo(parkingItemArrayList.get(position).getPrkplceNo());
        parkingItem.setStoreNm(parkingItemArrayList.get(position).getStoreNm());
        parkingItem.setPrkplceSe(parkingItemArrayList.get(position).getPrkplceSe());
        parkingItem.setPrkplceType(parkingItemArrayList.get(position).getPrkplceType());
        parkingItem.setRdnmadr(parkingItemArrayList.get(position).getRdnmadr());
        parkingItem.setLnmadr(parkingItemArrayList.get(position).getLnmadr());
        parkingItem.setPrkcmprt(parkingItemArrayList.get(position).getPrkcmprt());
        parkingItem.setFeedingSe(parkingItemArrayList.get(position).getFeedingSe());
        parkingItem.setEnforceSe(parkingItemArrayList.get(position).getEnforceSe());
        parkingItem.setOperDay(parkingItemArrayList.get(position).getOperDay());
        parkingItem.setWeekdayOperOpenHhmm(parkingItemArrayList.get(position).getWeekdayOperOpenHhmm());
        parkingItem.setWeekdayOperColseHhmm(parkingItemArrayList.get(position).getWeekdayOperColseHhmm());
        parkingItem.setSatOperOperOpenHhmm(parkingItemArrayList.get(position).getSatOperOperOpenHhmm());
        parkingItem.setSatOperCloseHhmm(parkingItemArrayList.get(position).getSatOperCloseHhmm());
        parkingItem.setHolidayOperOpenHhmm(parkingItemArrayList.get(position).getHolidayOperOpenHhmm());
        parkingItem.setHolidayCloseOpenHhmm(parkingItemArrayList.get(position).getHolidayCloseOpenHhmm());
        parkingItem.setParkingchrgeInfo(parkingItemArrayList.get(position).getParkingchrgeInfo());
        parkingItem.setBasicTime(parkingItemArrayList.get(position).getBasicTime());
        parkingItem.setBasicCharge(parkingItemArrayList.get(position).getBasicCharge());
        parkingItem.setAddUnitTime(parkingItemArrayList.get(position).getAddUnitTime());
        parkingItem.setAddUnitCharge(parkingItemArrayList.get(position).getAddUnitCharge());
        parkingItem.setDayCmmtktAdjTime(parkingItemArrayList.get(position).getDayCmmtktAdjTime());
        parkingItem.setDayCmmtkt(parkingItemArrayList.get(position).getDayCmmtkt());
        parkingItem.setMonthCmmtkt(parkingItemArrayList.get(position).getMonthCmmtkt());
        parkingItem.setMetpay(parkingItemArrayList.get(position).getMetpay());
        parkingItem.setSpcmnt(parkingItemArrayList.get(position).getSpcmnt());
        parkingItem.setInstitutionNm(parkingItemArrayList.get(position).getInstitutionNm());
        parkingItem.setPhoneNumber(parkingItemArrayList.get(position).getPhoneNumber());
        parkingItem.setLatitude(parkingItemArrayList.get(position).getLatitude());
        parkingItem.setHardness(parkingItemArrayList.get(position).getHardness());
        parkingItem.setReferenceDate(parkingItemArrayList.get(position).getReferenceDate());
        parkingItem.setInsttCode(parkingItemArrayList.get(position).getInsttCode());
        parkingItem.setInsttNm(parkingItemArrayList.get(position).getInsttNm());

        return parkingItem;
    }

    private void setParkingArrayData(String url) {

        AQuery aQuery = new AQuery((CommonApplication)getApplicationContext());

        aQuery.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                if (object != null) {

                    Log.i(">>url: ", url);
                    Log.i(">>callback: ", object.toString());

                    try {
                        JSONArray jsonArray = object.optJSONObject("response").optJSONObject("body").optJSONArray("items");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.optJSONObject(i);

                            ParkingItem item = new ParkingItem();

                            item.setPrkplceNo(jsonObject.optString("prkplceNo"));
                            item.setStoreNm(jsonObject.optString("prkplceNm"));
                            item.setPrkplceSe(jsonObject.optString("prkplceSe"));
                            item.setPrkplceType(jsonObject.optString("prkplceType"));
                            item.setRdnmadr(jsonObject.optString("rdnmadr"));
                            item.setLnmadr(jsonObject.optString("lnmadr"));
                            item.setPrkcmprt(jsonObject.optInt("prkcmprt"));
                            item.setFeedingSe(jsonObject.optInt("feedingSe"));
                            item.setEnforceSe(jsonObject.optString("enforceSe"));
                            item.setOperDay(jsonObject.optString("operDay"));
                            item.setWeekdayOperOpenHhmm(jsonObject.optString("weekdayOperOpenHhmm"));
                            item.setWeekdayOperColseHhmm(jsonObject.optString("weekdayOperColseHhmm"));
                            item.setSatOperOperOpenHhmm(jsonObject.optString("satOperOperOpenHhmm"));
                            item.setSatOperCloseHhmm(jsonObject.optString("satOperCloseHhmm"));
                            item.setHolidayOperOpenHhmm(jsonObject.optString("holidayOperOpenHhmm"));
                            item.setHolidayCloseOpenHhmm(jsonObject.optString("holidayCloseOpenHhmm"));
                            item.setParkingchrgeInfo(jsonObject.optString("parkingchrgeInfo"));
                            item.setBasicTime(jsonObject.optInt("basicTime"));
                            item.setBasicCharge(jsonObject.optInt("basicCharge"));
                            item.setAddUnitTime(jsonObject.optInt("addUnitTime"));
                            item.setAddUnitCharge(jsonObject.optInt("addUnitCharge"));
                            item.setDayCmmtktAdjTime(jsonObject.optInt("dayCmmtktAdjTime"));
                            item.setDayCmmtkt(jsonObject.optInt("dayCmmtkt"));
                            item.setMonthCmmtkt(jsonObject.optInt("monthCmmtkt"));
                            item.setMetpay(jsonObject.optString("metpay"));
                            item.setSpcmnt(jsonObject.optString("spcmnt"));
                            item.setInstitutionNm(jsonObject.optString("institutionNm"));
                            item.setPhoneNumber(jsonObject.optString("phoneNumber"));
                            item.setLatitude(jsonObject.optDouble("latitude"));
                            item.setHardness(jsonObject.optDouble("hardness"));
                            item.setReferenceDate(jsonObject.optString("referenceDate"));
                            item.setInsttCode(jsonObject.optInt("insttCode"));
                            item.setInsttNm(jsonObject.optString("insttNm"));
                            item.setMapPOIItem(makeMapPOIItem(jsonObject.optString("prkplceNm"), jsonObject.optDouble("latitude"), jsonObject.optDouble("hardness")));

                            parkingItemArrayList.add(item);
                        }

                        Log.i(">>json: ", parkingItemArrayList.get(0).getStoreNm());
                        if(parkingItemArrayList.size() > 0) {
                            Log.i(">>parkingArray: ", parkingItemArrayList.toString());

                            ((MainActivity)MainActivity.context).addMapPOIItem();
                        }

                    } catch (Exception e) {
                        Log.i(">>catch: ", e.toString());
                        e.printStackTrace();
                    }

                } else { //fail
                    Log.i(">>parking array func", "fail");
                    callFireBase();
                }
            }
        }.timeout(120000));
    }

    public WashCarItem getWashCarItemData(int position) {

        WashCarItem washCarItem = new WashCarItem();

        if (position >= washCarItemArrayList.size()) {
            return washCarItem;
        }

        washCarItem.setStoreNm(washCarItemArrayList.get(position).getStoreNm());
        washCarItem.setCtprvnNm(washCarItemArrayList.get(position).getCtprvnNm());
        washCarItem.setSignguNm(washCarItemArrayList.get(position).getSignguNm());
        washCarItem.setCarwshInduty(washCarItemArrayList.get(position).getCarwshInduty());
        washCarItem.setCarwshType(washCarItemArrayList.get(position).getCarwshType());
        washCarItem.setRdnmadr(washCarItemArrayList.get(position).getRdnmadr());
        washCarItem.setRstde(washCarItemArrayList.get(position).getRstde());
        washCarItem.setWeekdayOperOpenHhmm(washCarItemArrayList.get(position).getWeekdayOperOpenHhmm());
        washCarItem.setWeekdayOperColseHhmm(washCarItemArrayList.get(position).getWeekdayOperColseHhmm());
        washCarItem.setHolidayOperOpenHhmm(washCarItemArrayList.get(position).getHolidayOperOpenHhmm());
        washCarItem.setHolidayCloseOpenHhmm(washCarItemArrayList.get(position).getHolidayCloseOpenHhmm());
        washCarItem.setCarwshChrgeInfo(washCarItemArrayList.get(position).getCarwshChrgeInfo());
        washCarItem.setRprsntvNm(washCarItemArrayList.get(position).getRprsntvNm());
        washCarItem.setPhoneNumber(washCarItemArrayList.get(position).getPhoneNumber());
        washCarItem.setQltwtrPrmisnNo(washCarItemArrayList.get(position).getQltwtrPrmisnNo());
        washCarItem.setLatitude(washCarItemArrayList.get(position).getLatitude());
        washCarItem.setHardness(washCarItemArrayList.get(position).getHardness());
        washCarItem.setReferenceDate(washCarItemArrayList.get(position).getReferenceDate());
        washCarItem.setInsttCode(washCarItemArrayList.get(position).getInsttCode());
        washCarItem.setInsttNm(washCarItemArrayList.get(position).getInsttNm());

        return washCarItem;
    }

    private void setWashCarArrayData(String url) {

        AQuery aQuery = new AQuery((CommonApplication)getApplicationContext());

        aQuery.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                if (object != null) {

                    Log.i(">>url: ", url);
                    Log.i(">>callback: ", object.toString());

                    try {
                        JSONArray jsonArray = object.optJSONObject("response").optJSONObject("body").optJSONArray("items");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.optJSONObject(i);

                            WashCarItem item = new WashCarItem();

                            item.setStoreNm(jsonObject.optString("carwshNm"));
                            item.setCtprvnNm(jsonObject.optString("ctprvnNm"));
                            item.setSignguNm(jsonObject.optString("signguNm"));
                            item.setCarwshInduty(jsonObject.optString("carwshInduty"));
                            item.setCarwshType(jsonObject.optString("carwshType"));
                            item.setRdnmadr(jsonObject.optString("rdnmadr"));
                            item.setRstde(jsonObject.optString("rdnmadr"));
                            item.setRstde(jsonObject.optString("rstde"));
                            item.setWeekdayOperOpenHhmm(jsonObject.optString("weekdayOperOpenHhmm"));
                            item.setWeekdayOperColseHhmm(jsonObject.optString("weekdayOperColseHhmm"));
                            item.setHolidayOperOpenHhmm(jsonObject.optString("holidayOperOpenHhmm"));
                            item.setHolidayCloseOpenHhmm(jsonObject.optString("holidayCloseOpenHhmm"));
                            item.setCarwshChrgeInfo(jsonObject.optString("carwshChrgeInfo"));
                            item.setRprsntvNm(jsonObject.optString("rprsntvNm"));
                            item.setPhoneNumber(jsonObject.optString("phoneNumber"));
                            item.setQltwtrPrmisnNo(jsonObject.optString("qltwtrPrmisnNo"));
                            item.setLatitude(jsonObject.optDouble("latitude"));
                            item.setHardness(jsonObject.optDouble("hardness"));
                            item.setReferenceDate(jsonObject.optString("referenceDate"));
                            item.setInsttCode(jsonObject.optInt("insttCode"));
                            item.setInsttNm(jsonObject.optString("insttNm"));
                            item.setMapPOIItem(makeMapPOIItem(jsonObject.optString("carwshNm"), jsonObject.optDouble("latitude"), jsonObject.optDouble("hardness")));

                            washCarItemArrayList.add(item);
                        }

                        if(washCarItemArrayList.size() > 0) {
                            Log.i(">>wash car Array: ", washCarItemArrayList.toString());

                            ((MainActivity)MainActivity.context).addMapPOIItem();
                        }

                    } catch (Exception e) {
                        Log.i(">>catch: ", e.toString());
                        e.printStackTrace();
                    }

                } else { //fail
                    Log.i(">>wash car array func", "fail");
                    callFireBase();
                }
            }
        }.timeout(120000));
    }

    public RestAreaItem getRestAreaItemData(int position) {

        RestAreaItem restAreaItem = new RestAreaItem();

        if (position >= restAreaItemArrayList.size()) {
            return restAreaItem;
        }

        restAreaItem.setStoreNm(restAreaItemArrayList.get(position).getStoreNm());
        restAreaItem.setRoadKnd(restAreaItemArrayList.get(position).getRoadKnd());
        restAreaItem.setRoadRouteNo(restAreaItemArrayList.get(position).getRoadRouteNo());
        restAreaItem.setRoadRouteNm(restAreaItemArrayList.get(position).getRoadRouteNm());
        restAreaItem.setRoadRouteDrc(restAreaItemArrayList.get(position).getRoadRouteDrc());
        restAreaItem.setLatitude(restAreaItemArrayList.get(position).getLatitude());
        restAreaItem.setHardness(restAreaItemArrayList.get(position).getHardness());
        restAreaItem.setRestAreaType(restAreaItemArrayList.get(position).getRestAreaType());
        restAreaItem.setOperOpenHhmm(restAreaItemArrayList.get(position).getOperOpenHhmm());
        restAreaItem.setOperCloseHhmm(restAreaItemArrayList.get(position).getOperCloseHhmm());
        restAreaItem.setOcpatAr(restAreaItemArrayList.get(position).getOcpatAr());
        restAreaItem.setPrkplceCo(restAreaItemArrayList.get(position).getPrkplceCo());
        restAreaItem.setCrrpwrkYn(restAreaItemArrayList.get(position).getCrrpwrkYn());
        restAreaItem.setOltYn(restAreaItemArrayList.get(position).getOltYn());
        restAreaItem.setLpgYn(restAreaItemArrayList.get(position).getLpgYn());
        restAreaItem.setElctyYn(restAreaItemArrayList.get(position).getElctyYn());
        restAreaItem.setBusTrnsitYn(restAreaItemArrayList.get(position).getBusTrnsitYn());
        restAreaItem.setShltrYn(restAreaItemArrayList.get(position).getShltrYn());
        restAreaItem.setToiletYn(restAreaItemArrayList.get(position).getToiletYn());
        restAreaItem.setParmacyYn(restAreaItemArrayList.get(position).getParmacyYn());
        restAreaItem.setNrsgYn(restAreaItemArrayList.get(position).getNrsgYn());
        restAreaItem.setShopYn(restAreaItemArrayList.get(position).getShopYn());
        restAreaItem.setRstrtYn(restAreaItemArrayList.get(position).getRstrtYn());
        restAreaItem.setEtcCvntl(restAreaItemArrayList.get(position).getEtcCvntl());
        restAreaItem.setRprsntvRstrt(restAreaItemArrayList.get(position).getRprsntvRstrt());
        restAreaItem.setPhoneNumber(restAreaItemArrayList.get(position).getPhoneNumber());
        restAreaItem.setReferenceDate(restAreaItemArrayList.get(position).getReferenceDate());
        restAreaItem.setInsttCode(restAreaItemArrayList.get(position).getInsttCode());
        restAreaItem.setInsttNm(restAreaItemArrayList.get(position).getInsttNm());

        return restAreaItem;
    }

    private void setRestAreaArrayData(String url) {

        AQuery aQuery = new AQuery((CommonApplication)getApplicationContext());

        aQuery.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                if (object != null) {

                    Log.i(">>url: ", url);
                    Log.i(">>callback: ", object.toString());

                    try {
                        JSONArray jsonArray = object.optJSONObject("response").optJSONObject("body").optJSONArray("items");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.optJSONObject(i);

                            RestAreaItem item = new RestAreaItem();

                            item.setStoreNm(jsonObject.optString("entrpsNm"));
                            item.setRoadKnd(jsonObject.optString("roadKnd"));
                            item.setRoadRouteNo(jsonObject.optString("roadRouteNo"));
                            item.setRoadRouteNm(jsonObject.optString("roadRouteNm"));
                            item.setRoadRouteDrc(jsonObject.optString("roadRouteDrc"));
                            item.setLatitude(jsonObject.optDouble("latitude"));
                            item.setHardness(jsonObject.optDouble("hardness"));
                            item.setRestAreaType(jsonObject.optString("restAreaType"));
                            item.setOperOpenHhmm(jsonObject.optString("operOpenHhmm"));
                            item.setOperCloseHhmm(jsonObject.optString("operCloseHhmm"));
                            item.setOcpatAr(jsonObject.optDouble("ocpatAr"));
                            item.setPrkplceCo(jsonObject.optInt("prkplceCo"));
                            item.setCrrpwrkYn(jsonObject.optString("crrpwrkYn"));
                            item.setOltYn(jsonObject.optString("oltYn"));
                            item.setLpgYn(jsonObject.optString("lpgYn"));
                            item.setElctyYn(jsonObject.optString("elctyYn"));
                            item.setBusTrnsitYn(jsonObject.optString("busTrnsitYn"));
                            item.setShltrYn(jsonObject.optString("shltrYn"));
                            item.setToiletYn(jsonObject.optString("toiletYn"));
                            item.setParmacyYn(jsonObject.optString("parmacyYn"));
                            item.setNrsgYn(jsonObject.optString("nrsgYn"));
                            item.setShopYn(jsonObject.optString("shopYn"));
                            item.setRstrtYn(jsonObject.optString("rstrtYn"));
                            item.setEtcCvntl(jsonObject.optString("etcCvntl"));
                            item.setRprsntvRstrt(jsonObject.optString("rprsntvRstrt"));
                            item.setPhoneNumber(jsonObject.optString("phoneNumber"));
                            item.setReferenceDate(jsonObject.optString("referenceDate"));
                            item.setInsttCode(jsonObject.optInt("insttCode"));
                            item.setInsttNm(jsonObject.optString("insttNm"));
                            item.setMapPOIItem(makeMapPOIItem(jsonObject.optString("entrpsNm"), jsonObject.optDouble("latitude"), jsonObject.optDouble("hardness")));

                            restAreaItemArrayList.add(item);
                        }

                        if(restAreaItemArrayList.size() > 0) {
                            Log.i(">>rest area Array: ", restAreaItemArrayList.toString());

                            ((MainActivity)MainActivity.context).addMapPOIItem();
                        }

                    } catch (Exception e) {
                        Log.i(">>catch: ", e.toString());
                        e.printStackTrace();
                    }

                } else { //fail
                    Log.i(">>rest area array func", "fail");
                    callFireBase();
                }
            }
        }.timeout(120000));
    }

    public RepairShopItem getRepairShopItemData(int position) {

        RepairShopItem repairShopItem = new RepairShopItem();

        if (position >= repairShopItemArrayList.size()) {
            return repairShopItem;
        }

        repairShopItem.setStoreNm(repairShopItemArrayList.get(position).getStoreNm());
        repairShopItem.setInspofcType(repairShopItemArrayList.get(position).getInspofcType());
        repairShopItem.setRdnmadr(repairShopItemArrayList.get(position).getRdnmadr());
        repairShopItem.setLnmadr(repairShopItemArrayList.get(position).getLnmadr());
        repairShopItem.setLatitude(repairShopItemArrayList.get(position).getLatitude());
        repairShopItem.setHardness(repairShopItemArrayList.get(position).getHardness());
        repairShopItem.setBizrnoDate(repairShopItemArrayList.get(position).getBizrnoDate());
        repairShopItem.setAr(repairShopItemArrayList.get(position).getAr());
        repairShopItem.setBsnSttus(repairShopItemArrayList.get(position).getBsnSttus());
        repairShopItem.setClsbizDate(repairShopItemArrayList.get(position).getClsbizDate());
        repairShopItem.setSssBeginDate(repairShopItemArrayList.get(position).getSssBeginDate());
        repairShopItem.setSssEndDate(repairShopItemArrayList.get(position).getSssEndDate());
        repairShopItem.setOperOpenHm(repairShopItemArrayList.get(position).getOperOpenHm());
        repairShopItem.setOperCloseHm(repairShopItemArrayList.get(position).getOperCloseHm());
        repairShopItem.setInstitutionNm(repairShopItemArrayList.get(position).getInstitutionNm());
        repairShopItem.setInstitutionPhoneNumber(repairShopItemArrayList.get(position).getInstitutionPhoneNumber());
        repairShopItem.setReferenceDate(repairShopItemArrayList.get(position).getReferenceDate());
        repairShopItem.setInsttCode(repairShopItemArrayList.get(position).getInsttCode());
        repairShopItem.setInsttNm(repairShopItemArrayList.get(position).getInsttNm());

        return repairShopItem;
    }

    private void setRepairShopArrayData(String url) {

        AQuery aQuery = new AQuery((CommonApplication)getApplicationContext());

        aQuery.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                if (object != null) {

                    Log.i(">>url: ", url);
                    Log.i(">>callback: ", object.toString());

                    try {
                        JSONArray jsonArray = object.optJSONObject("response").optJSONObject("body").optJSONArray("items");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.optJSONObject(i);

                            RepairShopItem item = new RepairShopItem();

                            item.setStoreNm(jsonObject.optString("inspofcNm"));
                            item.setInspofcType(jsonObject.optString("inspofcType"));
                            item.setRdnmadr(jsonObject.optString("rdnmadr"));
                            item.setLnmadr(jsonObject.optString("lnmadr"));
                            item.setLatitude(jsonObject.optDouble("latitude"));
                            item.setHardness(jsonObject.optDouble("hardness"));
                            item.setBizrnoDate(jsonObject.optString("bizrnoDate"));
                            item.setAr(jsonObject.optDouble("ar"));
                            item.setBsnSttus(jsonObject.optString("bsnSttus"));
                            item.setClsbizDate(jsonObject.optString("clsbizDate"));
                            item.setSssBeginDate(jsonObject.optString("sssBeginDate"));
                            item.setSssEndDate(jsonObject.optString("sssEndDate"));
                            item.setOperOpenHm(jsonObject.optString("xoperOpenHm"));
                            item.setOperCloseHm(jsonObject.optString("operCloseHm"));
                            item.setPhoneNumber(jsonObject.optString("phoneNumber"));
                            item.setInstitutionNm(jsonObject.optString("institutionNm"));
                            item.setInstitutionPhoneNumber(jsonObject.optString("institutionPhoneNumber"));
                            item.setReferenceDate(jsonObject.optString("referenceDate"));
                            item.setInsttCode(jsonObject.optInt("insttCode"));
                            item.setInsttNm(jsonObject.optString("insttNm"));
                            item.setMapPOIItem(makeMapPOIItem(jsonObject.optString("inspofcNm"), jsonObject.optDouble("latitude"), jsonObject.optDouble("hardness")));

                            repairShopItemArrayList.add(item);
                        }

                        if(repairShopItemArrayList.size() > 0) {
                            Log.i(">>repairShopArray: ", repairShopItemArrayList.toString());

                            ((MainActivity)MainActivity.context).addMapPOIItem();
                        }

                    } catch (Exception e) {
                        Log.i(">>catch: ", e.toString());
                        e.printStackTrace();
                    }

                } else { //fail
                    Log.i(">>repairShop array func", "fail");
                    callFireBase();
                }
            }
        }.timeout(120000));
    }

    public ChargingCarItem getChargingCarItemData(int position) {

        ChargingCarItem chargingCarItem = new ChargingCarItem();

        if (position >= chargingCarItemArrayList.size()) {
            return chargingCarItem;
        }

        chargingCarItem.setStoreNm(chargingCarItemArrayList.get(position).getStoreNm());
        chargingCarItem.setChrstnLcDesc(chargingCarItemArrayList.get(position).getChrstnLcDesc());
        chargingCarItem.setInstlCtprvnNm(chargingCarItemArrayList.get(position).getInstlCtprvnNm());
        chargingCarItem.setRestde(chargingCarItemArrayList.get(position).getRestde());
        chargingCarItem.setUseOpenTime(chargingCarItemArrayList.get(position).getUseOpenTime());
        chargingCarItem.setUseCloseTime(chargingCarItemArrayList.get(position).getUseCloseTime());
        chargingCarItem.setSlowChrstnYn(chargingCarItemArrayList.get(position).getSlowChrstnYn());
        chargingCarItem.setFastChrstnYn(chargingCarItemArrayList.get(position).getFastChrstnYn());
        chargingCarItem.setFastChrstnType(chargingCarItemArrayList.get(position).getFastChrstnType());
        chargingCarItem.setSlowChrstnCo(chargingCarItemArrayList.get(position).getSlowChrstnCo());
        chargingCarItem.setFastChrstnCo(chargingCarItemArrayList.get(position).getFastChrstnCo());
        chargingCarItem.setPrkplceLevyYn(chargingCarItemArrayList.get(position).getPrkplceLevyYn());
        chargingCarItem.setRdnmadr(chargingCarItemArrayList.get(position).getRdnmadr());
        chargingCarItem.setLnmadr(chargingCarItemArrayList.get(position).getLnmadr());
        chargingCarItem.setInstitutionNm(chargingCarItemArrayList.get(position).getInstitutionNm());
        chargingCarItem.setPhoneNumber(chargingCarItemArrayList.get(position).getPhoneNumber());
        chargingCarItem.setLatitude(chargingCarItemArrayList.get(position).getLatitude());
        chargingCarItem.setHardness(chargingCarItemArrayList.get(position).getHardness());
        chargingCarItem.setReferenceDate(chargingCarItemArrayList.get(position).getReferenceDate());
        chargingCarItem.setInsttCode(chargingCarItemArrayList.get(position).getInsttCode());
        chargingCarItem.setInsttNm(chargingCarItemArrayList.get(position).getInsttNm());

        return chargingCarItem;
    }

    private void setChargingCarArrayData(String url) {

        AQuery aQuery = new AQuery((CommonApplication)getApplicationContext());

        aQuery.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {

            @SuppressLint("LongLogTag")
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                if (object != null) {

                    Log.i(">>url: ", url);
                    Log.i(">>callback: ", object.toString());

                    try {
                        JSONArray jsonArray = object.optJSONObject("response").optJSONObject("body").optJSONArray("items");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.optJSONObject(i);

                            ChargingCarItem item = new ChargingCarItem();

                            item.setStoreNm(jsonObject.optString("chrstnNm"));
                            item.setChrstnLcDesc(jsonObject.optString("chrstnLcDesc"));
                            item.setInstlCtprvnNm(jsonObject.optString("instlCtprvnNm"));
                            item.setRestde(jsonObject.optString("restde"));
                            item.setUseOpenTime(jsonObject.optString("useOpenTime"));
                            item.setUseCloseTime(jsonObject.optString("useCloseTime"));
                            item.setSlowChrstnYn(jsonObject.optString("slowChrstnYn"));
                            item.setFastChrstnYn(jsonObject.optString("fastChrstnYn"));
                            item.setFastChrstnType(jsonObject.optString("fastChrstnType"));
                            item.setSlowChrstnCo(jsonObject.optInt("slowChrstnCo"));
                            item.setFastChrstnCo(jsonObject.optInt("fastChrstnCo"));
                            item.setPrkplceLevyYn(jsonObject.optString("prkplceLevyYn"));
                            item.setRdnmadr(jsonObject.optString("rdnmadr"));
                            item.setLnmadr(jsonObject.optString("lnmadr"));
                            item.setInstitutionNm(jsonObject.optString("institutionNm"));
                            item.setPhoneNumber(jsonObject.optString("phoneNumber"));
                            item.setLatitude(jsonObject.optDouble("latitude"));
                            item.setHardness(jsonObject.optDouble("hardness"));
                            item.setReferenceDate(jsonObject.optString("referenceDate"));
                            item.setInsttCode(jsonObject.optInt("insttCode"));
                            item.setInsttNm(jsonObject.optString("insttNm"));
                            item.setMapPOIItem(makeMapPOIItem(jsonObject.optString("chrstnNm"), jsonObject.optDouble("latitude"), jsonObject.optDouble("hardness")));

                            chargingCarItemArrayList.add(item);
                        }

                        if(chargingCarItemArrayList.size() > 0) {
                            Log.i(">>chargingCarArray: ", chargingCarItemArrayList.toString());

                            ((MainActivity)MainActivity.context).addMapPOIItem();
                        }

                    } catch (Exception e) {
                        Log.i(">>catch: ", e.toString());
                        e.printStackTrace();
                    }

                } else { //fail
                    Log.i(">>Charging Car array func", "fail");
                    callFireBase();
                }
            }
        }.timeout(120000));
    }

    public RentalCarItem getRentalCarItemData(int position) {

        RentalCarItem rentalCarItem = new RentalCarItem();

        if (position >= rentalCarItemArrayList.size()) {
            return rentalCarItem;
        }

        rentalCarItem.setStoreNm(rentalCarItemArrayList.get(position).getStoreNm());
        rentalCarItem.setBplcType(rentalCarItemArrayList.get(position).getBplcType());
        rentalCarItem.setRdnmadr(rentalCarItemArrayList.get(position).getRdnmadr());
        rentalCarItem.setLnmadr(rentalCarItemArrayList.get(position).getLnmadr());
        rentalCarItem.setLatitude(rentalCarItemArrayList.get(position).getLatitude());
        rentalCarItem.setHardness(rentalCarItemArrayList.get(position).getHardness());
        rentalCarItem.setGarageRdnmadr(rentalCarItemArrayList.get(position).getGarageRdnmadr());
        rentalCarItem.setGarageLnmadr(rentalCarItemArrayList.get(position).getGarageLnmadr());
        rentalCarItem.setGarageAceptncCo(rentalCarItemArrayList.get(position).getGarageAceptncCo());
        rentalCarItem.setVhcleHoldCo(rentalCarItemArrayList.get(position).getVhcleHoldCo());
        rentalCarItem.setCarHoldCo(rentalCarItemArrayList.get(position).getCarHoldCo());
        rentalCarItem.setVansHoldCo(rentalCarItemArrayList.get(position).getVansHoldCo());
        rentalCarItem.setEleCarHoldCo(rentalCarItemArrayList.get(position).getEleCarHoldCo());
        rentalCarItem.setEleVansCarHoldCo(rentalCarItemArrayList.get(position).getEleVansCarHoldCo());
        rentalCarItem.setLghvhclChrge(rentalCarItemArrayList.get(position).getLghvhclChrge());
        rentalCarItem.setCmhvhclChrge(rentalCarItemArrayList.get(position).getCmhvhclChrge());
        rentalCarItem.setMdhvhclChrge(rentalCarItemArrayList.get(position).getMdhvhclChrge());
        rentalCarItem.setLgshvhclChrge(rentalCarItemArrayList.get(position).getLgshvhclChrge());
        rentalCarItem.setVahvhclChrge(rentalCarItemArrayList.get(position).getVahvhclChrge());
        rentalCarItem.setLshvhclChrge(rentalCarItemArrayList.get(position).getLshvhclChrge());
        rentalCarItem.setImhvhclChrge(rentalCarItemArrayList.get(position).getImhvhclChrge());
        rentalCarItem.setWeekdayOperOpenHhmm(rentalCarItemArrayList.get(position).getWeekdayOperOpenHhmm());
        rentalCarItem.setWeekdayOperColseHhmm(rentalCarItemArrayList.get(position).getWeekdayOperColseHhmm());
        rentalCarItem.setWeekdayOperOpenHhmm(rentalCarItemArrayList.get(position).getWeekdayOperOpenHhmm());
        rentalCarItem.setWeekdayOperColseHhmm(rentalCarItemArrayList.get(position).getWeekdayOperColseHhmm());
        rentalCarItem.setHolidayOperOpenHhmm(rentalCarItemArrayList.get(position).getHolidayOperOpenHhmm());
        rentalCarItem.setHolidayCloseOpenHhmm(rentalCarItemArrayList.get(position).getHolidayCloseOpenHhmm());
        rentalCarItem.setRstde(rentalCarItemArrayList.get(position).getRstde());
        rentalCarItem.setHomepageUrl(rentalCarItemArrayList.get(position).getHomepageUrl());
        rentalCarItem.setRprsntvNm(rentalCarItemArrayList.get(position).getRprsntvNm());
        rentalCarItem.setPhoneNumber(rentalCarItemArrayList.get(position).getPhoneNumber());
        rentalCarItem.setReferenceDate(rentalCarItemArrayList.get(position).getReferenceDate());
        rentalCarItem.setInsttCode(rentalCarItemArrayList.get(position).getInsttCode());
        rentalCarItem.setInsttNm(rentalCarItemArrayList.get(position).getInsttNm());

        return rentalCarItem;
    }

    private void setRentalCarArrayData(String url) {

        AQuery aQuery = new AQuery((CommonApplication)getApplicationContext());

        aQuery.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {

            @SuppressLint("LongLogTag")
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                if (object != null) {

                    Log.i(">>url: ", url);
                    Log.i(">>callback: ", object.toString());

                    try {
                        JSONArray jsonArray = object.optJSONObject("response").optJSONObject("body").optJSONArray("items");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.optJSONObject(i);

                            RentalCarItem item = new RentalCarItem();

                            item.setStoreNm(jsonObject.optString("entrpsNm"));
                            item.setBplcType(jsonObject.optString("bplcType"));
                            item.setRdnmadr(jsonObject.optString("rdnmadr"));
                            item.setLnmadr(jsonObject.optString("lnmadr"));
                            item.setLatitude(jsonObject.optDouble("latitude"));
                            item.setHardness(jsonObject.optDouble("hardness"));
                            item.setGarageRdnmadr(jsonObject.optString("garageRdnmadr"));
                            item.setGarageLnmadr(jsonObject.optString("garageLnmadr"));
                            item.setGarageAceptncCo(jsonObject.optDouble("garageAceptncCo"));
                            item.setVhcleHoldCo(jsonObject.optInt("vhcleHoldCo"));
                            item.setCarHoldCo(jsonObject.optInt("carHoldCo"));
                            item.setVansHoldCo(jsonObject.optInt("vansHoldCo"));
                            item.setEleCarHoldCo(jsonObject.optInt("eleCarHoldCo"));
                            item.setEleVansCarHoldCo(jsonObject.optInt("eleVansCarHoldCo"));
                            item.setLghvhclChrge(jsonObject.optInt("lghvhclChrge"));
                            item.setCmhvhclChrge(jsonObject.optInt("cmhvhclChrge"));
                            item.setMdhvhclChrge(jsonObject.optInt("mdhvhclChrge"));
                            item.setLgshvhclChrge(jsonObject.optInt("lgshvhclChrge"));
                            item.setVahvhclChrge(jsonObject.optInt("vahvhclChrge"));
                            item.setLshvhclChrge(jsonObject.optInt("lshvhclChrge"));
                            item.setImhvhclChrge(jsonObject.optInt("imhvhclChrge"));
                            item.setWeekdayOperOpenHhmm(jsonObject.optString("weekdayOperOpenHhmm"));
                            item.setWeekdayOperColseHhmm(jsonObject.optString("weekdayOperColseHhmm"));
                            item.setWkendOperOpenHhmm(jsonObject.optString("wkendOperOpenHhmm"));
                            item.setWkendOperCloseHhmm(jsonObject.optString("wkendOperCloseHhmm"));
                            item.setHolidayOperOpenHhmm(jsonObject.optString("holidayOperOpenHhmm"));
                            item.setHolidayCloseOpenHhmm(jsonObject.optString("holidayCloseOpenHhmm"));
                            item.setRstde(jsonObject.optString("rstde"));
                            item.setHomepageUrl(jsonObject.optString("homepageUrl"));
                            item.setRprsntvNm(jsonObject.optString("rprsntvNm"));
                            item.setPhoneNumber(jsonObject.optString("phoneNumber"));
                            item.setReferenceDate(jsonObject.optString("referenceDate"));
                            item.setInsttCode(jsonObject.optInt("insttCode"));
                            item.setInsttNm(jsonObject.optString("insttNm"));
                            item.setMapPOIItem(makeMapPOIItem(jsonObject.optString("entrpsNm"), jsonObject.optDouble("latitude"), jsonObject.optDouble("hardness")));

                            rentalCarItemArrayList.add(item);
                        }

                        if(rentalCarItemArrayList.size() > 0) {
                            Log.i(">>RentalCarArray: ", rentalCarItemArrayList.toString());

                            ((MainActivity)MainActivity.context).addMapPOIItem();
                        }

                    } catch (Exception e) {
                        Log.i(">>catch: ", e.toString());
                        e.printStackTrace();
                    }

                } else { //fail
                    Log.i(">>Rental Car array func", "fail");
                    callFireBase();
                }
            }
        }.timeout(120000));
    }

    public MapPOIItem makeMapPOIItem(String name, double latitude, double longitude) {

        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(name);
        marker.setTag(0);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);

        return marker;
    }



    public MapView setInitMapView (MapView mapView, int position) {

//        private static final MapPoint CUSTOM_MARKER_POINT = MapPoint.mapPointWithGeoCoord(37.48294050000001, 126.90103499999998);




//        MapPoint currentLoc = mapView.getMapCenterPoint();
//
//        MapController mapController = MapController.getInstance();
//        MapCoord coord = mapController.getDestinationMapViewpoint();
//        MapPoint mapPoint = MapPoint.mapPointWithWCONGCoord(coord.getX(), coord.getY());

        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

        MapPOIItem.ImageOffset trackingImageAnchorPointOffset = new MapPOIItem.ImageOffset(0, 0);
        mapView.setCustomCurrentLocationMarkerTrackingImage(R.drawable.current_marker, trackingImageAnchorPointOffset);

        Log.i(">>tab tag: ", String.valueOf(position));

//        MapPOIItem customMarker = new MapPOIItem();
//        customMarker.setItemName("Custom Marker"); //TODO: change name
//        customMarker.setTag(0);
//        customMarker.setMapPoint(mapPoint);
//        customMarker.setMarkerType(MapPOIItem.MarkerType.BluePin);
//        customMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
//        mapView.addPOIItem(customMarker);
//        mapView.selectPOIItem(customMarker, true);








        return mapView;
    }










    public void loadParkingData() {

        AQuery aq = new AQuery(this);

        HashMap<String, String> params = new HashMap<>();
        params.put("ServiceKeu", serviceKey);
    }


}
