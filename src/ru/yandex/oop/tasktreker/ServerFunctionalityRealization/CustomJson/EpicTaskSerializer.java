package ru.yandex.oop.tasktreker.ServerFunctionalityRealization.CustomJson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ru.yandex.oop.tasktreker.model.EpicTask;

import java.lang.reflect.Type;

public class EpicTaskSerializer implements JsonSerializer<EpicTask> {
    @Override
    public JsonElement serialize(EpicTask epicTask, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();

        result.addProperty("Type", "EPICTASK");
        result.addProperty("Id", epicTask.getId());
        result.addProperty("name", epicTask.getName());
        result.addProperty("Description", epicTask.getDescription());

        switch (epicTask.getStatus()) {
            case NEW -> result.addProperty("Status", "NEW");
            case IN_PROGRESS -> result.addProperty("Status", "IN_PROGRESS");
            case DONE -> result.addProperty("Status", "DONE");
        }
        result.addProperty("Duration", epicTask.getDuration().toMinutes());//???
        return result;
    }
}

