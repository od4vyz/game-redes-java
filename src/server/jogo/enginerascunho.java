
                     if (parametros[0].equals("AMBIENTE") || parametros[0].equals("SALA")) {
                         // --- MODIFICAÇÃO: Lógica duplicada movida para cá ---
                        if (localAtual.getNome().equals("Hall") && EstadoGlobal.getInstance().isCaldeiraoAtivo()) {
                            return "DESCRICAO|" + localAtual.getDescricaoCompleta().replace("\n", "\\n") + "\\nUma passagem secreta se abriu na parede à esquerda, você pode ir para a 'galeria' através dela.";
                        }
                        if (localAtual.getNome().equals("Galeria") && EstadoGlobal.getInstance().isCaldeiraoAtivo()) {
                             return "DESCRICAO|A galeria é a mesma porém uma das molduras se abriu como uma porta. Você pode ir para a 'casa principal' (Hall) através da porta secreta.";
                        }
                        // --------------------------------------------------
                        return "DESCRICAO|" + localAtual.getDescricaoCompleta().replace("\n", "\\n"); // Retorno padrão, codifica \n
                    } else {
                         // Se tinha um parâmetro mas não foi tratado na sala específica (ex: OLHAR item_inexistente)
                         return "DESCRICAO|Não vejo nenhum(a) '" + String.join(" ", parametros) + "' aqui.";
                    }
                    // Não precisa de break, já tem return

                case "IR": //
                    if (parametros.length == 0) return "ERRO|Ir para onde?";

                    // --- MODIFICAÇÃO: Lógica de Navegação Genérica ---
                    String destino = String.join(" ", parametros).toLowerCase(); // Junta todos os params e normaliza

                    // Tentativa de saída direta
                    Sala proximaSala = localAtual.getSaida(destino); 
                    
                    // Tratamento especial para "casa principal"
                    if (destino.equals("casa principal")) {
                         proximaSala = localAtual.getSaida("hall"); // Mapeia para "hall"
                         destino = "hall"; // Atualiza destino para lógica condicional
                    }

                    if (proximaSala != null) {
                         // Verificar condições de portas secretas/bloqueios
                        if ((destino.equals("galeria") || destino.equals("hall")) &&
                            (localAtual.getNome().equals("Galeria") || localAtual.getNome().equals("Hall"))) {
                            if (!EstadoGlobal.getInstance().isCaldeiraoAtivo()) {
                                return "ERRO|Não há passagem disponível."; //
                            }
                        }
                        // Adicionar mais condições aqui se necessário (ex: porta trancada no Hall)

                        // Mover jogador
                        jogador.setSalaAtual(proximaSala);
                        
                        // Atualizar EstadoGlobal
                        // --- MODIFICAÇÃO: Descobrir qual jogador é (J1 ou J2) ---
                        // Uma forma é comparar a sala inicial, mas pode ser frágil.
                        // Idealmente, o objeto Jogador teria um ID ou o conectarJogador armazenaria isso.
                        // Solução temporária: verificar se o nome do jogador é o primeiro a conectar
                        boolean isJogador1 = jogadoresConectados.values().iterator().next() == jogador; // Pega o primeiro jogador do Map
                        if (isJogador1) {
                             EstadoGlobal.getInstance().setLocalizacaoJ1(proximaSala.getNome());
                        } else {
                             EstadoGlobal.getInstance().setLocalizacaoJ2(proximaSala.getNome());
                        }
                        // ----------------------------------------------------------

                        // Notificar encontro [cite: 90]
                        verificarEEnviarNotificacaoEncontro(jogador); 

                        // Notificar P1 se P2 se moveu entre Galeria/Fazenda [cite: 62]
                        if (!isJogador1 && (localAtual.getNome().equals("Galeria") && proximaSala.getNome().equals("Fazenda") ||
                                           localAtual.getNome().equals("Fazenda") && proximaSala.getNome().equals("Galeria"))) {
                             notificarOutroJogador(jogador, "NARRACAO|Você escuta sons de passos por perto.");
                        }

                        // Retorna descrição da nova sala
                        return "DESCRICAO|" + proximaSala.getDescricaoInicial().replace("\n", "\\n"); // Codifica \n
                    }
                    // Se não encontrou saída direta, cai para o erro final
                    break; // Fim do 'case "IR"'

                // --- MODIFICAÇÃO: Comando VOLTAR --- 
                case "VOLTAR":
                     if (localAtual.getNome().equals("Cozinha")) {
                         Sala hall = localAtual.getSaida("hall");
                         if (hall != null) {
                             jogador.setSalaAtual(hall);
                             EstadoGlobal.getInstance().setLocalizacaoJ1(hall.getNome()); // Assume J1
                              verificarEEnviarNotificacaoEncontro(jogador);
                             return "DESCRICAO|" + hall.getDescricaoInicial().replace("\n", "\\n");
                         }
                     }
                      if (localAtual.getNome().equals("Moinho")) {
                         Sala fazenda = localAtual.getSaida("fazenda");
                         if (fazenda != null) {
                             jogador.setSalaAtual(fazenda);
                             EstadoGlobal.getInstance().setLocalizacaoJ2(fazenda.getNome()); // Assume J2
                              verificarEEnviarNotificacaoEncontro(jogador);
                             return "DESCRICAO|" + fazenda.getDescricaoInicial().replace("\n", "\\n");
                         }
                     }
                     // Adicionar mais casos se necessário
                     break; // Se não for um local com VOLTAR definido, cai no erro

            } // --- FIM DO SWITCH BÁSICO ---

        } catch (ArrayIndexOutOfBoundsException e) {
             // Captura erros se comandos básicos esperarem parâmetros que não vieram
             return "ERRO|Comando incompleto. O que você quer " + comando.toLowerCase() + "?";
        } catch (Exception e) {
             System.err.println("Erro inesperado processando comando básico '" + comando + "': " + e.getMessage());
             e.printStackTrace();
             return "ERRO|Ocorreu um erro interno ao processar seu comando.";
        }

        // --- 3. RETORNO FINAL ---
        return "ERRO|Não entendi o que você quis dizer."; // [cite: 113]
    }
    
    // --- MODIFICAÇÃO: Métodos auxiliares para notificações ---
    /**
     * Envia uma mensagem de NARRACAO para o *outro* jogador conectado.
     */
    private synchronized void notificarOutroJogador(Jogador remetente, String mensagemProtocolo) {
        if (jogadoresConectados.size() < 2) return; // Não há outro jogador

        for (Jogador j : jogadoresConectados.values()) {
            if (j != remetente) {
                // Precisa da referência ao ConexaoJogador para enviar a mensagem.
                // Isso requer que a Engine tenha acesso à lista 'conexoesAtivas' do Servidor,
                // ou que o ConexaoJogador tenha um método para enviar msgs a outros.
                // SOLUÇÃO MAIS SIMPLES POR AGORA: Usar SistemaNotificacao (se aplicável)
                // ou retornar uma lista de mensagens a serem enviadas pela ConexaoJogador.
                
                // Vamos usar SistemaNotificacao como paliativo (requer que o cliente leia eventos)
                SistemaNotificacao.getInstance().enviarEvento(mensagemProtocolo, remetente.getNome()); 
                System.out.println("[Engine Notifica Outro]: " + mensagemProtocolo); // Log
                break; // Só há um outro jogador
            }
        }
        // TODO: Implementar envio direto via ConexaoJogador se SistemaNotificacao não for ideal.
    }

    /**
     * Verifica se os jogadores estão na mesma sala e envia notificação NARRACAO
     * para ambos na primeira vez que se encontram. [cite: 90]
     */
    private synchronized void verificarEEnviarNotificacaoEncontro(Jogador jogadorQueSeMoveu) {
         EstadoGlobal eg = EstadoGlobal.getInstance();
         // A lógica de verificar encontro já está no EstadoGlobal ao setar localização
         
         if (eg.isJogadoresNaMesmaSala() && !eg.isEncontroJaNotificado()) {
              String msg = "NARRACAO|Você vê " + obterNomeOutroJogador(jogadorQueSeMoveu) + " aqui!";
              // Enviar para ambos os jogadores
              // TODO: Precisa implementar broadcast a partir da Engine ou retornar lista de msgs
               SistemaNotificacao.getInstance().enviarEvento(msg, "Sistema"); // Paliativo
               System.out.println("[Engine Encontro]: " + msg); // Log
               eg.setEncontroJaNotificado(true); // Marca como notificado
         } else if (!eg.isJogadoresNaMesmaSala()) {
              eg.setEncontroJaNotificado(false); // Reseta se saíram da mesma sala
         }
    }
    
    /**
     * Método auxiliar para pegar o nome do outro jogador.
     */
     private synchronized String obterNomeOutroJogador(Jogador jogadorAtual) {
          for(Jogador j : jogadoresConectados.values()) {
               if (j != jogadorAtual) {
                    return j.getNome();
               }
          }
          return "alguém"; // Fallback
     }
    // ----------------------------------------------------
}