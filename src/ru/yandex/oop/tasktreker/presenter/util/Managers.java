package ru.yandex.oop.tasktreker.presenter.util;

import ru.yandex.oop.tasktreker.exception.ManagerLoadException;
import ru.yandex.oop.tasktreker.model.Task;
import ru.yandex.oop.tasktreker.model.enums.TaskType;
import ru.yandex.oop.tasktreker.presenter.HistoryManager;
import ru.yandex.oop.tasktreker.presenter.impl.FileBackedTasksManager;
import ru.yandex.oop.tasktreker.presenter.impl.InMemoryHistoryManager;
import ru.yandex.oop.tasktreker.presenter.impl.InMemoryTaskManager;
import ru.yandex.oop.tasktreker.presenter.TaskManager;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Managers {

    private final static File file = new File("./file.csv");

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileBackedTaskManager() {
        return loadFromFile(file);
    }


    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fromFile = new FileBackedTasksManager(file.getPath());
        BufferedReader reader = null;
        StringBuilder stringFile = new StringBuilder();
        try {
//            reader = new BufferedReader(new FileReader(file));
            reader = new BufferedReader(new FileReader(FileBackedTasksManager.getFile()));
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
                    fromFile.createTaskAndReturnId(tasks.get(i));
                }
            }

            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getTaskType() != TaskType.EPICTASK) {
                    fromFile.createTaskAndReturnId(tasks.get(i));
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

//    public static FileBackedTasksManager loadFromFile(File file) {
//        FileBackedTasksManager fromFile = new FileBackedTasksManager(file.getPath());
//        BufferedReader reader;
//        StringBuilder sb = new StringBuilder();
//        try {
//            reader = new BufferedReader(new FileReader(file));
//            while (reader.ready()) {
//                sb.append(reader.readLine());
//            }
//            String[] lines = sb.toString().split(System.lineSeparator());
//            for (int i = 1; i < lines.length; i++) {
//                if (!lines[i].isBlank() && i != lines.length - 1) {
//                    switch (fromFile.fromString(lines[i]).getTaskType()) {
//                        case  TASK -> fromFile.saveTask(fromFile(lines[i]));
//                        case  EPICTASK: -> fromFile.saveEpic(fromFile(lines[i]));
//                        case  SUBTASK: -> fromFile.saveSubTask(fromFile(lines[i]));
//                    }
//                }
//            }
//        } catch (IOException e) {
//            throw new ManagerLoadException("Не загрузить  данные " + "\n ошибка",e.getCause());
//        }
//        return null;
//    }
}



