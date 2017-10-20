# osb-service-broker-example
## Description

An empty Cloud Foundry Service Broker missing concrete implementation of a distinct service.   
Supports deployment to OpenStack.  
Uses MongoDB Database for management.   
Configuration files and deployment scripts must be added.  
Concrete Service logic and binding logic has to be added.  


## Start with this example
1. Clone it.
2. Build it. `mvn clean install`
3. Provide a valid configuration. 
4. Run it or push it to Cloud Foundry.

##### Example Configuratuion


    spring:
      ### Profile ###
      profiles: defaut
    
    ### Persistence ###
    #### MongoDB ####
      data:
        mongodb:
          host: $host
          port: 27017
          database: $authDatabase
          username: $user
          password: $password
    
    ### Deployment ###
    #### Existing MongoDB Server/Cluster ####
    existing:
      endpoint:
        hosts: 
          - 127.0.0.1
        port: 11111
        database: foo
        username: bar
        password: rol
    
    ### Service Key Generation ###
    #### HAProxy ####
    haproxy:
      uri: $haporxyUrl
      auth:
        token: $haProxyAuthToken
    
    ### Login Information ### 
    login:
      username: $authUser
      password: $authPassword
      role: USER
    
    ## OpenStack Settings ## (OPTIONAL)
    #openstack:
    #  endpoint: 
    #  user:
    #    username: 
    #    password: 
    #    domainName: 
    #  project:
    #    domainName: 
    #    projectName: 
    #  networkId:
    #  subnetId: 
    #  imageId: 
    #  keypair: 
    #  cinder:
    #    az: zone00
    
    catalog:
      services:
        - id: sample-local
          name: Sample-local
          description: Sample Instances
          bindable: true
          dashboard: 
            url: $endpoint_uri
            auth_endpoint: $uaa_uri
          dashboard_client:
            id: sample
            secret: sample
            redirect_uri: $endpoint_uri/dashboard/manage
          plans:
            - id: sample_s_local
              name: S
              description: A simple sample Local plan.
              free: false
              volumeSize: 25
              volumeUnit: M
              platform: EXISTING_SERVICE
              connections: 4



## Start Custom Implementation
To implement custom service creation behaviour implement a Service that inherits from `ExistingServiceFactory` or `OpenstackPlatformService` or 
start from scratch with a new CPI by Implementing the `PlatformService`.   
To manipulate the service binding behaviour you can inherit from `BindingService` or `BindingServiceImpl`.
For the full framework implementation see the documentaion in [evoila/osb-service-broker](https://github.com/evoila/osb-service-broker)


  
