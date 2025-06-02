<h1 align="center"> Simulador de Votação 🗳️ </h1>

<p align="center">
  <img src="https://img.shields.io/badge/Language-Java-orange"/>
  <img src="https://img.shields.io/badge/Framework-Swing-blue"/>
  <img src="https://img.shields.io/badge/Chart_Library-JFreeChart-green"/>
</p>

## Visão Geral

Este projeto implementa um **Simulador de Votação Online** utilizando **Java Swing** para a interface gráfica. Embora os requisitos originais mencionassem a construção de uma "API", esta implementação foca em fornecer todas as funcionalidades de um sistema de votação através de uma aplicação desktop interativa, mantendo os dados em memória durante a execução.

O sistema permite aos usuários criar enquetes com múltiplas opções, votar nessas opções, e visualizar os resultados em tempo real, incluindo uma representação gráfica. Um mecanismo simples de identificação do participante é usado para prevenir votos múltiplos na mesma enquete.

## Funcionalidades Implementadas ✨

* **Criação de Enquetes:**
    * Definir um título para a enquete.
    * Adicionar múltiplas opções de voto.
* **Votação:**
    * Usuários (simulados pela inserção de um nome) podem votar em uma das opções disponíveis em uma enquete ativa.
* **Visualização de Resultados:**
    * Listagem de todas as enquetes criadas.
    * Exibição dos resultados de cada enquete, incluindo:
        * Número de votos por opção.
        * Porcentagem de votos por opção.
        * Total de votos na enquete.
        * Gráfico de pizza (utilizando JFreeChart) para visualização da distribuição dos votos.
    * Exibição dos detalhes de quem votou em qual opção.
* **Controle de Votos:**
    * Impede que um mesmo "participante" (identificado por nome) vote mais de uma vez na mesma enquete.
* **Gerenciamento de Enquetes:**
    * Opção de "Encerrar Votação", que congela a enquete e apenas exibe os resultados.
    * Opção de "Editar Enquete" (antes de ser encerrada), permitindo modificar o título e as opções (esta ação reseta os votos existentes da enquete).
    * Geração de uma enquete de teste para demonstração rápida.
* **Interface Gráfica:**
    * Interface intuitiva construída com Java Swing.
    * Janelas modais para criação, votação e visualização de resultados.
    * Atualização dinâmica dos resultados parciais durante a votação.

## Conformidade com os Requisitos Avaliativos 📜

| Critério                     | Atendimento                                                                                                                                                                                                                                                           |
| :--------------------------- | :-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Funcionamento da lógica de votos (30%)** | A classe `Enquete` gerencia toda a lógica de adição de votos, contagem, e recuperação de resultados. A votação é armazenada em memória.                                                                                                                      |
| **Controle de múltiplos votos (20%)** | A classe `Enquete` utiliza um `Set<String>` (`participantesQueVotaram`) para rastrear os nomes dos participantes que já votaram, impedindo votos duplicados na mesma enquete. Uma mensagem de erro é exibida caso haja tentativa de voto múltiplo.                |
| **Testes unitários (20%)** | **Não implementado.** Esta é uma área para desenvolvimento futuro. Recomenda-se o uso de JUnit para testar a lógica da classe `Enquete` (adição de votos, prevenção de duplicados, cálculo de resultados).                                                              |
| **Organização do código (20%)** | O código está organizado em classes com responsabilidades definidas: `Enquete` (lógica de negócio e dados), `PainelCriarEnquete` (GUI para criação/edição), e `SystemVotoSimula` (janela principal e orquestração). Embora não siga a estrutura `controller/service/model` de uma API web, há uma separação clara de preocupações entre a lógica de dados e a apresentação (GUI). |
| **Apresentação/documentação (10%)** | Este README.md serve como a documentação principal do projeto.                                                                                                                                                                                                         |

## Tecnologias Utilizadas 🛠️

* **Java:** Linguagem de programação principal.
* **Swing:** Para a construção da interface gráfica do usuário (GUI).
* **JFreeChart:** Biblioteca para a criação de gráficos (especificamente, gráficos de pizza para os resultados das enquetes). (Nota: É necessário ter as bibliotecas JFreeChart e JCommon no classpath).

## Estrutura do Projeto (Simplificada) 📂

O código fonte está contido no pacote `src`:

src/
├── Enquete.java             // Modela uma enquete, sua lógica de votos e resultados.
├── PainelCriarEnquete.java  // JPanel para a interface de criação e edição de enquetes.
└── SystemVotoSimula.java    // Classe principal JFrame, orquestra a aplicação.

Embora a estrutura de pacotes `com.example.votingsystem` com `controller`, `service`, `model`, `dto`, `util` seja comum para APIs web, esta aplicação desktop adota uma estrutura mais simples, focada nas classes de GUI e na classe de lógica de enquete.

## Como Executar 🚀

1.  **Pré-requisitos:**
    * JDK 8 ou superior instalado.
    * Bibliotecas JFreeChart (`jfreechart-x.y.z.jar`) e JCommon (`jcommon-x.y.z.jar`). Você pode baixá-las do site oficial do JFreeChart.

2.  **Compilação:**
    Coloque os arquivos JAR do JFreeChart e JCommon em um diretório (por exemplo, `lib/`).
    Navegue até o diretório que contém a pasta `src` e execute:
    ```bash
    javac -cp ".:lib/*" src/SystemVotoSimula.java src/Enquete.java src/PainelCriarEnquete.java
    # ou, se estiver dentro da pasta src e os jars em ../lib
    # javac -cp ".:../lib/*" *.java
    ```
    (Ajuste `lib/*` conforme a localização dos seus JARs).

3.  **Execução:**
    Ainda no diretório que contém `src` (ou `src` se tornou `classes` após a compilação):
    ```bash
    java -cp ".:lib/*:src" src.SystemVotoSimula
    # ou, se compilou para uma pasta 'classes' e os jars estão em 'lib'
    # java -cp ".:lib/*:classes" src.SystemVotoSimula
    ```

    Alternativamente, importe o projeto em uma IDE como Eclipse, IntelliJ IDEA ou NetBeans, adicione os JARs do JFreeChart ao build path do projeto e execute a classe `SystemVotoSimula`.

## Interface do Usuário 🎨

A aplicação apresenta uma janela principal onde as enquetes são listadas. Botões permitem:
* **"Teste":** Cria rapidamente uma enquete de exemplo.
* **"Enquete":** Abre uma nova janela para criar uma enquete personalizada.

Cada enquete listada possui botões para:
* **"Visualizar" / "Visualizar Resultados":** Abre uma janela com os resultados tabulados, um gráfico de pizza e detalhes dos votos. Permite encerrar a votação ou editar a enquete (se não estiver encerrada).
* **"Iniciar Votação":** Abre uma janela para que os participantes registrem seus votos. Mostra resultados parciais em tempo real.

## Próximos Passos / Melhorias Futuras 📈

* **Testes Unitários:** Implementar testes com JUnit para a classe `Enquete`.
* **Persistência de Dados:** Atualmente, as enquetes e votos são perdidos ao fechar a aplicação. Implementar salvamento e carregamento de dados (ex: arquivos JSON, XML ou banco de dados leve como SQLite).
* **Autenticação de Usuário (Opcional):** Embora o requisito seja "sem autenticação", para uma versão mais robusta, um sistema simples de login poderia ser adicionado.
* **Melhorias na UI/UX:** Refinamentos visuais e de usabilidade.
* **Internacionalização:** Suporte a múltiplos idiomas.
