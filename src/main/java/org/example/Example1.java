package org.example;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;



public class Example1 {
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    public static JSONArray sendHttpGetRequestJSONArray(String url) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(BASE_URL + url);
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity);

            System.out.println("Response String: " + responseString);

            return new JSONArray(responseString);
        }
    }
    public static  JSONObject sendHttpGetRequestJSONObject(String url) throws IOException{
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(BASE_URL + url);
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity);

            System.out.println("Response String: " + responseString);

            return new JSONObject(responseString);
        }
    }

    public static JSONObject sendHttpPostRequest(String url, JSONObject data) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(BASE_URL + url);
            request.addHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(data.toString()));
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity);
            return new JSONObject(responseString);
        }
    }

    public static JSONObject sendHttpPutRequest(String url, JSONObject data) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPut request = new HttpPut(BASE_URL + url);
            request.addHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(data.toString()));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity);

                if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() < 300) {
                    System.out.println(responseString);
                    return new JSONObject(responseString);

                } else {
                    System.err.println("HTTP PUT request failed. Status code: " + response.getStatusLine().getStatusCode());
                    return null;
                }
            }
        }
    }

    private static void sendHttpDeleteRequest(String url) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpDelete request = new HttpDelete(BASE_URL + url);
            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 200 && statusCode < 300) {
                System.out.println("Delete request successful. Status code: " + statusCode);
            } else {
                System.err.println("Delete request failed. Status code: " + statusCode);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) throws IOException {
        // Завдання 1
        JSONObject newUser = new JSONObject();
        newUser.put("name", "John Doe");
        newUser.put("username", "johndoe");
        newUser.put("email", "johndoe@example.com");

        JSONObject createdUser = sendHttpPostRequest("/users", newUser);
        System.out.println("Created User: " + createdUser);

        JSONObject updatedUser = createdUser;
        JSONObject updatedResult = sendHttpPutRequest("/users/" + createdUser.getInt("id"), updatedUser);
        System.out.println("Updated User: " + updatedResult);

        int userIdToDelete = createdUser.getInt("id");
        sendHttpDeleteRequest("/users/" + userIdToDelete);

        JSONArray allUsers = sendHttpGetRequestJSONArray("/users");
        System.out.println("All Users: " + allUsers);

        int userIdToFetch = 1;
        JSONObject userById = sendHttpGetRequestJSONObject("/users/" + userIdToFetch);
        System.out.println("User by ID: " + userById);

        String usernameToFetch = "Bret";
        JSONArray userByUsername = sendHttpGetRequestJSONArray("/users?username=" + usernameToFetch);
        System.out.println("User by Username: " + userByUsername);

        JSONArray userPosts = sendHttpGetRequestJSONArray("/users/" + userIdToFetch + "/posts");
        JSONObject lastPost = userPosts.getJSONObject(userPosts.length() - 1);
        int postId = lastPost.getInt("id");

        JSONArray postComments = sendHttpGetRequestJSONArray("/posts/" + postId + "/comments");
        String fileName = "user-" + userIdToFetch + "-post-" + postId + "-comments.json";
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(postComments.toString());
        }
        System.out.println("Comments saved to " + fileName);

        JSONArray userTodos = sendHttpGetRequestJSONArray("/users/" + userIdToFetch + "/todos");
        for (int i = 0; i < userTodos.length(); i++) {
            JSONObject todo = userTodos.getJSONObject(i);
            if (!todo.getBoolean("completed")) {
                System.out.println("Open Todo: " + todo);
            }
        }
    }


}
