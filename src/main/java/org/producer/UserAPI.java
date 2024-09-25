package org.producer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class UserAPI {
    String baseURL = "http://localhost:8000";
    Connection con;

    UserAPI(Connection con) {
        this.con = con;
    }
    boolean createUser(String name, String email, int age,  String gender, String password) throws IOException {
        User user = new User(0, name, email, age, gender, password, false, false);
        Gson gson = new Gson();
        String body = gson.toJson(user);

        URL url = new URL(baseURL + "/adduser");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");

        //Convert string to byte format
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = body.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        conn.connect();
        return conn.getResponseCode() == 200;
    }

    boolean updateConsent(int id, boolean cookieConsent, boolean dataConsent) throws IOException {

        String body = String.format("{\"cookie_consent\": \"%s\", \"data_consent\": \"%s\"}", cookieConsent, dataConsent);

        URL url = new URL(baseURL + "/updateuserconsent/" + id);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("PUT");

        //Convert string to byte format
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = body.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        conn.connect();
        return conn.getResponseCode() == 200;
    }

    ArrayList<User> getUsers() throws IOException {
        URL url = new URL(baseURL + "/getusers");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        } else {

            StringBuilder inline = new StringBuilder();
            Scanner scanner = new Scanner(url.openStream());

            //Write all the JSON data into a string using a scanner
            while (scanner.hasNext()) {
                inline.append(scanner.nextLine());
            }

            //Close the scanner
            scanner.close();

            // Create a Gson instance
            Gson gson = new Gson();

            // Define the type of ArrayList<User>
            Type userListType = new TypeToken<ArrayList<User>>() {}.getType();

            // Deserialize the JSON into an ArrayList<User>
            return gson.fromJson(inline.toString(), userListType);
        }
    }
    boolean deleteUser(int id) throws IOException {
        URL url = new URL(baseURL + "/deleteuser/" + id);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("DELETE");
        conn.connect();
        int responsecode = conn.getResponseCode();
        return responsecode == 200;
    }
}
