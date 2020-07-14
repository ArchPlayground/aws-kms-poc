source ./kms_constants.sh

echo "Using Key ID: $KEY_ID"
echo "Using Encryption context: $ENCRYPTION_CONTEXT"

echo "Encrypting : $SECRET_BLOB"

aws kms encrypt --key-id $KEY_ID --plaintext $SECRET_BLOB --encryption-context $ENCRYPTION_CONTEXT --output text --query CiphertextBlob | base64 --decode >$OUTPUT_BLOB

echo "Encryption complete. Encrypted output stored in $OUTPUT_BLOB"

echo "Encrypted content:"
echo ""
echo ""
echo ""
cat $OUTPUT_BLOB
