import org.junit.jupiter.api.Test;
import ru.yandex.oop.tasktreker.exception.TaskValidationException;
import ru.yandex.oop.tasktreker.model.EpicTask;
import ru.yandex.oop.tasktreker.model.SubTask;
import ru.yandex.oop.tasktreker.model.Task;
import ru.yandex.oop.tasktreker.model.enums.TaskStatus;
import ru.yandex.oop.tasktreker.model.enums.TaskType;
import ru.yandex.oop.tasktreker.presenter.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.oop.tasktreker.model.enums.TaskType.*;

abstract class TaskManagerTest<T extends TaskManager> {
    T manager;

    public Task create(TaskType taskType) {
        if (taskType.equals(TaskType.TASK)) {
            return new Task("01t", "01t", Duration.ofMinutes(10), LocalDateTime.parse("2007-12-03T10:15:30"));
        } else if (taskType.equals(TaskType.EPICTASK)) {
            return new EpicTask("01e", "01e");
        }
        return new SubTask("newSubtask", "description newSubtask", 0, Duration.ofMinutes(15), "2008-01-03T10:15:30");
    }


    @Test
    public void addAnyTaskWithIntersectedTime() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Duration.ofMinutes(10), LocalDateTime.parse("2007-12-03T10:15:30"));
        manager.createTaskAndReturnId(task);
        Task task2 = new Task("Test addNewTask2", "Test addNewTask2 description", Duration.ofMinutes(10), LocalDateTime.parse("2007-12-03T10:40:30"));
        manager.createTaskAndReturnId(task2);
        Task task3 = new Task("Test addNewTask3", "Test addNewTask3 description", Duration.ofMinutes(10), LocalDateTime.parse("2007-12-03T10:45:30"));

        assertThrows(TaskValidationException.class, () -> manager.createTaskAndReturnId(task3));
//        assertEquals(0, manager.createTaskAndReturnId(task3));
    }

    @Test
    public void getPrioritizedTasks(){
        Task task = new Task("Test addNewTask", "Test addNewTask description", Duration.ofMinutes(10), LocalDateTime.parse("2007-12-03T10:40:30"));
        manager.createTaskAndReturnId(task);
        Task task2 = new Task("Test addNewTask2", "Test addNewTask2 description", Duration.ofMinutes(10), LocalDateTime.parse("2007-12-03T10:15:30"));
        manager.createTaskAndReturnId(task2);
        Task task3 = new Task("Test addNewTask3", "Test addNewTask3 description", Duration.ofMinutes(10), null);
        manager.createTaskAndReturnId(task3);
//        Task[] prioritizedTasks = new Task[3];
//        prioritizedTasks[0] = task2;
//        prioritizedTasks[1] = task;
//        prioritizedTasks[2] = task3;

        assertEquals(task2,manager.getPrioritizedTasks().get(0));

    }



    @Test
    public void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Duration.ofMinutes(10), LocalDateTime.parse("2007-12-03T10:15:30"));
        final int taskId = manager.createTaskAndReturnId(task);
        final Task savedTask = manager.getAnyTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    public void addNewEpicTask() {
        EpicTask epic = new EpicTask("Test addNewEpicTask", "Test addNewEpicTask description");
        final int epicId = manager.createTaskAndReturnId(epic);
        final Task savedEpic = manager.getAnyTask(epicId);
        SubTask subTask = new SubTask("newSubtask", "description newSubtask", epic.getId(), Duration.ofMinutes(15), "2008-12-03T10:15:30");
        subTask.setStatus(TaskStatus.NEW);
        manager.createTaskAndReturnId(subTask);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
        assertEquals(1, manager.getEpicTaskMap().size(), "Неверное количество эпиков");
        assertEquals(TaskStatus.NEW, savedEpic.getStatus(), "Статусы не совпадают");

    }

    @Test
    public void addNewSubtask() {
        EpicTask epic = new EpicTask("Test addNewTask", "Test addNewTask description");
        final int epicId = manager.createTaskAndReturnId(epic);
        SubTask subTask = new SubTask("newSubtask", "description newSubtask", epic.getId(), Duration.ofMinutes(15), "2008-12-03T10:15:30");
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
        Task task = new Task("Test addNewTask", "Test addNewTask description", Duration.ofMinutes(10), LocalDateTime.parse("2007-12-03T10:15:30"));
        manager.createTaskAndReturnId(task);
        EpicTask epic = new EpicTask("Test addNewEpicTask", "Test addNewEpicTask description");
        manager.createTaskAndReturnId(epic);
        SubTask subTask = new SubTask("newSubtask", "description newSubtask", epic.getId(), Duration.ofMinutes(15), "2008-12-03T10:15:30");
        manager.createTaskAndReturnId(subTask);

        Collection<? extends Task> taskByTypeTask = manager.getTaskByType(TASK);
        //     assertIterableEquals(List.of(task), taskByType);// сравнение коллекций одинаковые ли  элементы и по порядку ли они
        assertArrayEquals(List.of(task).toArray(), taskByTypeTask.toArray());
        Collection<? extends Task> taskByTypeEpic = manager.getTaskByType(EPICTASK);
        assertArrayEquals(List.of(epic).toArray(), taskByTypeEpic.toArray());
        Collection<? extends Task> taskByTypeSubTask = manager.getTaskByType(SUBTASK);
        assertArrayEquals(List.of(subTask).toArray(), taskByTypeSubTask.toArray());
    }

    @Test
    public void getByIdAndTypeTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Duration.ofMinutes(10), LocalDateTime.parse("2007-12-03T10:15:30"));
        final int taskId = manager.createTaskAndReturnId(task);
        EpicTask epic = new EpicTask("Test addNewEpicTask", "Test addNewEpicTask description");
        final int epicId = manager.createTaskAndReturnId(epic);
        SubTask subTask = new SubTask("newSubtask", "description newSubtask", epic.getId(), Duration.ofMinutes(15), "2008-12-03T10:15:30");
        int subtaskId = manager.createTaskAndReturnId(subTask);

        assertEquals(task, manager.getByIdAndTypeTask(manager.getTaskMap().get(taskId).getId(), TASK));
        assertEquals(epic, manager.getByIdAndTypeTask(manager.getEpicTaskMap().get(epicId).getId(), EPICTASK));
        assertEquals(subTask, manager.getByIdAndTypeTask(manager.getSubTaskMap().get(subtaskId).getId(), SUBTASK));
    }

    @Test
    public void deleteTasksByType() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Duration.ofMinutes(10), LocalDateTime.parse("2007-12-03T10:15:30"));
        manager.createTaskAndReturnId(task);
        EpicTask epic = new EpicTask("Test addNewEpicTask", "Test addNewEpicTask description");
        manager.createTaskAndReturnId(epic);
        SubTask subTask = new SubTask("newSubtask", "description newSubtask", epic.getId(), Duration.ofMinutes(15), "2008-12-03T10:15:30");
        manager.createTaskAndReturnId(subTask);
        manager.deleteTasksByType(TASK);
        manager.deleteTasksByType(EPICTASK);
        manager.deleteTasksByType(SUBTASK);

        assertEquals(new HashMap<Integer, Task>(), manager.getTaskMap());
        assertEquals(new HashMap<Integer, Task>(), manager.getEpicTaskMap());
        assertEquals(new HashMap<Integer, Task>(), manager.getSubTaskMap());
    }

    @Test
    public void deleteByIdAndTypeTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Duration.ofMinutes(10), LocalDateTime.parse("2007-12-03T10:15:30"));
        final int taskId = manager.createTaskAndReturnId(task);
        EpicTask epic = new EpicTask("Test addNewEpicTask", "Test addNewEpicTask description");
        final int epicId = manager.createTaskAndReturnId(epic);
        SubTask subTask = new SubTask("newSubtask", "description newSubtask", epic.getId(), Duration.ofMinutes(15), "2008-12-03T10:15:30");
        final int subtaskId = manager.createTaskAndReturnId(subTask);
        manager.deleteByIdAndTypeTask(taskId, TASK);
        manager.deleteByIdAndTypeTask(subtaskId, SUBTASK);
        manager.deleteByIdAndTypeTask(epicId, EPICTASK);

        assertTrue(manager.getTaskMap().isEmpty());
        assertTrue(manager.getEpicTaskMap().isEmpty());
        assertTrue(manager.getSubTaskMap().isEmpty());
    }

    @Test
    public void createTaskAndReturnId() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Duration.ofMinutes(10), LocalDateTime.parse("2007-12-03T10:15:30"));
        final int taskId = manager.createTaskAndReturnId(task);
        EpicTask epic = new EpicTask("Test addNewEpicTask", "Test addNewEpicTask description");
        final int epicId = manager.createTaskAndReturnId(epic);
        SubTask subTask = new SubTask("newSubtask", "description newSubtask", epic.getId(), Duration.ofMinutes(15), "2008-12-03T10:15:30");
        final int subtaskId = manager.createTaskAndReturnId(subTask);

        assertEquals(1, taskId);
        assertEquals(2, epicId);
        assertEquals(3, subTask.getId());
    }


    @Test
    public void getTaskAndEpicTaskAndSubtaskWhenTaskMapIsEmpty() {

        assertNull(manager.getAnyTask(1));
    }



    @Test
    public void deleteByIdAndTypeTaskWhenHashMapIsEmpty() {
        assertFalse(manager.deleteByIdAndTypeTask(0, TASK));
        assertFalse(manager.deleteByIdAndTypeTask(0, SUBTASK));
        assertFalse(manager.deleteByIdAndTypeTask(0, EPICTASK));
    }

    @Test
    public void deleteTasksByTypeWhenHashMapIsEmpty() {
        assertFalse(manager.deleteTasksByType(TASK));
        assertFalse(manager.deleteTasksByType(SUBTASK));
        assertFalse(manager.deleteTasksByType(EPICTASK));
    }



    @Test
    public void getByIdAndTypeTaskWhenHashMapIsEmpty() {
        assertFalse(manager.deleteByIdAndTypeTask(0,TASK));
        assertFalse(manager.deleteByIdAndTypeTask(0, SUBTASK));
        assertFalse(manager.deleteByIdAndTypeTask(0, EPICTASK));
    }

    @Test
    public void addNewTaskAndEpicTaskAndSubtaskByInvalidId() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Duration.ofMinutes(10), LocalDateTime.parse("2007-12-03T10:15:30"));
        EpicTask epic = new EpicTask("Test addNewEpicTask", "Test addNewEpicTask description");
        SubTask subTask = new SubTask("newSubtask", "description newSubtask", epic.getId(), Duration.ofMinutes(15), "2008-12-03T10:15:30");

        assertNull(manager.getAnyTask(5));
        assertNull(manager.getByIdAndTypeTask(3, TASK));
        assertNull(manager.getByIdAndTypeTask(8, SUBTASK));
        assertNull(manager.getByIdAndTypeTask(15, EPICTASK));
    }

    @Test
    public void deleteByIdAndTypeTaskByInvalidId() {
        assertEquals(false, manager.deleteByIdAndTypeTask(0, TASK), "Нельзя удалить задачу №" + 0 + ", так как ее нет.");
        assertEquals(false, manager.deleteByIdAndTypeTask(0, SUBTASK), "Нельзя удалить задачу №" + 0 + ", так как ее нет.");
        assertEquals(false, manager.deleteByIdAndTypeTask(0, EPICTASK), "Нельзя удалить задачу №" + 0 + ", так как ее нет.");
    }


}