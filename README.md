# EDUGENAI Email API

## Overview

Email API is a Spring Boot microservice
responsible for sending email notifications.

## Technologies

- Java 17
- Spring Boot
- Spring Web
- Java Mail Sender
- Jakarta Validation
- Maven
- JUnit 5
- Mockito

## API Endpoint

POST /api/email/send

## Local URL

http://localhost:8082

## Request Example

{
  "to": "student@gmail.com",
  "subject": "Test Email",
  "message": "Welcome to EDUGENAI"
}

## Features

- Email delivery
- Request validation
- Error handling
- Unit testing
- REST API integration

## Running the Application

mvn spring-boot:run

## Running Tests

mvn test