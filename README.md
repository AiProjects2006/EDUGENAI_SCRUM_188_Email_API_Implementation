# EDUGENAI Email API

## Overview

The EDUGENAI Email API is a Spring Boot 3 microservice that sends general-purpose
emails and OTP verification emails through Gmail SMTP. OTP generation, storage,
verification, and password-reset logic remain outside this service.

## Technology stack

- Java 17
- Spring Boot 3.5
- Spring Web
- Spring Mail / JavaMailSender
- Jakarta Validation
- SpringDoc OpenAPI
- Maven
- JUnit 5 and Mockito

## Project structure

```text
src/main/java/com/ailearning/emailapi/
  controller/EmailController.java
  dto/
  exception/GlobalExceptionHandler.java
  service/EmailService.java
src/main/resources/application.yaml
src/test/java/com/ailearning/emailapi/
```

## SMTP environment variables

```text
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

The application defaults `MAIL_HOST` to `smtp.gmail.com` and `MAIL_PORT` to `587`.
Credentials must be supplied through environment variables and must not be
committed to source control.

## Start the application

```bash
mvn spring-boot:run
```

The API listens on port `8086`.

## General email API

`POST /api/email/send`

```json
{
  "to": "student@gmail.com",
  "subject": "Welcome",
  "message": "Welcome to EDUGENAI"
}
```

Success response:

```json
{
  "status": "SUCCESS",
  "message": "Email sent successfully"
}
```

## OTP email API

`POST /api/email/send-otp`

Registration request:

```json
{
  "to": "student@gmail.com",
  "otp": "483921",
  "purpose": "REGISTRATION",
  "expiryMinutes": 5
}
```

Password-reset request:

```json
{
  "to": "student@gmail.com",
  "otp": "483921",
  "purpose": "PASSWORD_RESET",
  "expiryMinutes": 5
}
```

Success response:

```json
{
  "status": "SUCCESS",
  "message": "OTP email sent successfully"
}
```

### Validation

- `to` must be a valid email address.
- `otp` is required and must contain exactly six numeric digits.
- `purpose` must be `REGISTRATION` or `PASSWORD_RESET`.
- `expiryMinutes` must be from 1 through 30.

### Error responses

Invalid request (`400 Bad Request`):

```json
{
  "status": "FAILED",
  "message": "Invalid request"
}
```

SMTP delivery errors (`502 Bad Gateway`) use one of:

```json
{"status":"FAILED","message":"SMTP authentication failed"}
```

```json
{"status":"FAILED","message":"SMTP connection failed"}
```

```json
{"status":"FAILED","message":"Email delivery failed"}
```

Internal SMTP details and OTP values are not exposed in API responses or logs.

## API documentation

When running, OpenAPI is available at:

- Swagger UI: `http://localhost:8086/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8086/v3/api-docs`

## Run tests

```bash
mvn clean test
```

Build the package:

```bash
mvn clean package
```
