package com.qusarbyte.ggldjp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;


@SpringBootApplication
public class SpringBootConsoleApplication
        implements CommandLineRunner {

    private final static Logger logger = LoggerFactory.getLogger(SpringBootConsoleApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringBootConsoleApplication.class, args);
    }

    @Override
    public void run(String... args) {
        if (args.length < 2) {
            System.err.println("Error: Please provide both input and output file paths as arguments.");
            System.err.println("Usage: java -jar <your-jar-name>.jar <inputFilePath> <outputFilePath>");
            System.exit(1);  // Exit with error code 1
        }

        String inputFilePath = args[0];  // First argument is input file path
        String outputFilePath = args[1]; // Second argument is output file path

        ObjectMapper mapper = new ObjectMapper();

        try {
            // Read the input JSON file
            JsonNode rootNode = mapper.readTree(new File(inputFilePath));

            // Modify the JSON structure by recursively traversing
            JsonNode modifiedRootNode = modifyJson(rootNode);

            // Write the modified JSON to the output file
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputFilePath), modifiedRootNode);

            System.out.println("JSON modification complete. Output saved to: " + outputFilePath);

        } catch (IOException e) {
            System.err.println("Error: Unable to process the files. Please ensure the file paths are correct.");
            System.err.println("Exception: " + e.getMessage());
            System.exit(1);  // Exit with error code 1
        }
    }

    // Recursively modify the JSON object
    private static JsonNode modifyJson(JsonNode rootNode) {
        // If the node has "methods", process the methods
        if (rootNode.has("methods")) {
            processMethodsNode(rootNode.get("methods"));
        }

        // Iterate through the fields of the current node and recurse into object fields
        Iterator<String> fieldNames = rootNode.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode childNode = rootNode.get(fieldName);

            // If the field is an object, recursively explore it
            if (childNode.isObject()) {
                modifyJson(childNode); // Recurse into the child node
            }
        }

        return rootNode;
    }

    // Process a "methods" node to modify "path", "flatPath", and add "originalPath"
    private static void processMethodsNode(JsonNode methodsNode) {
        // Iterate through each method inside the "methods" node
        Iterator<String> methodNames = methodsNode.fieldNames();
        while (methodNames.hasNext()) {
            String methodName = methodNames.next();
            JsonNode methodNode = methodsNode.get(methodName);

            // Check if both "path" and "flatPath" exist
            if (methodNode.has("path") && methodNode.has("flatPath")) {
                String path = methodNode.get("path").asText();
                String flatPath = methodNode.get("flatPath").asText();

                // Modify the "path" and add "originalPath"
                String modifiedPath = modifyPath(path, flatPath);

                // Create a mutable ObjectNode from methodNode to add "originalPath" and modify "path"
                if (methodNode instanceof ObjectNode mutableMethodNode) {
                    mutableMethodNode.put("originalPath", path);  // Store original path
                    mutableMethodNode.put("path", modifiedPath);  // Modify path
                }
            }
        }
    }

    // Modify the "path" by replacing "v1beta/" with the prefix from "flatPath"
    private static String modifyPath(String path, String flatPath) {
        int curlyBraceIndex = flatPath.indexOf("{");

        // Check if flatPath contains a "{"
        if (curlyBraceIndex == -1) {
            // If there's no "{", we cannot modify the path based on the flatPath. Return the original path.
            return path;
        }

        String prefix = flatPath.substring(0, curlyBraceIndex);
        return path.replace("v1beta/", prefix);
    }

}