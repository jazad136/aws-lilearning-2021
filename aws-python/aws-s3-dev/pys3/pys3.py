# -*- coding: utf-8 -*-
"""
@author: Jonathan A. Saddler

Remember to enable programmatic access. 
"""
import os
from botocore.exceptions import ClientError
ACCESS_KEY = 'AWS_ACCESS_KEY_ID'
SECRET_KEY = 'AWS_SECRET_ACCESS_KEY'
REGION = 'us-east-2'
PRI_BUCKET_NAME = 'jaslil'
TRANSIENT_BUCKET_NAME = 'jaslil2'
TEMPORARY_BUCKET_NAME = 'jastemp2'
THIRD_BUCKET_NAME = 'jaslilv3'

F1 = 'lil1.txt'
F2 = 'lil2.txt'
F3 = 'lil3.txt'

base = os.path.join(os.path.sep,"C:",os.path.sep, "Users", "jsaddle", "code", "repos", "plain", "AWS_Moley_exercise2")
DIR = os.path.join(base, 'aws-s3-dev', 's3')
DOWN_DIR = os.path.join(base, 'aws-s3-dev', 's3-alt')


def upload_file(bucket, directory, file, s3, s3path=None):
    file_path = os.path.join(directory, file)
    
    remote_path = s3path
    if remote_path is None:
        remote_path = file
    try:
        s3.Bucket(bucket).upload_file(file_path, remote_path)
    except ClientError as ce:
        print('error', ce)
        
def download_file(bucket, directory, local_name, key_name, s3):
    '''

    Parameters
    ----------
    bucket : str
        bucket name
    directory : str
        location where to put file on local file system
    local_name : str
        file to name on file system
    key_name : str
        file to find
    s3 : a boto3 S3 resource
        s3 object to use to call the download file function

    '''
    
    file_path = os.path.join(directory, local_name)
    try: 
        s3.Bucket(bucket).download_file(key_name, file_path)
    except ClientError as ce:
        print('error', ce)
    
def delete_files(bucket, keys, s3):
    objects = []
    for key in keys:
        objects.append({'Key': key})
    try:
        s3.Bucket(bucket).delete_objects(Delete={'Objects':objects})
    except ClientError as ce:
        print('error', ce)

def list_objects(bucket, s3): 
    try: 
        response = s3.meta.client.list_objects(Bucket=bucket)
        objects = []
        for content in response['Contents']:
            objects.append(content['Key'])
        print(bucket, 'contains', len(objects), 'files')
        return objects
    
    except ClientError as ce:
        print('error', ce)
        return []

def copy_file(source_bucket, destination_bucket, source_key, destination_key, s3):
    try: 
        source = {
            'Bucket': source_bucket,
            'Key': source_key
        }
        s3.Bucket(destination_bucket).copy(source, destination_key)
        
    except ClientError as ce:
        print('error', ce)

def prevent_public_access(bucket, s3):
    try:
        s3.meta.client.put_public_access_block(Bucket=bucket,
            PublicAccessBlockConfiguration={
                'BlockPublicAcls': True,
                'IgnorePublicAcls': True,
                'BlockPublicPolicy': True,
                'RestrictPublicBuckets': True
            });
    except ClientError as ce:
        pass

def create_bucket(name, s3, secure=False):
    try:
        location = {'LocationConstraint': REGION}
        s3.create_bucket(Bucket=name, CreateBucketConfiguration=location)
        if secure:
            prevent_public_access(name, s3)
    except ClientError as ce:
        print('error', ce)

def generate_download_link(bucket, key, expiration_seconds, s3):
    try:
        response = s3.meta.client.generate_presigned_url('get_object', Params = {
            'Bucket': bucket,
            'Key': key
        }, ExpiresIn=expiration_seconds)
        print(response)
    except ClientError as ce:
        print('error', ce)
    
def delete_bucket(bucket, s3):
    try:
        s3.Bucket(bucket).delete()
    except ClientError as ce: 
        print('error', ce)
        
def main():
    """entry point"""
    '''
    Create a temporary bucket
    Upload a file
    Copy a file from an existing bucket
    Get the files and print them
    Download a file
    Generate a presigned URL
    Delete the files
    Delete the bucket
    '''
    import boto3
    # might not be a good idea to upload these to git... 
    # storing these elsewhere now...
    access = os.getenv(ACCESS_KEY) 
    secret = os.getenv(SECRET_KEY)
    s3 = boto3.resource('s3', 
                        aws_access_key_id=access,
                        aws_secret_access_key=secret)
    
    #Create a temporary bucket
    create_bucket(TEMPORARY_BUCKET_NAME, s3)
    #Upload a file
    upload_file(TEMPORARY_BUCKET_NAME, DIR, F1, s3)
    #Copy a file
    copy_file(PRI_BUCKET_NAME, TEMPORARY_BUCKET_NAME, F2, F2, s3)
    #Get the files
    print(list_objects(TEMPORARY_BUCKET_NAME, s3))
    
    #Download a file
    download_file(TEMPORARY_BUCKET_NAME, DOWN_DIR, F1, F1, s3)
    #Generate presigned URL
    generate_download_link(TEMPORARY_BUCKET_NAME, F1, 300, s3)
    #Delete the files
    delete_files(TEMPORARY_BUCKET_NAME, [F1, F2], s3)
    #Delete the bucket
    delete_bucket(TEMPORARY_BUCKET_NAME, s3)
    '''
    What should happen?
    Bucket jastemp should be created on my s3 instance. 
    Bucket jastemp contains file F1 (it reads "This is file 1")
    Bucket jastemp contains file F2 (it reads "This is file 2")
    The contents of jastemp should be printed to contain F1 and F2.
    The file F1 should be downloaded to s3-alt
    A presigned URL should be printed to the console, and we should be able to use it to download F1
    The files F1 and F2 should not exist in jastemp
    The bucket jastemp should no longer exist. 
    '''
if __name__ == '__main__':
    main()