import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.oop.tasktreker.model.EpicTask;
import ru.yandex.oop.tasktreker.model.SubTask;
import ru.yandex.oop.tasktreker.model.enums.TaskStatus;
import ru.yandex.oop.tasktreker.presenter.TaskManager;
import ru.yandex.oop.tasktreker.presenter.impl.InMemoryTaskManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTaskTest {
    private TaskManager taskManager;
    private EpicTask epicTask1;


    @BeforeEach
    public void beforeEach() {
    taskManager  = new InMemoryTaskManager();
    epicTask1 = new EpicTask("Test epicTask", "description test epicTask");
    epicTask1.setId(taskManager.createTaskAndReturnId(epicTask1));
    }

    @Test
    public void addNewEpicTaskNoSubTask(){ // Пустой список подзадач.
        assertEquals(TaskStatus.NEW,epicTask1.getStatus());
    }

    @Test
    public void addNewEpicTaskWithNewSubTask() { //Все подзадачи со статусом NEW
        SubTask subTask1 = new SubTask("subTask1 epicTask", "description test subTask1", epicTask1.getId());
        SubTask subTask2 = new SubTask("subTask1 epicTask", "description test subTask2", epicTask1.getId());
        subTask1.setStatus(TaskStatus.NEW);
        subTask2.setStatus(TaskStatus.NEW);
        taskManager.createTaskAndReturnId(subTask1);
        taskManager.createTaskAndReturnId(subTask2);

        assertEquals(TaskStatus.NEW,epicTask1.getStatus());
    }

    @Test
    public void addNewEpicTaskWithDoneSubTask() { //Все подзадачи со статусом DONE
        SubTask subTask1 = new SubTask("subTask1 epicTask", "description test subTask1", epicTask1.getId());
        SubTask subTask2 = new SubTask("subTask1 epicTask", "description test subTask2", epicTask1.getId());
        subTask1.setStatus(TaskStatus.DONE);
        subTask2.setStatus(TaskStatus.DONE);
        taskManager.createTaskAndReturnId(subTask1);
        taskManager.createTaskAndReturnId(subTask2);

        assertEquals(TaskStatus.DONE,epicTask1.getStatus());
    }

    @Test
    public void addNewEpicTaskWithDoneAndNewSubTask() { //Подзадачи со статусами NEW и DONE
        SubTask subTask1 = new SubTask("subTask1 epicTask", "description test subTask1", epicTask1.getId());
        SubTask subTask2 = new SubTask("subTask1 epicTask", "description test subTask2", epicTask1.getId());
        subTask1.setStatus(TaskStatus.DONE);
        subTask2.setStatus(TaskStatus.NEW);
        taskManager.createTaskAndReturnId(subTask1);
        taskManager.createTaskAndReturnId(subTask2);

        assertEquals(TaskStatus.IN_PROGRESS,epicTask1.getStatus());
    }

    @Test
    public void addNewEpicTaskWithInProgressSubTask() { //Подзадачи со статусом IN_PROGRESS
        SubTask subTask1 = new SubTask("subTask1 epicTask", "description test subTask1", epicTask1.getId());
        SubTask subTask2 = new SubTask("subTask1 epicTask", "description test subTask2", epicTask1.getId());
        subTask1.setStatus(TaskStatus.IN_PROGRESS);
        subTask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.createTaskAndReturnId(subTask1);
        taskManager.createTaskAndReturnId(subTask2);

        assertEquals(TaskStatus.IN_PROGRESS,epicTask1.getStatus());
    }
}