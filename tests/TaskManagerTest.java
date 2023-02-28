import org.junit.jupiter.api.Test;
import ru.yandex.oop.tasktreker.model.EpicTask;
import ru.yandex.oop.tasktreker.model.SubTask;
import ru.yandex.oop.tasktreker.model.Task;
import ru.yandex.oop.tasktreker.model.enums.TaskStatus;
import ru.yandex.oop.tasktreker.model.enums.TaskType;
import ru.yandex.oop.tasktreker.presenter.TaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.oop.tasktreker.model.enums.TaskType.*;

abstract class TaskManagerTest<T extends TaskManager> {
    T manager;

    @Test
    public void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        final int taskId = manager.createTaskAndReturnId(task);
        final Task savedTask = manager.getAnyTask(taskId);

        assertNotNull(savedTask,"Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    public void addNewEpicTask() {
        EpicTask epic = new EpicTask("Test addNewEpicTask", "Test addNewEpicTask description");
        final int epicId = manager.createTaskAndReturnId(epic);
        final Task savedEpic = manager.getAnyTask(epicId);
        SubTask subTask = new SubTask("newSubtask", "description newSubtask", epic.getId());
        subTask.setStatus(TaskStatus.NEW);
        manager.createTaskAndReturnId(subTask);

        assertNotNull(savedEpic,"Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
        assertEquals(1, manager.getEpicTaskMap().size(), "Неверное количество эпиков");
        assertEquals(TaskStatus.NEW, savedEpic.getStatus(), "Статусы не совпадают");

    }

    @Test
    public void addNewSubtask() {
        EpicTask epic = new EpicTask("Test addNewTask", "Test addNewTask description");
        final int epicId = manager.createTaskAndReturnId(epic);
        SubTask subTask = new SubTask("newSubtask", "description newSubtask", epic.getId());
        subTask.setStatus(TaskStatus.NEW);
        int subtaskId = manager.createTaskAndReturnId(subTask);

        assertEquals(1, manager.getSubTaskMap().size(), "Неверное количество подзадач");
        assertEquals(subTask, manager.getSubTaskMap().get(subtaskId), "Подзадачи не совпадают.");
        assertNotNull(manager.getEpicTaskMap().get(epicId));
        assertFalse(manager.getSubTaskMap().isEmpty());
        assertTrue(manager.getSubTaskMap().containsKey(subtaskId));
    }

    @Test
    public void getTaskByType() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        final int taskId = manager.createTaskAndReturnId(task);
        EpicTask epic = new EpicTask("Test addNewEpicTask", "Test addNewEpicTask description");
        final int epicId = manager.createTaskAndReturnId(epic);
        SubTask subTask = new SubTask("newSubtask", "description newSubtask", epic.getId());
        int subtaskId = manager.createTaskAndReturnId(subTask);

     //   assertEquals(task, manager.getTaskByType());


    }

    @Test
    public void getByIdAndTypeTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        final int taskId = manager.createTaskAndReturnId(task);
        EpicTask epic = new EpicTask("Test addNewEpicTask", "Test addNewEpicTask description");
        final int epicId = manager.createTaskAndReturnId(epic);
        SubTask subTask = new SubTask("newSubtask", "description newSubtask", epic.getId());
        int subtaskId = manager.createTaskAndReturnId(subTask);

        assertEquals(task, manager.getByIdAndTypeTask(manager.getTaskMap().get(taskId).getId(), TASK ));
        assertEquals(epic, manager.getByIdAndTypeTask(manager.getEpicTaskMap().get(epicId).getId(),EPICTASK));
        assertEquals(subTask, manager.getByIdAndTypeTask(manager.getSubTaskMap().get(subtaskId).getId(), SUBTASK));
    }

    @Test
    public  void deleteTasksByType() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        final int taskId = manager.createTaskAndReturnId(task);
        EpicTask epic = new EpicTask("Test addNewEpicTask", "Test addNewEpicTask description");
        final int epicId = manager.createTaskAndReturnId(epic);
        SubTask subTask = new SubTask("newSubtask", "description newSubtask", epic.getId());
        int subtaskId = manager.createTaskAndReturnId(subTask);
        manager.deleteTasksByType(TASK);
        manager.deleteTasksByType(EPICTASK);
        manager.deleteTasksByType(SUBTASK);

//        assertTrue(manager.getTaskMap().isEmpty());
//        assertEquals(0, manager.getTaskMap());

    }

    @Test
    public void deleteByIdAndTypeTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        final int taskId = manager.createTaskAndReturnId(task);
        EpicTask epic = new EpicTask("Test addNewEpicTask", "Test addNewEpicTask description");
        final int epicId = manager.createTaskAndReturnId(epic);
        SubTask subTask = new SubTask("newSubtask", "description newSubtask", epic.getId());
        int subtaskId = manager.createTaskAndReturnId(subTask);
        manager.deleteByIdAndTypeTask(taskId, TASK);
        manager.deleteByIdAndTypeTask(epicId, EPICTASK);
        manager.deleteByIdAndTypeTask(subtaskId, SUBTASK);


//        assertTrue(manager.getTaskMap().isEmpty());
//        assertEquals(0, manager.getTaskMap());
    }


}