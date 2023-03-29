package ru.yandex.oop.tasktreker.serverfunctionalityrealization.customjson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ru.yandex.oop.tasktreker.model.Task;

import java.lang.reflect.Type;

public class TaskSerializer implements JsonSerializer<Task> {
    @Override
    public JsonElement serialize(Task task, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();

        result.addProperty("Type", "TASK");
        result.addProperty("Id", task.getId());
        result.addProperty("name", task.getName());
        result.addProperty("Description", task.getDescription());

        switch (task.getStatus()){
            case NEW -> result.addProperty("Status", "NEW");
            case IN_PROGRESS -> result.addProperty("Status", "IN_PROGRESS");
            case DONE -> result.addProperty("Status", "DONE");
        }
        result.addProperty("Duration", task.getDuration().toMinutes());//???
        result.addProperty("StartTime", task.getStartTime().getMinute());//???
        return result;
    }
}
