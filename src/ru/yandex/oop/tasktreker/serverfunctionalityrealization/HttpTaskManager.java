package ru.yandex.oop.tasktreker.serverfunctionalityrealization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ru.yandex.oop.tasktreker.exception.ManagerSaveException;
import ru.yandex.oop.tasktreker.serverfunctionalityrealization.customjson.HttpTaskManagerSerializer;
import ru.yandex.oop.tasktreker.model.EpicTask;
import ru.yandex.oop.tasktreker.model.SubTask;
import ru.yandex.oop.tasktreker.model.Task;
import ru.yandex.oop.tasktreker.model.enums.TaskStatus;
import ru.yandex.oop.tasktreker.model.enums.TaskType;
import ru.yandex.oop.tasktreker.presenter.impl.FileBackedTasksManager;
import ru.yandex.oop.tasktreker.presenter.util.Managers;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient kvTaskClient;
    private String key;
    private final Gson gson;

    public HttpTaskManager(String urlServer) {
        super();
        kvTaskClient = new KVTaskClient(urlServer);
        gson = new GsonBuilder().setPrettyPrinting().
                registerTypeAdapter(HttpTaskManager.class, new HttpTaskManagerSerializer()).create();
    }

    @Override
    public void save() {

        Map<Integer, Task> taskMap = new HashMap<>();
        for (Task value :  getTaskMap().values()) taskMap.put(value.getId(), value);
        for (EpicTask value : getEpicTaskMap().values()) taskMap.put(value.getId(), value);
        for (SubTask value : getSubTaskMap().values()) taskMap.put(value.getId(), value);


        kvTaskClient.put(key, gson.toJson(taskMap));
    }





    public HttpTaskManager loadFromServer() {
        HttpTaskManager newHttpTaskManager = Managers.getDefault();
        String response = kvTaskClient.load(key);
        String stringFromServer = gson.fromJson(response, String.class);

        List<String> tmp = new ArrayList<>();
        String isHistory = "";

        try (Reader reader = new StringReader(stringFromServer); BufferedReader br = new BufferedReader(reader)) {
            String line = "";
            while (br.ready() && !line.equals("It is history:" + null) ) {
                line = isHistory + br.readLine();
                if (!line.isBlank() && !line.equals("It is history:" + null)) {
                    tmp.add(line);
                } else {
                    isHistory = "It is history:";
                }
            }
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время распаковки данных с сервера");
        }

        for (int i = 1; i < tmp.size(); i++) {
            if (tmp != null) {
                if (!tmp.get(i).isBlank() && !tmp.get(i).contains("It is history:")) {
                    String[] tmpArray = tmp.get(i).split(",");  //строковый массив после деления строки по ","
                    switch (TaskType.valueOf(tmpArray[1])) {
                        case TASK:
                            Task tmpTask = fromString(tmp.get(i));
                            newHttpTaskManager.createTaskAndReturnId(tmpTask);
                            break;
                        case EPICTASK:
                            EpicTask tmpEpic = (EpicTask) fromString(tmp.get(i));
                            newHttpTaskManager.createTaskAndReturnId(tmpEpic);
                            break;
                        case SUBTASK:
                            SubTask tmpSubTask = (SubTask) fromString(tmp.get(i));
                            newHttpTaskManager.createTaskAndReturnId(tmpSubTask);
                            break;
                    }
                } else if (tmp.get(i).isBlank()) {
                    continue;
                } else {


                    String historyIdsFromTask = tmp.get(i).substring(isHistory.length());
                    for (Integer id : historyFromString(historyIdsFromTask)) {
                        newHttpTaskManager.getAnyTask(id);
                        newHttpTaskManager.getAnyTask(id);
                        newHttpTaskManager.getAnyTask(id);
                    }
                }
            }
        }
        return newHttpTaskManager;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}