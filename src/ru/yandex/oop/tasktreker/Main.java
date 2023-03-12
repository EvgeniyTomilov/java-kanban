package ru.yandex.oop.tasktreker;

import ru.yandex.oop.tasktreker.presenter.HistoryManager;
import ru.yandex.oop.tasktreker.presenter.TaskManager;
import ru.yandex.oop.tasktreker.presenter.impl.InMemoryTaskManager;

public class Main {


    public static void main(String[] args) {

        TaskManager manager = new InMemoryTaskManager();
        HistoryManager historyManager = manager.getHistoryManager();
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


        System.out.println("FIN");
    }

}
