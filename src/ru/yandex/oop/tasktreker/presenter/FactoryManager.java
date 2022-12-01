package ru.yandex.oop.tasktreker.presenter;

public class FactoryManager {

    public Manager getManager (TaskType taskType){
        switch (taskType){
            case TASK: return new TaskManager();
            case SUBTASK: return new SubTaskManager();
            case EPICTASK: return new EpicManager();
            default: return null;  }// какую ошибку выбрать?????

    }
    }

