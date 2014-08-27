package co.qualityc.cleaner.net;

/**
 * Created by banan on 04.06.14.
 */
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import co.qualityc.cleaner.net.requests.AbstractGetRequest;

public class RequestManager {

    private HttpClient httpClient;

    /**
     * initialize httpClient if not exists, if exists just returns it
     * @return initialized HttpClient
     * @throws org.apache.http.client.ClientProtocolException
     * @throws java.io.IOException
     */
    private HttpClient getHttpClient() throws ClientProtocolException, IOException {
        if (httpClient != null)
            return httpClient;
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 10000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 15000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        httpClient = new DefaultHttpClient(httpParameters);
        return httpClient;
    }

    /**
     * method executes given query and returns server response in plain text
     *
     * @param query
     * @return server reply as String
     * @throws org.apache.http.client.ClientProtocolException
     * @throws java.io.IOException
     */
    private  String executeRawGetQuery(String query) throws ClientProtocolException, IOException {
        BufferedReader in = null;
        String result;
        try {
            Log.d(getClass().getSimpleName(), "url: " + query);
            HttpClient httpClient = getHttpClient();
            HttpGet httpGet = new HttpGet(query);

            HttpResponse response = httpClient.execute(httpGet);
            if(response == null){
                throw new IOException();
            }
            in = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            result = sb.toString();
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
        }
        Log.v(getClass().getSimpleName(), "Server replied: " + result);
        return result;
    }

    public String executeGetRequest(AbstractGetRequest request) throws IOException {
//        return executeRawGetQuery(request.getRequestUrl());
        // Mock a service doesnt exists
        return "[\"com.evernote\", \"com.facebook\"]";
    }

}
