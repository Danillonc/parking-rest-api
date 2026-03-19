# ==========================================
# Estágio 1: Build (Compilação)
# ==========================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Passo estratégico para cache: Copia apenas o POM primeiro.
# Se as dependências não mudarem, o Docker usa o cache nesta camada, acelerando builds futuros.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Agora copia o código-fonte e faz o build (pulando testes para a imagem subir rápido,
# assumindo que os testes já rodaram na pipeline de CI)
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================================
# Estágio 2: Run (Imagem Final Leve)
# ==========================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Cria um usuário não-root por questões de segurança (Best Practice K8s/Docker)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copia apenas o JAR gerado no estágio 1 (descarta todo o código fonte e a pasta ~/.m2 do Maven)
COPY --from=builder /app/target/*.jar app.jar

# Expõe a porta padrão do Spring Boot
EXPOSE 8080

# Ponto de entrada otimizado
ENTRYPOINT ["java", "-jar", "app.jar"]