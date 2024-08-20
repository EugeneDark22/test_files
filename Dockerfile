# Використання офіційного образу OpenJDK як базового образу
FROM openjdk:17-jdk-slim

# Додати аргумент для версії JAR
ARG JAR_FILE=target/*.jar

# Створення директорії для збереження файлів
RUN mkdir -p /app/files

# Копіювати JAR файл у контейнер
COPY ${JAR_FILE} app.jar

# Виставити порт 8080
EXPOSE 8080

# Запустити додаток
ENTRYPOINT ["java", "-jar", "/app.jar"]
