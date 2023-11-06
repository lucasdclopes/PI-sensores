# PI-sensores (Este documento é um TODO, não está completo)

## Sobre

Este projeto é do PI (Projeto Integrador ) do curso de Engenheria de Computação da Univesp. O principal tema é a utilização e integração de sensores IoT

Este repositório contém o backend. O frontend pode ser encontrado no outro repositório: [https://github.com/lucasdclopes/PI-Analise-Dados-Frontend](https://github.com/lucasdclopes/PI-sensores-Frontend)

O backend é feito em Java 17, utiliza o Framework Jakarta EE10 e o servidor Wildfly 30.0.0

## Requisitos

Para executar este projeto você precisa do OpenJDK 17. O sistema foi testado com a build da Azul x86 64 bits: https://www.azul.com/downloads/?version=java-17-lts&architecture=x86-64-bit&package=jdk#zulu

O banco de dados utilizado é o Microsoft SQL Server 2022 (16.0.1050.5).

A IDE utilizada é o Eclipse, mas pode-se trabalhar com a IDE de sua preferência

## Banco de dados

Os arquivos para criar o banco de dados estão no root do repositório.

`criar_estrutura.sql` cria a estrutura(schema) do banco de dados: campos, tabelas, chaves e índices 

Note que o banco de dados não é criado automáticamente. É necessário utilizar os scripts acima. Para gerenciar o SQL Server, recomanda-se utilizar o SQL Server Managment Studio: https://learn.microsoft.com/en-us/sql/ssms/download-sql-server-management-studio-ssms?view=sql-server-ver16

## Acesso ao banco de dados

Os dados de acesso ao banco de dados estão no arquivo `/src/main/resources/application.properties`

## Executando

É necessário realizar o deploy em um servidor Wildfly 30. Recomendo consultar a documentação deste servidor. https://docs.wildfly.org/30/


## Créditos

Este é um projeto feito em grupo, foi necessário elaborar o tema, realizar reuniões, escolher e montar os sensores de hardware, montar a estrutura do banco de dados, desenhar a UX, desenvolver o front e backend e realizar testes. Além da extensa e trabalhosa documentação escrita, que não está inclusa neste repositório.

Integrantes **está incompleto**:

**Bruno de Freitas**

**Fabio**

**Lucas de Carvalho Lopes**

**Lucas Goss Dias**

**Luiz**

