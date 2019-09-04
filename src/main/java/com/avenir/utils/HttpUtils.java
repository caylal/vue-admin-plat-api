package com.avenir.utils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HttpUtils {
    private static final CloseableHttpClient httpClient;

    static {
        RequestConfig config = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).build();
        httpClient  = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
    }

    public static JSONObject doPost(String url, JSONArray jsonArray){
        HttpPost httpPost = new HttpPost(url);
        JSONObject respones = null;
        try{
            StringEntity s = new StringEntity(jsonArray.toString(),"utf-8");
            s.setContentEncoding("UTF-8");
            s.setContentType("application/json");
            httpPost.setEntity(s);
            httpPost.addHeader("Content-type", "application/json; charset=utf-8");
            httpPost.setHeader("Accept", "application/json; Accept-Charset=utf-8");
            HttpResponse res = httpClient.execute(httpPost);
            if(res != null){
                HttpEntity entity = res.getEntity();
                if(entity != null){
                    String result = EntityUtils.toString(entity);
                    respones = JSONObject.parseObject(result);
                }
            }
        }catch(Exception e){
            LogUtils.error(e.getMessage());
        }
        return respones;
    }
    public static String doPost(String url, Map<String,String> map) throws Exception{
        HttpPost httpPost = null;
        String result = null;
        httpPost = new HttpPost(url);

        //设置参数
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String, String> elem = (Map.Entry<String, String>) iterator.next();
            list.add(new BasicNameValuePair(elem.getKey().toString(),elem.getValue().toString()));
        }
        if(list.size() > 0){
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,"utf-8");
            httpPost.setEntity(entity);
        }
        HttpResponse response = httpClient.execute(httpPost);
        if(response != null){
            HttpEntity resEntity = response.getEntity();
            if(resEntity != null){
                result = EntityUtils.toString(resEntity,"utf-8");
            }
        }
        return result;
    }
}
