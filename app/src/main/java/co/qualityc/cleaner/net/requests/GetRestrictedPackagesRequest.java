package co.qualityc.cleaner.net.requests;

/**
 * Created by banan on 27.08.14.
 */
public class GetRestrictedPackagesRequest extends AbstractGetRequest {

    private static String URL = "https://api.cleaner.com/excluded_packages";

    private GetRestrictedPackagesRequest() {}

    public static class Builder {
        // no arguments, no more methods

        public GetRestrictedPackagesRequest build() {
            GetRestrictedPackagesRequest request = new GetRestrictedPackagesRequest();
            request.setUrl(URL);
            return request;
        }
    }

}
