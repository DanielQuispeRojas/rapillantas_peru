# Rapillantas API en host/simulacion (Render/Hostinger/AWS o docker local).
# Espejo del Dockerfile de MOODU core: compilar con Maven y correr solo con JRE.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /data
COPY --from=build /app/target/rapillantas-api-1.0.0.jar /app/app.jar
EXPOSE 8080
# El perfil prod va DESPUES de -jar (argumento del programa, no -D).
# En simulacion local se puede forzar ddl-auto=update por env
# (SPRING_JPA_HIBERNATE_DDL_AUTO) porque las migraciones db/ aun no cubren
# el retiro del mini ERP; en el deploy real va validate + migracion V35.
CMD ["java", "-jar", "/app/app.jar", "--spring.profiles.active=prod"]
