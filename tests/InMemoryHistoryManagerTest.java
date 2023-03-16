import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.oop.tasktreker.model.EpicTask;
import ru.yandex.oop.tasktreker.model.SubTask;
import ru.yandex.oop.tasktreker.model.Task;
import ru.yandex.oop.tasktreker.presenter.HistoryManager;
import ru.yandex.oop.tasktreker.presenter.TaskManager;
import ru.yandex.oop.tasktreker.presenter.impl.InMemoryHistoryManager;
import ru.yandex.oop.tasktreker.presenter.impl.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private EpicTask epic;
    private TaskManager taskManager;


    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager();
        epic = new EpicTask("Test epicTask", "description test epicTask");
        epic.setId(taskManager.createTaskAndReturnId(epic));

    }
    @AfterEach
    public void afterEach() {
        if (historyManager.getHistory() != null) {
            historyManager.getHistory().clear();
        }
    }

    @Test
    public void add() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Duration.ofMinutes(10), LocalDateTime.parse("2007-12-03T10:15:30"));

        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    public void addWhenListIsEmpty() {
        final List<Task> history = new ArrayList<>();

        assertEquals(history, historyManager.getHistory());
    }

    @Test
    public void addByDuplication() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Duration.ofMinutes(10), LocalDateTime.parse("2007-12-03T10:15:30"));
        Task task2 = new Task("Test addNewTask", "Test addNewTask description", Duration.ofMinutes(15), LocalDateTime.parse("2008-12-03T10:15:30"));
        EpicTask epic2 = new EpicTask("Test addNewEpicTask", "Test addNewEpicTask description");
        SubTask subTask = new SubTask("newSubtask", "description newSubtask", epic.getId(), Duration.ofMinutes(15), "2008-01-03T10:15:30");
        SubTask subTask2 = new SubTask("newSubtask", "description newSubtask", epic.getId(), Duration.ofMinutes(15), "2008-02-03T10:15:30");

        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(epic);
        historyManager.add(epic2);
        historyManager.add(subTask);
        historyManager.add(subTask2);
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(2, history.size(), "История не пустая.");
    }






    @Test
    public void remove() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Duration.ofMinutes(10), LocalDateTime.parse("2007-12-03T10:15:30"));

        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        historyManager.remove(1);

        //       assertNotNull(history, "История не пустая.");
        assertEquals(0, history.size(), "История не пустая.");
    }
}

