package ru.yandex.oop.tasktreker;

import ru.yandex.oop.tasktreker.model.*;
import ru.yandex.oop.tasktreker.model.enums.TaskStatus;
import ru.yandex.oop.tasktreker.model.enums.TaskType;
import ru.yandex.oop.tasktreker.presenter.impl.InMemoryTaskManager;
import ru.yandex.oop.tasktreker.presenter.util.Managers;
import ru.yandex.oop.tasktreker.presenter.TaskManager;

public class Main {


    public static void main(String[] args) {
        TaskManager manager = new InMemoryTaskManager();
        System.out.println("**********************************TASK**********************************");
        Task task1 = new Task("Тренировка", "день грудь-плечи", TaskStatus.NEW, TaskType.TASK);
        Task task2 = new Task("Проснуться", "открыть глаза-встать с кровати", TaskStatus.DONE, TaskType.TASK);

        manager.createTaskAndReturnId(task1);
        manager.createTaskAndReturnId(task2);

        System.out.println("---History---");
        manager.getHistoryManager().getHistory().forEach(System.out::println);

        System.out.println("---History---");

        System.out.println(manager.getByIdAndTypeTask(0, TaskType.TASK));
        System.out.println(manager.getByIdAndTypeTask(1, TaskType.TASK));


        System.out.println("**********************************EPICTASK1**********************************");
        EpicTask epicTask1 = new EpicTask("Купить квартиру", "улучшить жилищьные условия", TaskType.EPICTASK);

        int epicId1 = manager.createTaskAndReturnId(epicTask1);
        System.out.println(manager.getByIdAndTypeTask(2, TaskType.EPICTASK));


        System.out.println("**********************************SUBTASK(Х2)**********************************");
        SubTask subTask1 = new SubTask("Деньги", "Накопить бабло", TaskStatus.IN_PROGRESS, TaskType.SUBTASK, epicId1);
        SubTask subTask2 = new SubTask("Квартира", "Найти хату", TaskStatus.DONE, TaskType.SUBTASK, epicId1);

        manager.createTaskAndReturnId(subTask1);
        manager.createTaskAndReturnId(subTask2);

        System.out.println(manager.getByIdAndTypeTask(3, TaskType.SUBTASK));
        System.out.println(manager.getByIdAndTypeTask(4, TaskType.SUBTASK));


        System.out.println("**********************************EPICTASK1(2SubTask)**********************************");
        System.out.println(manager.getByIdAndTypeTask(2, TaskType.EPICTASK));


        System.out.println("**********************************EPICTASK2**********************************");
        EpicTask epicTask2 = new EpicTask("купить продукты", "сходить в магазин", TaskType.EPICTASK);

        int epicId2 = manager.createTaskAndReturnId(epicTask2);
        System.out.println(manager.getByIdAndTypeTask(5, TaskType.EPICTASK));


        System.out.println("**********************************SUBTASK(Х1)**********************************");
        SubTask subTask3 = new SubTask("Купить мясо", "купить 2 кг свинины", TaskStatus.IN_PROGRESS, TaskType.SUBTASK, epicId2);

        manager.createTaskAndReturnId(subTask3);

        System.out.println(manager.getByIdAndTypeTask(6, TaskType.SUBTASK));

        System.out.println("---History---");
        manager.getHistoryManager().getHistory().forEach(System.out::println); // напечатать все таски из истории
        System.out.println("---History---");

        System.out.println("**********************************EPICTASK2(1SubTask)**********************************");
        System.out.println(manager.getByIdAndTypeTask(5, TaskType.EPICTASK));


        System.out.println("**********************************UPDETEID(TASK)**********************************");
        Task updateTask0 = new Task("Тренировка", "день грудь-плечи", TaskStatus.IN_PROGRESS, TaskType.TASK);

        Managers.updateTask(0, updateTask0, manager);


        System.out.println(manager.getByIdAndTypeTask(0, TaskType.TASK));

        System.out.println("---History---");
        manager.getHistoryManager().getHistory().forEach(System.out::println); // напечатать все таски из истории
        System.out.println("---History---");

        System.out.println("**********************************DELETEID(TASK)**********************************");
        System.out.println(manager.deleteByIdAndTypeTask(0, TaskType.TASK));
        System.out.println(manager.getTaskByType(TaskType.TASK));


        System.out.println("**********************************DELETEID(SUBTASK)**********************************");
        manager.deleteByIdAndTypeTask(3, TaskType.SUBTASK);
        System.out.println(manager.getByIdAndTypeTask(3, TaskType.SUBTASK));
        System.out.println(manager.getByIdAndTypeTask(2, TaskType.EPICTASK));


        System.out.println("**********************************DELETEID(EPICTASK2)**********************************");
        System.out.println(manager.deleteByIdAndTypeTask(5, TaskType.EPICTASK));
        System.out.println(manager.getTaskByType(TaskType.SUBTASK));
        System.out.println("FIN");
    }
}
