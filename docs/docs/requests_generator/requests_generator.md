# Requests generator

## About
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
usage: http-requests-generator [-h] [-l LOWER_LIMIT] [-u UPPER_LIMIT] [-m {generate,g,generate-and-save,gs,generate-and-run,gr,load-and-run,lr}] [-o OUTPUT] [-i INPUT] [--csv-output CSV_OUTPUT] url requests_num timespan

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
                        Path to the file where requests will be saved
  -i INPUT, --input INPUT
                        Path to the file where requests are stored
  --csv-output CSV_OUTPUT
                        Path to the file where response time results will be saved
```

### Available modes
There are 3 modes implemented:  
- `g` or `generate` flags should be used if we only want to generate a sample of requests and receive them printed in our CLI  
- `gs` or `generate-and-save` flags should be used if we want to generate a sample of requests and save it into a file without actually running those requests  
- `gr` or `generate-and-run` flags should be used if we want to generate a sample of requests and immediately run them. This is the default behaviour  
- `lr` or `load-and-run` flags should be used if we want to load a previously generated sample of requests and run them (due to this fact, the `url`, `requests_num` and `timestamp` arguments' values are omitted during the script execution)

## User guide

### How to run tests?
Before we run tests we need to have our environment set up. Instructions on how to this are in following sections [`VirtualBox`](../VirtualBox/virtualbox.md), [`IaaS`](../IaaS/iaas.md) and [`PaaS`](../PaaS/paas.md).  
Once the environment is configured we can open up a terminal window in the directory where the request generator is stored and type following command:  
```shell
python3 requests_generator.py <url>/factorize <requests_num> <timespan>
```
Where  
- `<url>` is the URL of the load balancer or a FQDN when we use `PaaS`  
- `<requests_num>` is the approximate number of requests that we want to send. The requests distribution is generated according to [Poisson distribution](https://en.wikipedia.org/wiki/Poisson_distribution) using [`scipy.stats.poisson.rvs`](https://docs.scipy.org/doc/scipy/reference/generated/scipy.stats.poisson.html) method  
- `<timespan` represents the number of units of time in which the requests should be sent  