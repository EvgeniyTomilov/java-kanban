import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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



public class HttpTaskManagerTest {
        private KVServer kvServer;
        HttpTaskManager httpTaskManager;

        @BeforeEach
        public void beforeEach() throws IOException {
            kvServer = new KVServer();
            kvServer.start();
        }

        @AfterEach
        public void afterEach() {
            kvServer.stop();
        }

        @Test
        public void addAnyTaskWithIntersectedTime() {
            httpTaskManager = Managers.getDefault();
            Task task1 = new Task("Test addNewTask1", "Test addNewTask description1", Duration.ofMinutes(10), LocalDateTime.parse("2023-03-03T10:15:30"));
            httpTaskManager.createTaskAndReturnId(task1);
            Task task2 = new Task("Test addNewTask2", "Test addNewTask description2", Duration.ofMinutes(15), LocalDateTime.parse("2023-04-03T10:15:30"));
            httpTaskManager.createTaskAndReturnId(task2);
            //Добавляем задачу с пересекающим временем
            Task task3 = new Task("Test addNewTask2", "Test addNewTask description2", Duration.ofMinutes(15), LocalDateTime.parse("2023-04-03T10:15:40"));
            assertEquals(0, httpTaskManager.createTaskAndReturnId(task3));

        }

        @Test
        public void saveAndLoadHttpTaskManager() {
            httpTaskManager = Managers.getDefault();
            httpTaskManager.setKey("shpion"); //Создаем ключ пользователя
            //Создаем различные задачи
            Task task1 = new Task("Test addNewTask1", "Test addNewTask description1", Duration.ofMinutes(10), LocalDateTime.parse("2023-03-03T10:15:30"));
            Task task2 = new Task("Test addNewTask2", "Test addNewTask description2", Duration.ofMinutes(15), LocalDateTime.parse("2023-04-03T10:15:30"));
            Task task3 = new Task("Test addNewTask3", "Test addNewTask description3", Duration.ofMinutes(20), LocalDateTime.parse("2023-05-03T10:15:30"));
            int task1Id = httpTaskManager.createTaskAndReturnId(task1);
            int task2Id = httpTaskManager.createTaskAndReturnId(task2);
            int task3Id = httpTaskManager.createTaskAndReturnId(task3);
            EpicTask epic1 = new EpicTask("Test addNewEpicTask1", "Test addNewEpicTask1 description");
            int epicId1 = httpTaskManager.createTaskAndReturnId(epic1);
            EpicTask epic2 = new EpicTask("Test addNewEpicTask2", "Test addNewEpicTask2 description");
            int epicId2 = httpTaskManager.createTaskAndReturnId(epic2);
            SubTask subTask1 = new SubTask("newSubtask", "description newSubtask", epic1.getId(), Duration.ofMinutes(20), "2023-05-03T10:15:30");
            subTask1.setStatus(TaskStatus.NEW);
            int subTaskId1 = httpTaskManager.createTaskAndReturnId(subTask1);
            SubTask subTask2 = new SubTask("newSubtask", "description newSubtask", epic2.getId(), Duration.ofMinutes(20), null);
            subTask1.setStatus(TaskStatus.NEW);
            int subTaskId2 = httpTaskManager.createTaskAndReturnId(subTask2);
            //Создаем историю
            httpTaskManager.getAnyTask(task1Id);
            httpTaskManager.getAnyTask(task2Id);
            httpTaskManager.getAnyTask(epicId1);
            httpTaskManager.getAnyTask(subTaskId1);
            httpTaskManager.getAnyTask(epicId2);
            httpTaskManager.getAnyTask(subTaskId2);
            httpTaskManager.getAnyTask(task3Id);
            //Создаем новый менеджер на основе серверных данных
            HttpTaskManager loadedFromServerManager = httpTaskManager.loadFromServer();
            //Проверяем таски
            assertEquals(loadedFromServerManager.getAllTasks().toString(), httpTaskManager.getAllTasks().toString());
            //Проверяем историю
            assertEquals(loadedFromServerManager.getHistoryManager().toString(), httpTaskManager.getHistoryManager().toString());
            //Проверяем задачи по приоритету
            assertEquals(loadedFromServerManager.getPrioritizedTasks().toString(), httpTaskManager.getPrioritizedTasks().toString());
        }
    }

