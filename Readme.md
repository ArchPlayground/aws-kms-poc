# Introduction

This is a PoC on how to use AWS KMS to securely encrypt and decrpyt data. PoC contains two types of usage:
- using-cli: this demonstrates how to use `aws-cli` to encrypt and decrypt a text file
- spring-boot-app: this demonstrates how a spring boot app can encrypt and decrypt data using AWS KMS

## Local Setup

## cli:
    
    ```
    cd using-cli
    ```
    
    Run `encrypt.sh` to encrypt data in `secret.txt`. This will store the encrypted output in `encrypted-secret.txt` file. This also displays contents of this file on `stdout`
    Run `decrypt.sh` to decrypt data from above step. This will display the decrypted text on `stdout`. The decrypted text should match the contents of `secret.txt`