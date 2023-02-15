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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic");
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
        StringBuilder stringFile = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(FileBackedTasksManager.getFile()))) {
            while (reader.ready()) {
                stringFile.append(reader.readLine());
                stringFile.append(System.lineSeparator());
            }
            String[] lines = stringFile.toString().split(System.lineSeparator());
            List<Task> tasks = new ArrayList<>();

            for (int i = 1; i < lines.length; i++) {
                if (!lines[i].isBlank() && i != lines.length - 1) {
                    tasks.add(fromFile.fromString(lines[i]));
                }
            }
            // сложили эпики
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getTaskType() == TaskType.EPICTASK) {
//                    fromFile.createTaskAndReturnId(tasks.get(i));
                    fromFile.getEpicTaskMap().put(tasks.get(i).getId(), (EpicTask) tasks.get(i));
                }
            }

            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getTaskType() == TaskType.SUBTASK) {
                    fromFile.getSubTaskMap().put(tasks.get(i).getId(), (SubTask) tasks.get(i));
                }
                if (tasks.get(i).getTaskType() == TaskType.TASK) {
                    fromFile.getTaskMap().put(tasks.get(i).getId(), tasks.get(i));
                }
            }

            List<Integer> history = FileBackedTasksManager.historyFromString(lines[lines.length - 1]);
            if (history.size() != 0) {
                for (Integer taskId : history) {
                    fromFile.getHistoryManager().add(fromFile.getAnyTask(taskId));
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException("ERROR");
        }
        return fromFile;
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
        TaskType taskType = TaskType.valueOf(element[1]); //создали поле тасктайп

//        if (TaskType.valueOf(element[1]).equals(TaskType.TASK)) { // сравнили значение в массиве со значением из энама через тустринг
//            taskType = TaskType.TASK; // присвоили тасктайпу значение по энаму
//        } else if (TaskType.valueOf(element[1]).equals(TaskType.SUBTASK)) {
//            taskType = TaskType.SUBTASK;
//        } else if (TaskType.valueOf(element[1]).equals(TaskType.EPICTASK)) {
//            taskType = TaskType.EPICTASK;
//        } else {
//            taskType = null;
//        }

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

        if (element.length == 6) { // если количество элементов = 6, то это сабтаск
            int idEpic = Integer.parseInt(element[5]); // создали доп поле эпикайди через парсинт
            task = new SubTask(name, desc, idEpic); // создали новую сабтаску
            task.setStatus(taskStatus); // присвоили статус новой сабтаске
            task.setId(id); // установили айди сабтаске
        } else {

            if (taskType == TaskType.TASK) { // если поле таски таска, то это таска
                task = new Task(name, id, desc, taskStatus); // создали новую таску

            } else { // иначе это эпиктаск
                task = new EpicTask(name, desc);// создали новый эпик таск
                task.setId(id);// присвоили айди
            }
        }

        return task;
    }

    private static String historyToString(HistoryManager manager) {
        StringBuilder stringBuilder = new StringBuilder();// создали стрингбилдер
        List<Task> tasks = manager.getHistory();// создали лист тасок из истории
        for (Task task : tasks) {
            stringBuilder.append(task.getId());
            stringBuilder.append(",");
        }//достали айди из таски сложили их через ","
        return stringBuilder.toString(); //вернули стринговое значение стрингбилдера
    }

    public static List<Integer> historyFromString(String str) {
        String[] element = str.split(",");
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < element.length; i++) {
            list.add(Integer.parseInt(element[i]));// добавили элементы массива в лист
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

    public static void main(String[] args) {
        TaskManager manager = loadFromFile(new File("resource\\file.csv"));
        HistoryManager historyManager = manager.getHistoryManager();

        manager.getAllTasks().forEach(System.out::println);

        //**********************************TASK*************************************;
        Task task1 = new Task("Тренировка", "день грудь-плечи");
        Task task2 = new Task("Тренировка", "день ноги");
        manager.createTaskAndReturnId(task1);
        manager.createTaskAndReturnId(task2);
        task1.setStatus(TaskStatus.IN_PROGRESS);


//        **********************************EPIC*************************************
        EpicTask epicTask1 = new EpicTask("Купить квартиру", "улучшить жилищьные условия");
        EpicTask epicTask2 = new EpicTask("Сходить в магазин", "Купить продукты");
        epicTask1.setId(manager.createTaskAndReturnId(epicTask1));
        epicTask2.setId(manager.createTaskAndReturnId(epicTask2));

//       **********************************SUBTASK**********************************
        SubTask subTask1 = new SubTask("Деньги", "Накопить бабло", epicTask1.getId());
        SubTask subTask2 = new SubTask("Квартира", "Найти хату", epicTask1.getId());
        SubTask subTask3 = new SubTask("Банк", "Найти банк с наименьшим %", epicTask1.getId());
        subTask1.setStatus(TaskStatus.IN_PROGRESS);
        subTask2.setStatus(TaskStatus.DONE);
        subTask3.setStatus(TaskStatus.NEW);
        manager.createTaskAndReturnId(subTask1);
        manager.createTaskAndReturnId(subTask2);
        manager.createTaskAndReturnId(subTask3);

        Task t1 = manager.getAnyTask(4);
        Task t2 = manager.getAnyTask(2);
        Task t3 = manager.getAnyTask(1);


        t1 = manager.getAnyTask(5);
        t1 = manager.getAnyTask(5);
        t1 = manager.getAnyTask(5);


        List<Task> history = historyManager.getHistory();

        history.forEach(System.out::println);

    }
}
