# Sistema de Microsserviços para Gestão de Utilizadores e Transações Financeiras

Este projeto implementa uma arquitetura de microsserviços para o processamento de transações e gestão de perfis de acesso, utilizando Java 17, Spring Boot, Apache Kafka e Docker.

# Arquitetura do Ecossistema

O sistema é dividido em três serviços principais que comunicam de forma síncrona (Feign Client) e assíncrona (Kafka):

1. ms-user: Gestão de credenciais, autenticação JWT e controlo de perfis (ADMIN/USER). Porta 8081.
2. ms-transaction-api: Interface para recebimento de transações e publicação no tópico do Kafka. Porta 8082.
3. ms-transaction-worker: Consumidor que processa as mensagens da fila e persiste os dados no Postgres.

# Requisitos Técnicos

* Java 17 (JDK) e Maven 3.8+
* Docker e Docker Compose
* Ferramenta para testes de API (Insomnia ou Postman)

# Estrutura de Diretórios e Ficheiros

Para o correto funcionamento dos scripts de automação, a estrutura deve ser:

RAIZ DO PROJETO/
├── docker-compose.yml
├── executar_projeto.bat
├── ms-user/
│   ├── Dockerfile
│   └── pom.xml
├── ms-transaction-api/
│   ├── Dockerfile
│   └── pom.xml
└── ms-transaction-worker/
├── Dockerfile
└── pom.xml




# Instruções de Execução

1. Build e Inicialização Automática:
Execute o ficheiro executar_projeto.bat na raiz do projeto. Este script automatiza o mvn clean package em todos os módulos e inicia o docker-compose up --build.
2. Gestão Manual do Banco de Dados (Postgres):
O Docker cria automaticamente apenas o user_db. É obrigatório criar o banco de dados das transações manualmente após a primeira subida dos containers. Utilize os comandos abaixo no terminal:

Criar banco de dados:
docker exec -it postgres createdb -U devuser transacao_db

Consultar transações (Validação do Worker):
docker exec -it postgres psql -U devuser -d transacao_db -c "SELECT * FROM transacoes;"

Consultar utilizadores:
docker exec -it postgres psql -U devuser -d user_db -c "SELECT * FROM usuarios;"

# Segurança e Autenticação

* JWT Secret: Configurado no docker-compose com escape de cifrão ($$) para compatibilidade com o ambiente Docker.
* RBAC (Controlo de Acesso):
* ROLE_USER: Permissão para criar transações.
* ROLE_ADMIN: Permissão para relatórios analíticos e gestão total.



# Exemplos de Chamadas de API

Registo de Utilizador (POST http://localhost:8081/users):
{
"nome": "Utilizador Teste",
"cpf": "12345678901",
"email": "teste@email.com",
"senha": "123",
"role": "USER"
}

Envio de Transação (POST http://localhost:8082/transacoes):
{
"usuarioId": 1,
"valor": 100.0,
"tipo": "SAQUE",
"categoria": "ALIMENTACAO",
"descricao": "Almoço executivo"
}

# Comandos Úteis de Manutenção

* Parar e limpar volumes: docker-compose down -v
* Ver logs em tempo real: docker-compose logs -f
* Rebuild manual: mvn clean package -DskipTests




# Portas OpenAPI

http://localhost:8082/swagger-ui.html

http://localhost:8081/swagger-ui/index.html
