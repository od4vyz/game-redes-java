package server.jogo.modelo;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class Sala {
    String nome;
    String descricaoInicial;
    String descricaoLonga;
    List<Item> itens;
    Map<String, Sala> salasVizinhas;
    Map<String, Object> estados;
    Map<String, String> detalhes;
    Map<String, String> descricoesAlternativas;

    public Sala(String nome, String descricaoInicial, String descricaoLonga) {
        this.nome = nome;
        this.descricaoInicial = descricaoInicial;
        this.descricaoLonga = descricaoLonga;
        this.itens = new ArrayList<>();
        this.salasVizinhas = new HashMap<>();
        this.estados = new HashMap<>();
        this.detalhes = new HashMap<>();
        this.descricoesAlternativas = new HashMap<>();
    }

    public String getNome() {
        return nome;
    }

    public String getDescricaoInicial() {
        return descricaoInicial;
    }

    public String getDescricaoCompleta() {
        return descricaoLonga;
    }

    public void setDescricaoLonga(String descricaoLonga) {
        this.descricaoLonga = descricaoLonga;
    }

    public List<Item> getItens() {
        return itens;
    }

    public void setItens(List<Item> itens) {
        this.itens = itens;
    }

    public void setSalasVizinhas(Map<String, Sala> salasVizinhas) {
        this.salasVizinhas = salasVizinhas;
    }

    public void pegarItem(Item item) {
        itens.remove(item);
    }

    public Sala getSaida(String direcao) {
        if (!salasVizinhas.containsKey(direcao)) {
            return null;
        }
        return salasVizinhas.get(direcao);
    }

    public void adicionarEstado(String chave, Object valor) {
        estados.put(chave, valor);
    }

    public Object getEstado(String chave) {
        return estados.get(chave);
    }

    public void setEstado(String chave, Object valor) {
        estados.put(chave, valor);
    }

    public void adicionarDetalhe(String chave, String descricao) {
        detalhes.put(chave, descricao);
    }

    public String getDetalhe(String chave) {
        return detalhes.get(chave);
    }

    public void adicionarDescricaoAlternativa(String chave, String descricao) {
        descricoesAlternativas.put(chave, descricao);
    }

    public String getDescricaoAlternativa(String chave) {
        return descricoesAlternativas.get(chave);
    }

    public void adicionarSaida(String direcao, Sala sala) {
        salasVizinhas.put(direcao, sala);
    }

    public String observar() {
        return getDescricaoCompleta();
    }
}