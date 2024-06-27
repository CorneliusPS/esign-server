FROM khipu/openjdk17-alpine

WORKDIR /app

COPY target/finalproject.jar /app/finalproject.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "finalproject.jar"]