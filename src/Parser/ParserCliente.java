package Parser;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Locale;

public class ParserCliente {

    //Conjunto de palavras que serão removidas dos comandos (em maiúsculas)
    private static final Set<String> REMOVE_WORDS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
        "O", "A", "OS", "AS", "UM", "UMA", "UNS", "UMAS", "ANTE", "APÓS", "APOS", "ATÉ", "ATE", "COM", "CONTRA", 
        "DE", "DESDE", "EM", "ENTRE", "PRO", "PRA", "PROS", "PRAS", "PRUM", "PRUNS",  "PARA", "PER", "PERANTE", "POR", "SEM", "SOB", "SOBRE", "TRÁS", "TRAS",
        "AO", "AOS", "À", "ÀS", "DO", "DA", "DOS", "DAS", "NO", "NA", "NOS", "NAS", "PELO",
        "PELA", "PELOS", "PELAS", "NUM", "NUMA", "NUNS", "NUMAS", "DUM", "DUMA", "DUMS", "DUMAS", "DUNS"
    )));

    //Conjunto de ações
    private static final Set<String> ACTIONS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
        "OLHAR", "IR", "PEGAR", "USAR", "FALAR", "TESTAR", "INVENTARIO", "CONECTAR"
    )));

    // Mapa de sinônimos: formas variadas -> forma canônica (ex.: "enxergar" -> "OLHAR")
    private static final Map<String, String> SYNONYMS;
    static {
        Map<String, String> m = new HashMap<>();
        // todas as chaves em minúsculas; valores em MAIÚSCULAS (ou como preferir)
        m.put("VER", "OLHAR");
        m.put("OLHAR", "OLHAR");
        m.put("ENXERGAR", "OLHAR");
        m.put("OBSERVAR", "OLHAR");
        m.put("EXAMINAR", "OLHAR");
        m.put("ESPIAR", "OLHAR");
        m.put("MIRAR", "OLHAR");
        m.put("CONSIDERAR", "OLHAR");
        m.put("CONTEMPLAR", "OLHAR");
        m.put("VISUALIZAR", "OLHAR");
        m.put("VERIFICAR", "OLHAR");
        m.put("LER", "OLHAR");


        m.put("ANDAR", "IR");
        m.put("CAMINHAR", "IR");
        m.put("IR", "IR");
        m.put("MOVER", "IR");
        m.put("VAI", "IR");
        m.put("PELA", "IR");
        m.put("SUBIR", "IR");
        m.put("DESCER", "IR");
        m.put("ENTRAR", "IR");
        m.put("VOLTAR", "IR");
        m.put("SAIR", "IR");


        m.put("PEGAR", "PEGAR");
        m.put("APANHAR", "PEGAR");
        m.put("COLETAR", "PEGAR");
        m.put("TOMAR", "PEGAR");
        m.put("AGARRAR", "PEGAR");
        m.put("SEGURAR", "PEGAR");
        m.put("RECOLHER", "PEGAR");
        m.put("CAPTURAR", "PEGAR");
        m.put("PEGUE", "PEGAR");
        m.put("TOCAR", "PEGAR");

        m.put("USAR", "USAR");
        m.put("UTILIZAR", "USAR");
        m.put("EMPREGAR", "USAR");
        m.put("APLICAR", "USAR");
        m.put("LIGAR", "USAR");
        m.put("APERTAR", "USAR");
        m.put("ACIONAR", "USAR");
        m.put("ABRIR", "USAR");
        m.put("COLOCAR", "USAR");
        m.put("FECHAR", "USAR");


        m.put("FALAR", "FALAR");
        m.put("DIZER", "FALAR");

        m.put("TENTAR", "TESTAR");
        m.put("TESTAR", "TESTAR");
        m.put("EXPERIMENTAR", "TESTAR");
        m.put("PROVAR", "TESTAR");

        m.put("INVENTARIAR", "INVENTARIO");
        m.put("INVENTÁRIO", "INVENTARIO");
        m.put("INVENTARIO", "INVENTARIO");

        SYNONYMS = Collections.unmodifiableMap(m);
    }

    public static String normalize(String linha) {
        if (linha == null) return "";
        String linhaMaiuscula = linha.toUpperCase(Locale.ROOT);
        return Arrays.stream(linhaMaiuscula.strip().split("\\s+"))
                .map(tok -> tok.replaceAll("[^\\p{L}0-9_-]", "")) // remove pontuação básica
                .filter(tok -> !tok.isEmpty())
                .collect(Collectors.joining(" "));
    }

    /**
     * Substitui, em toda a linha, palavras por sua forma canônica definida em SYNONYMS.
     * Mantém a ordem dos tokens e preserva tokens que não têm sinônimo.
     */
    public static String actionsMap(String linha){
        if (linha == null) return "";
        return Arrays.stream(linha.split("\\s+"))
                .map(tok -> {
                    if (tok.isEmpty()) return tok;
                    String key = tok;
                    return SYNONYMS.getOrDefault(key, tok);
                })
                .filter(tok -> !tok.isEmpty())
                .collect(Collectors.joining(" "));
    }

    public static String removeStopWords(String linha) {
        if (linha == null) return "";
        String cleaned = Arrays.stream(linha.split("\\s+"))
                .filter(tok -> !tok.isEmpty() && !REMOVE_WORDS.contains(tok))
                .collect(Collectors.joining(" "));
        return cleaned;
    }

    public static String getAction(String linha){
        if (linha == null || linha.isEmpty()) {
            return "";
        }

        String[] tokens = linha.split("\\s+");
        if (tokens.length == 0) {
            return "";
        }

        String action = Arrays.stream(tokens)
                .filter(ACTIONS::contains)
                .findFirst()
                .orElse("");
        return action;
    }

    public static String parseProtocol(String linha, String comando) {
        String[] tokens = linha.split("\\s+", 2);
        switch (comando) {
            case "CONECTAR":
                if (tokens.length < 2) {
                    return comando; // sem alvo
                } 
                else {
                    String[] parts = linha.split("\\s+");
                    StringBuilder protocol = new StringBuilder(comando);
                        if(parts[0].equalsIgnoreCase(comando))
                            protocol.append('|').append(parts[1]);
                        else
                            protocol.append('|').append(parts[0]);
                    return protocol.toString();
                }

            case "OLHAR":
                if (tokens.length < 2) {
                    return comando; // sem alvo
                } 
                else {
                    String[] parts = linha.split("\\s+");
                    StringBuilder protocol = new StringBuilder(comando);
                        if(parts[0].equalsIgnoreCase(comando))
                            protocol.append('|').append(parts[1]);
                        else
                            protocol.append('|').append(parts[0]);
                    return protocol.toString();
                }

            case "INVENTARIO":
                return comando;

            case "IR":
                if (tokens.length < 2) {
                    return comando; // sem alvo
                } 
                else {
                    String[] parts = linha.split("\\s+");
                    StringBuilder protocol = new StringBuilder(comando);
                        if(parts[0].equalsIgnoreCase(comando))
                            protocol.append('|').append(parts[1]);
                        else
                            protocol.append('|').append(parts[0]);
                    return protocol.toString();
                }
            case "PEGAR":
                if (tokens.length < 2) {
                    return comando; // sem alvo
                } 
                else {
                    String[] parts = linha.split("\\s+");
                    StringBuilder protocol = new StringBuilder(comando);
                        if(parts[0].equalsIgnoreCase(comando))
                            protocol.append('|').append(parts[1]);
                        else
                            protocol.append('|').append(parts[0]);
                    return protocol.toString();
                }

            case "USAR":
                if (tokens.length < 2) {
                    return comando; // sem alvo
                } 
                else {
                    String[] parts = linha.split("\\s+");
                    StringBuilder protocol = new StringBuilder(comando);
                        if(parts[0].equalsIgnoreCase(comando))
                            protocol.append('|').append(parts[1]);
                        else
                            protocol.append('|').append(parts[0]);
                    return protocol.toString();
                }

            case "FALAR":
                if (tokens.length < 2) {
                    return comando; // sem alvo
                } 
                else {
                    String[] parts = linha.split("\\s+");
                    StringBuilder protocol = new StringBuilder(comando);
                        if(parts[0].equalsIgnoreCase(comando))
                            protocol.append('|').append(parts[1]);
                        else
                            protocol.append('|').append(parts[0]);
                    return protocol.toString();
                }

            case "TESTAR":
                if (tokens.length < 2) {
                    return comando; // sem alvo
                } else {
                    String[] parts = linha.split("\\s+");
                    StringBuilder protocol = new StringBuilder(comando);
                    for (int i = 0; i < parts.length; i++) {
                        if(!parts[i].equalsIgnoreCase(comando))
                            protocol.append('|').append(parts[i]);
                    }
                    return protocol.toString();
                }
            default:
                return ""; // comando inválido
        }
    }

    /**
     * Remove stop-words e aplica o mapeamento de sinônimos.
     * Retorna a linha processada (ou vazia se não restar nada).
     */
    public static String Analisar(String linha) {
        if (linha == null) {
            return "";
        }

        String linhaNormalizada = normalize(linha);
        String linhaSinonimo = actionsMap(linhaNormalizada);
        String linhaLimpa = removeStopWords(linhaSinonimo);
        String comando = getAction(linhaLimpa);

        String protocol = parseProtocol(linhaLimpa, comando);
        

        if (protocol.isEmpty()) {
            return "";
        } else {
            return protocol;
        }
    }
}