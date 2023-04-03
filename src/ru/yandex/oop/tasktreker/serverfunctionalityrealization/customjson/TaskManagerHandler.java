package ru.yandex.oop.tasktreker.serverfunctionalityrealization;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.oop.tasktreker.model.EpicTask;
import ru.yandex.oop.tasktreker.model.SubTask;
import ru.yandex.oop.tasktreker.model.Task;
import ru.yandex.oop.tasktreker.model.enums.Endpoint;

import ru.yandex.oop.tasktreker.presenter.impl.InMemoryTaskManager;
import ru.yandex.oop.tasktreker.serverfunctionalityrealization.customjson.EpicTaskSerializer;
import ru.yandex.oop.tasktreker.serverfunctionalityrealization.customjson.SubTaskSerializer;
import ru.yandex.oop.tasktreker.serverfunctionalityrealization.customjson.TaskSerializer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static ru.yandex.oop.tasktreker.model.enums.TaskType.*;

public class TaskManagerHandler implements HttpHandler {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final Gson gson;
    private final InMemoryTaskManager manager;


    public TaskManagerHandler(InMemoryTaskManager manager) {
        this.manager = manager;

        gson = new GsonBuilder().
                registerTypeAdapter(Task.class, new TaskSerializer()).
                registerTypeAdapter(EpicTask.class, new EpicTaskSerializer()).
                registerTypeAdapter(SubTask.class, new SubTaskSerializer()).
                create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod(), exchange.getRequestURI().getQuery());
        switch (endpoint) {
            case GET_TASK -> handleGetTask(exchange);
            case GET_EPICTASK -> handleGetEpic(exchange);
            case GET_SUBTASK -> handleGetSubtask(exchange);
            case GET_TASKS -> handleGetTasks(exchange);
            case GET_EPICTASKS -> handleGetEpics(exchange);
            case GET_SUBTASKS -> handleGetSubtasks(exchange);
            case GET_EPICTASK_SUBTASKS -> handleGetEpicSubtasks(exchange);
            case GET_HISTORY -> handleGetHistory(exchange);
            case GET_PRIORITIZED -> handleGetPrioritized(exchange);
            case POST_UPDATE_TASK -> handlePostOrUpdateTask(exchange);
            case POST_UPDATE_EPICTASK -> handlePostOrUpdateEpic(exchange);
            case POST_UPDATE_SUBTASK -> handlePostOrUpdateSubtask(exchange);
            case DELETE_TASK -> handleDeleteTask(exchange);
            case DELETE_EPICTASK -> handleDeleteEpic(exchange);
            case DELETE_SUBTASK -> handleDeleteSubtask(exchange);
            case DELETE_TASKS -> handleDeleteTasks(exchange);
            case DELETE_EPICTASKS -> handleDeleteEpics(exchange);
            case DELETE_SUBTASKS -> handleDeleteSubtasks(exchange);
            case UNKNOWN -> writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod, String query) {
        String[] pathParts = requestPath.split("/");
        switch (requestMethod) {
            case "GET":
                switch (pathParts[2]) {
                    case "task":
                        if (pathParts.length == 3 && query == null) {
                            return Endpoint.GET_TASKS;
                        }
                        return Endpoint.GET_TASK;
                    case "epic":
                        if (pathParts.length == 3 && query == null) {
                            return Endpoint.GET_EPICTASKS;
                        }
                        return Endpoint.GET_EPICTASK;
                    case "subtask":
                        if (pathParts.length == 3 && query == null) {
                            return Endpoint.GET_SUBTASKS;
                        } else if (pathParts.length == 3) {
                            return Endpoint.GET_SUBTASK;
                        } else if (pathParts[3].equals("epic")) {
                            return Endpoint.GET_EPICTASK_SUBTASKS;
                        }
                    case "history":
                        return Endpoint.GET_HISTORY;
                    case "prioritized":
                        return Endpoint.GET_PRIORITIZED;
                }
            case "POST":
                switch (pathParts[2]) {
                    case "task" -> {
                        return Endpoint.POST_UPDATE_TASK;
                    }
                    case "epic" -> {
                        return Endpoint.POST_UPDATE_EPICTASK;
                    }
                    case "subtask" -> {
                        return Endpoint.POST_UPDATE_SUBTASK;
                    }
                }
            case "DELETE":
                switch (pathParts[2]) {
                    case "task" -> {
                        if (pathParts.length == 3 && query == null) {
                            return Endpoint.DELETE_TASKS;
                        }
                        return Endpoint.DELETE_TASK;
                    }
                    case "epic" -> {
                        if (pathParts.length == 3 && query == null) {
                            return Endpoint.DELETE_EPICTASKS;
                        }
                        return Endpoint.DELETE_EPICTASK;
                    }
                    case "subtask" -> {
                        if (pathParts.length == 3 && query == null) {
                            return Endpoint.DELETE_SUBTASKS;
                        }
                        return Endpoint.DELETE_SUBTASK;
                    }
                }
        }
        return Endpoint.UNKNOWN;
    }

    private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        if (responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }

    private Optional<Integer> getId(HttpExchange exchange) {
        String query = exchange.getRequestURI().getQuery();
        String id = query.substring(3);
        try {
            return Optional.of(Integer.parseInt(id));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    private void handleGetTask(HttpExchange exchange) throws IOException {
        Optional<Integer> optionalInteger = getId(exchange);
        if (optionalInteger.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор задачи", 400);
            return;
        }
        int id = optionalInteger.get();
        if (manager.getAnyTask(id) != null) {
            writeResponse(exchange, gson.toJson(manager.getAnyTask(id)), 200);
            return;
        }
        writeResponse(exchange, "Задача с идентификатором " + id + " не найдена", 404);
    }

    private void handleGetEpic(HttpExchange exchange) throws IOException {
        Optional<Integer> optionalInteger = getId(exchange);
        if (optionalInteger.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор эпика", 400);
            return;
        }
        int id = optionalInteger.get();
        if (manager.getAnyTask(id) != null) {
            writeResponse(exchange, gson.toJson(manager.getAnyTask(id)), 200);
            return;
        }
        writeResponse(exchange, "Эпик с идентификатором " + id + " не найден", 404);
    }

    private void handleGetSubtask(HttpExchange exchange) throws IOException {
        Optional<Integer> optionalInteger = getId(exchange);
        if (optionalInteger.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор подзадачи", 400);
            return;
        }
        int id = optionalInteger.get();
        if (manager.getAnyTask(id) != null) {
            writeResponse(exchange, gson.toJson(manager.getAnyTask(id)), 200);
            return;
        }
        writeResponse(exchange, "Подзадача с идентификатором " + id + " не найдена", 404);
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        if (!manager.getTaskMap().isEmpty()) {
            writeResponse(exchange, gson.toJson(manager.getAllTasks().toString()), 200);
            return;
        }
        writeResponse(exchange, "Задачи отсутствуют.", 404);
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        if (!manager.getEpicTaskMap().isEmpty()) {
            writeResponse(exchange, gson.toJson(manager.getAllTasks().toString()), 200);
            return;
        }
        writeResponse(exchange, "Эпики отсутствуют.", 404);
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        if (!manager.getSubTaskMap().isEmpty()) {
            writeResponse(exchange, gson.toJson(manager.getAllTasks().toString()), 200);
            return;
        }
        writeResponse(exchange, "Подзадачи отсутствуют.", 404);
    }

    private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
        Optional<Integer> optionalInteger = getId(exchange);
        if (optionalInteger.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор эпика", 400);
            return;
        }
        int id = optionalInteger.get();
        if (manager.getAnyTask(id) != null) {
            if (manager.getEpicSubtasks(id) != null) {
                writeResponse(exchange, gson.toJson(manager.getEpicSubtasks(id).toString()), 200);
                return;
            }
            writeResponse(exchange, "У эпика с идентификатором " + id + " нет подзадач.", 404);
            return;
        }
        writeResponse(exchange, "Эпик с идентификатором " + id + " не найден, нельзя вывести подзадачи", 404);
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        if (manager.getHistoryManager() != null) {
            writeResponse(exchange, gson.toJson(manager.getHistoryManager().toString()), 200);
            return;
        }
        writeResponse(exchange, "Истории нет", 404);
    }

    private void handleGetPrioritized(HttpExchange exchange) throws IOException {
        if (manager.getPrioritizedTasks() != null) {
            writeResponse(exchange, gson.toJson(manager.getPrioritizedTasks().toString()), 200);
            return;
        }
        writeResponse(exchange, "Нет задач, чтобы вывести по приоритету", 404);
    }

    private void handlePostOrUpdateTask(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        try {
            JsonElement jsonElement = JsonParser.parseString(body);
            Task newTask = deserializeTask(jsonElement);
            if (newTask.getId() != 0 && manager.getAnyTask(newTask.getId()) != null) {
                manager.updateTask(newTask.getId(), newTask, manager);//????
                writeResponse(exchange, "Задача " + newTask.getId() + " обновлена", 201);
                return;
            } else if (newTask.getId() == 0){
                manager.createTaskAndReturnId(newTask);
                writeResponse(exchange, "Задача " + newTask.getId() + " сохранена", 201);
                return;
            }
            writeResponse(exchange,
                    "Задача не была добавлена, так как ее время пересекается с другой задачей",
                    400);
        } catch (JsonSyntaxException ex) {
            writeResponse(exchange, "Получен некорректный JSON", 400);
        }
    }

    private void handlePostOrUpdateEpic(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        try {
            JsonElement jsonElement = JsonParser.parseString(body);
            EpicTask newEpicTask = deserializeEpic(jsonElement);
            if (newEpicTask.getId() != 0 && manager.getAnyTask(newEpicTask.getId()) != null) {
                manager.updateTask(newEpicTask.getId(), newEpicTask, manager);//??
                writeResponse(exchange, "Эпик " + newEpicTask.getId() + " обновлен", 201);
                return;
            } else if (newEpicTask.getId() == 0){
                manager.createTaskAndReturnId(newEpicTask);
                writeResponse(exchange, "Эпик " + newEpicTask.getId() + " сохранен", 201);
                return;
            }
            writeResponse(exchange,
                    "Эпик не был добавлен.",
                    400);
        } catch (JsonSyntaxException ex) {
            writeResponse(exchange, "Получен некорректный JSON", 400);
        }
    }

    private void handlePostOrUpdateSubtask(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        try {
            JsonElement jsonElement = JsonParser.parseString(body);
            SubTask newSubtask = deserializeSubtask(jsonElement);
            if (newSubtask.getId() != 0 && manager.getAnyTask(newSubtask.getId()) != null) {
                manager.updateTask(newSubtask.getId(), newSubtask, manager);//????
                writeResponse(exchange, "Подзадача " + newSubtask.getId() + " обновлена", 201);
                return;
            } else if (newSubtask.getId() == 0) {
                int id = manager.createTaskAndReturnId(newSubtask);
                writeResponse(exchange, "Подзадача " + id + " сохранена", 201);
                return;
            }
            writeResponse(exchange,
                    "Подзадача не была добавлена, так как ее время пересекается с другой задачей",
                    400);
        } catch (JsonSyntaxException ex) {
            writeResponse(exchange, "Получен некорректный JSON", 400);
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        Optional<Integer> optionalInteger = getId(exchange);
        if (optionalInteger.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор задачи", 400);
            return;
        }
        int id = optionalInteger.get();
        writeResponse(exchange, gson.toJson(manager.deleteTaskByID(id)), 200);
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        Optional<Integer> optionalInteger = getId(exchange);
        if (optionalInteger.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор задачи", 400);
            return;
        }
        int id = optionalInteger.get();
        writeResponse(exchange, gson.toJson(manager.deleteEpicTaskByID(id)), 200);
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        Optional<Integer> optionalInteger = getId(exchange);
        if (optionalInteger.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор задачи", 400);
            return;
        }
        int id = optionalInteger.get();
        writeResponse(exchange, gson.toJson(manager.deleteSubTaskByID(id)), 200);
    }

    private void handleDeleteTasks(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(manager.deleteTasksByType(TASK)), 200);
    }

    private void handleDeleteEpics(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(manager.deleteTasksByType(EPICTASK)), 200);
    }

    private void handleDeleteSubtasks(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(manager.deleteTasksByType(SUBTASK)), 200);
    }

    private Task deserializeTask(JsonElement jsonElement){
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        int id = jsonObject.get("Id").getAsInt();
        String name = jsonObject.get("Name").getAsString();
        String description = jsonObject.get("Description").getAsString();
        Duration duration = Duration.ofMinutes(jsonObject.get("Duration").getAsLong());
        LocalDateTime startTime = LocalDateTime.parse(jsonObject.get("StartTime").getAsString());
        Task task = new Task(name, description, duration, startTime);
        task.setId(id);
        return task;
    }

    private SubTask deserializeSubtask(JsonElement jsonElement){
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        int id = jsonObject.get("Id").getAsInt();
        String name = jsonObject.get("Name").getAsString();
        String description = jsonObject.get("Description").getAsString();
        int epicId = jsonObject.get("EpicId").getAsInt();
        Duration duration = Duration.ofMinutes(jsonObject.get("Duration").getAsLong());
        String startTime = jsonObject.get("StartTime").getAsString();
        SubTask subTask = new SubTask(name, description, epicId, duration, startTime);
        subTask.setId(id);
        return subTask;
    }

    private EpicTask deserializeEpic(JsonElement jsonElement){
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        int id = jsonObject.get("Id").getAsInt();
        String name = jsonObject.get("Name").getAsString();
        String description = jsonObject.get("Description").getAsString();
        EpicTask epicTask = new EpicTask(name, description);
        epicTask.setId(id);
        return epicTask;
    }
}
