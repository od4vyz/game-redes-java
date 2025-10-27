package server.jogo;

import java.util.HashMap;
import java.util.Map;
import server.jogo.modelo.*;

public class Engine {
    private Map<String, Jogador> jogadoresConectados;
    public Sala SalaJogador1;
    public Sala proximaSala;
    public Sala SalaJogador2;

    private Mundo mundoLoader;

    public Engine() {
        this.jogadoresConectados = new HashMap<>();

        this.mundoLoader = new Mundo();
        this.mundoLoader.configurarNavegacao();

        this.SalaJogador1 = mundoLoader.getDeposito();
        this.SalaJogador2 = mundoLoader.getGaleria();

        SistemaNotificacao.getInstance().limparEventos();
        EstadoGlobal.getInstance().reset();
    }

    /*-------------MÉTODO GERADO POR IA-------------*/
    public synchronized String conectarJogador(String nome) {
        // Verificar se o nome já está em uso
        if (jogadoresConectados.containsKey(nome)) {
            return "ERRO|O nome '" + nome + "' já está em uso.";
        }

        // Verificar se o jogo está cheio (lógica para 2 jogadores)
        if (jogadoresConectados.size() >= 2) {
            return "ERRO|O servidor está cheio.";
        }

        // Criar o novo objeto Jogador
        Jogador novoJogador = new Jogador(nome);

        // Atribuir a sala inicial correta (Player 1 ou Player 2)
        if (jogadoresConectados.size() == 0) {
            // Este é o primeiro jogador
            System.out.println("Jogador 1 conectado: " + nome);
            novoJogador.setSalaAtual(this.SalaJogador1);
        } else {
            // Este é o segundo jogador
            System.out.println("Jogador 2 conectado: " + nome);
            novoJogador.setSalaAtual(this.SalaJogador2);
        }

        // Adicionar o jogador ao gerenciador
        jogadoresConectados.put(nome, novoJogador);

        // Retornar a primeira descrição da sala (protocolo DESCRICAO)
        return "DESCRICAO|" + novoJogador.getSalaAtual().getDescricaoInicial();
    }

    public Jogador getJogador(String nome) {
        return jogadoresConectados.get(nome);
    }

    public void desconectarJogador(Jogador jogador) {
        if (jogador != null) {
            jogadoresConectados.remove(jogador.getNome());
        }
    }
    /*-----------------------------------------*/

    public String processarComando(Jogador jogador, String comando, String parametros[]) {
        Sala localAtual = jogador.getSalaAtual();

        switch (localAtual.getNome()) {
            /********** LOGICA DO DEPOSITO **********/
            case "DEPOSITO":
                boolean luzAcesa = (boolean) localAtual.getEstado("luzAcesa");
                switch (comando) {
                    case "OLHAR":
                        // se a luz ainda nao foi acesa:
                        if (parametros[0].contains("AMBIENTE")) {
                            return "DESCRICAO|" + localAtual.getDescricaoCompleta();
                        }

                        if (!luzAcesa) {
                            return "DESCRICAO|Está escuro demais para fazer isso.";
                        }

                        // ao olhar a porta:
                        if (parametros[0].contains("PORTA")) {
                            return "DESCRICAO|Se esforçando um pouco, você vê alguns símbolos estranhos: um triângulo, um quadrado e um círculo.\\n";
                            // + "SYSTEM TIP: Para OLHAR mais de perto a fechadura, use: OLHAR fechadura";
                        }

                        // ao analisar a fechadura:
                        if (parametros[0].contains("FECHADURA")) {
                            return "DESCRICAO|A fechadura possui 3 desenhos de animais. Você observa muitos animais em cada uma das voltas, dentre elas um Cachorro, Pato, Elefante, Rinoceronte, Borboleta, Lobo, Dinossauro, Avestruz, Leão, Peixe.\\n[SYSTEM TIP] Para testar uma combinação, digite 'testar ABC', sendo que 'A B C' é a inicial do animal (a ordem importa).";
                        }
                        break;

                    case "USAR":
                        // quando o player aperta o interruptor:
                        if (parametros[0].contains("INTERRUPTOR")) {
                            if (!luzAcesa) {
                                localAtual.setEstado("luzAcesa", true);
                                localAtual.setDescricaoLonga(localAtual.getDescricaoAlternativa("descricaoLuzAcesa"));
                                return "DESCRICAO|Você anda em direção ao interruptor e o pressiona. "
                                        + localAtual.getDescricaoAlternativa("descricaoLuzAcesa");
                            } else {
                                return "DESCRICAO|O interruptor já está pressionado.";
                            }
                        }

                        // ABRIR PORTA (só funciona após resolver o puzzle)
                        if (parametros[0].contains("PORTA")) {
                            boolean portaAberta = (boolean) localAtual.getEstado("portaAberta");
                            if (portaAberta) {
                                return "DESCRICAO|A porta já está aberta! Uma escada está adiante de você.";
                            } else {
                                return "DESCRICAO|A porta está trancada. Preciso resolver o puzzle da fechadura primeiro.";
                            }
                        }
                        break;

                    case "IR":
                        // SUBIR ESCADA (transição para o HALL - Capítulo 2)
                        if (parametros[0].contains("ESCADA")) {
                            boolean portaAberta = (boolean) localAtual.getEstado("portaAberta");
                            if (portaAberta) {
                                proximaSala = localAtual.getSaida("HALL");
                                jogador.setSalaAtual(proximaSala);
                                EstadoGlobal.getInstance().setLocalizacaoJ1(proximaSala.getNome());
                                EstadoGlobal.getInstance().setJogador1NoHall(true);
                                return "DESCRICAO|" + proximaSala.getDescricaoInicial();
                            } else {
                                return "DESCRICAO|Não há escada disponível. Preciso abrir a porta primeiro.";
                            }
                        }
                        break;

                    case "TESTAR":
                        // O PUZZLE!
                        if (parametros.length < 3) {
                            return "ERRO|Formato inválido. Use: TESTAR ABC";
                        }

                        // A solução do puzzle baseada nas pistas
                        if (parametros[0].equalsIgnoreCase("ARP")) {
                            // SUCESSO! Marca o estado global e local
                            EstadoGlobal.getInstance().setPortasAbertas(true);
                            localAtual.setEstado("portaAberta", true);
                            localAtual.setDescricaoLonga(localAtual.getDescricaoAlternativa("descricaoPortaAberta"));

                            return "NARRACAO| Deposito: A porta se abre com um clique alto! Uma ESCADA está adiante de você.\\n Galeria: A galeria é a mesma, porém a PORTA atrás de você se abre automaticamente. Um som de aves e uma leve brisa sai dessa porta.";
                        } else {
                            return "DESCRICAO|Nada acontece. A combinação parece estar errada.";
                        }

                    default:
                        break;
                }
                break;

            /********** LOGICA DA GALERIA **********/
            case "GALERIA":
                boolean portasAbertas = EstadoGlobal.getInstance().isPortasAbertas();
                // System.out.println(comando);
                switch (comando) {
                    case "OLHAR":
                        if (parametros[0].contains("AMBIENTE")) {
                            if (EstadoGlobal.getInstance().isCaldeiraoAtivo()) {
                                return "DESCRICAO|A GALERIA é a mesma porém uma das molduras se abriu como uma porta. Você pode ir para o HALL através da porta secreta.";
                            }
                            return "DESCRICAO|" + localAtual.getDescricaoCompleta();
                        }

                        // OBSERVAR PORTA (apenas para observação, não movimento)
                        if (parametros[0].contains("PORTA")) {
                            if (portasAbertas) {
                                // Atualiza descrição da GALERIA se as portas foram abertas
                                if (!(boolean) localAtual.getEstado("portaAberta")) {
                                    localAtual.setEstado("portaAberta", true);
                                    localAtual.setDescricaoLonga(
                                            localAtual.getDescricaoAlternativa("descricaoPortaAberta"));
                                    return "DESCRICAO|" + localAtual.getDescricaoAlternativa("descricaoPortaAberta");
                                } else {
                                    return "DESCRICAO|A porta atrás de você está aberta. Um som de aves e uma leve brisa saem dela. Você pode ir para a FAZENDA.";
                                }
                            } else {
                                return "DESCRICAO|É uma porta aparentemente normal porém algo a distingue das outras, ela não tem fechadura nem maçaneta se tornando impossível de abrir.";
                            }
                        }

                        if (parametros[0].contains("QUADRO")) {
                            return "DESCRICAO|Você está muito longe para ver os detalhes. Tente 'ir quadro <numero>'.";
                        }
                        break;

                    case "USAR":
                        if (parametros[0].contains("PORTA")) {
                            if (portasAbertas) {
                                return "DESCRICAO|A porta já está aberta! Você pode entrar.";
                            } else {
                                return "DESCRICAO|Impossível abrir a porta, não tem maçaneta.";
                            }
                        }
                        break;

                    case "IR":
                        if (parametros[0].contains("QUADRO")) {
                            String detalhe = localAtual.getDetalhe(parametros[0] + " " + parametros[1]);
                            if (detalhe != null)
                                return "DESCRICAO|" + detalhe;
                            else
                                return "ERRO|Não consigo encontrar esse quadro.";
                        }

                        // ENTRAR/IR PARA A FAZENDA (Capítulo 2)
                        if (parametros[0].contains("PORTA") || parametros[0].contains("FAZENDA")) {
                            if (portasAbertas) {
                                proximaSala = localAtual.getSaida("FAZENDA");
                                jogador.setSalaAtual(proximaSala);
                                EstadoGlobal.getInstance().setLocalizacaoJ2(proximaSala.getNome());
                                EstadoGlobal.getInstance().setJogador2NaFazenda(true);
                                return "DESCRICAO|" + proximaSala.getDescricaoInicial();
                            } else {
                                return "DESCRICAO|A porta ainda não está aberta.";
                            }
                        }

                        // Ir para HALL via porta secreta
                        if (parametros[0].equals("HALL")) {
                            if (EstadoGlobal.getInstance().isCaldeiraoAtivo()) {
                                proximaSala = localAtual.getSaida("HALL");
                                jogador.setSalaAtual(proximaSala);
                                EstadoGlobal.getInstance().setLocalizacaoJ2(proximaSala.getNome());
                                return "DESCRICAO|" + proximaSala.getDescricaoInicial();
                            } else {
                                return "DESCRICAO| Não há passagem disponível para o HALL.";
                            }
                        }
                        break;

                    default:
                        break;
                }
                break;

            /*********** LOGICA DO HALL *********/
            case "HALL":
                switch (comando) {
                    case "OLHAR":
                        if (parametros[0].equals("AMBIENTE")) {
                            if (EstadoGlobal.getInstance().isCaldeiraoAtivo()) {
                                return "DESCRICAO|" + localAtual.getDescricaoCompleta()
                                        + "\\nUma passagem secreta se abriu na parede à esquerda, você pode ir para a 'GALERIA' através dela.";
                            }
                            return "DESCRICAO|" + localAtual.getDescricaoCompleta();
                        }

                        // Observar a porta
                        if (parametros[0].contains("PORTA")) {
                            return "DESCRICAO|A porta está trancada.";
                        }

                        if (!parametros[0].contains("fechadura") && !parametros[0].contains("porta")
                                && EstadoGlobal.getInstance().isCaldeiraoAtivo()) {
                            return "DESCRICAO|" + localAtual.getDescricaoCompleta()
                                    + " Uma passagem secreta se abriu na parede à esquerda, você pode ir para a 'GALERIA' através dela.";
                        }
                        break;

                    case "USAR":
                        // Usar CHAVE na porta
                        if (parametros[0].contains("CHAVE") && parametros[1].contains("PORTA")) {
                            if (!jogador.isTemChave()) {
                                return "ERRO|Você não tem uma CHAVE.";
                            }

                            // Primeira tentativa - porta errada
                            return "NARRACAO|Vocês tentar usar a CHAVE na porta porém ela não encaixa, é estranho pois parece ser exatamente para essa porta, só então você percebe ter colocado na porta errada, eram duas portas, na segunda ela encaixa perfeitamente.\\n\\nFINAL DO JOGO!\\n\\nCom a porta aberta você descobre que estava nO TITANIC NA EAC........";
                        }
                        break;

                    case "IR":
                        if (parametros[0].contains("PORTA")) {
                            return "DESCRICAO|A porta está trancada.";
                        }

                        if (parametros[0].contains("COZINHA")) { //
                            proximaSala = localAtual.getSaida("COZINHA");
                            jogador.setSalaAtual(proximaSala);
                            EstadoGlobal.getInstance().setLocalizacaoJ1(proximaSala.getNome());
                            return "DESCRICAO|" + proximaSala.getDescricaoInicial(); //
                        }

                        if (parametros[0].equals("GALERIA")) {
                            if (EstadoGlobal.getInstance().isCaldeiraoAtivo()) { // Só funciona se caldeirão ativo
                                proximaSala = localAtual.getSaida("GALERIA");
                                jogador.setSalaAtual(proximaSala);
                                return "DESCRICAO|" + proximaSala.getDescricaoInicial();
                            } else {
                                return "ERRO|Não há passagem disponível para a GALERIA.";
                            }
                        }
                        break;

                    default:
                        if (parametros[0].contains("ESCADA") && parametros[1].contains("SUPERIOR")) {
                            return "DESCRICAO|As escadas levam aos andares superiores, mas isso é para outro capítulo.";
                        }
                        break;
                }
                break;

            /********** LOGICA DA COZINHA **********/
            case "COZINHA":
                switch (comando) {
                    case "OLHAR":
                        if (parametros[0].equals("AMBIENTE")) {
                            return "DESCRICAO|" + localAtual.getDescricaoCompleta();
                        }

                        if (parametros[0].contains("LISTA")) {
                            String detalhe = localAtual.getDetalhe("lista de compras");
                            return "DESCRICAO|" + detalhe;
                        }

                        if (parametros[0].contains("GELADEIRA")) {
                            String detalhe = localAtual.getDetalhe("geladeira");
                            return "DESCRICAO|" + detalhe;
                        }
                        break;

                    case "PEGAR":
                        if (parametros[0].contains("PAO")) {
                            String resultado = jogador.pegarPaoMofado();
                            return "DESCRICAO|" + resultado;
                        }
                        break;

                    case "IR":

                        if (parametros[0].equals("HALL")) {
                            proximaSala = localAtual.getSaida("HALL");
                            jogador.setSalaAtual(proximaSala);
                            return "DESCRICAO|" + proximaSala.getDescricaoInicial();
                        }
                        break;

                    default:
                        break;
                }
                break;

            /********** LOGICA DA FAZENDA **********/

            case "FAZENDA":
                if (localAtual instanceof Fazenda) {
                    Fazenda FAZENDA = (Fazenda) localAtual;

                    switch (comando) {
                        case "OLHAR":
                            if (parametros[0].contains("AMBIENTE")) {
                                return "DESCRICAO|" + FAZENDA.observar();
                            }
                            String ingredienteOlhar = parametros[0];
                            if (FAZENDA.ingredienteDisponivel(ingredienteOlhar)) {
                                return "DESCRICAO|" + FAZENDA.observarIngrediente(ingredienteOlhar);
                            }
                            break;

                        case "USAR":
                            // Usar foice para pegar ingredientes
                            if (parametros[0].contains("FOICE")) {

                                if (!jogador.isTemFoice()) {
                                    return "ERRO|Você não tem uma foice. Vá ao moinho para pegar uma.";
                                }

                                String ingrediente = parametros[1];

                                if (!FAZENDA.ingredienteDisponivel(ingrediente)) {
                                    return "ERRO|Não há " + ingrediente + " aqui. Ingredientes disponíveis: " +
                                            String.join(", ", FAZENDA.getIngredientesDisponiveis());
                                }

                                return "DESCRICAO|" + jogador.coletarIngrediente(ingrediente);
                            }
                            break;

                        case "IR":
                            if (parametros[0].contains("GALERIA")) {
                                proximaSala = localAtual.getSaida("GALERIA");
                                jogador.setSalaAtual(proximaSala);
                                EstadoGlobal.getInstance().setLocalizacaoJ2(proximaSala.getNome());
                                return "DESCRICAO|" + proximaSala.getDescricaoInicial();
                            }

                            if (parametros[0].contains("MOINHO")) {
                                proximaSala = localAtual.getSaida("MOINHO");
                                jogador.setSalaAtual(proximaSala);
                                return "DESCRICAO|" + proximaSala.getDescricaoInicial();
                            }
                            break;

                        default:
                            break;
                    }
                }
                break;

            /********** LOGICA DO MOINHO **********/
            case "MOINHO":
                if (localAtual instanceof Moinho) {
                    Moinho moinho = (Moinho) localAtual;

                    switch (comando) {
                        case "OLHAR":
                            if (parametros[0].equals("AMBIENTE")) {
                                return "DESCRICAO|" + moinho.observar();
                            }
                            break; // Cai para comandos básicos

                        case "PEGAR":
                            // Pegar foice
                            if (parametros[0].contains("FOICE")) {
                                String resultadoMoinho = moinho.pegarFoice();
                                if (!resultadoMoinho.contains("já pegou")) {
                                    jogador.pegarFoice();
                                }
                                return "DESCRICAO|" + resultadoMoinho;
                            }

                            // Pegar colher
                            if (parametros[0].contains("COLHER")) {
                                String resultadoMoinho = moinho.pegarColher();
                                if (!resultadoMoinho.contains("já pegou")) {
                                    jogador.pegarColher();
                                }
                                return "DESCRICAO|" + resultadoMoinho;
                            }
                            break;

                        case "USAR":
                            // Jogar pão no caldeirão
                            if (parametros[0].contains("PAO") && parametros[1].contains("CALDEIRAO")) {

                                if (!jogador.isTemPaoMofado()) {
                                    return "ERRO|Você não tem pão mofado.";
                                }

                                String resultado = moinho.jogarPaoNoCaldeirao();

                                if (resultado.contains("chave")) {
                                    if (!jogador.isTemChave()) {
                                        jogador.receberChave();
                                        EstadoGlobal.getInstance().setChaveObtida(true);
                                    }
                                    return "NARRACAO|" + resultado.replace("\\n", "\\n");
                                }
                                return "DESCRICAO|Você já pegou a CHAVE.";
                            }

                            // Colocar ingrediente no caldeirão
                            if (parametros[1].contains("CALDEIRAO")) {

                                // Procurar ingrediente no comando
                                String ingrediente = parametros[0];

                                if (!jogador.temIngrediente(ingrediente)) {
                                    return "ERRO|Você não tem " + ingrediente + ". Ingredientes que você tem: "
                                            + String.join(", ", jogador.getIngredientesPegos());
                                }

                                String resultado = moinho.adicionarIngrediente(ingrediente, mundoLoader);

                                // Verificar se o caldeirão ficou ativo e enviar evento
                                if (moinho.isCaldeiraoAtivo() && !EstadoGlobal.getInstance().isCaldeiraoAtivo()) {
                                    EstadoGlobal.getInstance().setCaldeiraoAtivo(true);
                                    EstadoGlobal.getInstance().setMolduraAberta(true);
                                    // return "NARRACAO|" + resultado.replace("\n", "\\n");

                                    if (jogador.isTemFoice()) {
                                        return "DESCRICAO|Você escuta um barulho na galeria (parece estar vindo da fazenda).";
                                    } else {
                                        return "DESCRICAO|Voce escuta um barulho na sala";
                                    }
                                }

                                return "DESCRICAO|" + resultado.replace("\n", "\\n");
                            }

                            break;

                        case "IR":
                            if (parametros[0].contains("FAZENDA")) {
                                Sala proximaSala = localAtual.getSaida("FAZENDA");
                                jogador.setSalaAtual(proximaSala);
                                return "DESCRICAO|" + proximaSala.getDescricaoInicial();
                            }
                            break;

                        default:
                            break;
                    }
                }
                break;

        }

        // --------------COMANDOS BASICOS---------
        switch (comando) {
            case "INVENTARIO":
                System.out.println(jogador.InventarioString());
                return "DESCRICAO|" + jogador.InventarioString();

            case "OLHAR":
                if (parametros[0].contains("AMBIENTE")) {
                    if (localAtual.getNome().equals("HALL") && EstadoGlobal.getInstance().isCaldeiraoAtivo()) {
                        return "DESCRICAO|" + localAtual.getDescricaoCompleta().replace("\n", "\\n")
                                + "\\nUma passagem secreta se abriu na parede à esquerda, você pode ir para a 'GALERIA' através dela.";
                    }

                    if (localAtual.getNome().equals("GALERIA") && EstadoGlobal.getInstance().isCaldeiraoAtivo()) {
                        return "DESCRICAO|A GALERIA é a mesma porém uma das molduras se abriu como uma porta. Você pode ir para o 'HALL' através da porta secreta.";
                    }

                    return "DESCRICAO|" + localAtual.getDescricaoCompleta().replace("\n", "\\n"); // Retorno padrão,
                                                                                                  // codifica \n
                }

            case "IR":
                // --- NAVEGAÇÃO AUTOMÁTICA ENTRE SALAS VIZINHAS ---
                // Verifica se é um comando de movimento para uma sala vizinha
                // Verificar comandos com múltiplas parametros primeiro
                if (comando.contains("HALL")) {
                    if (localAtual.getSaida("HALL") != null) {
                        if (localAtual.getNome().contains("GALERIA")
                                && !EstadoGlobal.getInstance().isCaldeiraoAtivo()) {
                            return "ERRO|Não há passagem disponível.";
                        }
                    }
                }

            default:
                return "ERRO|Não entendi o que você quis dizer.";
        }
    }
}