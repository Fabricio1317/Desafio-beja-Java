# Sistema de Microsservicos para Gestao de Usuarios e Transacoes de Gestao Financeira

Este projeto implementa uma arquitetura de microsservicos voltada para o gerenciamento de usuarios e processamento de transacoes financeiras. A solucao utiliza seguranca baseada em perfis de acesso (RBAC) via tokens JWT e comunicacao assincrona via Kafka.

# Arquitetura do Sistema

O ecossistema e composto por tres servicos principais que se comunicam via rede interna do Docker:

1. ms-user: Responsavel pelo cadastro de usuarios, autenticacao e gestao de perfis (ADMIN e USER). Localizado na porta 8081.
2. ms-transaction-api: Gerencia o recebimento de transacoes financeiras e publica mensagens no Kafka. Localizado na porta 8082.
3. ms-transaction-worker: Escuta o topico do Kafka (listener) para processar e persistir as transacoes de forma assincrona no banco de dados.

# Requisitos Tecnicos

Para a execucao do projeto, e necessario ter instalado:

* Docker e Docker Compose
* Java 17 e Maven 3.8 (para a etapa de build)

# Estrutura de Diretorios

.
├── docker-compose.yml
├── README.md
├── ms-user/
├── ms-transaction-api/
└── ms-transaction-worker/

# Instrucoes de Execucao

Para rodar o ecossistema completo em containers, siga os passos abaixo:

1. Build das Aplicacoes
E necessario gerar os arquivos executaveis (.jar) de cada servico antes de iniciar os containers. Execute os comandos abaixo na raiz do projeto:

cd ms-user && mvn clean package -DskipTests
cd ../ms-transaction-api && mvn clean package -DskipTests
cd ../ms-transaction-worker && mvn clean package -DskipTests
cd ..

2. Inicializacao com Docker Compose
Com o arquivo docker-compose.yml presente na raiz, execute o comando:

docker-compose up --build

# Observacoes Tecnicas Importantes

1. Gestao de Bancos de Dados: O container Postgres cria automaticamente apenas o banco user_db. Apos subir os containers pela primeira vez, e obrigatorio acessar o banco (porta 5433) e executar o comando: CREATE DATABASE transacao_db; para que os servicos de transacao e o worker funcionem corretamente.
2. Segredo JWT: A senha original utilizada e 1@3$5¨7ddaP. No arquivo docker-compose.yml, ela deve ser representada com cifrao duplo ($$) para evitar erros de interpretacao do Docker.
3. Rede Interna: Os servicos estao configurados para se comunicarem pelos nomes dos containers (postgres:5432 e kafka:29092).
4. Persistencia: Foi configurado um volume para o Postgres (postgres_data) para garantir que as informacoes nao sejam perdidas ao encerrar os containers.

# Configuracao de Seguranca e Perfis

O sistema utiliza tokens JWT para autorizacao. Ao realizar o login, o token gerado carrega a permissao do usuario:

* ROLE_USER: Permissao para operacoes comuns, como criacao de transacoes.
* ROLE_ADMIN: Permissao total, incluindo exclusao de usuarios, importacao de dados e acesso a relatorios analiticos.

# Exemplos de Uso (JSON)

Cadastro de novo usuario (POST http://localhost:8081/users)

{
"nome": "User",
"cpf": "24445648991",
"email": "user@email.com",
"senha": "12345",
"role": "USER"
}

Registro de nova transacao (POST http://localhost:8082/transacoes)

{
"usuarioId": 4,
"valor": 200.00,
"tipo": "SAQUE",
"categoria": "CAIXA_ELETRONICO",
"descricao": "Saque para o fim de semana"
}

# Fluxo de Validacao no Insomnia

1. Criar Usuario: Envie o POST para /users definindo a role desejada.
2. Autenticacao: Envie um POST para /auth/login para obter o Bearer Token.
3. Testar Seguranca: No POST de transacao ou na busca de relatorios, adicione o token no cabecalho Authorization. Se tentar acessar um relatorio sendo ROLE_USER, o sistema retornara 403 (Forbidden).

# Comandos de Gerenciamento

Encerrar servicos:
docker-compose down

Visualizar logs em tempo real:
docker-compose logs -f

Reiniciar ambiente limpando dados do banco:
docker-compose down -v && docker-compose up --build
