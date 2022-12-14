package ru.yandex.oop.tasktreker.presenter.impl;

import ru.yandex.oop.tasktreker.model.*;
import ru.yandex.oop.tasktreker.model.enums.TaskStatus;
import ru.yandex.oop.tasktreker.model.enums.TaskType;
import ru.yandex.oop.tasktreker.presenter.HistoryManager;
import ru.yandex.oop.tasktreker.presenter.TaskManager;
import ru.yandex.oop.tasktreker.presenter.util.Managers;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> taskMap;
    private final Map<Integer, SubTask> subTaskMap;
    private final Map<Integer, EpicTask> epicTaskMap;
    private int nextId;

//    private List<Task> historyTaskList;

    private HistoryManager historyManager = Managers.getDefaultHistory();


    public InMemoryTaskManager() {
        this.taskMap = new HashMap<>();
        this.subTaskMap = new HashMap<>();
        this.epicTaskMap = new HashMap<>();
        nextId = 0;
//        this.historyTaskList = new ArrayList<>();
    }

    /**
     * методы бизнес-логики
     */
    @Override
    public Collection<? extends Task> getTaskByType(TaskType taskType) {

        Collection<? extends Task> tempListTasks;
        switch (taskType) {
            case TASK: {
                if (!taskMap.isEmpty()) {
                    tempListTasks = taskMap.values();

                    // добавляем таски в историю
                    for (Map.Entry<Integer, Task> pair : taskMap.entrySet()) {
//                        addToHistoryList(pair.getValue());
                        historyManager.add(pair.getValue());
                    }

                    return tempListTasks;
                } else {
                    return null;
                }
            }
            case SUBTASK: {
                if (!subTaskMap.isEmpty()) {
                    tempListTasks = subTaskMap.values();

                    // добавляем таски в историю
                    for (Map.Entry<Integer, SubTask> pair : subTaskMap.entrySet()) {
//                        addToHistoryList(pair.getValue());
                        historyManager.add(pair.getValue());
                    }

                    return tempListTasks;
                } else {
                    return null;
                }
            }
            case EPICTASK: {
                if (!epicTaskMap.isEmpty()) {
                    tempListTasks = epicTaskMap.values();

                    // добавляем таски в историю
                    for (Map.Entry<Integer, EpicTask> pair : epicTaskMap.entrySet()) {
//                        addToHistoryList(pair.getValue());
                        historyManager.add(pair.getValue());
                    }

                    return tempListTasks;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public Task getByIdAndTypeTask(int id, TaskType taskType) {
        switch (taskType) {
            case TASK: {
                boolean containsId = taskMap.containsKey(id);
                if (containsId) {
                    Task task = taskMap.get(id);
//                    addToHistoryList(task);
                    historyManager.add(task); // добавляем таску в лист истории

                    return task;
                } else {
                    return null;
                }
            }
            case SUBTASK: {
                boolean containsId = subTaskMap.containsKey(id);
                if (containsId) {
                    Task task = subTaskMap.get(id);
//                    addToHistoryList(task);
                    historyManager.add(task); // добавляем таску в лист истории
                    return task;
                } else {
                    return null;
                }
            }
            case EPICTASK: {
                boolean containsId = epicTaskMap.containsKey(id);
                if (containsId) {
                    Task task = epicTaskMap.get(id);
//                    addToHistoryList(task);
                    historyManager.add(task); // добавляем таску в лист истории
                    return task;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public boolean deleteTasksByType(TaskType taskType) {
        switch (taskType) {
            case TASK: {
                if (!taskMap.isEmpty()) {
                    taskMap.clear();
                    return true;
                } else {
                    return false;
                }
            }
            case SUBTASK: {
                if (!subTaskMap.isEmpty()) {

                    subTaskMap.clear();
                    for (Map.Entry<Integer, EpicTask> epicTask : epicTaskMap.entrySet()) {
                        epicTask.getValue().cLearAllSubTasksId();
                        Managers.updateTask(epicTask.getKey(), epicTask.getValue(), this);
                    }
                    return true;
                } else {
                    return false;
                }
            }
            case EPICTASK: {
                if (!epicTaskMap.isEmpty()) {
                    epicTaskMap.clear();
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
                    List<Integer> subTaskIds = deletedEpicTask.getSubTaskIds();
                    for (Integer subTaskIdFromEpic : subTaskIds) {
                        if (subTaskMap.containsKey(subTaskIdFromEpic)) {
                            subTaskMap.remove(subTaskIdFromEpic);
                        }
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

        historyManager.add(task); // добавляем таску в лист истории

        if (task instanceof SubTask) {
            this.subTaskMap.put(keyId, (SubTask) task);
            int epicIdOfSubtask = subTaskMap.get(keyId).getEpicId();
            EpicTask epicTask = epicTaskMap.get(epicIdOfSubtask);
            epicTask.addSubTaskId(keyId);
            changeStatus(epicTask.getId());
        } else if (task instanceof EpicTask) {
            this.epicTaskMap.put(keyId, (EpicTask) task);
        } else {
            this.taskMap.put(keyId, task);
        }
        return keyId;
    }

    @Override
    public List<SubTask> getListSubtask(int id) {
        boolean containsId = epicTaskMap.containsKey(id);
        List<Integer> subTasksId;
        if (containsId) {
            subTasksId = epicTaskMap.get(id).getSubTaskIds();
        } else {
            return Collections.emptyList();
        }

        List<SubTask> subTaskList = new ArrayList<>();
        SubTask task = null;
        for (Integer Id : subTasksId) {
            task = subTaskMap.get(Id);
            subTaskList.add(task);

//            addToHistoryList(task);
            historyManager.add(task); // добавляем таску в лист истории
        }
        return subTaskList;
    }

    @Override
    public void changeStatus(int epicId) {
        List<SubTask> subTaskList = new ArrayList<>();
        EpicTask tempEpicTask = ((EpicTask) getByIdAndTypeTask(epicId, TaskType.EPICTASK));
        List<Integer> subTaskIds = tempEpicTask.getSubTaskIds();
        SubTask task = null;
        for (Integer uid : subTaskIds) {
            task = subTaskMap.get(uid);
            subTaskList.add(task);
//            addToHistoryList(task);
            historyManager.add(task); // добавляем таску в лист истории
        }
        if (subTaskList.size() == 0) {
            epicTaskMap.get(epicId).setStatus(TaskStatus.NEW);
            return;
        }
        boolean isDone = false;
        boolean isInProgress = false;
        boolean isNew = false;
        for (SubTask subTask : subTaskList) {
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
        if (isNew && !isDone && !isInProgress) {
            epicTaskMap.get(epicId).setStatus(TaskStatus.NEW);
        } else if (!isNew && isDone && !isInProgress) {
            epicTaskMap.get(epicId).setStatus(TaskStatus.DONE);
        } else
            epicTaskMap.get(epicId).setStatus(TaskStatus.IN_PROGRESS);
    }

    @Override
    public int getNextId() {
        return nextId++;
    } // что-то здесь не так


    //    @Override
//    public void updateTask(int id, Task task) {  // этот метод умеет работать со всеми тасками, его надо заменить на утильный
//        task.setId(id);
//        if (task instanceof SubTask) {
//            this.subTaskMap.put(id, (SubTask) task);
//            int epicIdOfSubtask = subTaskMap.get(id).getEpicId();
//            changeStatus(epicIdOfSubtask);
//        }
//        if (task instanceof EpicTask) {
//            for (Map.Entry<Integer, SubTask> subTask : subTaskMap.entrySet()) {
//                int epicIdOfSubtask = subTask.getValue().getEpicId();
//                if (epicIdOfSubtask == id) {
//                    ((EpicTask) task).addSubTaskId(epicIdOfSubtask);
//                }
//            }
//            this.epicTaskMap.put(id, (EpicTask) task);
//            changeStatus(id);
//        } else {
//            this.taskMap.put(id, task);
//        }
//    }

//    @Override
//    public List<Task> getHistory() {
//        return historyTaskList;
//    }

//    @Override
//    public void addToHistoryList(Task task) {
//        if (historyTaskList.size() < 10) {
//            historyTaskList.add(task);
//        } else {
//            historyTaskList.remove(0);
//            historyTaskList.add(task);
//        }
//    }


    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public Task getTask(int id) {
        Task task = taskMap.get(id);
//        addToHistoryList(task);
        historyManager.add(task);
        return task;
    }

    @Override
    public SubTask getSubtask(int id) {
        SubTask task = subTaskMap.get(id);
//        addToHistoryList(task);
        historyManager.add(task);
        return task;
    }

    @Override
    public EpicTask getEpic(int id) {
        EpicTask task = epicTaskMap.get(id);
//        addToHistoryList(task);
        historyManager.add(task);
        return task;
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
}