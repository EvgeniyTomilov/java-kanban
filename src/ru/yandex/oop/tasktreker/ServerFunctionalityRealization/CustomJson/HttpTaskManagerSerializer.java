package ru.yandex.oop.tasktreker.ServerFunctionalityRealization.CustomJson;

import com.google.gson.*;
import ru.yandex.oop.tasktreker.ServerFunctionalityRealization.HttpTaskManager;
import ru.yandex.oop.tasktreker.model.EpicTask;
import ru.yandex.oop.tasktreker.model.SubTask;
import ru.yandex.oop.tasktreker.model.Task;

import java.lang.reflect.Type;

public class HttpTaskManagerSerializer implements JsonSerializer<HttpTaskManager> {

    @Override
    public JsonElement serialize(HttpTaskManager manager, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        JsonArray tasks = new JsonArray();
        result.addProperty("User key", manager.getKey());
        result.addProperty("TaskId", manager.getNextId());
        result.addProperty("EpicId", manager.getNextId());
        result.addProperty("SubtaskId", manager.getNextId());

        if (!manager.getTaskMap().isEmpty()) {
            for (Task task : manager.getTaskMap().values()) {
                tasks.add(manager.toString(task));
            }
            result.addProperty("Tasks", tasks.toString());
        } else {
            result.addProperty("Tasks", "null");
        }

        JsonArray epics = new JsonArray();
        if (!manager.getEpicTaskMap().isEmpty()) {
            for (EpicTask epic : manager.getEpicTaskMap().values()) {
                epics.add(manager.toString(epic));
            }
            result.addProperty("Epics", epics.toString());
        } else {
            result.addProperty("Epics", "null");
        }

        JsonArray subtasks = new JsonArray();
        if (!manager.getSubTaskMap().isEmpty()) {
            for (SubTask subtask : manager.getSubTaskMap().values()) {
                subtasks.add(manager.toString(subtask));
            }
            result.addProperty("Subtasks", subtasks.toString());
        } else {
            result.addProperty("Subtasks", "null");
        }

        if (manager.getHistoryManager() != null) {
            StringBuilder historyString = new StringBuilder();
            for (Task task : manager.getHistoryManager().getHistory()) {
                historyString.append(task.getId());
                historyString.append(",");
            }
            result.addProperty("History", historyString.toString());
        } else {
            result.addProperty("History", "null");
        }
        return result;
    }
}
