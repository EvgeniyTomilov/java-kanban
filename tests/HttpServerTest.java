import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.oop.tasktreker.model.EpicTask;
import ru.yandex.oop.tasktreker.model.SubTask;
import ru.yandex.oop.tasktreker.model.Task;
import ru.yandex.oop.tasktreker.model.enums.TaskStatus;
import ru.yandex.oop.tasktreker.presenter.impl.InMemoryTaskManager;
import ru.yandex.oop.tasktreker.serverfunctionalityrealization.HttpTaskServer;
import ru.yandex.oop.tasktreker.serverfunctionalityrealization.customjson.EpicTaskSerializer;
import ru.yandex.oop.tasktreker.serverfunctionalityrealization.customjson.SubTaskSerializer;
import ru.yandex.oop.tasktreker.serverfunctionalityrealization.customjson.TaskSerializer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpServerTest {

    private HttpTaskServer server;

    InMemoryTaskManager manager = new InMemoryTaskManager();

    private Gson gson;

    private Task task;

    private Task task2;

    private EpicTask epic;

    private EpicTask epic2;

    private SubTask subtask;

    private SubTask subtask2;

    private int taskId;

    private int epicId;

    private int subtaskId;


    @BeforeEach
    void beforeEach() throws IOException {
        gson = new GsonBuilder().
                registerTypeAdapter(Task.class, new TaskSerializer()).
                registerTypeAdapter(EpicTask.class, new EpicTaskSerializer()).
                registerTypeAdapter(SubTask.class, new SubTaskSerializer()).
                create();

        manager = new InMemoryTaskManager();
        server = new HttpTaskServer(manager);
        task = new Task("Test addNewTask1", "Test addNewTask description1", Duration.ofMinutes(10), LocalDateTime.parse("2023-03-03T10:15:30"));
        taskId = manager.createTaskAndReturnId(task);
        task2 = new Task("Test addNewTask2", "Test addNewTask description2", Duration.ofMinutes(15), LocalDateTime.parse("2023-04-03T10:15:30"));
        epic = new EpicTask("Test addNewEpicTask1", "Test addNewEpicTask1 description");
        epicId = manager.createTaskAndReturnId(epic);
        epic2 = new EpicTask("Test addNewEpicTask2", "Test addNewEpicTask2 description");
        subtask = new SubTask("newSubtask", "description newSubtask", epic.getId(), Duration.ofMinutes(20), "2023-05-03T10:15:30");
        subtaskId = manager.createTaskAndReturnId(subtask);
        subtask2 = new SubTask("newSubtask", "description newSubtask2", epic.getId(), Duration.ofMinutes(20), "2023-06-03T10:15:30");

        manager.getAnyTask(taskId);
        manager.getAnyTask(epicId);
        manager.getAnyTask(subtaskId);

        server.start();
    }

    @AfterEach
    void afterEach() {
        server.stop();
    }

    @Test
    public void getTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:6080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(task), response.body());
    }

    @Test
    public void getEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:6080/tasks/epic/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(epic), response.body());
    }

    @Test
    public void getSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:6080/tasks/subtask/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(subtask), response.body());
    }

    @Test
    public void getTasks() throws IOException, InterruptedException {
        manager.createTaskAndReturnId(task2); //Сохраняем дополнительную задачу
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:6080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

    }

    @Test
    public void getEpics() throws IOException, InterruptedException {
        manager.createTaskAndReturnId(epic2); //Сохраняем дополнительный эпик
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:6080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

    }

    @Test
    public void getSubtasks() throws IOException, InterruptedException {
        manager.createTaskAndReturnId(subtask2); //Сохраняем дополнительную подзадачу
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:6080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

    }

    @Test
    public void getHistory() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:6080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("\"" + manager.getHistoryManager().toString() + "\"", response.body());
    }

    @Test
    public void getPrioritized() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:6080/tasks/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void getEpicSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:6080/tasks/subtask/epic/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

    }

    @Test
    public void postTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:6080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2))).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        System.out.println(response.body());
        assertEquals("Задача 1 сохранена", response.body());
        assertNotNull(manager.getAnyTask(1));
    }

    @Test
    public void postEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:6080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic2))).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        System.out.println(response.body());
        assertEquals("Эпик 2 сохранен", response.body());
        assertNotNull(manager.getAnyTask(2));
    }
  @Test
    public void postUpdateEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:6080/tasks/epic/");
        epic2.setId(epicId); //Ставим ID на тот, который есть, чтобы было обновление
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic2))).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        System.out.println(response.body());
        assertEquals("Эпик 2 обновлен", response.body());
        assertNotNull(manager.getAnyTask(2));
    }

    @Test
    public void postUpdateSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:6080/tasks/subtask/");
        subtask2.setId(subtaskId); //Ставим ID на тот, который есть, чтобы было обновление
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask2))).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        System.out.println(response.body());
        assertEquals("Подзадача 3 обновлена", response.body());
        assertNotNull(manager.getAnyTask(3));
    }

    @Test
    public void deleteTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:6080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertNull(manager.getAnyTask(1));
    }

    @Test
    public void deleteEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:6080/tasks/epic/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertNull(manager.getAnyTask(2));
    }

    @Test
    public void deleteSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:6080/tasks/subtask/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertNull(manager.getAnyTask(3));
    }

    @Test
    public void deleteTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:6080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

    }
    @Test
    public void deleteEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:6080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

    }

    @Test
    public void deleteSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:6080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

    }

    @Test
    public void unknown() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:6080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(HttpRequest.BodyPublishers.ofString("test")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Такого эндпоинта не существует", response.body());
    }
}
