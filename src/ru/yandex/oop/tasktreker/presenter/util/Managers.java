package ru.yandex.oop.tasktreker.presenter.util;


import ru.yandex.oop.tasktreker.exception.ManagerLoadException;
import ru.yandex.oop.tasktreker.exception.ManagerSaveException;
import ru.yandex.oop.tasktreker.presenter.HistoryManager;
import ru.yandex.oop.tasktreker.presenter.impl.FileBackedTasksManager;
import ru.yandex.oop.tasktreker.presenter.impl.InMemoryHistoryManager;
import ru.yandex.oop.tasktreker.presenter.impl.InMemoryTaskManager;
import ru.yandex.oop.tasktreker.presenter.TaskManager;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager loadFromFile(File file) {

       try {
           String string = Files.readString(Path.of(file.getPath()));
       }catch (IOException e){
          throw new ManagerLoadException("Не загрузить  данные " + "\n ошибка",e.getCause());
       }

        return null;
    }

}



