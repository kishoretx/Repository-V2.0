package com.example.repov2;

import com.example.repov2.util.Utility_JsonToStructConverter;
import com.google.protobuf.Struct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ClasspathFileReader {
    public static void main(String[] args) {
        String fileName = "Tree_with_3K_nodes.txt"; // File name or path relative to the classpath

        try {
            // Open an input stream for the file
            InputStream inputStream = ClasspathFileReader.class.getClassLoader().getResourceAsStream(fileName);

            if (inputStream != null) {
                // Wrap the input stream in a BufferedReader to read lines
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                // Read lines from the file and append them to the StringBuilder
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                String jsonString = stringBuilder.toString();
                // Close the reader and print the content of the file
                reader.close();
                System.out.println("File content:");
                //System.out.println(stringBuilder.toString());

                Struct struct = Utility_JsonToStructConverter.convertJsonToStruct(jsonString);
                struct.getFieldsMap().keySet().stream().forEach( System.out::println);
                System.out.println(struct);
            } else {
                System.err.println("File not found: " + fileName);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}
