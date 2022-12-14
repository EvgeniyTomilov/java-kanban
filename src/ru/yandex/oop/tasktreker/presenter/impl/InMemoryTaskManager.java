package ru.yandex.oop.tasktreker.presenter.impl;

import ru.yandex.oop.tasktreker.model.EpicTask;
import ru.yandex.oop.tasktreker.model.SubTask;
import ru.yandex.oop.tasktreker.model.Task;
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


    private HistoryManager historyManager = Managers.getDefaultHistory();


    public InMemoryTaskManager() {
        this.taskMap = new HashMap<>();
        this.subTaskMap = new HashMap<>();
        this.epicTaskMap = new HashMap<>();
        nextId = 0;

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
                    return tempListTasks;
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
    public Task getByIdAndTypeTask(int id, TaskType taskType) {
        switch (taskType) {
            case TASK: {
                boolean containsId = taskMap.containsKey(id);
                if (containsId) {
                    Task task = taskMap.get(id);
                    return task;
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
                        updateTask(epicTask.getKey(), epicTask.getValue(), this);
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

    public static void updateTask(int id, Task task, TaskManager manager) {  // этот метод умеет работать со всеми тасками, его надо заменить на утильный
        task.setId(id);
        manager.getHistoryManager().add(task);

        if (task instanceof SubTask) {
            manager.getSubTaskMap().put(id, (SubTask) task);
            int epicIdOfSubtask = manager.getSubTaskMap().get(id).getEpicId();
            manager.changeStatus(epicIdOfSubtask);
        }
        if (task instanceof EpicTask) {
            for (Map.Entry<Integer, SubTask> subTask : manager.getSubTaskMap().entrySet()) {
                int epicIdOfSubtask = subTask.getValue().getEpicId();
                if (epicIdOfSubtask == id) {
                    ((EpicTask) task).addSubTaskId(epicIdOfSubtask);
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
    public Task getTask(int id) {
        Task task = taskMap.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public SubTask getSubtask(int id) {
        SubTask task = subTaskMap.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public EpicTask getEpic(int id) {
        EpicTask task = epicTaskMap.get(id);
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