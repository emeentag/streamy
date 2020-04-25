## Streamy-Cloud Based Data Streamer

In this project we tried to create a data stream cloud. We aim to process batch data and realtime data together in the project.

### Technologies that we used:

#### Backend:

- **Springboot:** We used Java Springboot basic projects for our microservices. All microservices that we have inherited with springboot.
- **Kafka:** As a message broker we used Kafka inorder to get connected each service.
- **Spark:** We used Spark data processing for batch and realtime data.
- **Hadoop:** We used Hadoop to store the bulk data.
- **Hive:** We used Hive inorder to store structered data.
- **Vagrant:** We combine several services in a vagrant virtualization env. For this setup thanks to [link martinprobson github project.](https://github.com/martinprobson/vagrant-hadoop-hive-spark)

#### Frontend:

- We used React with Material UI inorder to develop our simple frontend project.

### How to use:

Use following orphan branches for accessing each app we create.

- cloud: Contains vagrant envs. for the services.
- base: Essential springboot project for a microservice. It contains several dependencies for creating the projects.
- app: Simple web app for frontend serving and initiate the data processing.

Thanks.
