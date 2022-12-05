package ru.yandex.oop.tasktreker.presenter;

import ru.yandex.oop.tasktreker.model.EpicTask;
import ru.yandex.oop.tasktreker.model.SubTask;
import ru.yandex.oop.tasktreker.model.Task;
import ru.yandex.oop.tasktreker.model.TaskType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Manager {

    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, SubTask> subTasks;
    private final HashMap<Integer, EpicTask> epicTasks;
    int id;


    public OldManager {
        this.tasks = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.epicTasks = new HashMap<>();
        id = 0;
    }

    /**
     * Основные методы бизнес-логики
     */
    public Collection<? extends Task> getTaskByType(TaskType taskType) {

        EpicTask epicTask = new EpicTask();
        epicTask.addSubTaskId(1);

        Collection<? extends Task> tempListTasks;
        switch (taskType) {
            case TASK: {
                if (!tasks.isEmpty()) {
                    tempListTasks = tasks.values();
                    return tempListTasks;
                } else return null;
            }
            case SUBTASK: {
                if (!subTasks.isEmpty()) {
                    tempListTasks = subTasks.values();
                    return tempListTasks;
                } else return null;
            }
            case EPICTASK: {
                if (!epicTasks.isEmpty()) {
                    tempListTasks = epicTasks.values();
                    return tempListTasks;
                } else return null;
            }
        }
        return null;
    }



    public Task getByIdAndTypeTask(int id, TaskType taskType) {
        switch (taskType) {
            case TASK: {
                boolean containsId = Tasks.containsKey(id);
                if (containsId) {
                    return Tasks.get(id);
                } else return null;
            }
            case SUBTASK: {
                boolean containsId = subTasks.containsKey(id);
                if (containsId) {
                    return subTasks.get(id);
                } else return null;
            }
            case EPICTASK: {
                boolean containsId = epicTasks.containsKey(id);
                if (containsId) {
                    return epicTasks.get(id);
                } else return null;
            }
        }
        return null;
    }



    public boolean deleteTasksByType(TaskType taskType) {
        switch (taskType) {
            case TASK: {
                if (!tasks.isEmpty()) {
                    tasks.clear();
                    return true;
                } else return false;
            }
            case SUBTASK: {
                if (!subTasks.isEmpty()) {

                    subTasks.clear();
                    for (Map.Entry<Integer, EpicTask> epicTask : epicTasks.entrySet()) {
                        epicTask.getValue().cLearAllSubTasksId();
                        updateTask(epicTask.getKey(), epicTask.getValue());
                    }
                    return true;
                } else return false;
            }
            case EPICTASK: {
                if (!epicTasks.isEmpty()) {
                    epicTasks.clear();
                    subTasks.clear();
                    return true;
                } else return false;
            }
        }
        return false;
    }



    public int createTaskAndReturnId(Task task){
        int keyId = getNextId ();
        task.setId(keyId);
        if (task instanceof SubTask) {
            this.subTasks.put(keyId, (SubTask) task);
            int epicIdOfSubtask = subTasks.get(keyId).getEpicId();
            epicTasks.get(epicIdOfSubtask).addSubTaskId(keyId);
            changeStatus(keyId);
        } else if (task instanceof EpicTask) {
            this.epicTasks.put(keyId, (EpicTask) task);
            changeStatus(keyId);
        }else
            this.tasks.put(keyId, task);
        return keyId;
    }




    public SubTask createSubtask (SubTask subTask) {
        subTask.setId(getNextId());
        subTask.put(subTask.getId(), subTask);

        int epicId = SubTask.getEpicId();
        EpicTask epicTask= epicTasks.get(epicId);

        ArrayList<Integer> subTasksId = epicTask.getSubTaskIds();
        subTasksId.add(subTask.getId());

        return subTask;
    }


    public void updateTask (int id,Task task) {
        task.setId(id);
        if (task instanceof SubTask) {
            this.subTasks.put(id, (SubTask) task);
            int epicIdOfSubtask = subTasks.get(id).getEpicId();
            changeStatus(epicIdOfSubtask);
        }
        if (task instanceof EpicTask) {
            for (Map.Entry<Integer, SubTask> subTask : subTasks.entrySet()) {
                int epicIdOfSubtask = subTask.getValue().getEpicId();
                if (epicIdOfSubtask == id) {
                    ((EpicTask) task).addSubTaskId(epicIdOfSubtask);
                }
            }
            this.epicTasks.put(id, (EpicTask) task);
            changeStatus(id);
        }else
            this.tasks.put(id,task);
    }


    public boolean deleteByIdAndTypeTask (Integer id, TaskType taskType) {
        switch (taskType){
            case TASK: {
               boolean containsId = tasks.containsKey(id);
               if (containsId){
                   tasks.remove(id);
                   return true;
               }else return false;
            }
            case SUBTASK:{
                boolean containsId = subTasks.containsKey(id);
                if (containsId){
                    int epicIdOfSubTask= subTasks.get(id).getEpicId();
                    subTasks.remove(id);
                    epicTasks.get(epicIdOfSubTask).removeSubTaskId(id); //  удаление Id SubTsk из EpicTask
                    changeStatus(epicIdOfSubTask);
                     return true;
                }else return false;
            }
            case EPICTASK: {
                boolean containsId = epicTasks.containsKey(id);
                if (containsId){
                    EpicTask deletedEpicTask= epicTasks.remove(id);
                    недосписал (((...

                }
            }
    }


























    }