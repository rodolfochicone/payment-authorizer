# Payment Authorizer

Este README fornece instruções para configurar, executar e testar o projeto.

## Pré-requisitos

Certifique-se de ter instalado:

  * JDK 21 ou superior
  * Kotlin
  * Gradle
  * Docker e Docker Compose

## Configuração do Projeto

1. Clone o repositório:

~~~github
git clone https://github.com/rodolfochicone/payment-authorizer.git
cd payment-authorizer
~~~

## Executando o Redis com Docker Compose

1. Navegue até o diretório do projeto onde está o arquivo docker-compose.yml.
2. Execute o seguinte comando para iniciar o Redis:

~~~docker
docker-compose up -d redis
~~~

## Compilando e Executando o Projeto

1. Limpando e Compilando o projeto
~~~gradle
./gradlew clean build
~~~

2. Execute a aplicação
~~~gradle
./gradlew bootRun
~~~

A aplicação estará disponível em http://localhost:8080 (ou na porta configurada).

## Executando os Testes

Para rodar os testes unitários e de integração:
~~~gradle
./gradlew test
~~~