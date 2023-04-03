package ru.yandex.oop.tasktreker.serverfunctionalityrealization;

import com.google.gson.*;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.oop.tasktreker.serverfunctionalityrealization.customjson.EpicTaskSerializer;
import ru.yandex.oop.tasktreker.serverfunctionalityrealization.customjson.SubTaskSerializer;
import ru.yandex.oop.tasktreker.serverfunctionalityrealization.customjson.TaskSerializer;
import ru.yandex.oop.tasktreker.model.EpicTask;
import ru.yandex.oop.tasktreker.model.SubTask;
import ru.yandex.oop.tasktreker.model.Task;
import ru.yandex.oop.tasktreker.model.enums.Endpoint;
import ru.yandex.oop.tasktreker.presenter.impl.InMemoryTaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static ru.yandex.oop.tasktreker.model.enums.TaskType.*;

public class HttpTaskServer {
    private final HttpServer server;
    private static final int PORT = 6080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final Gson gson;
    private final InMemoryTaskManager manager;

    public HttpTaskServer(InMemoryTaskManager manager) throws IOException {
        this.manager = manager;

        server = HttpServer.create();

        server.bind(new InetSocketAddress(PORT), 0);

        // TODO test and remove these; and do we need a save context?
//        server.createContext("/tasks/task/", new taskManagerHandler());

        server.createContext("/tasks", new ru.yandex.oop.tasktreker.serverfunctionalityrealization.TaskManagerHandler(manager));
//        server.createContext("/tasks/task", new taskManagerHandler());
//        server.createContext("/tasks/subtask/", new taskManagerHandler());
        gson = new GsonBuilder().
                registerTypeAdapter(Task.class, new TaskSerializer()).
                registerTypeAdapter(EpicTask.class, new EpicTaskSerializer()).
                registerTypeAdapter(SubTask.class, new SubTaskSerializer()).
                create();
    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP-сервер остановлен на " + PORT + " порту!");
    }


}