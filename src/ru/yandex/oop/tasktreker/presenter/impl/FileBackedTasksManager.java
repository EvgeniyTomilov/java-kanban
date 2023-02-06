package ru.yandex.oop.tasktreker.presenter.impl;

import ru.yandex.oop.tasktreker.exception.ManagerLoadException;
import ru.yandex.oop.tasktreker.exception.ManagerSaveException;
import ru.yandex.oop.tasktreker.model.EpicTask;
import ru.yandex.oop.tasktreker.model.SubTask;
import ru.yandex.oop.tasktreker.model.Task;
import ru.yandex.oop.tasktreker.model.enums.TaskStatus;
import ru.yandex.oop.tasktreker.model.enums.TaskType;
import ru.yandex.oop.tasktreker.presenter.HistoryManager;
import ru.yandex.oop.tasktreker.presenter.TaskManager;
import ru.yandex.oop.tasktreker.presenter.util.Managers;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private static String file = "src/ru/yandex/oop/tasktreker/resource/file.csv";

    private HistoryManager historyManager = Managers.getDefaultHistory();

    public FileBackedTasksManager() {
        super();
    }

    public FileBackedTasksManager(String path) {

    }// как сделать конструктор для loadFromFile

    public static void main(String[] args){
        TaskManager manager = new FileBackedTasksManager();
        HistoryManager historyManager = manager.getHistoryManager();

//        System.out.println("**********************************TASK*************************************");
        Task task1 = new Task("Тренировка", "день грудь-плечи", TaskType.TASK);
        Task task2 = new Task("Тренировка", "день ноги", TaskType.TASK);
        manager.createTaskAndReturnId(task1);
        manager.createTaskAndReturnId(task2);
        task1.setStatus(TaskStatus.IN_PROGRESS);
        System.out.println(1111);
        historyManager.getHistory().forEach(System.out::println);

//        System.out.println("**********************************EPIC*************************************");
//        EpicTask epicTask1 = new EpicTask("Купить квартиру", "улучшить жилищьные условия", TaskType.EPICTASK);
//        EpicTask epicTask2 = new EpicTask("Сходить в магазин", "Купить продукты", TaskType.EPICTASK);
//        epicTask1.setId(manager.createTaskAndReturnId(epicTask1));
//        epicTask2.setId(manager.createTaskAndReturnId(epicTask2));
//
//        System.out.println("**********************************SUBTASK**********************************");
//        SubTask subTask1 = new SubTask("Деньги", "Накопить бабло",  TaskType.SUBTASK, epicTask1.getId());
//        SubTask subTask2 = new SubTask("Квартира", "Найти хату",  TaskType.SUBTASK, epicTask1.getId());
//        SubTask subTask3 = new SubTask("Банк", "Найти банк с наименьшим %", TaskType.SUBTASK, epicTask1.getId());
//        subTask1.setStatus(TaskStatus.IN_PROGRESS);
//        subTask2.setStatus(TaskStatus.DONE);
//        subTask3.setStatus(TaskStatus.NEW);
//        manager.createTaskAndReturnId(subTask1);
//        manager.createTaskAndReturnId(subTask2);
//        manager.createTaskAndReturnId(subTask3);

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

//        List<Task> history = historyManager.getHistory();
//        history.forEach(System.out::println);
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fromFile = new FileBackedTasksManager(file.getPath());
        try {
            String string = Files.readString(Path.of(file.getPath()));
            String[] lines = string.split(System.lineSeparator());
            for (int i = 1; i < lines.length; i++) {
                if (!lines[i].isBlank() && i != lines.length - 1) {
                    fromFile.createTaskAndReturnId(fromFile.fromString(lines[i]));
                }
                if (i == lines.length -1) {
                    List<Integer> historyList = historyFromString(lines[lines.length -1]);
                    for (int id : historyList) {
                        fromFile.getHistoryManager().add(fromFile.getAnyTask(id));
                    }
                }
            }
            return fromFile;
        } catch (IOException e){
            throw new ManagerLoadException("Не загрузить  данные " + "\n ошибка",e.getCause());
        }
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic" + "\n");
            for (Map.Entry<Integer, Task> pair : getTaskMap().entrySet()) {
                writer.write(toString(pair.getValue()) + "\n");
            }
            for (Map.Entry<Integer, SubTask> pair : getSubTaskMap().entrySet()) {
                writer.write(toString(pair.getValue()) + "\n");
            }
            for (Map.Entry<Integer, EpicTask> pair : getEpicTaskMap().entrySet()) {
                writer.write(toString(pair.getValue()) + "\n");
            }
            writer.write("\n");

//            for (Task task : historyManager.getHistory()) {
//                writer.write(task.getId() + ",");
//            }
            writer.write(historyToString(historyManager));


        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранит данные " + "\n ошибка",e.getCause());
        }
    }

    public String toString(Task task) {


        if (task instanceof SubTask) {
            return task.getId() + "," + task.getTaskType() + "," + task.getName() + "," + task.getStatus() + ","
                    + task.getDescription() + "," + ((SubTask) task).getEpicId();
        } else {
            return task.getId() + "," + task.getTaskType() + "," + task.getName() + "," + task.getStatus() + ","
                    + task.getDescription();
        }
    }

    public Task fromString(String str) {

            String[] element = str.split(",");// разбили строку по запятой
            int id = Integer.parseInt(element[0]);//создали поле айди через парсинт
            TaskType taskType; //создали поле тасктайп
            if (element[1].equals(TaskType.TASK.toString())) { // сравнили значение в массиве со значением из энама через тустринг
                taskType = TaskType.TASK; // присвоили тасктайпу значение по энаму
            } else if (element[1].equals(TaskType.SUBTASK.toString())) {
                taskType = TaskType.SUBTASK;
            } else if (element[1].equals(TaskType.EPICTASK.toString())) {
                taskType = TaskType.EPICTASK;
            } else {
                taskType = null;
            }
            String name = element[2];
            TaskStatus taskStatus = null; // создали поле таскстатус
            if (element[3].equals(TaskStatus.NEW.toString())) { // сравнили значение в массиве со значением енама через тустринг
                taskStatus = TaskStatus.NEW;  // присвоили таскстатусу значение по энаму
            } else if (element[3].equals(TaskStatus.IN_PROGRESS.toString())) {
                taskStatus = TaskStatus.IN_PROGRESS;
            } else if (element[3].equals(TaskStatus.DONE.toString())) {
                taskStatus = TaskStatus.DONE;
            }
            String desc = element[4];
            Task task = null;

            if (element.length == 6) { // если количество элементов = 6 то это сабтаск
                int idEpic = Integer.parseInt(element[5]); // создали доп поле эпикайди через парсинт
                task = new SubTask(name, desc, taskType, idEpic); // создали новую сабтаску
                task.setStatus(taskStatus); // присвоили статус новой сабтаске
                task.setId(id); // установили айди сабтаске
            } else  {

                if (taskType == TaskType.TASK) { // если поле таски таска, то это таска
                    task = new Task(name, id, desc, taskStatus, taskType); // создали новую таску

                } else { // иначе это эпиктаск
                    task = new EpicTask(name, desc, taskType);// создали новый эпик таск
                    task.setId(id);// присвоили айди
                }
            }

            return task;
        }

     private static String historyToString(HistoryManager manager){
        StringBuilder stringBuilder = new StringBuilder();// создали стрингбилдер
        List<Task> tasks = manager.getHistory();// создали лист тасок из истории
         for (Task task : tasks) {
             stringBuilder.append(task.getId());
             stringBuilder.append(",");
         }//достали айди из таски сложили их через ","
         return stringBuilder.toString(); //вернули стринговое значение стрингбилдера
    }

    public static List<Integer> historyFromString(String str){
        String[] element = str.split(",");
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i<element.length; i++) {
            list.add(Integer.parseInt(element[i])) ;// добавили элементы массива в лист
        }
        return list;
    }

    @Override
    public Collection<? extends Task> getTaskByType(TaskType taskType) {
        Collection<? extends Task> tasksByType = super.getTaskByType(taskType);
        save();
        return tasksByType;
    }

    @Override
    public Task getByIdAndTypeTask(int id, TaskType taskType) {
        Task tasksByIdAndType = super.getByIdAndTypeTask(id, taskType);
        save();
        return tasksByIdAndType;
    }

    @Override
    public boolean deleteTasksByType(TaskType taskType) {
        boolean tasksByType = super.deleteTasksByType(taskType);
        save();
        return tasksByType;
    }

    @Override
    public boolean deleteByIdAndTypeTask(Integer id, TaskType taskType) {
        boolean deleteByIdAndTypeTask = super.deleteByIdAndTypeTask(id, taskType);
        save();
        return deleteByIdAndTypeTask;
    }

    @Override
    public int createTaskAndReturnId(Task task) {
        return super.createTaskAndReturnId(task);

    }

    @Override
    public List<SubTask> getListSubtask(int id) {
        return super.getListSubtask(id);

    }

    @Override
    public void changeStatus(int epicId) {
        super.changeStatus(epicId);
        save();
    }

    @Override
    public int getNextId() {
        return super.getNextId();
    }

    @Override
    public HistoryManager getHistoryManager() {
        HistoryManager HistoryManager = super.getHistoryManager();
        save();
        return HistoryManager;
    }

    @Override
    public Task getAnyTask(int id) {
        return super.getAnyTask(id);
    }

    @Override
    public Task getTask(int id) {
        return super.getTask(id);
    }

    @Override
    public SubTask getSubtask(int id) {

        return super.getSubtask(id);

    }

    @Override
    public EpicTask getEpic(int id) {
        return super.getEpic(id);
    }

    @Override
    public Map<Integer, Task> getTaskMap() {
        return super.getTaskMap();
    }

    @Override
    public Map<Integer, SubTask> getSubTaskMap() {
        return super.getSubTaskMap();
    }

    @Override
    public Map<Integer, EpicTask> getEpicTaskMap() {
        return super.getEpicTaskMap();
    }
}
