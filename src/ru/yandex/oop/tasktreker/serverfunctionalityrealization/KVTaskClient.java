package ru.yandex.oop.tasktreker.serverfunctionalityrealization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final HttpClient client;
    private final String url;
    private String apiToken;

    public KVTaskClient(String url) {
        client = HttpClient.newHttpClient();
        this.url = url;
        this.apiToken = null;

        URI registerUrl = URI.create(url + "register");
        HttpRequest apiRequest = HttpRequest.newBuilder().uri(registerUrl).GET().build();
        try {
            final HttpResponse<String> response = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonElement jsonElement = JsonParser.parseString(response.body());
                this.apiToken = jsonElement.getAsString();
                System.out.println("Регистрация ключа прошла успешно!");
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (NullPointerException | IOException | InterruptedException e) {
            System.out.println("Во время регистрации возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    public void put(String key, String json) {
        URI saveUrl = URI.create(url + "save/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest postRequest = HttpRequest.newBuilder().uri(saveUrl).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        try {
            final HttpResponse<String> response = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Данные успешно сохранены на сервере");
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (NullPointerException | IOException | InterruptedException e) {
            System.out.println("Во время сохранения возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    public String load(String key) {
        URI loadUrl = URI.create(url + "load/"+ key + "?API_TOKEN=" + apiToken);
        HttpRequest postRequest = HttpRequest.newBuilder().uri(loadUrl).GET().build();
        try {
            final HttpResponse<String> response = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonElement jsonElement = JsonParser.parseString(response.body());

                System.out.println("Данные успешно выгружены с сервера");
                return jsonElement.toString();
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (NullPointerException | IOException | InterruptedException e) {
            System.out.println("Во время выгрузки возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
        return null;
    }
}
