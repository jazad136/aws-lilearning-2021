import json
import uuid
import boto3
import os
import json
import uuid
import boto3
import os
def create_quote(event, context):
    tableName = os.getenv('TABLE_NAME')
    dynamodb = boto3.client('dynamodb')
    
    row = {
        'id': uuid.uuid1().hex,
        'Name': event['name']
    }
    dynamodb.put_item(TableName=tableName, Item={'id':{'S':uuid.uuid1().hex},'name':{'S':event['name']}})
    return {
        'statusCode': 200,
        'body': json.dumps(str('Put [' + event['name'] + ']. Line 12'))
    }
