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
    public void save() throws ManagerSaveException {   // сохранять состояние менеджера в строку или в json
        // и отправлять на сервер.
        Writer writer = new StringWriter();
        Map<Integer, Task> tmpStorage = new HashMap<>();
        for (Task value :  getTaskMap().values()) tmpStorage.put(value.getId(), value);
        for (EpicTask value : getEpicTaskMap().values()) tmpStorage.put(value.getId(), value);
        for (SubTask value : getSubTaskMap().values()) tmpStorage.put(value.getId(), value);


        try (BufferedWriter bw = new BufferedWriter(writer) ) {  //OUT
            bw.write("id,type,name,status,description,startTime,duration,epic");
            bw.newLine();
            for (Task value : tmpStorage.values()) {
                bw.write(toString(value));
                bw.newLine();
            }
            bw.newLine();
            bw.write(historyToString(getHistoryManager()));


        } catch (IOException exception) {
            throw new ManagerSaveException("Во время записи данных менеджера в строку произошла ошибка!", exception);
        }
        String stringForSave = writer.toString();
        kvTaskClient.put(key, gson.toJson(stringForSave));
    }


//    @Override
//    public void save() {
//        kvTaskClient.put(key, gson.toJson(this));
//    }



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







//    public HttpTaskManager loadFromServer() {
//        HttpTaskManager newHttpTaskManager = Managers.getDefault();
//        String response = kvTaskClient.load(key);
//        if (response != null) {
//            String[] split = response.split("@");
//            newHttpTaskManager.setKey(split[0]);
//            newHttpTaskManager.setNextId(Integer.parseInt(split[1]));
//            newHttpTaskManager.setNextId(Integer.parseInt(split[2]));
//            newHttpTaskManager.setNextId(Integer.parseInt(split[3]));
//            if (!split[4].equals("null")) {
//                saveTaskFromServer(split[4], newHttpTaskManager);
//            }
//            if (!split[5].equals("null")) {
//                saveTaskFromServer(split[5], newHttpTaskManager);
//            }
//            if (!split[6].equals("null")) {
//                saveTaskFromServer(split[6], newHttpTaskManager);
//            }
//            if (!split[7].equals("null")) {
//                saveHistoryFromServer(split[7], newHttpTaskManager);
//            }
//        }
//        return newHttpTaskManager;
//    }
//    @Override
//    public String toString(Task task) {
//        if (task instanceof SubTask) {
//            return task.getId() + "/" + task.getTaskType() + "/" + task.getName() + "/" + task.getStatus() + "/"
//                    + task.getDescription() + "/" + task.getStartTime() + "/" + task.getDuration() + "/" + ((SubTask) task).getEpicId();
//        } else {
//            return task.getId() + "/" + task.getTaskType() + "/" + task.getName() + "/" + task.getStatus() + "/"
//                    + task.getDescription() + "/" + task.getStartTime() + "/" + task.getDuration();
//        }
//    }
//
//    public Task taskFromString(String str) { // заменить String stringFromServer = gson.fromJson(response, String.class);
//        String[] element = str.split("/");// разбили строку по запятой
//        int id = Integer.parseInt(element[0]);//создали поле айди через парсинт
//        TaskType taskType = TaskType.valueOf(element[1]); //создали поле тасктайп
//        String name = element[2];
//        TaskStatus taskStatus = TaskStatus.valueOf(element[3]);
//        String desc = element[4];
//        Task task = null;
//
//        if (element.length == 8) { // если количество элементов = 8, то это сабтаск
//            int idEpic = Integer.parseInt(element[7]); // создали доп поле эпикайди через парсинт
//            task = new SubTask(name, desc, idEpic, Duration.parse(element[6]), element[5]); // создали новую сабтаску
//            task.setStatus(taskStatus); // присвоили статус новой сабтаске
//            task.setId(id); // установили айди сабтаске
//        } else {
//            if (taskType == TaskType.TASK) { // если поле таски таска, то это таска
//                task = new Task(name, id, desc, taskStatus, Duration.parse(element[6]), LocalDateTime.parse(element[5])); // создали новую таску
//            } else { // иначе это эпиктаск
//                task = new EpicTask(name, desc);// создали новый эпик таск
//                task.setId(id);// присвоили айди
//            }
//        }
//        return task;
//    }
//
//    public void saveTaskFromServer(String stringToSplit, HttpTaskManager manager) {
//        String[] tasksSplit = stringToSplit.split(",");
//        for (int i = 0; i < tasksSplit.length; i++) {
//            String substring;
//            if (tasksSplit.length == 1) {
//                substring = tasksSplit[i].substring(2, tasksSplit[i].length() - 2);
//            } else if (i == 0) {
//                substring = tasksSplit[i].substring(2, tasksSplit[i].length() - 1);
//            } else if (i == tasksSplit.length - 1) {
//                substring = tasksSplit[i].substring(1, tasksSplit[i].length() - 2);
//            }  else {
//                substring = tasksSplit[i].substring(1, tasksSplit[i].length() - 1);
//            }
//            switch (taskFromString(substring).getTaskType()) {
//                case TASK -> manager.createTaskAndReturnId(taskFromString(substring));
//                case EPICTASK -> manager.createTaskAndReturnId((EpicTask) taskFromString(substring));
//                case SUBTASK -> manager.createTaskAndReturnId((SubTask) taskFromString(substring));
//            }
//        }
//    }
//
//    public void saveHistoryFromServer(String stringToSplit, HttpTaskManager manager) {
//        String[] historySplit = stringToSplit.split(",");
//        for (String s : historySplit) {
//            if (s != null) {
//                manager.getHistoryManager().add(manager.getAnyTask(Integer.parseInt(s)));
//                manager.getHistoryManager().add(manager.getAnyTask(Integer.parseInt(s)));
//                manager.getHistoryManager().add(manager.getAnyTask(Integer.parseInt(s)));
//            }
//        }
//    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}