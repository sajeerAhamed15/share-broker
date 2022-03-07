package com.ntu.sharebroker.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {
    public static String httpRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connURL = (HttpURLConnection) url.openConnection();
        connURL.setRequestMethod("GET");
        System.out.println("\nMaking HTTP call: " + connURL + "\n");
        connURL.connect();

        BufferedReader ins = new BufferedReader(new InputStreamReader(connURL.getInputStream()));
        String inString;
        StringBuilder sb = new StringBuilder();
        while ((inString = ins.readLine()) != null) {
            sb.append(inString);
        }
        ins.close();
        connURL.disconnect();

        System.out.println("\nResponse: " + sb + "\n");
        return sb.toString();
    }

    public static String httpRequestTwitter(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connURL = (HttpURLConnection) url.openConnection();
        connURL.setRequestMethod("GET");
        connURL.setRequestProperty("Authorization", "Bearer AAAAAAAAAAAAAAAAAAAAAGHKZwEAAAAAj73OPM9SS8Pks3J%2F0wAIKQMcskc%3DTzQKZwiP6q7HwBROCAC0o7awZvmGIoBR3DNMHcSDG4da5fvLsx");
        System.out.println("\nMaking HTTP call: " + connURL + "\n");
        connURL.connect();

        BufferedReader ins = new BufferedReader(new InputStreamReader(connURL.getInputStream()));
        String inString;
        StringBuilder sb = new StringBuilder();
        while ((inString = ins.readLine()) != null) {
            sb.append(inString);
        }
        ins.close();
        connURL.disconnect();

        System.out.println("\nResponse: " + sb + "\n");
        return sb.toString();
    }
}
