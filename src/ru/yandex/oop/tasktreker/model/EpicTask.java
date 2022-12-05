package ru.yandex.oop.tasktreker.model;

import java.util.List;

public class EpicTask extends Task {

    private List<Integer> subTasksID;

    public List<Integer> getSubTaskIds (){
        return subTasksID;
    }

    public void removeSubTaskId (Integer subTaskId) {
        subTasksID.remove(subTaskId);
    }

    public void setStatus(Status status){
        this.status= status;
    }

    public EpicTask (String name, String description, TaskType taskType){
        //чето надо написать
    }

    public void addSubTaskId( int subTaskId) {
        subTasksID.add(subTaskId);
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                ", name='" + name + '\'' +
                "id=" + id +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", SubTasks=" + subTasksID.toString()+
                '}';
    }

    /*
    Каждый эпик знает, какие подзадачи в него входят.
    Завершение всех подзадач эпика считается завершением эпика.
     */
}
