	@echo off
echo Iniciando o build dos microsserviços...

cd ms-user
call mvn clean package -DskipTests
cd ..

cd ms-transaction-api
call mvn clean package -DskipTests
cd ..

cd ms-transaction-worker
call mvn clean package -DskipTests
cd ..

echo Build finalizado com sucesso!
echo Subindo os containers com Docker Compose...

docker-compose up --build

pause