# Telegram API bot

This project implements a simple web server that exposes some REST API to send a message to a Telegram bot. In order to send a message to the Telgram bot, a user must be created and the he must request an access toke.

Exposed REST APIs are:

* Create a new user: POST /auth/signup 
  ``` json
    {
      "username": "string",
      "password": "string",
      "password2": "string"
    }
  ```
* Request an access token: POST /auth/signin
  ``` json
    {
      "username": "string",
      "password": "string"
    }
  ```
* Send Telegram message: POST /telegram/send_message
  ```
    Headers:
      - Authorization Bearer Token
    Multipart body:
      - text -> string
      - image -> file
  ```
  
  ## Technologies involved
  
  This project is written in Java 17, using Springboot. JUnit tests are implemented.
  A Github Actions workflow to build and test the project has been created.
  
  A screenshot of a sent message is attached below:
  
  
