FROM gcr.io/distroless/java17
ENV JAVA_TOOL_OPTIONS -XX:+ExitOnOutOfMemoryError
ENV APPNAME=fint-kontroll-access-management-catalog-
ENV TZ=UTC

WORKDIR /data

COPY /home/gradle/build/libs/$APPNAME*.jar /data/app.jar

CMD ["/data/app.jar"]
