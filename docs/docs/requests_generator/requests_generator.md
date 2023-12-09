# Requests generator

HTTP requests generator can be downloaded from [this repository](https://github.com/WasilewM/http-requests-generator.git).  

!!! Note
    Before using the script please we need to make sure that we have `python3` and `pip3` installed as well as all required modules. Running:  
    ```shell
    pip3 install -r requirements
    ```
    installs all required modules.

The requests generator has CLI implemented with [argparse](https://docs.python.org/3/library/argparse.html) module. In order to check available options we can run:  
```shell
python3 requests_generator.py -h
```
We should receive following output:  
```shell
usage: http-requests-generator [-h] [-l LOWER_LIMIT] [-u UPPER_LIMIT] [-m {generate,g,generate-and-save,gs,generate-and-run,gr,load-and-run,lr}] [-o OUTPUT] [-i INPUT] url requests_num timespan

Generates http requests for given URL with random numbers appended at the end of the URL

positional arguments:
  url                   URL to your service. Remember that random integers will be added at the end of the URL request
  requests_num          Average number of requests you want to send over the timespan
  timespan              Timespan during which the requests have to be sent

options:
  -h, --help            show this help message and exit
  -l LOWER_LIMIT, --lower_limit LOWER_LIMIT
                        Lower limit of integer values that will be randomly added to the URL
  -u UPPER_LIMIT, --upper_limit UPPER_LIMIT
                        Lower limit of integer values that will be randomly added to the URL
  -m {generate,g,generate-and-save,gs,generate-and-run,gr,load-and-run,lr}, --mode {generate,g,generate-and-save,gs,generate-and-run,gr,load-and-run,lr}
                        Mode which should be executed
  -o OUTPUT, --output OUTPUT
                        Path to file where requests will be saved
  -i INPUT, --input INPUT
                        Path to file where requests are stored
```