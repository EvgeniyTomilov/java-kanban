import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import org.junit.jupiter.api.function.Executable;
import ru.yandex.oop.tasktreker.exception.TaskValidationException;
import ru.yandex.oop.tasktreker.model.EpicTask;
import ru.yandex.oop.tasktreker.model.SubTask;
import ru.yandex.oop.tasktreker.model.Task;
import ru.yandex.oop.tasktreker.model.enums.TaskStatus;
import ru.yandex.oop.tasktreker.presenter.util.Managers;
import ru.yandex.oop.tasktreker.serverfunctionalityrealization.HttpTaskManager;
import ru.yandex.oop.tasktreker.serverfunctionalityrealization.KVServer;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager>{
    private KVServer kvServer;

    @BeforeEach
    public void beforeEach() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        manager = Managers.getDefault();
    }

    @AfterEach
    public void afterEach() {
        kvServer.stop();
    }

    @Test
    public void addAnyTaskWithIntersectedTime() {
        manager = Managers.getDefault();
        Task task1 = new Task("Test addNewTask1", "Test addNewTask description1", Duration.ofMinutes(10), LocalDateTime.parse("2023-03-03T10:15:30"));
        manager.createTaskAndReturnId(task1);
        Task task2 = new Task("Test addNewTask2", "Test addNewTask description2", Duration.ofMinutes(15), LocalDateTime.parse("2023-04-03T10:15:30"));
        manager.createTaskAndReturnId(task2);
        //Добавляем задачу с пересекающим временем
        Task task3 = new Task("Test addNewTask2", "Test addNewTask description2", Duration.ofMinutes(15), LocalDateTime.parse("2023-04-03T10:15:40"));
        assertThrows(TaskValidationException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                manager.createTaskAndReturnId(task3);
            }
        });
    }

    @Test
    public void saveAndLoadHttpTaskManager() {
        manager = Managers.getDefault();
        manager.setKey("shpion"); //Создаем ключ пользователя
        //Создаем различные задачи
        Task task1 = new Task("Test addNewTask1", "Test addNewTask description1", Duration.ofMinutes(10), LocalDateTime.parse("2023-03-03T10:15:30"));
        Task task2 = new Task("Test addNewTask2", "Test addNewTask description2", Duration.ofMinutes(15), LocalDateTime.parse("2023-04-03T10:15:30"));
        Task task3 = new Task("Test addNewTask3", "Test addNewTask description3", Duration.ofMinutes(20), LocalDateTime.parse("2023-05-03T10:15:30"));
        int task1Id = manager.createTaskAndReturnId(task1);
        int task2Id = manager.createTaskAndReturnId(task2);
        int task3Id = manager.createTaskAndReturnId(task3);
        EpicTask epic1 = new EpicTask("Test addNewEpicTask1", "Test addNewEpicTask1 description");
        int epicId1 = manager.createTaskAndReturnId(epic1);
        EpicTask epic2 = new EpicTask("Test addNewEpicTask2", "Test addNewEpicTask2 description");
        int epicId2 = manager.createTaskAndReturnId(epic2);
        SubTask subTask1 = new SubTask("newSubtask", "description newSubtask", epic1.getId(), Duration.ofMinutes(20), "2023-05-03T10:15:30");
        subTask1.setStatus(TaskStatus.NEW);
        int subTaskId1 = manager.createTaskAndReturnId(subTask1);
        SubTask subTask2 = new SubTask("newSubtask", "description newSubtask", epic2.getId(), Duration.ofMinutes(20), null);
        subTask1.setStatus(TaskStatus.NEW);
        int subTaskId2 = manager.createTaskAndReturnId(subTask2);
        //Создаем историю
        manager.getAnyTask(task1Id);
        manager.getAnyTask(task2Id);
        manager.getAnyTask(epicId1);
        manager.getAnyTask(subTaskId1);
        manager.getAnyTask(epicId2);
        manager.getAnyTask(subTaskId2);
        manager.getAnyTask(task3Id);
        //Создаем новый менеджер на основе серверных данных
        HttpTaskManager loadedFromServerManager = manager.loadFromServer();
        //Проверяем таски
        assertEquals(loadedFromServerManager.getAllTasks().toString(), manager.getAllTasks().toString());
        //Проверяем историю
        assertEquals(loadedFromServerManager.getHistoryManager().toString(), manager.getHistoryManager().toString());
        //Проверяем задачи по приоритету
        assertEquals(loadedFromServerManager.getPrioritizedTasks().toString(), manager.getPrioritizedTasks().toString());
    }
}