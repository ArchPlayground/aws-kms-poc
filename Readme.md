# Introduction

This is a PoC on how to use AWS KMS to securely encrypt and decrpyt data. PoC contains two types of usage:
- using-cli: this demonstrates how to use `aws-cli` to encrypt and decrypt a text file
- spring-boot-app: this demonstrates how a spring boot app can encrypt and decrypt data using AWS KMS

## Local Setup

## Pre-requisite
 Please ensure the `aws cli` is setup and configured to work with your AWS account on your local machine
## cli:
        
```
cd using-cli
```
        
- Run `encrypt.sh` to encrypt data in `secret.txt`. This will store the encrypted output in `encrypted-secret.txt` file. This also displays contents of this file on `stdout`
- Run `decrypt.sh` to decrypt data from above step. This will display the decrypted text on `stdout`. The decrypted text should match the contents of `secret.txt`

## Spring Boot Application

### Running the App
- Make sure `Java 11` is installed
- Run app using `mvn spring-boot:run`
- Application has `swagger-ui` available @ `http://localhost:8080/swagger-ui.html`

### Usage
Application exposes two controllers:
- `encryption-controller`
    - To encrypt a path using main cloud CMS key
    ```
    curl -X POST "http://localhost:8080/encrypt/123/filePath" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"path\": \"hello\"}"```
- `decryption-controller`: 
    - To decrypt above path using main cloud CMS key. Use the output from above endpoint and run
    ```
    curl -X POST "http://localhost:8080/decrypt/123/filePath" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"path\": \"encrypted-path\"}"```
