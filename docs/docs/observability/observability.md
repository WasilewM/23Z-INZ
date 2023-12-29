# Observability

## About
The observability is handled by the [`Prometheus`](https://prometheus.io/docs/introduction/overview/) and [`Grafana`](https://grafana.com/) tools. `Prometheus` scrapes logs from defined endpoints (`spring_boot_actuator` in the example below):
```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

rule_files:

scrape_configs:
  - job_name: 'prometheus'
    static_configs:
      - targets: ['127.0.0.1:9090']

  - job_name: 'spring_boot_actuator'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
    static_configs:
      - targets: ['%app_url%']
        labels:
          instance: 'server_app'
```
`Grafana` reads the metrics stored in prometheus:
```yaml
apiVersion: 1

datasources:
  - name: Prometheus
    type: prometheus
    url: '%prometheus_url%'
    isDefault: true
    editable: true
```
And makes it easy to visualize them in its UI.

## User guide
!!! Note
    In `Iaas` and `PaaS` deployment models a VM with the observability stack is configured automatically.

### How to use it?
The easiest way to start using the `Promethues` and `Grafana` is by running them in containers. In `src/main/observability` of the repository a `Dockerfile` can be found:
```yaml
version: "3"
services:
  prometheus:
    image: prom/prometheus
    ports:
      - "9090:9090"
    restart: unless-stopped
    volumes:
      - ./prometheus/prometheus.yaml:/etc/prometheus/prometheus.yml
      - prometheus_data:/prometheus
    command:
      - "--config.file=/etc/prometheus/prometheus.yml"
    depends_on:
      - grafana
  grafana:
    image: grafana/grafana
    ports:
      - "3000:3000"
    restart: unless-stopped
    volumes:
      - ./grafana:/etc/grafana/provisioning
volumes:
  prometheus_data:
```
It can be used to start up the `Prometheus` and `Grafana` containers, but before we do that we need to update config files for `Prometheus` and `Grafana`.

#### How to configure Prometheus?
`Prometheus` config file can be found at `src/main/observability/prometheus/prometheus.yaml` path in the repository:
```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

rule_files:

scrape_configs:
  - job_name: 'prometheus'
    static_configs:
      - targets: ['127.0.0.1:9090']

  - job_name: 'spring_boot_actuator'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
    static_configs:
      - targets: ['%app_url%']
        labels:
          instance: 'server_app'
```
We need to replace the `%app_url%` with the actual IP address and port of the test service. In `IaaS` model it will be some IP address that Microsoft Azure has assigned to our VM. In `PaaS` model it will be a [FQDN](https://pl.wikipedia.org/wiki/Fully_Qualified_Domain_Name) like:
```
paas-spring-apps-svc-paas-spring-server-app.azuremicroservices.io
```

#### How to configure Grafana?
`Grafana` config files can be found at `src/main/observability/grafana` directory. The structure of this directory is presented below:
```
.
├── dashboards
│   ├── dashboards_provisioning.yaml
│   └── performance_dashboard.json
└── datasources
    └── datasources.yaml
```

The `dashboards` directory contains an exemplary dashboard in the `performance_dashboard.json` file and the `dashboards_provisioning.yaml` file, which configures the `Grafana` container to load provided dashboards:
```yaml
apiVersion: 1

providers:
  - name: dashboards
    type: file
    updateIntervalSeconds: 30
    options:
      path: /etc/grafana/provisioning/dashboards
      foldersFromFilesStructure: true
```
Instructions on how to create new dashboard can be found [here](https://grafana.com/docs/grafana/latest/dashboards/).  
The second directory, `datasources`, contains file `datasources.yaml` which configures `Grafana` to read data from `Prometheus` instance:
```yaml
apiVersion: 1

datasources:
  - name: Prometheus
    type: prometheus
    url: '%prometheus_url%'
    isDefault: true
    editable: true
```
Here we need to replace the `%prometheus_url%` with the IP address of the VM that we have create in Azure (or `on premise` in `VirtualBox`).

### How to run containers?
When we finish updating the configuration files, we need to change our directory it terminal to be located in `src/main/observability/` directory of the repository. Now we can use following command to start up the containers:
```shell
docker compose up -d
```
The flag `-d` is used to run the containers in [`detached mode`](https://docs.docker.com/engine/reference/commandline/compose_up/#options).