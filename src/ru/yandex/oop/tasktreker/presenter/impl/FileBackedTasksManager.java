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

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private static File file;

    public FileBackedTasksManager() {
        super();
    }

    public FileBackedTasksManager(String path) {
        file = new File(path);
    }

    public static File getFile() {
        return file;
    }

    public void save() {
        String fileName = "resource/file.csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("id,type,name,status,description,epic,startTime,duration");
            writer.newLine();
            for (Map.Entry<Integer, Task> pair : getTaskMap().entrySet()) {
                writer.write(toString(pair.getValue()));
                writer.newLine();
            }
            for (Map.Entry<Integer, SubTask> pair : getSubTaskMap().entrySet()) {
                writer.write(toString(pair.getValue()));
                writer.newLine();
            }
            for (Map.Entry<Integer, EpicTask> pair : getEpicTaskMap().entrySet()) {
                writer.write(toString(pair.getValue()));
                writer.newLine();
            }
            writer.newLine();
            writer.write(historyToString(getHistoryManager()));
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранит данные " + "\n ошибка", e.getCause());
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fromFile = new FileBackedTasksManager(file.getPath());
        int taskCount = 0;
        boolean isContainsHistory = false;
        StringBuilder stringFile = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(FileBackedTasksManager.getFile()))) {
            while (reader.ready()) {
                stringFile.append(reader.readLine());
                stringFile.append(System.lineSeparator());
            }
            String[] lines = stringFile.toString().split(System.lineSeparator());
            List<Task> tasks = new ArrayList<>();

            for (int i = 1; i < lines.length; i++) {
                if (lines[i].isBlank()) {
                    if (i < lines.length - 1) {
                        isContainsHistory = true;
                    }
                    break;
                }
                tasks.add(fromFile.fromString(lines[i]));
            }
            // сложили эпики
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getTaskType() == TaskType.EPICTASK) {
                    fromFile.getEpicTaskMap().put(tasks.get(i).getId(), (EpicTask) tasks.get(i));
                    taskCount++;
                }
            }

            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getTaskType() == TaskType.SUBTASK) {
                    fromFile.getSubTaskMap().put(tasks.get(i).getId(), (SubTask) tasks.get(i));
                    fromFile.getEpicTaskMap().get(((SubTask) tasks.get(i)).getEpicId()).addSubTask((SubTask) tasks.get(i));
                    taskCount++;
                }
                if (tasks.get(i).getTaskType() == TaskType.TASK) {
                    fromFile.getTaskMap().put(tasks.get(i).getId(), tasks.get(i));
                    taskCount++;
                }
            }

            fromFile.setNextId(taskCount);
            if (isContainsHistory) {
                List<Integer> history = FileBackedTasksManager.historyFromString(lines[lines.length - 1]);
                if (history.size() != 0) {
                    for (Integer taskId : history) {
                        fromFile.getHistoryManager().add(fromFile.getAnyTask(taskId));
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException("ERROR", e);
        }
        return fromFile;
    }


    public String toString(Task task) {
        if (task instanceof SubTask) {
            return task.getId() + "," + task.getTaskType() + "," + task.getName() + "," + task.getStatus() + ","
                    + task.getDescription() + "," + task.getStartTime() + "," + task.getDuration() + "," + ((SubTask) task).getEpicId();
        } else {
            return task.getId() + "," + task.getTaskType() + "," + task.getName() + "," + task.getStatus() + ","
                    + task.getDescription() + "," + task.getStartTime() + "," + task.getDuration();
        }
    }

//    ("id,type,name,status,description,startTime,duration,epic");

    public Task fromString(String str) {
        String[] element = str.split(",");// разбили строку по запятой
        int id = Integer.parseInt(element[0]);//создали поле айди через парсинт
        TaskType taskType = TaskType.valueOf(element[1]); //создали поле тасктайп
        String name = element[2];
        TaskStatus taskStatus = TaskStatus.valueOf(element[3]);
        String desc = element[4];
        Task task = null;

        if (element.length == 8) { // если количество элементов = 8, то это сабтаск
            int idEpic = Integer.parseInt(element[7]); // создали доп поле эпикайди через парсинт
            task = new SubTask(name, desc, idEpic, Duration.parse(element[6]), element[5]); // создали новую сабтаску
            task.setStatus(taskStatus); // присвоили статус новой сабтаске
            task.setId(id); // установили айди сабтаске
        } else {
            if (taskType == TaskType.TASK) { // если поле таски таска, то это таска
                task = new Task(name, id, desc, taskStatus, Duration.parse(element[6]), LocalDateTime.parse(element[5])); // создали новую таску
            } else { // иначе это эпиктаск
                task = new EpicTask(name, desc);// создали новый эпик таск
                task.setId(id);// присвоили айди
            }
        }
        return task;
    }

     protected static String historyToString(HistoryManager manager) {
        StringBuilder stringBuilder = new StringBuilder();// создали стрингбилдер
        List<Task> tasks = manager.getHistory();// создали лист тасок из истории
        if (tasks.size() > 1) {
            for (Task task : tasks) {
                stringBuilder.append(task.getId());
                stringBuilder.append(",");
            }
        } else {
            for (Task task : tasks) {
                stringBuilder.append(task.getId());
            }
        }
        return stringBuilder.toString();
    }


    public static List<Integer> historyFromString(String str) {
        List<Integer> list = new ArrayList<>();
        if (str.length() == 0) {
            return list;
        } else if (str.length() > 1) {
            for (String s : str.split(",")) {
                list.add(Integer.parseInt(s));
            }
        } else {
            list.add(Integer.parseInt(str));
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
    public void changeStatus(int epicId) {
        super.changeStatus(epicId);
        save();
    }

    @Override
    public Task getAnyTask(int id) {
        Task getTask = super.getAnyTask(id);
        save();
        return getTask;
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> list = new ArrayList<>();
        for (Map.Entry<Integer, Task> pair : getTaskMap().entrySet()) {
            list.add(pair.getValue());
        }
        for (Map.Entry<Integer, SubTask> pair : getSubTaskMap().entrySet()) {
            list.add(pair.getValue());
        }
        for (Map.Entry<Integer, EpicTask> pair : getEpicTaskMap().entrySet()) {
            list.add(pair.getValue());
        }
        return list;
    }

    @Override
    public String deleteTaskByID(int id){
        String str = super.deleteTaskByID(id);
        save();
        return str;
    }

    @Override
    public String deleteEpicTaskByID(int epicId) {
        String str = super.deleteEpicTaskByID(epicId);
        save();
        return str;
    }

    @Override
    public String deleteSubTaskByID(int id) {
        String str = super.deleteSubTaskByID(id);
        save();
        return str;
    }


    public static void main(String[] args) {
       TaskManager manager = loadFromFile(new File("resource\\file.csv"));
//        TaskManager manager = new FileBackedTasksManager();
//        HistoryManager historyManager = manager.getHistoryManager();
//
//        manager.getAllTasks().forEach(System.out::println);
//
//        Task task1 = new Task("Тренировка", "день грудь-плечи", Duration.ofMinutes(10L), LocalDateTime.parse("2007-12-03T10:15:30"));
//        Task task2 = new Task("Тренировка", "день ноги", Duration.ofMinutes(15L), LocalDateTime.parse("2007-12-03T10:15:30"));
//        manager.createTaskAndReturnId(task1);
//        manager.createTaskAndReturnId(task2);
//        task1.setStatus(TaskStatus.IN_PROGRESS);
//
////        **********************************EPIC*************************************
//        EpicTask epicTask1 = new EpicTask("Купить квартиру", "улучшить жилищьные условия");
//        EpicTask epicTask2 = new EpicTask("Сходить в магазин", "Купить продукты");
//        epicTask1.setId(manager.createTaskAndReturnId(epicTask1));
//        epicTask2.setId(manager.createTaskAndReturnId(epicTask2));
//
////       **********************************SUBTASK**********************************
//        SubTask subTask1 = new SubTask("Деньги", "Накопить бабло", epicTask1.getId(), Duration.ofMinutes(110L), "2007-12-03T10:15:30");
//        SubTask subTask2 = new SubTask("Квартира", "Найти хату", epicTask1.getId(), Duration.ofMinutes(120L), "2007-12-03T10:15:30");
//        SubTask subTask3 = new SubTask("Банк", "Найти банк с наименьшим %", epicTask1.getId(), Duration.ofMinutes(130L), "2007-12-03T10:15:30");
//        subTask1.setStatus(TaskStatus.IN_PROGRESS);
//        subTask2.setStatus(TaskStatus.NEW);
//        subTask3.setStatus(TaskStatus.NEW);
//        manager.createTaskAndReturnId(subTask1);
//        manager.createTaskAndReturnId(subTask2);
//        manager.createTaskAndReturnId(subTask3);
//
//        Task t1 = manager.getAnyTask(4);
//        Task t2 = manager.getAnyTask(2);
//        Task t3 = manager.getAnyTask(1);
//
//        t1 = manager.getAnyTask(5);
//        t1 = manager.getAnyTask(5);
//        t1 = manager.getAnyTask(5);
//
//        System.out.println("HISTORY");
//        List<Task> history = historyManager.getHistory();
//        history.forEach(System.out::println);
        manager.getAllTasks().forEach(System.out::println);
    }
}
