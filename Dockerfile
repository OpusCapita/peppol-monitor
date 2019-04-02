## using multistage docker build for speed
## temp container to build
FROM ayltai/circleci-openjdk-node:jdk8-node11 AS TEMP_BUILD_IMAGE

ENV APP_HOME=/usr/app/
ENV NODE_ENV=development NODE_PATH=$APP_HOME/node_modules

WORKDIR $APP_HOME

COPY build.gradle settings.gradle gradlew $APP_HOME
COPY gradle $APP_HOME/gradle
COPY . $APP_HOME

# building frontend
RUN sudo npm install && sudo npm cache verify
RUN sudo npx webpack

# building backend
RUN chmod +x ./gradlew
RUN ./gradlew build || return 0

## actual container
FROM openjdk:8
LABEL author="Ibrahim Bilge <Ibrahim.Bilge@opuscapita.com>"

ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

COPY --from=TEMP_BUILD_IMAGE $APP_HOME/build/libs/peppol-monitor.jar .

HEALTHCHECK --interval=15s --timeout=3s --retries=15 \
  CMD curl --silent --fail http://localhost:3041/api/health/check || exit 1

EXPOSE 3041
ENTRYPOINT ["java","-jar","peppol-monitor.jar"]