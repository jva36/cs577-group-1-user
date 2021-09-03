#SE577 Group 1 ~ Microservices: User

# Train Sim

An application to simulate purchasing train tickets for SE577 Group Homework Assignment 2.

These 2 calls are provided for user:
1) http://localhost:8002/api/user?email=email@xxx.com for registered user
2) http://localhost:8002/api/user?email=email@xxx.com&type=Guest for guest

## Getting Started

The following tools are required to build and run this project: Docker, Docker Compose, Maven, Java 11, and npm.

From the root of the project run:

```
mvn clean install
docker-compose up (this command may need to be executed twice when creating database)
```

### trainsim-user-db

This is a PostgreSQL database. The database contents are stored in the `data/` directory. Every script in the `scripts/` directory will be run on startup if the database doesn't exist yet. During development, the easiest way to make a change to delete `data/` and modify `scripts/000-init.sql`.

### trainsim-user-service

This is a Java web server. It uses trainsim-db and trainsim-planner to answer requests from the frontend. It is where most of the use cases will be implemented. We use a few small libraries to implement the server. Check `trainsim-api/pom.xml` for details.

## Development

You can start the project at any time with `docker-compose up`.

To apply changes you have made to the backend, you could stop all services (with Ctrl + C) and start docker-compose again, but it may be easier to open a new shell and restart just the `trainsim-api` service by running `docker-compose restart trainsim-api`.

If you make any changes to the frontend, simply run `npm run build` to apply your changes. As an alternative to `npm run build`, you can use `npm run watch` (in a different shell from `docker-compose up`.) This will cause webpack to rebuild the frontend every time a file is saved. This can make for a more fluid development experience.

### Debugging

These directions are for Visual Studio Code but other IDEs should be similar.

#### Debugging trainsim-user-service

Because the application code is running in a docker container, it is slightly more invovled to debug the application. Modify the `MAVEN_OTPS` environment variable inside the `docker-compose.yml` file. You should see:

```
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:1044
```

Change the `suspend=n` to `suspend=y`. This tells the Java runtime to pause until a remote debugger is attached. At runtime, it will wait for a connection on port `1044`. In VS Code add the following to your [launch.json](https://code.visualstudio.com/docs/editor/debugging):

```
{
    "type": "java",
    "name": "Attach to 'trainsim-api'",
    "request": "attach",
    "hostName": "localhost",
    "port": "1044",
    "sourcePaths": [
        "trainsim-api/"
    ]
}
```

Now set any breakpoints you would like and start the application with `docker-compose up` and attach the debugger by going to the "Run and Debug" tab of VS Code and running the "Attach to 'trainsim-api'" task.

### Inspect Database

If you would like to use pgAdmin to inspect the database manually, you can run:

```
docker run --network="trainsim_default" -p 8080:80 -e "PGADMIN_DEFAULT_EMAIL=me@example.org" -e "PGADMIN_DEFAULT_PASSWORD=password" dpage/pgadmin4
```

Then navigate to http://localhost:8080/ and login with user "me@example.org" and password "password". Then right click "Servers" and select "Create > Server...". Enter "trainsim-db" as the name and then in the "Connection" tab, enter "trainsim-db", "5432", "user", and "password" as the Host, Port, Username and Password respectively. (I have had issues with this in the past. Contact the TAs if you cannot get it working.)