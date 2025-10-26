package cliente;

import java.rmi.server.UnicastRemoteObject;
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
        "PELA", "PELOS", "PELAS", "NUM", "NUMA", "NUNS", "NUMAS", "DUM", "DUMA", "DUMS", "DUMAS", "DUNS", "ONDE", "QUE", "QUAL", "QUAIS", "QUEM", "CUJO", "CUJA", "CUJOS", "CUJAS",
        "ME", "TE", "SE", "NOS", "VOS", "LHE", "LHES", "MIM", "TI", "SI", "QUAL", "QUAIS", "COMIGO", "CONTIGO", "CONSIGO", "EU", "TU", "ELE", "ELA", "NÓS", "VÓS", "ELES", "ELAS", "MEU",
        "MINHA", "MEUS", "MINHAS", "TEU", "TUA", "TEUS", "TUAS", "SEU", "SUA", "SEUS", "SUAS", "NOSSO", "NOSSA", "NOSSOS", "NOSSAS", "VOSSO", "VOSSA", "VOSSOS", "VOSSAS",
        "ESTÁ", "ESTAO", "ESTAMOS", "ESTAIS", "ESTÃO", "SOU", "SOMOS", "SOIS", "SÃO", "FUI", "FOMOS", "FOSTES", "FORAM", "TÁ", "OQUE", "PQ", "PORQUE", "PORQUÊ", "POR QUÊ", "TODO", 
        "TODA", "TODOS", "TODAS", "TUDO", "ALGO", "NADA", "NENHUM", "NENHUMA", "NENHUNS", "NENHUMAS"
    )));

    //Conjunto de ações
    private static final Set<String> ACTIONS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
        "OLHAR", "IR", "PEGAR", "USAR", "FALAR", "TESTAR", "INVENTARIO", "CONECTAR", "SAIRDOJOGO"
    )));

    // Mapa de sinônimos: formas variadas -> forma canônica (ex.: "enxergar" -> "OLHAR")
    private static final Map<String, String> SYNONYMS;
    static {
        Map<String, String> m = new HashMap<>();
        // todas as chaves em minúsculas; valores em MAIÚSCULAS (ou como preferir)
        m.put("VER", "OLHAR");
        m.put("OLHO", "OLHAR");
        m.put("OLHOS", "OLHAR");
        m.put("OLHA", "OLHAR");
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
        m.put("ANALISAR", "OLHAR");


        m.put("ANDAR", "IR");
        m.put("ANDO", "IR");
        m.put("ANDA", "IR");
        m.put("CAMINHAR", "IR");
        m.put("CAMINHO", "IR");
        m.put("CAMINHA", "IR");
        m.put("IR", "IR");
        m.put("VOU", "IR");
        m.put("VAI", "IR");
        m.put("MOVER", "IR");
        m.put("MOVO", "IR");
        m.put("MOVO-ME", "IR");
        m.put("MOVE", "IR");
        m.put("MOVE-SE", "IR");
        m.put("VOU", "IR");
        m.put("SUBIR", "IR");
        m.put("SUBO", "IR");
        m.put("ESCALO", "IR");
        m.put("ESCALAR", "IR");
        m.put("DESCER", "IR");
        m.put("DESÇO", "IR");
        m.put("DESCO", "IR");
        m.put("DECE", "IR");
        m.put("ENTRAR", "IR");
        m.put("ENTRO", "IR");
        m.put("ENTRA", "IR");
        m.put("VOLTAR", "IR");
        m.put("VOLTO", "IR");
        m.put("VOLTA", "IR");
        m.put("SAIR", "IR");
        m.put("SAIO", "IR");
        m.put("SAI", "IR");


        m.put("PEGAR", "PEGAR");
        m.put("PEGO", "PEGAR");
        m.put("PEGA", "PEGAR");
        m.put("APANHAR", "PEGAR");
        m.put("APANHO", "PEGAR");
        m.put("APANHA", "PEGAR");
        m.put("COLETAR", "PEGAR");
        m.put("COLETO", "PEGAR");
        m.put("COLETA", "PEGAR");
        m.put("TOMAR", "PEGAR");
        m.put("TOMO", "PEGAR");
        m.put("TOMA", "PEGAR");
        m.put("AGARRAR", "PEGAR");
        m.put("AGARRO", "PEGAR");
        m.put("AGARRA", "PEGAR");
        m.put("SEGURAR", "PEGAR");
        m.put("SEGURO", "PEGAR");
        m.put("SEGURA", "PEGAR");
        m.put("RECOLHER", "PEGAR");
        m.put("RECOLHO", "PEGAR");
        m.put("RECOLHE", "PEGAR");
        m.put("CAPTURAR", "PEGAR");
        m.put("CAPTURO", "PEGAR");
        m.put("CAPTURA", "PEGAR");
        m.put("PEGUE", "PEGAR");
        m.put("PEGO", "PEGAR");
        m.put("PEGA", "PEGAR");
        m.put("TOCAR", "PEGAR");
        m.put("TOCO", "PEGAR");
        m.put("TOCA", "PEGAR");

        m.put("USAR", "USAR");
        m.put("USO", "USAR");
        m.put("USO", "USAR");
        m.put("UTILIZAR", "USAR");
        m.put("UTILIZO", "USAR");
        m.put("UTILIZA", "USAR");
        m.put("EMPREGAR", "USAR");
        m.put("EMPREGO", "USAR");
        m.put("EMPREGA", "USAR");
        m.put("POE", "USAR");
        m.put("PÕE", "USAR");
        m.put("PONHO", "USAR");
        m.put("JOGAR", "USAR");
        m.put("JOGO", "USAR");
        m.put("JOGA", "USAR");
        m.put("APLICAR", "USAR");
        m.put("APLICO", "USAR");
        m.put("APLICA", "USAR");
        m.put("LIGAR", "USAR");
        m.put("LIGO", "USAR");
        m.put("LIGA", "USAR");
        m.put("APERTAR", "USAR");
        m.put("APERTO", "USAR");
        m.put("APERTA", "USAR");
        m.put("ACIONAR", "USAR");
        m.put("ACIONO", "USAR");
        m.put("ACIONA", "USAR");
        m.put("ABRIR", "USAR");
        m.put("ABRO", "USAR");
        m.put("ABRE", "USAR");
        m.put("COLOCAR", "USAR");
        m.put("COLOCO", "USAR");
        m.put("COLOCA", "USAR");
        m.put("FECHAR", "USAR");
        m.put("FECHO", "USAR");
        m.put("FECHA", "USAR");
        m.put("ACENDER", "USAR");


        m.put("FALAR", "FALAR");
        m.put("FALO", "FALAR");
        m.put("FALA", "FALAR");
        m.put("DIZER", "FALAR");
        m.put("DIGO", "FALAR");
        m.put("DIZ", "FALAR");
        m.put("GRITAR", "FALAR");
        m.put("GRITO", "FALAR");

        m.put("TENTAR", "TESTAR");
        m.put("TESTAR", "TESTAR");
        m.put("EXPERIMENTAR", "TESTAR");
        m.put("PROVAR", "TESTAR");

        m.put("INVENTARIAR", "INVENTARIO");
        m.put("ITENS", "INVENTARIO");
        m.put("COISAS", "INVENTARIO");
        m.put("TROÇOS", "INVENTARIO");
        m.put("COISA", "INVENTARIO");
        m.put("COISO", "INVENTARIO");
        m.put("TROÇO", "INVENTARIO");
        m.put("INVENTÁRIO", "INVENTARIO");
        m.put("INVENTARIO", "INVENTARIO");

        m.put("REDOR", "AMBIENTE");
        m.put("AMBIENTE", "AMBIENTE");
        m.put("LUGAR", "AMBIENTE");
        m.put("SALA", "AMBIENTE");
        
        m.put("LUZ", "INTERRUPTOR");
        m.put("INTERRUPTOR", "INTERRUPTOR");

        m.put("ESCADA", "ESCADA");
        m.put("ESCADARIA", "ESCADA");

        SYNONYMS = Collections.unmodifiableMap(m);
    }

    private static String normalize(String linha) {
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
    private static String actionsMap(String linha){
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

    private static String removeStopWords(String linha) {
        if (linha == null) return "";
        String cleaned = Arrays.stream(linha.split("\\s+"))
                .filter(tok -> !tok.isEmpty() && !REMOVE_WORDS.contains(tok))
                .collect(Collectors.joining(" "));
        return cleaned;
    }

    private static String getAction(String linha){
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

    private static String parseProtocol(String linha, String comando) {
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
            
            case "SAIRDOJOGO":
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
    public String Analisar(String linha) {
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