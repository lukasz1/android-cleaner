package co.qualityc.cleaner.net.requests;

import java.util.HashMap;
import java.util.Map;

/**
 * abstract class to represent http get requests
 */
public abstract class AbstractGetRequest {

    protected Map<String,String> arguments = new HashMap<String, String>(0);
    protected String url = "";

    public String getRequestUrl() {
        StringBuilder builder = new StringBuilder(url);
        builder.append("?");
        for (Map.Entry<String, String> entry : arguments.entrySet()) {
            builder.append(entry.getKey());
            builder.append("=");
            builder.append(entry.getValue());
            builder.append("&");
        }
        return builder.toString();
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
