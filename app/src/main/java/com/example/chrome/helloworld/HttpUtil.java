package com.example.chrome.helloworld;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
/**
 * Created by chrome on 16/05/14.
 */
public class HttpUtil {
    //private static String url_base = "https://pinponapp.herokuapp.com/api/";
    private static String url_base = "http://192.168.100.101:3000/api/";

    public static int attack(String myName, String uuid){
        String str = "users?params=ATK_" + myName + "_" + uuid;
        ArrayList<String> result = __send(url_base + str);
        return Integer.parseInt(result.get(0));
    }

    public static int inquireRequest(String myName){
        String str = "users?params=INQR_" + myName;
        ArrayList<String> result = __send(url_base + str);
        return Integer.parseInt(result.get(0));
    }

    public static int block(String myName){
        String str = "users?params=BLK_" + myName;
        ArrayList<String> result = __send(url_base + str);
        return Integer.parseInt(result.get(0));
    }
    public static int cantBlock(String myName){
        String str = "users?params=NBLK_" + myName;
        ArrayList<String> result = __send(url_base + str);
        return Integer.parseInt(result.get(0));
    }
    public static int attacked(String myName){
        String str = "users?params=ATKD_" + myName;
        ArrayList<String> result = __send(url_base + str);
        return Integer.parseInt(result.get(0));
    }

    public static int makeUser(String myName, String mail, String pass, String uuid){
        String str = "users?params=MKUR_" + myName + "_" + mail + "_" + pass + "_" + uuid;
        ArrayList<String> result = __send(url_base + str);
        if(result.isEmpty()) return -1;
        return Integer.parseInt(result.get(0));
    }



    private static String[] remDoubleQuot(ArrayList<String> in) {
        String data = in.get(0);
        data = data.replaceAll("[","").replaceAll("]", "").replaceAll("\"", "");
        String[] splitedData = data.split(",");
        return splitedData;
    }

    public static ArrayList getMyPoint(String myName){
        String str = "users?params=GPNT_" + myName;
        //ArrayList<String> result = send(url_base + str);


        ArrayList<String> result = new ArrayList<>();
        String test = "[[1,\"hoge\",100],[2,\"sanjo\",80],[3,\"hoge2\",70],[4,\"test\",40],[5,\"hoge3\",0],[6,\"but\",-20]]";
        result.add(test);


        String[] splitedData = remDoubleQuot(result);

        ArrayList<DataVector> dva = new ArrayList();
        for(int i=0;i<splitedData.length/3;i+=3) {
            DataVector dv = new DataVector(splitedData[i], splitedData[i+1], splitedData[i+2]);
            dva.add(dv);
        }
        return dva;
    }


    private static ArrayList<String> send(final String urlStr) {
        Log.d("send url",""+urlStr);
        final ArrayList<String> list = new ArrayList();
        Thread thread = new Thread(){
            @Override
            public void run(){
                ArrayList<String> tmp = __send(urlStr);
                for(String it : tmp){
                    list.add(it);
                }
            }
        };
        thread.run();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static ArrayList<String> __send(String urlStr){
        ArrayList<String> list = new ArrayList();
        try{
            URL url = new URL(urlStr);
            HttpURLConnection urlConne = (HttpURLConnection) url.openConnection();
            urlConne.setRequestMethod("GET");
            urlConne.setDoInput(true);
            urlConne.setDoOutput(false);
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConne.getInputStream()));
            String str = null;
            while((str=br.readLine())!=null){
                list.add(str);
            }
            br.close();
            urlConne.disconnect();
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }


}