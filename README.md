# google-generativelanguage-discovery-json-patcher-app

Application that patches Google Generative Language Discovery documents by modifying API paths.

## Overview

This repository provides a Spring Boot application that patches Google Generative Language Discovery documents by modifying API paths. The tool replaces `v1beta/` in API paths with a stable version prefix from the `flatPath` field, ensuring consistency and compatibility for OpenAPI generation. It also preserves the original paths for reference by adding an `originalPath` field.

## Usage

After running the application, the patched JSON file can be used with the [google-discovery-to-swagger](https://github.com/APIs-guru/google-discovery-to-swagger.git) tool to generate an **OpenAPI specification**. 

This OpenAPI specification can then be used to generate a **Gemini API Client Library** using the [`openapi-generator-cli`](https://openapi-generator.tech/docs/installation/#jar) tool. This allows seamless integration with various programming languages.

## Requirements to build application

- Java 21+
- Apache Maven

## Requirements to run application

- Java 21+
- Download https://generativelanguage.googleapis.com/$discovery/rest?version=v1beta&key=$GOOGLE_API_KEY file,
    where GOOGLE_API_KEY you can generate with Google AI Studio following by the link https://aistudio.google.com/app/prompts/new_chat  
    Save downloaded file to by path ".\documents\generativelanguage-discovery.json"