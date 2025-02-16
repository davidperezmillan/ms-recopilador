# Usar una imagen de base de OpenJDK con una versión JDK apropiada
FROM openjdk:17-jdk-alpine
LABEL authors="david"

# Argumento para pasar el nombre del archivo JAR (esto lo hace genérico)
ARG JAR_FILE=target/ms-black-0.0.1-SNAPSHOT.jar

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo JAR generado en el contenedor
COPY ${JAR_FILE} app.jar

# Comando para ejecutar la aplicación
ENTRYPOINT ["java","-jar","/app/app.jar"]
