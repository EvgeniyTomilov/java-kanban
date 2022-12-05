package ru.yandex.oop.tasktreker.model;



public abstract class Task {
    protected String name;
    protected int id;
    protected String description;
    protected Status status;
    protected TaskType taskType;

    public Task (){
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    public Task(String name, String description, Status status, TaskType taskType) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.taskType = taskType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    /*
    На наш взгляд, самым безопасным способом решения этой задачи будет создание публичного не абстрактного класса Task.
     Он представляет отдельно стоящую задачу. Далее от него создать два подкласса: Subtask и Epic. Такая структура
     с одной стороны позволит менять свойства сразу всех видов задач, а с другой — оставит пространство для манёвров,
     если потребуется изменить только одну из них.

     У каждого типа задач есть идентификатор. Это целое число, уникальное для всех типов задач. По нему мы находим,
     обновляем, удаляем задачи. При создании задачи менеджер присваивает ей новый идентификатор.
     ля генерации идентификаторов можно использовать числовое поле класса менеджер, увеличивая его на 1, когда нужно
      получить новое значение.
     */
}
