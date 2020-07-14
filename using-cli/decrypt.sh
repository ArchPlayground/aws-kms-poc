source ./kms_constants.sh

echo "Using Key ID: $KEY_ID"
echo "Using Encryption context: $ENCRYPTION_CONTEXT"


echo "Decrypting contents of : $OUTPUT_BLOB"

aws kms decrypt --ciphertext-blob fileb://$OUTPUT_BLOB --encryption-context $ENCRYPTION_CONTEXT --query Plaintext --output text | base64 --decode
