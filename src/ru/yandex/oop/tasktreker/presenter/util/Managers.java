package ru.yandex.oop.tasktreker.presenter.util;

import ru.yandex.oop.tasktreker.serverfunctionalityrealization.HttpTaskManager;
import ru.yandex.oop.tasktreker.presenter.HistoryManager;
import ru.yandex.oop.tasktreker.presenter.impl.FileBackedTasksManager;
import ru.yandex.oop.tasktreker.presenter.impl.InMemoryHistoryManager;
import ru.yandex.oop.tasktreker.presenter.impl.InMemoryTaskManager;

import java.io.File;

public class Managers {

    private final static File file = new File("./file.csv");

    public static HttpTaskManager getDefault() {
        return new HttpTaskManager("http://localhost:8078/");
    }

    public static InMemoryTaskManager getInMemory() {
        return new InMemoryTaskManager();
    }

    public static FileBackedTasksManager getFileBacked(String filePath){
        return new FileBackedTasksManager(filePath);
}

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }


}



