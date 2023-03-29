//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import ru.yandex.oop.tasktreker.ServerFunctionalityRealization.HttpTaskManager;
//import ru.yandex.oop.tasktreker.ServerFunctionalityRealization.KVServer;
//import ru.yandex.oop.tasktreker.model.Task;
//import ru.yandex.oop.tasktreker.presenter.util.Managers;
//
//import java.io.IOException;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//
//    public class HttpTaskManagerTest {
//        private KVServer kvServer;
//        HttpTaskManager httpTaskManager;
//
//        @BeforeEach
//        public void beforeEach() throws IOException {
//            kvServer = new KVServer();
//            kvServer.start();
//        }
//
//        @AfterEach
//        public void afterEach() {
//            kvServer.stop();
//        }
//
//        @Test
//        public void addAnyTaskWithIntersectedTime() {
//            httpTaskManager = Managers.getDefault();
//            Task task1 = new Task("#1", "#1", Status.NEW, 15,"2001-11-09 10:30");
//            httpTaskManager.saveTask(task1);
//            Task task2 = new Task("#2", "#2", Status.NEW, 15,"2000-11-09 10:30");
//            httpTaskManager.saveTask(task2);
//            //Добавляем задачу с пересекающим временем
//            Task task3 = new Task("#3", "#3", Status.NEW, 15,"2000-11-09 10:40");
//            assertEquals(0, httpTaskManager.saveTask(task3));
//        }
//
//        @Test
//        public void saveAndLoadHttpTaskManager() {
//            httpTaskManager = Managers.getDefault();
//            httpTaskManager.setKey("varya"); //Создаем ключ пользователя
//            //Создаем различные задачи
//            Task task1 = new Task("1", "1", Status.NEW, 10, "2016-11-09 10:30");
//            Task task2 = new Task("2", "2", Status.NEW, 15, "2017-11-09 10:30");
//            Task task3 = new Task("3", "3", Status.IN_PROGRESS, 0, "2019-11-09 10:30");
//            int task1Id = httpTaskManager.saveTask(task1);
//            int task2Id = httpTaskManager.saveTask(task2);
//            int task3Id = httpTaskManager.saveTask(task3);
//            Epic epic1 = new Epic("4", "4", Status.NEW);
//            int epic1Id = httpTaskManager.saveEpic(epic1);
//            Epic epic2 = new Epic("5", "5", Status.NEW);
//            int epic2Id = httpTaskManager.saveEpic(epic2);
//            Subtask subtask1 = new Subtask("6", "6", Status.NEW, epic1Id, 20, "2003-10-10 19:00");
//            int subtask1Id = httpTaskManager.saveSubtask(subtask1);
//            Subtask subtask2 = new Subtask("7", "7", Status.NEW, epic2Id, 30, null);
//            int subtask2Id = httpTaskManager.saveSubtask(subtask2);
//            //Содаем историю
//            httpTaskManager.getTaskByID(task1Id);
//            httpTaskManager.getTaskByID(task2Id);
//            httpTaskManager.getEpicByID(epic1Id);
//            httpTaskManager.getSubtaskByID(subtask1Id);
//            httpTaskManager.getEpicByID(epic2Id);
//            httpTaskManager.getSubtaskByID(subtask2Id);
//            httpTaskManager.getTaskByID(task3Id);
//            //Создаем новый менеджер на основе серверных данных
//            HttpTaskManager loadedFromServerManager = httpTaskManager.loadFromServer();
//            //Проверяем таски
//            assertEquals(loadedFromServerManager.getAllTasks().toString(), httpTaskManager.getAllTasks().toString());
//            //Проверяем эпики
//            assertEquals(loadedFromServerManager.getAllEpics().toString(), httpTaskManager.getAllEpics().toString());
//            //Проверяем сабтаски
//            assertEquals(loadedFromServerManager.getAllSubtasks().toString(), httpTaskManager.getAllSubtasks().toString());
//            //Проверяем историю
//            assertEquals(loadedFromServerManager.getHistory().toString(), httpTaskManager.getHistory().toString());
//            //Проверяем задачи по приоритету
//            assertEquals(loadedFromServerManager.getPrioritizedTasks().toString(), httpTaskManager.getPrioritizedTasks().toString());
//        }
//    }
//}
