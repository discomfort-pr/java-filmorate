package ru.yandex.practicum.filmorate.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class UserSerializer extends JsonSerializer<User> {

    @Override
    public void serialize(User user, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        writeNoFriends(user, jsonGenerator, serializerProvider, false);

        jsonGenerator.writeArrayFieldStart("friends");
        for (User friend : user.getFriends()) {
            writeNoFriends(friend, jsonGenerator, serializerProvider, true);
        }
        jsonGenerator.writeEndArray();


        jsonGenerator.writeEndObject();
    }

    private void writeNoFriends(User user, JsonGenerator jsonGenerator, SerializerProvider serializerProvider, boolean end) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", user.getId());
        jsonGenerator.writeStringField("email", user.getEmail());
        jsonGenerator.writeStringField("login", user.getLogin());
        jsonGenerator.writeStringField("name", user.getName());
        jsonGenerator.writeStringField("birthday", user.getBirthday().format(DateTimeFormatter.ISO_LOCAL_DATE));
        if (end) {
            jsonGenerator.writeEndObject();
        }
    }
}
