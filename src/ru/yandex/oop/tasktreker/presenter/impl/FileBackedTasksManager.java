package ru.yandex.oop.tasktreker.presenter.impl;

import ru.yandex.oop.tasktreker.exception.ManagerSaveException;
import ru.yandex.oop.tasktreker.model.EpicTask;
import ru.yandex.oop.tasktreker.model.SubTask;
import ru.yandex.oop.tasktreker.model.Task;
import ru.yandex.oop.tasktreker.model.enums.TaskStatus;
import ru.yandex.oop.tasktreker.model.enums.TaskType;
import ru.yandex.oop.tasktreker.presenter.HistoryManager;
import ru.yandex.oop.tasktreker.presenter.util.Managers;

import java.io.*;
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

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic" + "\n");
            for (Map.Entry<Integer, Task> pair : getTaskMap().entrySet()) {
                writer.write(toString(pair.getValue()));
            }
            for (Map.Entry<Integer, SubTask> pair : getSubTaskMap().entrySet()) {
                writer.write(toString(pair.getValue()) + "\n");
            }
            for (Map.Entry<Integer, EpicTask> pair : getEpicTaskMap().entrySet()) {
                writer.write(toString(pair.getValue()) + "\n");
            }
            writer.write("\n");

            for (Task task : historyManager.getHistory()) {
                writer.write(task.getId() + ",");
            }


        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранит данные",e.getCause());
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
        StringBuilder stringBuilder = new StringBuilder();
        List<Task> tasks = manager.getHistory();
         for (Task task : tasks) {
             stringBuilder.append(task.getId());
             stringBuilder.append(",");
         }
         return stringBuilder.toString();
    }

    static List<Integer> historyFromString(String value){
        String[] line = value.split(",");
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i<line.length; i++) {
            list.add(Integer.parseInt(line[i])) ;
        }
        return list;
    }

    @Override
    public Collection<? extends Task> getTaskByType(TaskType taskType) {
        Collection<? extends Task> taskByType = super.getTaskByType(taskType);
        save();
        return taskByType;
    }

    @Override
    public Task getByIdAndTypeTask(int id, TaskType taskType) {
        return super.getByIdAndTypeTask(id, taskType);
    }

    @Override
    public boolean deleteTasksByType(TaskType taskType) {
        return super.deleteTasksByType(taskType);
    }

    @Override
    public boolean deleteByIdAndTypeTask(Integer id, TaskType taskType) {
        return super.deleteByIdAndTypeTask(id, taskType);
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
        return super.getHistoryManager();
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
