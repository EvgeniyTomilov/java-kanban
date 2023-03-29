package ru.yandex.oop.tasktreker.serverfunctionalityrealization.customjson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ru.yandex.oop.tasktreker.model.SubTask;

import java.lang.reflect.Type;

public class SubTaskSerializer implements JsonSerializer<SubTask> {
    @Override
    public JsonElement serialize(SubTask subTask, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();

        result.addProperty("Type", "SUBTASK");
        result.addProperty("Id", subTask.getId());
        result.addProperty("name", subTask.getName());
        result.addProperty("Description", subTask.getDescription());

        switch (subTask.getStatus()) {
            case NEW -> result.addProperty("Status", "NEW");
            case IN_PROGRESS -> result.addProperty("Status", "IN_PROGRESS");
            case DONE -> result.addProperty("Status", "DONE");
        }
        result.addProperty("EpicID", subTask.getEpicId());
        result.addProperty("Duration", subTask.getDuration().toMinutes());//???
        result.addProperty("StartTime", subTask.getStartTime().getMinute());
        return result;
    }
}
