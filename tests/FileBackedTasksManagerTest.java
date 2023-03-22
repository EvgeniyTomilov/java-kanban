import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.oop.tasktreker.exception.TaskValidationException;
import ru.yandex.oop.tasktreker.model.EpicTask;
import ru.yandex.oop.tasktreker.model.SubTask;
import ru.yandex.oop.tasktreker.model.Task;
import ru.yandex.oop.tasktreker.presenter.TaskManager;
import ru.yandex.oop.tasktreker.presenter.impl.FileBackedTasksManager;
import ru.yandex.oop.tasktreker.presenter.util.Managers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {


    @BeforeEach
    public void beforeEach() {
        manager = Managers.getFileBacked("resource/file.csv");
    }

    @Test
    public void saveTasksIfTheirTimeIntersects() {
        Task task = new Task("01t", "01t", Duration.ofMinutes(10), LocalDateTime.parse("2007-12-03T10:15:30"));
        manager.createTaskAndReturnId(task);
        Task task2 = new Task("01t", "01t", Duration.ofMinutes(10), LocalDateTime.parse("2007-12-03T10:15:30"));
        assertThrows(TaskValidationException.class, () -> manager.createTaskAndReturnId(task2));

        EpicTask epic = new EpicTask("Test addNewEpicTask", "Test addNewEpicTask description");
        int epicId = manager.createTaskAndReturnId(epic);
        SubTask subTask = new SubTask("newSubtask", "description newSubtask", epicId, Duration.ofMinutes(15), "2007-12-03T10:15:30");
        assertEquals(3, manager.getAllTasks().size());

    }

    @Test
    public void saveAndLoadWhenTaskListIsEmpty() {
        manager.save();
        try {
            String stringFile = Files.readString(Path.of(manager.getFile().getPath()));
            String[] lines = stringFile.split("\n");
            assertEquals(2, lines.length);
            assertEquals("\r", lines[lines.length - 1]);
            assertEquals("id,type,name,status,description,epic,startTime,duration\r", lines[0]);
        } catch (IOException ignored) {
        }
        FileBackedTasksManager newManager = FileBackedTasksManager.loadFromFile(manager.getFile());
        List<Task> allTask = new ArrayList<>();
        assertEquals( allTask, newManager.getAllTasks());
    }

    @Test
    public void saveAndLoadWhenEpicSubtaskListIsEmpty() {
        EpicTask epic = new EpicTask("Test addNewEpicTask", "Test addNewEpicTask description");
        int epicId = manager.createTaskAndReturnId(epic);
        manager.save();
        try {
            String stringFile = Files.readString(Path.of(manager.getFile().getPath()));
            String[] lines = stringFile.split("\n");
            assertEquals(4, lines.length);
            assertEquals("\r", lines[lines.length - 2]);
            assertEquals("1,EPICTASK,Test addNewEpicTask,NEW,Test addNewEpicTask description,null,null\r", lines[1]);
        } catch (IOException ignored) {
        }
        FileBackedTasksManager newManager = FileBackedTasksManager.loadFromFile(manager.getFile());
        assertEquals(manager.getAnyTask(epicId).toString(), newManager.getAnyTask(epicId).toString());
    }

    @Test
    public void saveAndLoadWhenHistoryIsEmpty() {
        EpicTask epic = new EpicTask("Test addNewEpicTask", "Test addNewEpicTask description");
        int epicId = manager.createTaskAndReturnId(epic);
               try {
            String stringFile = Files.readString(Path.of(manager.getFile().getPath()));
            String[] lines = stringFile.split("\n");
            assertEquals(4, lines.length);
            assertEquals("\r", lines[lines.length - 2]);
            assertEquals("1,EPICTASK,Test addNewEpicTask,NEW,Test addNewEpicTask description,null,null\r", lines[1]);
        } catch (IOException ignored) {
        }
        FileBackedTasksManager newManager = FileBackedTasksManager.loadFromFile(manager.getFile());
        assertEquals(manager.getAnyTask(epicId).toString(), newManager.getAnyTask(epicId).toString());


    }

}