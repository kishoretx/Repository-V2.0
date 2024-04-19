package com.example.repov2.util;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Struct;
import com.google.protobuf.util.JsonFormat;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class Utility_JsonToStructConverter {

    public static Struct convertJsonToStruct(String jsonString) throws JsonSyntaxException, InvalidProtocolBufferException {
        Gson gson = new Gson();
        Struct.Builder structBuilder = Struct.newBuilder();

        // Parse JSON string using Gson
        Object jsonObject = gson.fromJson(jsonString, Object.class);

        // Convert JSON object to Struct
        String json = gson.toJson(jsonObject);
        JsonFormat.parser().merge(json, structBuilder);

        return structBuilder.build();
    }

    public static void main(String[] args) {
        // Example usage
        String jsonString = "{\"city\":\"Berlin\",\"country\":\"Germany\"}";

        try {
            Struct struct = convertJsonToStruct(jsonString);
            System.out.println("Struct object: " + struct);
        } catch (JsonSyntaxException | InvalidProtocolBufferException e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }
    }
}
