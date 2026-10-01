FROM eclipse-temurin:21-jre
# Quiero construir mi imagen partiendo de una imagen que ya tiene Java 21.

WORKDIR /app
# Establece /app como directorio de trabajo dentro del contenedor.

COPY target/finance-backend-0.0.1-SNAPSHOT.jar app.jar
# Copia el .jar de tu computadora hacia la imagen. 

EXPOSE 9000
# Indica que la aplicación dentro del contenedor utiliza el puerto 9000

ENTRYPOINT ["java", "-jar", "app.jar"]
# Cuando arranques, ejecuta este comando. Equivalente a java -jar app.jar
