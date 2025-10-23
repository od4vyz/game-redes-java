package server.jogo.modelo;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SistemaNotificacao {
    private static final String ARQUIVO_EVENTOS = "eventos_multiplayer.txt";
    private static SistemaNotificacao instancia = null;

    private SistemaNotificacao() {
    }

    public static SistemaNotificacao getInstance() {
        if (instancia == null) {
            instancia = new SistemaNotificacao();
        }
        return instancia;
    }

    // Envia um evento para ser lido por outros jogadores
    public void enviarEvento(String evento, String jogadorOrigem) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String linha = timestamp + "|" + jogadorOrigem + "|" + evento + System.lineSeparator();

            Files.write(Paths.get(ARQUIVO_EVENTOS), linha.getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Erro ao enviar evento: " + e.getMessage());
        }
    }

    // Lê eventos enviados por outros jogadores
    public String lerEventosRecentes(String jogadorAtual) {
        try {
            if (!Files.exists(Paths.get(ARQUIVO_EVENTOS))) {
                return null;
            }

            String conteudo = new String(Files.readAllBytes(Paths.get(ARQUIVO_EVENTOS)));
            String[] linhas = conteudo.split(System.lineSeparator());

            // Pega apenas os últimos eventos (últimas 5 linhas)
            StringBuilder eventos = new StringBuilder();
            int inicio = Math.max(0, linhas.length - 5);

            for (int i = inicio; i < linhas.length; i++) {
                if (linhas[i].trim().isEmpty())
                    continue;

                String[] partes = linhas[i].split("\\|");
                if (partes.length >= 3) {
                    String timestamp = partes[0];
                    String origem = partes[1];
                    String evento = partes[2];

                    // Só mostra eventos de outros jogadores
                    if (!origem.equals(jogadorAtual)) {
                        eventos.append("[").append(timestamp).append("] ")
                                .append(origem).append(": ").append(evento)
                                .append(System.lineSeparator());
                    }
                }
            }

            return eventos.length() > 0 ? eventos.toString().trim() : null;
        } catch (IOException e) {
            return null;
        }
    }

    // Verifica se há eventos específicos
    public boolean temEvento(String tipoEvento) {
        try {
            if (!Files.exists(Paths.get(ARQUIVO_EVENTOS))) {
                return false;
            }

            String conteudo = new String(Files.readAllBytes(Paths.get(ARQUIVO_EVENTOS)));
            return conteudo.contains(tipoEvento);
        } catch (IOException e) {
            return false;
        }
    }

    // Limpa o arquivo de eventos (útil para novos jogos)
    public void limparEventos() {
        try {
            if (Files.exists(Paths.get(ARQUIVO_EVENTOS))) {
                Files.delete(Paths.get(ARQUIVO_EVENTOS));
            }
        } catch (IOException e) {
            System.err.println("Erro ao limpar eventos: " + e.getMessage());
        }
    }

    // Limpa um evento específico
    public void limparEvento(String tipoEvento) {
        try {
            if (!Files.exists(Paths.get(ARQUIVO_EVENTOS))) {
                return;
            }

            String conteudo = new String(Files.readAllBytes(Paths.get(ARQUIVO_EVENTOS)));
            String[] linhas = conteudo.split("\n");
            StringBuilder novoConteudo = new StringBuilder();

            for (String linha : linhas) {
                if (!linha.contains(tipoEvento)) {
                    novoConteudo.append(linha).append("\n");
                }
            }

            Files.write(Paths.get(ARQUIVO_EVENTOS), novoConteudo.toString().getBytes());
        } catch (IOException e) {
            System.err.println("Erro ao limpar evento específico: " + e.getMessage());
        }
    }
}