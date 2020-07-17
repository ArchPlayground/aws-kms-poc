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
- Application exposes two types of controllers:
    - `encryption-controller`
        - To encrypt a path using main cloud CMS key
        ```
        curl -X POST "http://localhost:8080/encrypt/123/filePath" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"path\": \"hello\"}"```
    - `decryption-controller`: 
        - To decrypt above path using main cloud CMS key. Use the output from above endpoint and run
        ```
        curl -X POST "http://localhost:8080/decrypt/123/filePath" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"path\": \"encrypted-path\"}"```

 
- Application is setup to run in 4 different profiles. These profiles mimic below servers in our production setup:

     App Server (Encryptor running in Cloud).
        - Activate this profile by running `mvn spring-boot:run -Dspring-boot.run.profiles=encryptor`
    
    - Cloud Vault (Decryptor running in Cloud)
        - Activate this profile by running `mvn spring-boot:run -Dspring-boot.run.profiles=decryptor`
    
    - OnPrem Vault1 (Decryptor#1 running on prem in Customer's environment)
        - Activate this profile by running `mvn spring-boot:run -Dspring-boot.run.profiles=onprem1`
    
    - OnPrem Vault2 (Decryptor#2 running on prem in Customer's environment)
        - Activate this profile by running `mvn spring-boot:run -Dspring-boot.run.profiles=onprem2`

- Application uses three unique keys in KMS
    - Key tagged as `testKey` represents the main key that cloud encryptor and decryptors will use for companies that don't have a local vault
    - Key tagged as `testOnPremKey` is assigned to on-prem Vault in region-1 of Company A
    - Key tagged as `testOnPremKey2` is assigned to on-prem Vault in region-2 of Company A

### Use Cases Covered
- Application demonstrates these use cases using above keys:
    - AppServer (Encryptor running in cloud) should have access to encrypt the data using any of the 3 keys.
        - it enrypts data using `testKey` for companies that don't have a local vault setup. Company id is captured in the encryption context while encrypting.
        - it enrypts data using `testOnPremKey` and `testOnPremKey2` for Company A - because any on-prem vault of Company A (+Cloud Vault) should be able to read this data.

    - Cloud Vault (Decryptor running in cloud) should have access to decrypt the data using any of the 3 keys.
           - Each Decryptor first looks for a key in it's configuration file - if one is found (which will be the case with on-prem); then it uses that
           - If key is not found in config file; then PoC mocks a db lookup 
           - Cloud Vault decrypts the data using all the keys it has access to and checks encryption context for company id; successful result is returned only if company ids match
    
    - OnPrem Vault1 (Decryptor#1 running on prem in Customer's environment) should have access to decrypt the data using only `testOnPremKey`.
               - It reads it's key from the config file
               - For every decryption request; same key is used.
               - Any decryption attempt for paths that belongs to Company A succeeds.
               - Any decryption attempt for paths that don't belong to Company A fails
               
    - OnPrem Vault2 (Decryptor#2 running on prem in Customer's environment) should have access to decrypt the data using only `testOnPremKey2`.
               - It reads it's key from the config file
               - For every decryption request; same key is used.
               - Any decryption attempt for paths that belongs to Company A succeeds.
               - Any decryption attempt for paths that don't belong to Company A fails
               
- Additionally application also uses the Local Caching capabilities of `AWS Encryption Java SDK` to reduce the # of requests being sent to KMS. This is evident from this screenshot of CloudTrail logs:
    [![Screen-Shot-2020-07-17-at-3-16-36-AM.png](https://i.postimg.cc/k5XZzL3H/Screen-Shot-2020-07-17-at-3-16-36-AM.png)](https://postimg.cc/879trKCd)
    For about 50 local invocations; AWS received only 2 requests