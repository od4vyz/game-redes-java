package server.jogo;

import java.util.HashMap;
import java.util.Map;
import server.jogo.modelo.*;

public class Engine {
    private Map<String, Jogador> jogadoresConectados;
    public Sala SalaJogador1;
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

    public synchronized void desconectarJogador(Jogador jogador) {
        if (jogador != null) {
            jogadoresConectados.remove(jogador.getNome());
        }
    }
    /*-----------------------------------------*/

    public String processarComando(Jogador jogador, String comando, String parametros[]) {
        Sala localAtual = jogador.getSalaAtual();

        switch (localAtual.getNome()) {
            /**********LOGICA DO DEPOSITO**********/
            case "Depósito":
                boolean luzAcesa = (boolean) localAtual.getEstado("luzAcesa");
                switch (comando) {
                    case "OLHAR":
                        //se a luz ainda nao foi acesa:
                        if (parametros[0].contains("AMBIENTE")) {
                            return "DESCRICAO|" + localAtual.getDescricaoCompleta();
                        }

                        if (!luzAcesa) {
                            return "DESCRICAO|Está escuro demais para fazer isso.";
                        }

                        //ao olhar a porta:
                        if (parametros[0].contains("PORTA")) {
                            return "DESCRICAO|Se esforçando um pouco, você vê alguns símbolos estranhos: um triângulo, um quadrado e um círculo.\\n";
                                    // + "SYSTEM TIP: Para OLHAR mais de perto a fechadura, use: OLHAR fechadura";
                        }

                        //ao analisar a fechadura:
                        if (parametros[0].contains("FECHADURA")) {
                            return "DESCRICAO|A fechadura possui 3 desenhos de animais. Você observa muitos animais em cada uma das voltas, dentre elas um Cachorro, Pato, Elefante, Rinoceronte, Borboleta, Lobo, Dinossauro, Avestruz, Leão, Peixe.\\n[SYSTEM TIP] Para testar uma combinação, digite 'testar A B C', sendo que 'A B C' é a inicial do animal (a ordem importa).";
                        }
                        break;

                    case "USAR":
                        //quando o player aperta o interruptor:
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
                        // SUBIR ESCADA (transição para o Hall - Capítulo 2)
                        if (parametros[0].contains("ESCADAS")) {
                            boolean portaAberta = (boolean) localAtual.getEstado("portaAberta");
                            Sala proximaSala = localAtual.getSaida("hall");
                            if (portaAberta) {
                                jogador.setSalaAtual(proximaSala);
                                EstadoGlobal.getInstance().setJogador1NoHall(true);
                                return "DESCRICAO|" + proximaSala.getDescricaoInicial();
                            } else {
                                return "DESCRICAO|Não há escada disponível. Preciso abrir a porta primeiro.";
                            }
                        }

                        // // NAVEGAÇÃO LIVRE DO DEPÓSITO (quando porta estiver aberta)
                        // if ((boolean) localAtual.getEstado("portaAberta")) {
                        //     // Se a porta estiver aberta, permitir navegação automática
                        //     for (String palavra : parametros) {
                        //         if (localAtual.getSaida(palavra) != null) {
                        //             if (palavra.contains("hall"))
                        //                 return "TRANSICAO_HALL";
                        //         }
                        //     }
                        // }
                        break;
                    
                    case "TESTAR":
                        // O PUZZLE!
                        if (parametros.length < 3){
                            return "ERRO|Formato inválido. Use: TESTAR A B C";
                        }

                        // A solução do puzzle baseada nas pistas
                        if (parametros[0].equalsIgnoreCase("A") && parametros[1].equalsIgnoreCase("R")
                                && parametros[2].equalsIgnoreCase("P")) {
                            // SUCESSO! Marca o estado global e local
                            EstadoGlobal.getInstance().setPortasAbertas(true);
                            localAtual.setEstado("portaAberta", true);
                            localAtual.setDescricaoLonga(localAtual.getDescricaoAlternativa("descricaoPortaAberta"));
                            
                            return "NARRACAO|A porta se abre com um clique alto! Uma escada está adiante de você.";
                        } else {
                            return "DESCRICAO|Nada acontece. A combinação parece estar errada.";
                        }
                
                    default:
                        break;
                }
                break;

            /**********LOGICA DA GALERIA**********/
            case "Galeria":
                boolean portasAbertas = EstadoGlobal.getInstance().isPortasAbertas();
                switch (comando) {
                    case "OLHAR":
                        if (parametros[0].contains("AMBIENTE")) {
                            if (EstadoGlobal.getInstance().isCaldeiraoAtivo()) {
                                return "DESCRICAO|A galeria é a mesma porém uma das molduras se abriu como uma porta. Você pode ir para a 'casa principal' (Hall) através da porta secreta.";
                            }
                            return "DESCRICAO|" + localAtual.getDescricaoCompleta(); 
                        }

                        // OBSERVAR PORTA (apenas para observação, não movimento)
                        if (parametros[0].contains("PORTA")) {
                            if (portasAbertas) {
                                // Atualiza descrição da galeria se as portas foram abertas
                                if (!(boolean) localAtual.getEstado("portaAberta")) {
                                    localAtual.setEstado("portaAberta", true);
                                    localAtual.setDescricaoLonga(localAtual.getDescricaoAlternativa("descricaoPortaAberta"));
                                    return "DESCRICAO|" + localAtual.getDescricaoAlternativa("descricaoPortaAberta");
                                } else {
                                    return "DESCRICAO|A porta atrás de você está aberta. Um som de aves e uma leve brisa saem dela. Você pode ir para a fazenda.";
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
                        if (parametros[0].contains("QUADRO") && parametros.length > 1) {
                            String numeroQuadro = parametros[1]; // O número é o segundo parâmetro
                                String detalhe = localAtual.getDetalhe("quadro " + numeroQuadro);
                                return (detalhe != null) ? "DESCRICAO|" + detalhe : "ERRO|Não consigo encontrar esse quadro.";
                        }

                        // ENTRAR/IR PARA A FAZENDA (Capítulo 2)
                        if (parametros[0].contains("PORTA") || parametros[0].contains("FAZENDA")) {
                            if (portasAbertas) {
                                Sala proximSala = localAtual.getSaida("fazenda");
                                jogador.setSalaAtual(proximSala);
                                EstadoGlobal.getInstance().setJogador2NaFazenda(true);
                                return "DESCRICAO|" + proximSala.getDescricaoInicial();
                            } else {
                                return "DESCRICAO|A porta ainda não está aberta.";
                            }
                        }

                        // Ir para Hall via porta secreta
                        if (parametros[0].equals("HALL")) {
                            if (EstadoGlobal.getInstance().isCaldeiraoAtivo()) { // Só funciona se caldeirão ativo
                                Sala proximaSala = localAtual.getSaida("hall");
                                jogador.setSalaAtual(proximaSala);
                                return "DESCRICAO|" + proximaSala.getDescricaoInicial();
                            } else {
                                return "ERRO|Não há passagem disponível para o hall.";
                            }
                        }
                        break;
                
                    default:
                        break;
                }
                break;

            /***********LOGICA DO HALL*********/
            case "Hall":                    
                switch (comando) {
                    case "OLHAR":
                        if (parametros[0].equals("AMBIENTE")) {
                            if (EstadoGlobal.getInstance().isCaldeiraoAtivo()) {
                                return "DESCRICAO|" + localAtual.getDescricaoCompleta() + "\\nUma passagem secreta se abriu na parede à esquerda, você pode ir para a 'galeria' através dela.";
                            }
                            return "DESCRICAO|" + localAtual.getDescricaoCompleta(); 
                        }

                        // Observar a porta
                        if (parametros[0].contains("PORTA")) {
                            return "DESCRICAO|A porta está trancada.";
                        }

                        if (!parametros[0].contains("fechadura") && !parametros[0].contains("porta") && EstadoGlobal.getInstance().isCaldeiraoAtivo()) {
                            return "DESCRICAO|" + localAtual.getDescricaoCompleta() + " Uma passagem secreta se abriu na parede à esquerda, você pode ir para a 'galeria' através dela.";
                        }
                        break;

                    case "USAR":
                        // Usar chave na porta
                        if (parametros[0].contains("CHAVE") && parametros[1].contains("PORTA")) {
                            if (!jogador.isTemChave()) {
                                return "ERRO|Você não tem uma chave.";
                            }

                            // Primeira tentativa - porta errada
                            return "NARRACAO|Vocês tentaram usar a chave na porta porém ela não encaixa, é estranho pois parece ser exatamente para essa porta, só então você percebe ter colocado na porta errada, eram duas portas, na segunda ela encaixa perfeitamente.\\n\\nFINAL DO JOGO!\\n\\nCom a porta aberta você descobre que estava nO TITANIC NA EAC........";
                        }
                        break;

                    case "IR":
                        if (parametros[0].contains("PORTA")) {
                            return "DESCRICAO|A porta está trancada.";
                        }

                        if (parametros[0].contains("COZINHA")) { //
                            Sala proximaSala = localAtual.getSaida("cozinha");
                                jogador.setSalaAtual(proximaSala);
                                EstadoGlobal.getInstance().setLocalizacaoJ1(proximaSala.getNome()); 
                                return "DESCRICAO|" + proximaSala.getDescricaoInicial(); //
                        }

                         if (parametros[0].equals("GALERIA")) {
                             if (EstadoGlobal.getInstance().isCaldeiraoAtivo()) { // Só funciona se caldeirão ativo
                            Sala proximaSala = localAtual.getSaida("galeria");
                            jogador.setSalaAtual(proximaSala);
                            return "DESCRICAO|" + proximaSala.getDescricaoInicial();
                            } else {
                                return "ERRO|Não há passagem disponível para a galeria.";
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

            /**********LOGICA DA COZINHA**********/
                case "Cozinha":
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
                        if (parametros[0].contains("SALA")) {
                            return "TRANSICAO_HALL";
                        }

                        if ((parametros[0].contains("HALL") || parametros[0].contains("PRINCIPAL"))) {
                            return "TRANSICAO_HALL";
                        }

                        if (parametros[0].equals("HALL")) {
                            Sala proximaSala = localAtual.getSaida("hall");
                            jogador.setSalaAtual(proximaSala);
                            return "DESCRICAO|" + proximaSala.getDescricaoInicial();
                        }
                        break;
                
                    default:
                        break;
                }
                break;
            
            /**********LOGICA DA FAZENDA**********/
            case "Fazenda":
                if (localAtual instanceof Fazenda) {
                Fazenda fazenda = (Fazenda) localAtual;

                switch (comando) {
                    case "OLHAR":
                             if (parametros[0].contains("AMBIENTE")) {
                                 return "DESCRICAO|" + fazenda.observar();
                            }
                            String ingredienteOlhar = parametros[0];
                            if (fazenda.ingredienteDisponivel(ingredienteOlhar)) {
                                 return "DESCRICAO|" + fazenda.observarIngrediente(ingredienteOlhar);
                            }
                            break;

                    case "USAR":
                        // Usar foice para pegar ingredientes
                        if (parametros[0].contains("foice")) {

                            if (!jogador.isTemFoice()) {
                                return "ERRO Você não tem uma foice. Vá ao moinho para pegar uma.";
                            }

                            String ingrediente = parametros[0];
                            if (fazenda.ingredienteDisponivel(ingrediente)) {
                                 return "DESCRICAO|" + fazenda.observarIngrediente(ingrediente);
                            }

                            if (!fazenda.ingredienteDisponivel(ingrediente)) {
                                return "ERRO Não há " + ingrediente + " aqui. Ingredientes disponíveis: " +
                                        String.join(", ", fazenda.getIngredientesDisponiveis());
                            }

                            return "DESCRICAO|" + jogador.coletarIngrediente(ingrediente);
                        }
                        break;

                    case "IR":
                             if (parametros[0].contains("GALERIA")) {
                                Sala proximaSala = localAtual.getSaida("galeria");
                                jogador.setSalaAtual(proximaSala);
                                EstadoGlobal.getInstance().setLocalizacaoJ2(proximaSala.getNome());
                                return "DESCRICAO|" + proximaSala.getDescricaoInicial();
                            }

                            if (parametros[0].contains("MOINHO")) { 
                                Sala proximaSala = localAtual.getSaida("moinho");
                                jogador.setSalaAtual(proximaSala);
                                return "DESCRICAO|" + proximaSala.getDescricaoInicial(); 
                            }
                        break;
                
                    default:
                        break;
                    }
                }
                break;
                
            /**********LOGICA DO MOINHO**********/
            case "Moinho":
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
                        // Colocar ingrediente no caldeirão
                        if (parametros[1].contains("CALDEIRAO")) {

                            // Procurar ingrediente no comando
                            String ingrediente = parametros[0];

                            if (!jogador.temIngrediente(ingrediente)) {
                                return "ERRO|Você não tem " + ingrediente + ". Ingredientes que você tem: " + String.join(", ", jogador.getIngredientesPegos());
                            }

                            String resultado = moinho.adicionarIngrediente(ingrediente);

                            // Verificar se o caldeirão ficou ativo e enviar evento
                            if (moinho.isCaldeiraoAtivo() && !EstadoGlobal.getInstance().isCaldeiraoAtivo()) {
                                EstadoGlobal.getInstance().setCaldeiraoAtivo(true);
                                EstadoGlobal.getInstance().setMolduraAberta(true);

                                // Enviar evento para ambos os jogadores sobre barulho
                                SistemaNotificacao.getInstance().enviarEvento("CALDEIRAO_ATIVO", "Sistema");

                                return "NARRACAO|" + resultado.replace("\n", "\\n");
                            }

                            return "DESCRICAO|" + resultado.replace("\n", "\\n");
                        }

                        // Jogar pão no caldeirão
                        if (parametros[0].contains("PAO") && parametros[1].contains("CALDEIRAO")) {

                            if (!jogador.isTemPaoMofado()) {
                                return "ERRO|Você não tem pão mofado.";
                            }

                            String resultado = moinho.jogarPaoNoCaldeirao();

                            if (resultado.contains("chave")) {
                                if(!jogador.isTemChave()){
                                    jogador.receberChave();
                                    EstadoGlobal.getInstance().setChaveObtida(true);
                                }
                                return "NARRACAO|" + resultado.replace("\n", "\\n");
                            }
                            return "DESCRICAO|Você já pegou a chave.";
                        }
                        break;
                    
                    case "IR":
                        if (parametros[0].contains("fazenda")) {
                            Sala proximaSala = localAtual.getSaida("fazenda");
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

        //--------------COMANDOS BASICOS---------
        switch (comando) {
            case "INVENTARIO":
                return "DESCRICAO|" + jogador.InventarioString().replace("\\n", "\n");
            
            case "OLHAR":
                if (parametros[0].contains("AMBIENTE")) {
                    return "DESCRICAO|" + localAtual.getDescricaoCompleta();       
                }
        
            case "IR":
                // --- NAVEGAÇÃO AUTOMÁTICA ENTRE SALAS VIZINHAS ---
                // Verifica se é um comando de movimento para uma sala vizinha
                // Verificar comandos com múltiplas parametros primeiro
                if (comando.contains("casa principal")) {
                    if (localAtual.getSaida("casa principal") != null) {
                        if (localAtual.getNome().contains("Galeria") && !EstadoGlobal.getInstance().isCaldeiraoAtivo()) {
                            return "ERRO Não há passagem disponível.";
                        }
                        return "TRANSICAO_HALL";
                    }
                }

                // Procura por uma saída que corresponda ao comando
                for (String palavra : parametros) {
                    if (localAtual.getSaida(palavra) != null) {
                        // Verificar se é uma saída condicional (porta secreta)
                        if ((palavra.contains("galeria") || palavra.contains("hall")) &&
                                (localAtual.getNome().contains("Galeria") || localAtual.getNome().contains("Hall"))) {
                            // Porta secreta só funciona se caldeirão estiver ativo
                            if (!EstadoGlobal.getInstance().isCaldeiraoAtivo()) {
                                return "ERRO Não há passagem disponível.";
                            }
                        }

                        // Navegação normal - retorna a transição
                        if (palavra.contains("galeria"))
                            return "TRANSICAO_GALERIA";
                        if (palavra.contains("hall"))
                            return "TRANSICAO_HALL";
                        if (palavra.contains("cozinha"))
                            return "TRANSICAO_COZINHA";
                        if (palavra.contains("fazenda"))
                            return "TRANSICAO_FAZENDA";
                        if (palavra.contains("moinho"))
                            return "TRANSICAO_MOINHO";
                    }
                }

                if (localAtual.getNome().contains("Cozinha")) {
                    return "TRANSICAO_HALL";
                }

            default:
                return "ERRO|Não entendi o que você quis dizer.";
        }                
    }
}