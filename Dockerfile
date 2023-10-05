FROM gcr.io/distroless/java17
ENV JAVA_TOOL_OPTIONS -XX:+ExitOnOutOfMemoryError
ENV TZ=UTC

COPY /home/gradle/build/libs/fint-kontroll-access-management-catalog-*.jar /data/app.jar

CMD ["/data/app.jar"]
