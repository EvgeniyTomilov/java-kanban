package ru.yandex.oop.tasktreker.presenter.util;

import ru.yandex.oop.tasktreker.exception.ManagerLoadException;
import ru.yandex.oop.tasktreker.presenter.HistoryManager;
import ru.yandex.oop.tasktreker.presenter.impl.FileBackedTasksManager;
import ru.yandex.oop.tasktreker.presenter.impl.InMemoryHistoryManager;
import ru.yandex.oop.tasktreker.presenter.impl.InMemoryTaskManager;
import ru.yandex.oop.tasktreker.presenter.TaskManager;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

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
        BufferedReader reader;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new FileReader(file));
            while (reader.ready()) {
                sb.append(reader.readLine());
            }
            String[] lines = sb.toString().split(System.lineSeparator());
            for (int i = 1; i < lines.length; i++) {
                if (!lines[i].isBlank() && i != lines.length - 1) {
                    switch (fromFile.fromString(lines[i]).getTaskType()) {
                        case  TASK -> fromFile.saveTask(fromFile(lines[i]));
                        case  EPICTASK: -> fromFile.save(fromFile(lines[i]));
                        case  SUBTASK: -> fromFile.save(fromFile(lines[i]));
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException("Не загрузить  данные " + "\n ошибка",e.getCause());
        }
        return null;
    }
}



