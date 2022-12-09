package ru.yandex.oop.tasktreker.presenter;

import ru.yandex.oop.tasktreker.model.*;

import java.util.*;

public class Manager {

    private final Map<Integer, Task> taskMap;
    private final Map<Integer, SubTask> subTaskMap;
    private final Map<Integer, EpicTask> epicTaskMap;
    private int nextId;


    public Manager() {
        this.taskMap = new HashMap<>();
        this.subTaskMap = new HashMap<>();
        this.epicTaskMap = new HashMap<>();
        nextId = 0;
    }

    /**
     * методы бизнес-логики
     */
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


    public Task getByIdAndTypeTask(int id, TaskType taskType) {
        switch (taskType) {
            case TASK: {
                boolean containsId = taskMap.containsKey(id);
                if (containsId) {
                    return taskMap.get(id);
                } else {
                    return null;
                }
            }
            case SUBTASK: {
                boolean containsId = subTaskMap.containsKey(id);
                if (containsId) {
                    return subTaskMap.get(id);
                } else {
                    return null;
                }
            }
            case EPICTASK: {
                boolean containsId = epicTaskMap.containsKey(id);
                if (containsId) {
                    return epicTaskMap.get(id);
                } else {
                    return null;
                }
            }
        }
        return null;
    }


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
                        updateTask(epicTask.getKey(), epicTask.getValue());
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


    public void updateTask(int id, Task task) {  // этот метод умеет работать со всеми тасками, его надо заменить на утильный
        task.setId(id);
        if (task instanceof SubTask) {
            this.subTaskMap.put(id, (SubTask) task);
            int epicIdOfSubtask = subTaskMap.get(id).getEpicId();
            changeStatus(epicIdOfSubtask);
        }
        if (task instanceof EpicTask) {
            for (Map.Entry<Integer, SubTask> subTask : subTaskMap.entrySet()) {
                int epicIdOfSubtask = subTask.getValue().getEpicId();
                if (epicIdOfSubtask == id) {
                    ((EpicTask) task).addSubTaskId(epicIdOfSubtask);
                }
            }
            this.epicTaskMap.put(id, (EpicTask) task);
            changeStatus(id);
        } else {
            this.taskMap.put(id, task);
        }
    }


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


    public List<SubTask> getListSubtask(int id) {
        boolean containsId = epicTaskMap.containsKey(id);
        List<Integer> subTasksId;
        if (containsId) {
            subTasksId = epicTaskMap.get(id).getSubTaskIds();
        } else {
            return Collections.emptyList();
        }

        List<SubTask> subTaskList = new ArrayList<>();
        for (Integer Id : subTasksId) {
            subTaskList.add(subTaskMap.get(Id));
        }
        return subTaskList;
    }


    public void changeStatus(int epicId) {
        List<SubTask> subTaskList = new ArrayList<>();
        EpicTask tempEpicTask = ((EpicTask) getByIdAndTypeTask(epicId, TaskType.EPICTASK));
        List<Integer> subTaskIds = tempEpicTask.getSubTaskIds();
        for (Integer uid : subTaskIds) {
            subTaskList.add(subTaskMap.get(uid));
        }
        if (subTaskList.size() == 0) {
            epicTaskMap.get(epicId).setStatus(Status.NEW);
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
            epicTaskMap.get(epicId).setStatus(Status.NEW);
        } else if (!isNew && isDone && !isInProgress) {
            epicTaskMap.get(epicId).setStatus(Status.DONE);
        } else
            epicTaskMap.get(epicId).setStatus(Status.IN_PROGRESS);
    }


    private int getNextId() {
        return nextId++;
    } // что-то здесь не так

}