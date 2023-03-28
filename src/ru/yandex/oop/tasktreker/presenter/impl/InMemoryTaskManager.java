package ru.yandex.oop.tasktreker.presenter.impl;

import ru.yandex.oop.tasktreker.exception.TaskValidationException;
import ru.yandex.oop.tasktreker.model.EpicTask;
import ru.yandex.oop.tasktreker.model.SubTask;
import ru.yandex.oop.tasktreker.model.Task;

import ru.yandex.oop.tasktreker.model.enums.TaskStatus;
import ru.yandex.oop.tasktreker.model.enums.TaskType;
import ru.yandex.oop.tasktreker.presenter.HistoryManager;
import ru.yandex.oop.tasktreker.presenter.TaskManager;
import ru.yandex.oop.tasktreker.presenter.util.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> taskMap;
    private final Map<Integer, SubTask> subTaskMap;
    private final Map<Integer, EpicTask> epicTaskMap;
    private Set<Task> prioritizedTasks;
    private int nextId;

    private HistoryManager historyManager = Managers.getDefaultHistory();


    public InMemoryTaskManager() {
        prioritizedTasks = new TreeSet<>();
        this.taskMap = new HashMap<>();
        this.subTaskMap = new HashMap<>();
        this.epicTaskMap = new HashMap<>();
        nextId = 1;

    }

    @Override
    public List<Task> getPrioritizedTasks() {
        if (prioritizedTasks.isEmpty()) {
            return null;
        }
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void isTaskOverlap(Task task) {
        if (task.getStartTime() != null) {
            final LocalDateTime startTime;
            final LocalDateTime endTime;
            startTime = task.getStartTime();
            endTime = task.getEndTime();
            for (Task t : prioritizedTasks) {
                final LocalDateTime existStart = t.getStartTime();
                final LocalDateTime existEnd = t.getEndTime();
                if (existStart != null) {
                    if (endTime.isBefore(existStart)) {
                        continue;
                    }
                    if (existEnd.isBefore(startTime)) {
                        continue;
                    }
                    throw new TaskValidationException("Задача пересекается с id=" + t.getId() + " c " + existStart + " по " + existEnd);
                }
            }
        }
        prioritizedTasks.add(task);
    }

    public void removeFromPrioritizedTree(Task task) {
        ArrayList<Task> treeSet = new ArrayList<>(prioritizedTasks);
        treeSet.remove(task);
        prioritizedTasks = new TreeSet<>(treeSet);
    }

    @Override
    public Collection<? extends Task> getTaskByType(TaskType taskType) { //получить таску по типу (метод возвращает коллекцию, которая хранит в себе объекты которые являются наследником Task)

        Collection<? extends Task> tempListTasks;//
        switch (taskType) {
            case TASK: {
                if (!taskMap.isEmpty()) {  // если карта не пустая
                    tempListTasks = taskMap.values();  // то заполняем значениями из taskMap
                    return tempListTasks;  //возвращаем новую коллекцию
                } else {
                    return null;
                }
            }
            case SUBTASK: {
                if (!subTaskMap.isEmpty()) {
                    tempListTasks = subTaskMap.values();
                    return tempListTasks;
                } else {
                    return null;
                }
            }
            case EPICTASK: {
                if (!epicTaskMap.isEmpty()) {
                    tempListTasks = epicTaskMap.values();
                    return tempListTasks;
                } else {
                    return null;
                }
            }
        }
        return null;
    }


    @Override
    public Task getByIdAndTypeTask(int id, TaskType taskType) {// получить таску по id и по ее типу
        switch (taskType) {
            case TASK: {
                boolean containsId = taskMap.containsKey(id);// если taskMap содержит id то возвращает true
                if (containsId) {// если id содержится в taskMap
                    Task task = taskMap.get(id);// то тогда берем из taskMap таску и кладем ее в новую task
                    return task;// возвращаем новую task
                } else {
                    return null;
                }
            }
            case SUBTASK: {
                boolean containsId = subTaskMap.containsKey(id);
                if (containsId) {
                    Task task = subTaskMap.get(id);
                    return task;
                } else {
                    return null;
                }
            }
            case EPICTASK: {
                boolean containsId = epicTaskMap.containsKey(id);
                if (containsId) {
                    Task task = epicTaskMap.get(id);
                    return task;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public boolean deleteTasksByType(TaskType taskType) { // удаление всех тасок в Map по типу
        switch (taskType) {
            case TASK: {
                if (!taskMap.isEmpty()) { // если taskMap не пустая

                    for (Map.Entry<Integer, Task> pair : taskMap.entrySet()) {
                        historyManager.remove(pair.getKey());
                    }

                    taskMap.clear();// то удалить все значения из taskMap
                    return true;
                } else {
                    return false;// если пустая, то вернуть ошибку
                }
            }
            case SUBTASK: {
                if (!subTaskMap.isEmpty()) {

                    for (Map.Entry<Integer, SubTask> epicTask : subTaskMap.entrySet()) {
//                        epicTask.getValue().cLearAllSubTasksId();
                        updateTask(epicTask.getKey(), epicTask.getValue(), this);
                        historyManager.remove(epicTask.getKey());
                    }
                    subTaskMap.clear();
                    return true;
                } else {
                    return false;
                }
            }
            case EPICTASK: {
                if (!epicTaskMap.isEmpty()) {

                    for (Map.Entry<Integer, EpicTask> pair : epicTaskMap.entrySet()) {
                        historyManager.remove(pair.getKey());
                    }

                    epicTaskMap.clear();

                    for (Map.Entry<Integer, SubTask> pair : subTaskMap.entrySet()) {
                        historyManager.remove(pair.getKey());
                    }

                    subTaskMap.clear();
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public boolean deleteByIdAndTypeTask(Integer id, TaskType taskType) {
        switch (taskType) {
            case TASK: {
                boolean containsId = taskMap.containsKey(id);
                if (containsId) {
                    taskMap.remove(id);
                    return true;
                } else {
                    return false;
                }
            }
            case SUBTASK: {
                boolean containsId = subTaskMap.containsKey(id);
                if (containsId) {
                    int epicIdOfSubTask = subTaskMap.get(id).getEpicId();
                    subTaskMap.remove(id);
                    epicTaskMap.get(epicIdOfSubTask).removeSubTaskId(id); //  удаление Id SubTsk из EpicTask
                    changeStatus(epicIdOfSubTask);
                    return true;
                } else {
                    return false;
                }
            }
            case EPICTASK: {
                boolean containsId = epicTaskMap.containsKey(id);
                if (containsId) {
                    EpicTask deletedEpicTask = epicTaskMap.remove(id);
                    List<SubTask> subTaskIds = deletedEpicTask.getSubTasks();
                    for (SubTask subTaskIdFromEpic : subTaskIds) {
                        subTaskMap.remove(subTaskIdFromEpic);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public int createTaskAndReturnId(Task task) {
        int keyId = getNextId();
        task.setId(keyId);
        if (task instanceof SubTask) {
            this.subTaskMap.put(keyId, (SubTask) task);
            int epicIdOfSubtask = subTaskMap.get(keyId).getEpicId();
            epicTaskMap.get(epicIdOfSubtask).addSubTask((SubTask) task);
            changeStatus(epicTaskMap.get(epicIdOfSubtask).getId());
            prioritizedTasks.add(task);
        } else if (task instanceof EpicTask) {
            this.epicTaskMap.put(keyId, (EpicTask) task);
            isTaskOverlap(task);
        } else {
            this.taskMap.put(keyId, task);
            isTaskOverlap(task);
        }

        historyManager.add(task);
        return keyId;
    }

    @Override
    public List<SubTask> getListSubTask(int id) {
        boolean containsId = epicTaskMap.containsKey(id);
        List<SubTask> subTasks;
        if (containsId) {
            subTasks = epicTaskMap.get(id).getSubTasks();
        } else {
            return Collections.emptyList();
        }
        return subTasks;
    }

    @Override
    public void changeStatus(int epicId) {// обновление статуса
        EpicTask tempEpicTask = ((EpicTask) getByIdAndTypeTask(epicId, TaskType.EPICTASK));
        List<SubTask> subTasks = tempEpicTask.getSubTasks();
        if (subTasks.size() == 0) {
            epicTaskMap.get(epicId).setStatus(TaskStatus.NEW);
            return;
        }
        boolean isDone = false;
        boolean isInProgress = false;
        boolean isNew = false;
        for (SubTask subTask : subTasks) {
            switch (subTask.getStatus()) {
                case NEW:
                    isNew = true;
                    break;
                case DONE:
                    isDone = true;
                    break;
                case IN_PROGRESS:
                    isInProgress = true;
                    break;
            }
        }
        if (isNew && !isDone && !isInProgress) { // если новая, невыполненная, не в прогрессе
            epicTaskMap.get(epicId).setStatus(TaskStatus.NEW); // то статус новая
        } else if (!isNew && isDone && !isInProgress) { // если не новая, выполнена, не в прогрессе
            epicTaskMap.get(epicId).setStatus(TaskStatus.DONE);// то выполнена
        } else
            epicTaskMap.get(epicId).setStatus(TaskStatus.IN_PROGRESS); // иначе в прогрессе
    }

    public static void updateTask(int id, Task task, TaskManager manager) {  // обновление task
        task.setId(id);// обращаемся к task
        manager.getHistoryManager().add(task);// task записывается в историю

        if (task instanceof SubTask) { // если переданная task является SubTask
            manager.getSubTaskMap().put(id, (SubTask) task);//то тогда кладем ее SubTaskMap по ключу и значению
            int epicIdOfSubtask = manager.getSubTaskMap().get(id).getEpicId();
            manager.changeStatus(epicIdOfSubtask);
        }
        if (task instanceof EpicTask) {
            for (Map.Entry<Integer, SubTask> subTask : manager.getSubTaskMap().entrySet()) { // проходимся циклом по SubTaskMap (по его парам)
                int epicIdOfSubtask = subTask.getValue().getEpicId();
                if (epicIdOfSubtask == id) {
                    ((EpicTask) task).addSubTask(subTask.getValue());
                }
            }
            manager.getEpicTaskMap().put(id, (EpicTask) task);
            manager.changeStatus(id);
        } else {
            manager.getTaskMap().put(id, task);
        }
    }

    @Override
    public int getNextId() {
        return nextId++;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }



    @Override
    public Task getAnyTask(int id) {
        if (taskMap.containsKey(id)) {
            Task task = taskMap.get(id);
            historyManager.add(task);
            return task;
        }else if (subTaskMap.containsKey(id)) {
            SubTask task = subTaskMap.get(id);
            historyManager.add(task);
            return task;
        }else if (epicTaskMap.containsKey(id)) {
            EpicTask task = epicTaskMap.get(id);
            historyManager.add(task);
            return task;
        }
        return null;
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
    public List<SubTask> getEpicSubtasks(int epicId){
        if (!epicTaskMap.get(epicId).getSubTasks().isEmpty()) {
            return epicTaskMap.get(epicId).getSubTasks();
        }
        System.out.println("Список подзадач эпика №" + epicId + "пуст.");
        return null;
    }

    @Override
    public LocalDateTime getTaskEndTime(Task task) {
        return task.getStartTime().plusMinutes(task.getDuration().toMinutes());
    }

    @Override
    public Duration getEpicDuration(EpicTask epic) {
        Duration totalDurationOfSubtasks = Duration.ZERO;
        for (Map.Entry<Integer, SubTask> pair : getSubTaskMap().entrySet()) {
            if (pair.getValue().getEpicId() == epic.getId()) {
                totalDurationOfSubtasks.plus(pair.getValue().getDuration());
            }
        }
        return totalDurationOfSubtasks;
    }

    @Override
    public String deleteTaskByID(int id) {
        if (taskMap.get(id) != null){
            removeFromPrioritizedTree(taskMap.get(id));
            taskMap.remove(id);
            historyManager.remove(id);
            return "Задача №" + id + " удалена.";
        }
        return "Нельзя удалить задачу №" + id + ", так как ее нет.";
    }

    @Override
    public String deleteEpicTaskByID(int epicId) {
        if (epicTaskMap.get(epicId) != null) {
            for (int i = 0; i < epicTaskMap.get(epicId).getSubTasks().size(); i++) {
                subTaskMap.remove(epicTaskMap.get(epicId).getSubTasks().get(i).getId());
            }
            historyManager.remove(epicId);
            epicTaskMap.remove(epicId);
            return "Эпик №" + epicId + " удален.";

        }
        return "Нельзя удалить эпик №" + epicId + ", так как его нет.";
    }

    @Override
    public String deleteSubTaskByID(int id) {
        historyManager.remove(id);
        if (subTaskMap.containsKey(id)) {
            int epicID = subTaskMap.get(id).getEpicId();
            for (int i = 0; i < epicTaskMap.get(epicID).getSubTasks().size(); i++) {
                if (subTaskMap.get(id) == epicTaskMap.get(epicID).getSubTasks().get(i)) {
                    epicTaskMap.get(epicID).getSubTasks().remove(i);
                    epicTaskMap.get(epicID).getEpicStartTime();
                    break;
                }
            }
            removeFromPrioritizedTree(subTaskMap.get(id));
            subTaskMap.remove(id);
            return "Подзадача №" + id + " удалена.";
        }
        return "Нельзя удалить подзадачу №" + id + ", так как ее нет.";
    }



    public Map<Integer, Task> getTaskMap() {
        return taskMap;
    }

    public Map<Integer, SubTask> getSubTaskMap() {
        return subTaskMap;
    }

    public Map<Integer, EpicTask> getEpicTaskMap() {
        return epicTaskMap;
    }

    public void setNextId(int nextId) {
        this.nextId = nextId;
    }


}