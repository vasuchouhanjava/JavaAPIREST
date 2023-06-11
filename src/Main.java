import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.IOException;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main {

    static final String USERNAME     = "vasu.chouhan@verafin.com.vasudev";
    static final String PASSWORD     = "Hondacivic@19059trvIwHBSSpUZXXRt8Ylm8ckk5";
    static final String LOGINURL     = "https://test.salesforce.com";
    static final String GRANTSERVICE = "/services/oauth2/token?grant_type=password";
    static final String CLIENTID     = "3MVG9ZM6Cuht.9Ssuymzz6ekF4EIkrBrdq.UiQhFZOapHBzhExlEmvDWKl9HJ_UdPBc6C.x4Oe_pzufa1wm9W";
    static final String CLIENTSECRET = "FDE7BD9EE04E8BCC670F4BE8954EF9AEB2AB3B0B617E08E04CF039E7C1126308";


    public static void main(String[] args) {

        HttpClient httpclient = HttpClientBuilder.create().build();

        // Assemble the login request URL
        String loginURL = LOGINURL +
                GRANTSERVICE +
                "&client_id=" + CLIENTID +
                "&client_secret=" + CLIENTSECRET +
                "&username=" + USERNAME +
                "&password=" + PASSWORD;

        // Login requests must be POSTs
        HttpPost httpPost = new HttpPost(loginURL);
        HttpResponse response = null;

        try {
            // Execute the login POST request
            response = httpclient.execute(httpPost);
        } catch (ClientProtocolException cpException) {
            cpException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        // verify response is HTTP OK
        final int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            System.out.println("Error authenticating to Force.com: "+statusCode);
            // Error is in EntityUtils.toString(response.getEntity())
            return;
        }

        String getResult = null;
        try {
            getResult = EntityUtils.toString(response.getEntity());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        JSONObject jsonObject = null;
        String loginAccessToken = null;
        String loginInstanceUrl = null;
        try {
            jsonObject = (JSONObject) new JSONTokener(getResult).nextValue();
            loginAccessToken = jsonObject.getString("access_token");
            loginInstanceUrl = jsonObject.getString("instance_url");
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }

        System.out.println(response.getStatusLine());
        System.out.println("Successful login");
        System.out.println("  instance URL: "+loginInstanceUrl);
        System.out.println("  access token/session ID: "+loginAccessToken);

        //createCase();

        String caseSubject = "Test case June 10 7:06";
        String caseDescription = "This is the case description=Dec 15, 2022";
        String caseInternalassessment = "Internal assessemtn";
        String  caseOwnerId = "00GC00000030elBMAQ";


        String jsonPayload = String.format(
                "{\"subject\":\"%s\",\"description\":\"%s\",\"InternalAssessment__c\":\"%s\",\"OwnerId\":\"%s\"}",
                caseSubject,
                caseDescription,
                caseInternalassessment,
                caseOwnerId
        );

        System.out.println(jsonPayload);
        try {
            URL urlpostcase = new URL(loginInstanceUrl + "/services/data/v57.0/sobjects/Case");
            HttpURLConnection connectionpostcase = (HttpURLConnection) urlpostcase.openConnection();
            connectionpostcase.setRequestMethod("GET");
            connectionpostcase.setRequestProperty("Authorization", "Bearer " + loginAccessToken);
            connectionpostcase.setRequestProperty("Content-Type", "application/json");
            connectionpostcase.setDoOutput(true);

            DataOutputStream outputStream = new DataOutputStream(connectionpostcase.getOutputStream());
            outputStream.writeBytes(jsonPayload);
            outputStream.flush();
            outputStream.close();

            int responseCode = connectionpostcase.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                System.out.println("Case created successfully!");
            } else {
                System.out.println("Error creating the case. Response code: " + responseCode);
            }

            connectionpostcase.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        httpPost.releaseConnection();
    }
}