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

        String caseSubject = "New Case Subject";
        String caseDescription = "This is the case description";
        String caseInternalAssessment = "This is internal Assessment";
        String caseWarehouse = "test@verafin.com";
        String caseOwner = "ETL";

        String jsonPayload = String.format(
                "{\"Subject\":\"%s\",\"Description\":\"%s\",\"Internal Assessment\":\"%s\",\"Warehouse\":\"%s\",\"CaseOwner\":\"%s\"}",
                caseSubject,
                caseDescription,
                caseInternalAssessment,
                caseWarehouse,
                caseOwner

        );
        try {
            URL url = new URL(loginInstanceUrl + "/services/data/v57.0/sobjects/Case");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + loginAccessToken);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(jsonPayload);
            outputStream.flush();
            outputStream.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                System.out.println("Case created successfully!");
            } else {
                System.out.println("Error creating the case. Response code: " + responseCode);
            }

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        httpPost.releaseConnection();
    }
}