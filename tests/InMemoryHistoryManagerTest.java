import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.oop.tasktreker.model.EpicTask;
import ru.yandex.oop.tasktreker.model.SubTask;
import ru.yandex.oop.tasktreker.model.Task;
import ru.yandex.oop.tasktreker.model.enums.TaskType;
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

    private HistoryManager historyManager = new InMemoryHistoryManager();
    private EpicTask epic;
    private Task task;
    private SubTask subTask;
    private TaskManager taskManager;


    @BeforeEach
    public void beforeEach() {
        this.task = create(TaskType.TASK);
        this.task.setId(1001);
        this.epic = (EpicTask) create(TaskType.EPICTASK);
        this.epic.setId(2001);
        this.subTask = new SubTask("newSubtask", "description newSubtask", 0, Duration.ofMinutes(15), "2008-01-03T10:15:30");
        subTask.setId(3001);
        taskManager = new InMemoryTaskManager();


    }

    public Task create(TaskType taskType) {
        if (taskType.equals(TaskType.TASK)) {
            return new Task("01t", "01t", Duration.ofMinutes(10), LocalDateTime.parse("2007-12-03T10:15:30"));
        } else if (taskType.equals(TaskType.EPICTASK)) {
            return new EpicTask("01e", "01e");
        }
        return new SubTask("newSubtask", "description newSubtask", 0, Duration.ofMinutes(15), "2008-01-03T10:15:30");
    }

    @AfterEach
    public void afterEach() {
        if (historyManager.getHistory() != null) {
            historyManager.getHistory().clear();
        }
    }

    @Test
    public void add() {

        historyManager.add(task);
        assertEquals(1, historyManager.getHistory().size());
        historyManager.add(epic);
        assertEquals(2, historyManager.getHistory().size());
        historyManager.add(subTask);
        assertEquals(3, historyManager.getHistory().size());

        List<Task> history = new ArrayList<>();
        history.add(subTask);
        history.add(epic);
        history.add(task);
        assertEquals(history, historyManager.getHistory());

    }

    @Test
    public void getHistoryWhenListIsEmpty() {
        final List<Task> history = new ArrayList<>();

        assertEquals(history, historyManager.getHistory());

    }

    @Test
    public void addByDuplication() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subTask);
        historyManager.add(task);
        historyManager.add(epic);

        List<Task> history = new ArrayList<>();
        history.add(epic);
        history.add(task);
        history.add(subTask);

        assertEquals(history, historyManager.getHistory());
        assertEquals(3, historyManager.getHistory().size());


    }


    @Test
    public void removeFromTheBeginning() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subTask);
        historyManager.remove(task.getId());
        List<Task> history = new ArrayList<>();
        history.add(subTask);
        history.add(epic);

        assertEquals(history, historyManager.getHistory());
    }

    @Test
    public void removeFromTheMiddle() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subTask);
        historyManager.remove(epic.getId());
        List<Task> history = new ArrayList<>();
        history.add(subTask);
        history.add(task);

        assertEquals(history, historyManager.getHistory());
    }

    @Test
    public void removeFromTheEnd() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subTask);
        historyManager.remove(subTask.getId());
        List<Task> history = new ArrayList<>();
        history.add(epic);
        history.add(task);

        assertEquals(history, historyManager.getHistory());
    }
}

