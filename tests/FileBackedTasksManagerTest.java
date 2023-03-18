

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.oop.tasktreker.model.EpicTask;
import ru.yandex.oop.tasktreker.model.SubTask;
import ru.yandex.oop.tasktreker.model.Task;
import ru.yandex.oop.tasktreker.model.enums.TaskType;
import ru.yandex.oop.tasktreker.presenter.impl.FileBackedTasksManager;
import ru.yandex.oop.tasktreker.presenter.util.Managers;

import javax.imageio.IIOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest {

    FileBackedTasksManager manager = Managers.getFileBacked("./file.csv");

    @BeforeEach
    public void beforeEach() {
        manager = Managers.getFileBacked("./file.csv");
    }

    @Test
    public void saveTasksIfTheirTimeIntersects() {
        Task task = new Task("01t", "01t", Duration.ofMinutes(10), LocalDateTime.parse("2007-12-03T10:15:30"));
        manager.createTaskAndReturnId(task);
        Task task2 = new Task("01t", "01t", Duration.ofMinutes(10), LocalDateTime.parse("2007-12-03T10:15:30"));
        assertEquals(0, manager.createTaskAndReturnId(task2));

        EpicTask epic = new EpicTask("Test addNewEpicTask", "Test addNewEpicTask description");
        int epicId = manager.createTaskAndReturnId(epic);
        SubTask subTask = new SubTask("newSubtask", "description newSubtask", epicId, Duration.ofMinutes(15), "2007-12-03T10:15:30");
        assertEquals(0, manager.createTaskAndReturnId(subTask));

        List<Task> allTask = new ArrayList<>();
        allTask.add(task);
        assertEquals(allTask, manager.getAllTasks());
    }

    @Test
    public void saveAndLoadWhenTaskListIsEmpty() {
        manager.save();
        try {
            String stringFile = Files.readString(Path.of(manager.getFile().getPath()));
            String[] lines = stringFile.split("\n");
            assertEquals(3, lines.length);
            assertEquals(" ", lines[lines.length-1]);
            assertEquals("id,type,name,status,description,epic,startTime,duration", lines[0]);
        }catch (IIOException ignored){}
        FileBackedTasksManager newManager = FileBackedTasksManager.loadFromFile(manager.getFile());
        assertNull(newManager.getAllTasks());
    }

    @Test
    public void saveAndLoadWhenEpicSubtaskListIsEmpty() {
        EpicTask epic = new EpicTask("Test addNewEpicTask", "Test addNewEpicTask description");
        int epicId = manager.createTaskAndReturnId(epic);
        try {
            String stringFile = Files.readString(Path.of(manager.getFile().getPath()));
            String[] lines = stringFile.split("\n");
            assertEquals(4, lines.length);
            assertEquals(" ", lines[lines.length-1]);
            assertEquals("1,EPICTASK,Test addNewEpicTask,NEW,Test addNewEpicTask description,,null,null", lines[1]);
        }catch (IIOException ignored){}
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
            assertEquals(" ", lines[lines.length-1]);
            assertEquals("1,EPICTASK,Test addNewEpicTask,NEW,Test addNewEpicTask description,,null,null", lines[1]);
        }catch (IIOException ignored){}
        FileBackedTasksManager newManager = FileBackedTasksManager.loadFromFile(manager.getFile());
        assertEquals(manager.getAnyTask(epicId).toString(), newManager.getAnyTask(epicId).toString());
        assertNull(newManager.getHistoryManager());

    }

}