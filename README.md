# jhazelnut-csms-service
This is the main service for the jhazelnut-csms platform.

# How to run

Clone from here, build using gradle (or if you don't have gradle installed you could use the gradle wrapper).
Finally run the .jar generated to start te spring boot embedded web server (aka "tomcat el gato diablo").

    git clone https://github.com/hazafantasy/jhazelnut-csms-service.git
    cd jhazelnut-csms-service
    gradle clean build
    java -jar build/libs/jhazelnut-csms-service-0.1.0.jar

# REST API

These are the endpoints of the REST API

    POST /autosync/{userid}/all
    POST /autosync/{userid}/{driveId1}/{driveId2}