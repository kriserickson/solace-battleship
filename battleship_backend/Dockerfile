FROM openjdk:12-alpine

# create app directories
RUN mkdir -p /usr/app/env
RUN mkdir -p /usr/app

# copy source code
COPY target/battleship_backend-1.0-SNAPSHOT.jar /usr/app/

# set container's base dir
WORKDIR /usr/app

# run the container as a non-root user for best security practices
RUN addgroup -S app
RUN adduser -S -D -h /usr/app appuser app
RUN chown -R appuser:app /usr/app
USER appuser

# start the app
EXPOSE 3000
CMD [ "java", "-cp", "battleship_backend-1.0-SNAPSHOT.jar", "com.solace.battleship_backend.App"]
