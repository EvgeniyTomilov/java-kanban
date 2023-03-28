package ru.yandex.oop.tasktreker;

import ru.yandex.oop.tasktreker.ServerFunctionalityRealization.HttpTaskManager;
import ru.yandex.oop.tasktreker.ServerFunctionalityRealization.KVServer;
import ru.yandex.oop.tasktreker.model.EpicTask;
import ru.yandex.oop.tasktreker.model.SubTask;
import ru.yandex.oop.tasktreker.model.Task;
import ru.yandex.oop.tasktreker.model.enums.TaskStatus;
import ru.yandex.oop.tasktreker.presenter.HistoryManager;
import ru.yandex.oop.tasktreker.presenter.TaskManager;
import ru.yandex.oop.tasktreker.presenter.impl.InMemoryTaskManager;
import ru.yandex.oop.tasktreker.presenter.util.Managers;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {


    public static void main(String[] args) throws IOException {
        new KVServer().start();
        HttpTaskManager httpTaskManager = Managers.getDefault();
        httpTaskManager .setKey("joni");
        Task task1 = new Task("Test addNewTask1", "Test addNewTask description1", Duration.ofMinutes(10), LocalDateTime.parse("2023-03-03T10:15:30"));
        int id1 = httpTaskManager.createTaskAndReturnId(task1);
        Task task2 = new Task("Test addNewTask2", "Test addNewTask description2", Duration.ofMinutes(15), LocalDateTime.parse("2023-04-03T10:15:30"));
        int id2 = httpTaskManager.createTaskAndReturnId(task2);
        EpicTask epic1 = new EpicTask("Test addNewEpicTask1", "Test addNewEpicTask1 description");
        int epicId1 = httpTaskManager.createTaskAndReturnId(epic1);
        EpicTask epic2 = new EpicTask("Test addNewEpicTask2", "Test addNewEpicTask2 description");
        int epicId2 = httpTaskManager.createTaskAndReturnId(epic2);
        SubTask subTask1 = new SubTask("newSubtask", "description newSubtask", epic1.getId(), Duration.ofMinutes(20), "2023-05-03T10:15:30");
        subTask1.setStatus(TaskStatus.NEW);
        int subTask = httpTaskManager.createTaskAndReturnId(subTask1);

        httpTaskManager.getAnyTask(id1);
        httpTaskManager.getAnyTask(id2);
        httpTaskManager.getAnyTask(epicId1);
        httpTaskManager.getAnyTask(epicId2);
        httpTaskManager.getAnyTask(subTask);

        HttpTaskManager newHttpTaskManager = httpTaskManager.loadFromServer();
        System.out.println("Ключ");
        System.out.println(newHttpTaskManager.getKey());
        System.out.println("История");
       System.out.println(newHttpTaskManager.getHistoryManager());
        System.out.println("Все таски");
        System.out.println(newHttpTaskManager.getAllTasks());








 //        System.out.println("**********************************TASK**********************************");
//        Task task1 = new Task("Тренировка", "день грудь-плечи", TaskStatus.NEW, TaskType.TASK);
//        Task task2 = new Task("Проснуться", "открыть глаза-встать с кровати", TaskStatus.DONE, TaskType.TASK);
//
//        manager.createTaskAndReturnId(task1);
//        manager.createTaskAndReturnId(task2);
//
//        System.out.println("---History---");
//        manager.getHistoryManager().getHistory().forEach(System.out::println);
//
//        System.out.println("---History---");
//
//        System.out.println(manager.getByIdAndTypeTask(0, TaskType.TASK));
//        System.out.println(manager.getByIdAndTypeTask(1, TaskType.TASK));


//        System.out.println("**********************************EPICTASK1**********************************");
//        EpicTask epicTask1 = new EpicTask("Купить квартиру", "улучшить жилищьные условия", TaskType.EPICTASK);
//        epicTask1.setId(manager.createTaskAndReturnId(epicTask1));
//
//        EpicTask epicTask2 = new EpicTask("Купить квартиру", "улучшить жилищьные условия", TaskType.EPICTASK);
//        epicTask2.setId(manager.createTaskAndReturnId(epicTask2));
//
//        SubTask subTask1 = new SubTask("Деньги", "Накопить бабло",  TaskType.SUBTASK, epicTask1.getId());
//        subTask1.setStatus(TaskStatus.IN_PROGRESS);
//
//        SubTask subTask2 = new SubTask("Квартира", "Найти хату",  TaskType.SUBTASK, epicTask1.getId());
//        subTask2.setStatus(TaskStatus.DONE);
//
//        SubTask subTask3 = new SubTask("Банк", "Найти банк с наименьшим %", TaskType.SUBTASK, epicTask1.getId());
//        subTask3.setStatus(TaskStatus.NEW);
//
//
//        manager.createTaskAndReturnId(subTask1);
//        manager.createTaskAndReturnId(subTask2);
//        manager.createTaskAndReturnId(subTask3);
//
//        Task t1 = manager.getAnyTask(4);
//        Task t2 = manager.getAnyTask(2);
//        Task t3 = manager.getAnyTask(1);
//        Task t4 = manager.getAnyTask(3);
//        Task t5 = manager.getAnyTask(5);
//
//        t1 = manager.getAnyTask(5);
//        t1 = manager.getAnyTask(5);
//        t1 = manager.getAnyTask(5);
//        t1 = manager.getAnyTask(5);
//        t1 = manager.getAnyTask(5);
//
//
//       manager.deleteTasksByType(TaskType.SUBTASK);
//
//        List<Task> history = historyManager.getHistory();
//
//        history.forEach(System.out::println);


 //       System.out.println("FIN");
    }

}
