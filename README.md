# TaskAPI — Session e Layers

## 1. Tecnologias e dependências

O projeto utiliza as seguintes tecnologias e versões:

| Tecnologia | Versão |
|---|---|
| Java | 17 ou superior |
| Apache Tomcat | 11.0.4 |
| Jakarta Servlet | 6.1 |
| MySQL Connector/J | 9.1.0 |
| Gson | 2.11.0 |

As bibliotecas externas são utilizadas por meio de arquivos `.jar`, armazenados localmente em `src/main/webapp/WEB-INF/lib`.

Esses arquivos não são incluídos no versionamento do Git, sendo necessário disponibilizá-los no ambiente de execução.

## 2. Configuração do banco de dados

A TaskAPI utiliza o banco MySQL `taskdb`, responsável pelo armazenamento dos usuários e de suas respectivas tarefas.

Para preparar o banco, estão disponíveis dois scripts SQL:

- **`schema.sql`:** contém a estrutura completa do banco, indicada para instalações novas.
- **`migracao-03.sql`:** realiza as modificações necessárias sobre o banco utilizado na Tarefa Prática 02, preservando a estrutura existente e adaptando-a aos novos requisitos.

### 2.1. Execução da migração

Ao executar o script de migração pelo MySQL Workbench, pode ocorrer o erro `1175`, relacionado ao recurso de proteção contra atualizações sem condições consideradas seguras.

Caso isso aconteça, execute o comando abaixo antes da migração, na mesma sessão do MySQL:

```sql
SET SQL_SAFE_UPDATES = 0;
```

Após essa configuração, execute o conteúdo de `migracao-03.sql`.

### 2.2. Configuração da conexão

Os parâmetros de acesso ao banco podem ser definidos por variáveis de ambiente ou propriedades da JVM, utilizando os seguintes identificadores:

| Parâmetro | Finalidade | Valor padrão |
|---|---|---|
| `TASKDB_URL` | Endereço de conexão JDBC | `jdbc:mysql://localhost:3307/taskdb` |
| `TASKDB_USER` | Usuário do MySQL | `root` |
| `TASKDB_PASS` | Senha de acesso | Vazia |

Essas configurações permitem adaptar a aplicação a diferentes ambientes sem modificar diretamente o código-fonte.

### 3.3. Contas utilizadas nos testes

O banco disponibiliza dois usuários fictícios para verificar a autenticação e as restrições de acesso.

| Identificação | E-mail | Senha |
|---|---|---|
| Usuário A — Isabella | `isabella@taskapi.test` | `isa123` |
| Usuário B — Douglas | `douglas@taskapi.test` | `douglas123` |

As credenciais são utilizadas exclusivamente para testes.

Embora as senhas sejam apresentadas nesta documentação para facilitar a execução dos exemplos, o banco não armazena seus valores diretamente. O armazenamento utiliza PBKDF2 com HMAC-SHA256 e salt, permitindo verificar as credenciais sem manter a senha original na tabela de usuários.

## 4. Organização da aplicação

A estrutura do projeto foi organizada para distribuir as responsabilidades entre diferentes camadas.

```text
src/main/java/br/edu/ifsp/web/
├── model/
│   ├── Tarefa.java
│   └── Usuario.java
├── dao/
│   ├── ConnectionFactory.java
│   ├── TarefaDAO.java
│   └── UsuarioDAO.java
├── service/
├── command/
├── servlet/
├── filter/
├── listener/
└── cli/
    └── ImportadorConclusoes.java
```

Cada pacote possui uma finalidade específica:

- **model:** representa as entidades e concentra comportamentos relacionados ao domínio.
- **dao:** realiza a comunicação com o banco de dados e executa os comandos SQL.
- **service:** disponibiliza as operações de negócio, aplica validações e controla as transações.
- **command:** interpreta as operações solicitadas à API e encaminha as chamadas aos serviços.
- **servlet:** recebe as requisições HTTP e coordena seu processamento.
- **filter:** executa verificações anteriores ao processamento das requisições.
- **listener:** acompanha eventos relacionados ao ciclo de vida da aplicação e das sessões.
- **cli:** contém o cliente responsável por executar operações sem utilizar o servidor Web.

Essa separação evita que regras de negócio fiquem vinculadas exclusivamente aos controladores HTTP.

## 5. Importação de conclusões pela linha de comando

A aplicação disponibiliza a classe `ImportadorConclusoes`, que permite concluir tarefas a partir de um arquivo de texto.

Diferentemente das requisições realizadas pela API, o importador é executado diretamente pela JVM e não depende de uma sessão HTTP ou da execução do Tomcat.

### 5.1. Preparação do arquivo

Crie um arquivo chamado `ids.txt`, contendo um identificador de tarefa em cada linha.

Exemplo:

```text
4
1
7
99
```

Cada identificador será processado individualmente.

### 5.2. Compilação

Para compilar as classes Java, utilize:

```bash
javac --release 17 -d out -cp "<tomcat>/lib/servlet-api.jar;src/main/webapp/WEB-INF/lib/gson-2.11.0.jar" $(find src/main/java -name "*.java")
```

Substitua `<tomcat>` pelo diretório da instalação do Apache Tomcat.

A opção `--release 17` define a compatibilidade do código compilado com Java 17, evitando problemas quando o compilador instalado utiliza uma versão mais recente.

O comando apresentado utiliza a sintaxe de expansão de arquivos do Bash. Caso a compilação seja realizada pelo PowerShell, será necessário adaptar essa parte do comando.

No Windows, o separador dos elementos do classpath é `;`. Ao utilizar Git Bash, prefira caminhos no formato `C:/...` para a instalação do Tomcat.

### 5.3. Execução

Com as classes compiladas, execute:

```bash
java -cp "out;src/main/webapp/WEB-INF/lib/mysql-connector-j-9.1.0.jar" br.edu.ifsp.web.cli.ImportadorConclusoes ids.txt ana@taskapi.test ana123
```

O programa recebe três argumentos:

1. Caminho do arquivo que contém os identificadores.
2. E-mail do usuário responsável pelas operações.
3. Senha correspondente ao usuário informado.

Antes de iniciar o processamento, o importador verifica as credenciais utilizando o serviço de autenticação.

Se os dados de acesso estiverem incorretos, o programa apresenta uma mensagem de erro e encerra sua execução com código de saída `1`.

### 5.4. Processamento das tarefas

Para cada identificador informado, o programa apresenta um resultado indicando a situação encontrada.

Os resultados possíveis são:

- `concluida`: a tarefa foi localizada e concluída.
- `nao encontrada`: o identificador não corresponde a uma tarefa acessível ao usuário.
- `ja estava concluida`: a tarefa já havia sido finalizada anteriormente.

Uma falha no processamento de determinada linha não impede que os próximos identificadores sejam analisados.

A conclusão é realizada pela mesma operação da Service Layer utilizada pelo endpoint `PATCH /tarefas/{id}`, garantindo que as regras sejam aplicadas de maneira uniforme.

## 6. Testes de funcionamento da API

Os testes seguem a sequência definida no item 09 da especificação, contemplando autenticação, isolamento de dados, validações, sessões e execução do importador.

O endereço utilizado para acessar a aplicação é:

```text
http://localhost:8080/aula-taskapi
```

### 6.1. Exemplos de requisições

Os exemplos abaixo utilizam `curl` em um terminal Bash, mantendo os cookies no arquivo `c.txt`.

**Acessar tarefas sem autenticação:**

```bash
curl -i http://localhost:8080/aula-taskapi/tarefas
```

**Tentar autenticar com uma senha incorreta:**

```bash
curl -i -c c.txt -b c.txt \
  -H "Content-Type: application/json" \
  -d '{"email":"isabella@taskapi.test","senha":"errada"}' \
  http://localhost:8080/aula-taskapi/auth/login
```

**Autenticar com uma conta válida:**

```bash
curl -i -c c.txt -b c.txt \
  -H "Content-Type: application/json" \
  -d '{"email":"isabella@taskapi.test","senha":"isa123"}' \
  http://localhost:8080/aula-taskapi/auth/login
```

**Consultar as tarefas do usuário autenticado:**

```bash
curl -i -b c.txt http://localhost:8080/aula-taskapi/tarefas
```

O arquivo `c.txt` mantém os cookies recebidos do servidor, permitindo utilizar a sessão nas requisições seguintes.

### 6.2. Registro dos resultados

A tabela apresenta os resultados informados para as onze etapas de verificação.

| Etapa | Verificação realizada | Resultado registrado |
|---|---|---|
| 1 | Consultar tarefas sem autenticação | HTTP 401, com JSON `{"erro":"Usuario nao autenticado."}` |
| 2 | Realizar login com senha incorreta | HTTP 401, retornando `{"erro":"E-mail ou senha invalidos."}` |
| 3 | Autenticar o usuário Ana | HTTP 200, com ID, nome e e-mail, sem informações de senha. O cabeçalho contém `Set-Cookie: JSESSIONID=...; HttpOnly` |
| 4 | Consultar as tarefas após o login | HTTP 200, retornando as tarefas 1, 2, 4 e 5, pertencentes ao usuário de ID 1 |
| 5 | Acessar uma tarefa de outro usuário | HTTP 404 nas operações GET, PUT, PATCH e DELETE, com a mensagem `{"erro":"Tarefa 6 nao encontrada."}` |
| 6 | Criar uma tarefa sem informar título | HTTP 400, com `{"erro":"Titulo obrigatorio."}` |
| 7 | Cadastrar uma tarefa válida | HTTP 201, retornando a tarefa criada com ID 9, proprietário 1 e estado inicial pendente |
| 8 | Concluir a mesma tarefa duas vezes | Primeira chamada: HTTP 204. Segunda chamada: HTTP 409, com `{"erro":"Tarefa ja concluida."}` |
| 9 | Consultar os dados da sessão atual | HTTP 200, com os dados da Ana e `sessoesAtivas: 1` |
| 10 | Encerrar a sessão e tentar acessar tarefas | Logout com HTTP 204; tentativa posterior de acesso com HTTP 401 |
| 11 | Executar o importador com diferentes IDs | Tarefa 4 concluída; tarefa 1 já concluída; tarefas 7 e 99 não encontradas |

Na etapa 11, o processamento foi realizado com as credenciais da Ana, utilizando os identificadores `4`, `1`, `7` e `99`.

O ID 7 corresponde a uma tarefa pertencente ao Bruno, enquanto o ID 99 não corresponde a uma tarefa existente. Ambos são tratados como recursos não encontrados para o usuário autenticado.

Os registros acima devem ser conferidos no ambiente de execução antes da entrega definitiva, principalmente se o banco de dados tiver sido modificado após a realização dos testes.

## 7. Evidências de execução

Os registros visuais da aplicação devem ser armazenados no diretório `docs/`, conforme solicitado na atividade.

### 7.1. Sessões e tempo de processamento

Arquivo: `docs/console-sessoes-tempo.png`

A imagem deve demonstrar as mensagens emitidas pelo listener durante a criação e destruição de sessões, juntamente com os registros produzidos pelo filtro responsável pela medição do tempo das requisições.

[Visualizar registro do console](docs/console-sessoes-tempo.png)

### 7.2. Execução do importador

Arquivo: `docs/importador-etapa11.png`

A captura deve apresentar a execução do importador e os resultados individuais dos identificadores utilizados na última etapa de verificação.

[Visualizar execução do importador](docs/importador-etapa11.png)

## 8. Relatório técnico

### 8.1. Como uma tarefa é concluída pela API e pelo importador? Onde os fluxos se encontram?

Embora a API e o importador utilizem formas diferentes de entrada, ambos executam a conclusão de tarefas por meio da mesma operação de negócio.

No caso da API, uma requisição `PATCH /tarefas/{id}` passa inicialmente pelos filtros configurados na aplicação. O `FiltroTempoDeExecucao` acompanha o processamento, enquanto o `FiltroAutenticacao` verifica se existe um usuário autenticado na sessão. O `FiltroContentType` também participa da cadeia de filtros.

Após essas verificações, o `FrontController` identifica a operação solicitada e encaminha o processamento ao `ConcluirTarefaCommand`.

O Command interpreta o identificador presente na URL, obtém a identificação do usuário autenticado e solicita a conclusão por meio do contrato `TarefaService`, utilizando o método `concluir(usuario, id)`.

A implementação `TarefaServiceImpl` inicia a transação, consulta a tarefa por meio do `TarefaDAO` e verifica se ela pertence ao usuário informado. Em seguida, utiliza o método `Tarefa.concluir()` para aplicar a regra de domínio e persiste a alteração no MySQL.

Quando a operação termina corretamente, a API responde com HTTP 204. Caso a tarefa não seja encontrada ou já esteja concluída, as exceções são convertidas pelo `FrontController` nos códigos HTTP 404 e 409, respectivamente. Falhas internas são representadas por HTTP 500.

No importador, o processo começa no método `main`, que recebe as credenciais e realiza a autenticação utilizando `AutenticacaoService.autenticar`.

Depois, o programa percorre o arquivo de entrada e, para cada identificador, executa `TarefaService.concluir(usuario, id)`.

Portanto, **os dois fluxos passam a compartilhar o mesmo processamento a partir da chamada ao serviço de conclusão**. A diferença está apenas na maneira como cada cliente recebe os dados e apresenta os resultados.

### 8.2. Como foram organizadas as conexões e transações? Qual foi o destino do FiltroConexao?

O gerenciamento das conexões foi transferido para a camada de serviços, pois essa responsabilidade não deve depender da existência de uma requisição HTTP.

Na implementação atual, as operações utilizam `Transacao.executar`, responsável por solicitar uma conexão à `ConnectionFactory` e controlar sua utilização durante a execução da operação.

Antes de modificar os dados, o auto-commit é desabilitado. Se todas as etapas forem concluídas corretamente, a transação é confirmada por meio de `commit`. Caso aconteça alguma falha, é realizado um `rollback`, impedindo que alterações incompletas sejam mantidas no banco.

Independentemente do resultado, a conexão é encerrada ao final do processamento.

Problemas relacionados ao JDBC são convertidos em `FalhaInternaException`, evitando que os clientes precisem conhecer ou tratar diretamente exceções SQL.

O antigo `FiltroConexao` deixou de ser necessário porque sua responsabilidade estava associada ao ciclo de vida das requisições Web. Essa abordagem não atenderia ao importador, que funciona sem passar pelos filtros do servidor.

Ao centralizar o gerenciamento transacional na Service Layer, tanto a API quanto o cliente de linha de comando passam a utilizar a mesma estratégia de persistência.

Essa organização também evita a abertura de conexões para requisições bloqueadas pelo filtro de autenticação.

### 8.3. Por que os serviços recebem o usuário como argumento em vez de consultar a sessão HTTP?

A identificação do usuário é recebida como parâmetro para manter a camada de serviços independente da tecnologia utilizada pelo cliente.

Uma sessão HTTP é um mecanismo específico de aplicações Web. Se a Service Layer precisasse consultá-la diretamente, suas operações ficariam vinculadas ao ambiente Servlet.

Isso impediria que o importador executasse as mesmas funcionalidades de maneira independente, pois uma aplicação de linha de comando não possui `HttpServletRequest` ou `HttpSession`.

Além disso, a dependência direta do HTTP dificultaria a realização de testes unitários e reduziria a possibilidade de reutilização dos serviços em outros tipos de aplicação.

Na arquitetura adotada, cada cliente é responsável por identificar o usuário de acordo com seu próprio funcionamento.

A API recupera o identificador armazenado na sessão, enquanto o importador obtém o usuário após validar as credenciais recebidas por argumento.

Em ambos os casos, o serviço recebe explicitamente o usuário responsável pela operação e aplica as regras de acesso correspondentes.

Dessa forma, a Service Layer permanece reutilizável e não precisa importar classes de `jakarta.servlet`.

### 8.4. Qual é a finalidade de alterar o identificador da sessão no login? Por que retornar 404 para tarefas de outros usuários?

A alteração do identificador da sessão após uma autenticação bem-sucedida é uma medida de segurança contra ataques de *session fixation*.

Nesse tipo de ataque, um identificador de sessão conhecido anteriormente poderia ser utilizado indevidamente depois que a vítima realizasse o login.

Ao executar `changeSessionId`, a aplicação substitui o identificador anterior, impedindo que ele continue sendo utilizado como referência para a sessão autenticada.

Já a escolha do código HTTP 404 para tarefas pertencentes a outros usuários está relacionada à proteção das informações armazenadas.

Uma resposta HTTP 403 indicaria que determinado recurso existe, mas que o usuário não possui autorização para acessá-lo.

Embora essa resposta seja válida em diversos contextos, ela poderia revelar a existência de tarefas pertencentes a outras pessoas.

Por esse motivo, a TaskAPI utiliza HTTP 404 tanto para identificadores inexistentes quanto para tarefas que não pertencem ao usuário autenticado.

Essa regra é reforçada na consulta ao banco, que considera o identificador da tarefa e o identificador de seu proprietário.

Assim, uma tarefa de outro usuário é tratada como inexistente para quem está realizando a solicitação.

### 8.5. A Service Layer era indispensável na Tarefa 02? O que motivou sua utilização agora? Por que foi escolhido um Page Controller para autenticação?

Na Tarefa Prática 02, a aplicação apresentava uma estrutura relativamente simples, voltada exclusivamente ao atendimento de requisições HTTP.

As funcionalidades eram próximas das operações tradicionais de cadastro, consulta, atualização e exclusão. Nesse cenário, os Commands conseguiam concentrar o processamento sem que a ausência de uma camada de serviços representasse um problema imediato.

Conforme discutido por Martin Fowler, a Service Layer não precisa ser introduzida obrigatoriamente em todas as aplicações. Sua utilização depende das necessidades e da complexidade do sistema.

Na Tarefa 03, entretanto, a aplicação passou a apresentar novas responsabilidades.

A autenticação de usuários, o controle de propriedade das tarefas e o gerenciamento de transações exigiram regras que não deveriam ficar espalhadas pelos controladores.

Além disso, a introdução do importador criou a necessidade de disponibilizar as mesmas operações para um segundo cliente, independente do protocolo HTTP.

Sem uma Service Layer, seria necessário repetir parte das validações e regras de acesso no importador, aumentando a duplicação de código e dificultando a manutenção.

A nova camada estabelece uma interface comum para as operações de negócio, enquanto os clientes permanecem responsáveis apenas pela comunicação e apresentação dos resultados.

Para as rotas de autenticação, foi adotado um **Page Controller**, implementado pela classe `AuthController`.

Essa escolha foi motivada pelo número reduzido e bem definido de funcionalidades: login, logout e consulta do usuário autenticado.

Diferentemente das operações de tarefas, que possuem diferentes métodos HTTP e identificadores dinâmicos, o gerenciamento da autenticação apresenta um fluxo mais específico.

Por isso, um controlador dedicado torna mais simples acompanhar a criação da sessão, a substituição de seu identificador e sua invalidação.

Mesmo utilizando um Page Controller, a autenticação continua sendo realizada por meio de `AutenticacaoService`, sem acesso direto ao DAO ou à linguagem SQL.

## 9. Considerações finais

A refatoração da TaskAPI permitiu separar com maior clareza as responsabilidades de comunicação HTTP, processamento das regras de negócio e persistência de dados.

O gerenciamento de sessões acrescentou identificação de usuários e controle de acesso, enquanto a Service Layer possibilitou compartilhar funcionalidades entre a API e o importador de linha de comando.

Outro resultado importante foi a centralização das transações e validações, reduzindo a dependência entre componentes e facilitando futuras alterações.

Com essas mudanças, a aplicação passa a apresentar uma arquitetura mais organizada, com maior reutilização de código e melhor separação entre as diferentes camadas do sistema.
