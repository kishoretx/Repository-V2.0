package com.example.repov2.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.protobuf.Struct;
import com.google.protobuf.util.JsonFormat;

import java.io.IOException;

public class ProtobufStructAdapter extends TypeAdapter<Struct> {

    @Override
    public void write(JsonWriter out, Struct value) throws IOException {
        out.jsonValue(JsonFormat.printer().print(value));
    }

    @Override
    public Struct read(JsonReader in) throws IOException {
        throw new UnsupportedOperationException("Reading Protobuf Struct from JSON is not supported.");
    }
}
