#образ взятый за основу
FROM openjdk:17-alpine
#Записываем в переменную путь до варника (необязательно)
ARG jarFile=target/authsber-0.0.1-SNAPSHOT.jar
#Куда переместить варник внутри контейнера
WORKDIR /app
#копируем наш джарник в новый внутри контейнера
COPY ${jarFile} authsber.jar
#Открываем порт
EXPOSE 9090
#Команда для запуска
ENTRYPOINT ["java", "-jar", "./authsber.jar"]