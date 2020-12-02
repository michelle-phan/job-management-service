import json, requests
from datetime import datetime, timedelta
from airflow.models import DAG
from airflow.operators.python_operator import PythonOperator

HEADERS = {'Content-Type': 'application/json'}
HOST = "<LIVY_HOST_NAME>"
DEFAULT_ARGS = {
    'owner': 'Airflow',
    'email': ['<EMAIL>'],
    'start_date': datetime(<START_DATE>),
    'retries': <RETRIES>,
    'retry_delay': timedelta(minutes=5),
    'pool': 'default_pool'
}


def send_request_livy(
    host,
    class_name,
    file_to_run,
    list_args,
    executor_core,
    driver_memory
):
    data = {
        'className': class_name,
        'file':file_to_run,
        'args': list_args,
        'driverMemory': driver_memory,
        'executorCores': executor_core
    }
    data_json = json.dumps(data)
    response = requests.post(
        host + '/batches',
        data=data_json,
        headers=HEADERS
    )

    try:
        response.raise_for_status()
    except requests.exceptions.HTTPError:
        print("[-_-] Data is invalid, please check and try again !!")
        raise ValueError("Data is invalid")

    return response.json()['id']

with DAG(
    '<DAG_NAME>',
    schedule_interval='<SCHEDULE_INTERVAL>',  # define base on crontab guru
    catchup=True,
    default_args=DEFAULT_ARGS
) as dag:
    PythonOperator(
        task_id='<TASK_ID_NAME>',
        python_callable=send_request_livy,
        op_kwargs = {
            'host': HOST,
            'class_name': '<CLASS_NAME>',
            'file_to_run': '<PATH>',
            'list_args': [<ARGS>],
            'executor_core': <EXECUTOR_CORE>,
            'driver_memory': '<DRIVER_MEMORY>'
        }
    )