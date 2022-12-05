package ru.yandex.oop.tasktreker.util;

import ru.yandex.oop.tasktreker.model.EpicTask;
import ru.yandex.oop.tasktreker.model.Status;
import ru.yandex.oop.tasktreker.model.SubTask;

import java.util.List;

public final class StatusUtil {

    private StatusUtil(){} // закрываем доступ к конструктору

    public static Status calculateNewStatusForEpic (EpicTask epicTask, List<SubTask> subTasks){
        boolean allTasksNew= true;
        for (Integer subTaskId : epicTask.getSubTaskIds()){
            SubTask subTask= subTasks.get(subTaskId);
            if(subTask.getStatus() == Status.IN_PROGRESS){
                return Status.IN_PROGRESS;
            }
            if (subTask.getStatus()!= Status.NEW){
                allTasksNew=false;
            }
        }
        if (allTasksNew){
            return Status.NEW;
        }else{
            return Status.DONE;
        }
    }
}
