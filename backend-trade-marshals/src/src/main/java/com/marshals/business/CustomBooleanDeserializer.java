package com.marshals.business;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public class CustomBooleanDeserializer extends JsonDeserializer<Boolean> {
    @Override
    public Boolean deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        String value = jsonParser.getText();
        if ("Y".equalsIgnoreCase(value)) {
            return true;
        } else if ("N".equalsIgnoreCase(value)) {
            return false;
        } else {
            throw new IOException("Invalid boolean value: " + value);
        }
    }
}
