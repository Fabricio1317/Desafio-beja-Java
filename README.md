
# Sistema de Microsservicos para Gestao de Usuarios e Transacoes de Gestão Financeira
Este projeto implementa uma arquitetura de microsservicos voltada para o gerenciamento de usuarios e processamento de transacoes financeiras. A solucao utiliza seguranca baseada em perfis de acesso (RBAC) via tokens JWT para garantir a integridade das operacoes.

# Arquitetura do Sistema

O ecossistema e composto por dois servicos principais que se comunicam via rede interna do Docker:

1. ms-user: Responsavel pelo cadastro de usuarios, autenticacao e gestao de perfis (ADMIN e USER). Localizado na porta 8081.
2. ms-transaction-api: Gerencia transacoes financeiras e gera relatorios de analise restritos a administradores. Localizado na porta 8082.

# Requisitos Tecnicos

Para a execucao do projeto, e necessario ter instalado:

* Docker
* Docker Compose
* Java 17 e Maven 3.8 (para a etapa de build)

# Instrucoes de Execucao

Para rodar o ecossistema completo em containers, siga os passos abaixo:

# 1. Build das Aplicacoes

E necessario gerar os arquivos executaveis de cada servico antes de iniciar os containers:

# 2. Inicializacao com Docker Compose

Na raiz do projeto, onde se encontra o arquivo docker-compose.yml, execute:

docker-compose up --build

# Configuracao de Seguranca e Perfis

O sistema utiliza tokens JWT para autorizacao. Ao realizar o login, o token gerado carrega a permissao do usuario.

# Niveis de Acesso

* ROLE_USER: Permissao para operacoes comuns, como criacao de transacoes.
* ROLE_ADMIN: Permissao total, incluindo exclusao de usuarios, importacao de dados e acesso a relatorios analiticos.

# Exemplos de Uso

# Fluxo de Validacao no Insomnia

1. Criar Usuario Admin: Envie um POST para /users com "role": "ADMIN" no corpo do JSON.
2. Autenticacao: Envie um POST para /auth/login com as credenciais para obter o token.
3. Acesso Protegido: Utilize o token no cabeçalho Authorization como Bearer Token para acessar rotas restritas no ms-transaction.

# Endpoints de Referencia

Cadastro de novo usuario
Este JSON deve ser enviado via POST para o ms-user (porta 8081).

POST http://localhost:8081/users

{ 
  "nome": "User",
  "cpf": "24445648991",
  "email": "user@email.com",
  "senha": "12345",
  "role": "USER"
}


Como testar a seguranca
Envie o POST de usuario para criar sua conta.

Realize o login para obter o Bearer Token.

Copie o token da resposta.

No POST de transacao, adicione o token no cabeçalho Authorization do Insomnia.

Se voce tentar enviar a transacao sem o token ou com um token expirado, o sistema retornara erro 401 (Unauthorized).
Se tentar acessar um relatorio sendo ROLE_USER, o sistema retornara 403 (Forbidden).

Observacao tecnica: O campo role deve ser sempre enviado em letras maiusculas (USER ou ADMIN) para que o Java consiga converter para o Enum corretamente.
O CPF deve conter apenas os 11 digitos numericos para evitar erros de validacao.

Registro de nova transacao
Este JSON deve ser enviado via POST para o ms-transaction-api (porta 8082).

{ 
  "usuarioId": 4,
  "valor": 200.00,
  "tipo": "SAQUE",
  "categoria": "CAIXA_ELETRONICO",
  "descricao": "Saque para o fim de semana"
}

Observacao tecnica: Certifique-se de que o usuarioId enviado ja existe no banco de dados do ms-user. 
O campo valor e um decimal e os campos tipo e categoria devem seguir exatamente os nomes definidos nos Enums do seu projeto de transacoes.

GET http://localhost:8082/relatorios/analise/1
Requer header Authorization: Bearer {token_admin}




# Comandos de Gerenciamento

Encerrar servicos:
docker-compose down

Visualizar logs em tempo real:
docker-compose logs -f

Reiniciar ambiente limpando dados do banco:
docker-compose down -v && docker-compose up --build
