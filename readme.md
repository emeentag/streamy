# Streamy-Cloud Based Data Streamer

In this project we tried to create a data stream cloud. We aim to process batch data and realtime data together in the project.

## Technologies that we used:

#### Backend:

- **Springboot:** We used Java Springboot basic projects for our microservices. All microservices that we have inherited with springboot.
- **Kafka:** As a message broker we used Kafka inorder to get connected each service.
- **Spark:** We used Spark data processing for batch and realtime data.
- **Hadoop:** We used Hadoop to store the bulk data.
- **Hive:** We used Hive inorder to store structered data.
- **Vagrant:** We combine several services in a vagrant virtualization env. For this setup thanks to [martinprobson github project.](https://github.com/martinprobson/vagrant-hadoop-hive-spark)

#### Frontend:

- We used React with Material UI inorder to develop our simple frontend project.

## How to use:

Use following packages for accessing each resource we create.

- [cloud:](https://github.com/emeentag/streamy/tree/master/cloud) Contains vagrant envs. for the services.
- [batchProcessor:](https://github.com/emeentag/streamy/tree/master/batchProcessor) Service for batch data processing.
- [realtimeProcessor:](https://github.com/emeentag/streamy/tree/master/realtimeProcessor) Service for realtime data processing.
- [app:](https://github.com/emeentag/streamy/tree/master/app) Simple web app for frontend serving and orchestration of other services.

Thanks.
