package ru.yandex.oop.tasktreker.presenter.util;


import ru.yandex.oop.tasktreker.exception.ManagerLoadException;
import ru.yandex.oop.tasktreker.presenter.HistoryManager;
import ru.yandex.oop.tasktreker.presenter.impl.FileBackedTasksManager;
import ru.yandex.oop.tasktreker.presenter.impl.InMemoryHistoryManager;
import ru.yandex.oop.tasktreker.presenter.impl.InMemoryTaskManager;
import ru.yandex.oop.tasktreker.presenter.TaskManager;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;




public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }


    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fromFile = new FileBackedTasksManager(file.getPath());
        try {
           String stringFile = Files.readString(Path.of(file.getPath()));
//           String[] lines = stringFile.split(System.lineSeparator());
//           for(int i = 1; i< lines.length; i++){
//               if (!lines[i].isBlank() && i != lines.length-1){
//                   switch (fromString(lines[i]).getType()) {
//                       case TASK -> fromFile.getTask(fromString(lines[i]));
//                       case EPICTASK -> fromFile.getEpic()
//
//                   }
//               }
//               if (i == lines.length-1){
//                   ArrayList<Integer> history = historyFromString(lines[i]);
//               }
//           }
       }catch (IOException e){
          throw new ManagerLoadException("Не загрузить  данные " + "\n ошибка",e.getCause());
       }

        return null;
    }

}



