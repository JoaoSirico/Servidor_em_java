package logica_jogo;

import Server.Conexoes;
import java.awt.Point;
import java.util.List;

/**
 * Gerencia o estado do jogo e os turnos dos jogadores
 *
 * @author J
 */
public class GerenciadorJogo {

    private List<Conexoes> jogadores;
    private int jogadorAtualIndex;
    private boolean jogoEmAndamento;
    private Point posicaoTesouro;
    private int linhas;
    private int colunas;

    public GerenciadorJogo(List<Conexoes> jogadores, int linhas, int colunas) {
        this.jogadores = jogadores;
        this.linhas = linhas;
        this.colunas = colunas;
        this.jogadorAtualIndex = 0;
        this.jogoEmAndamento = false;

        // Gera posição aleatória do tesouro
        this.posicaoTesouro = new Point(
            (int) (Math.random() * linhas),
            (int) (Math.random() * colunas)
        );

        System.out.println("Jogo criado! Tesouro em: [" + posicaoTesouro.x + "][" + posicaoTesouro.y + "]");
    }

    /**
     * Inicia o jogo
     */
    public void iniciarJogo() {
        this.jogoEmAndamento = true;
        this.jogadorAtualIndex = 0;
        System.out.println("Jogo iniciado! Vez de: " + getJogadorAtual().getUsername());
    }

    /**
     * Retorna o jogador atual
     */
    public Conexoes getJogadorAtual() {
        if (jogadores.isEmpty()) {
            return null;
        }
        return jogadores.get(jogadorAtualIndex);
    }

    /**
     * Retorna o índice do jogador atual
     */
    public int getJogadorAtualIndex() {
        return jogadorAtualIndex;
    }

    /**
     * Verifica se é a vez do jogador
     */
    public boolean isVezDoJogador(Conexoes jogador) {
        return getJogadorAtual() != null && getJogadorAtual().equals(jogador);
    }

    /**
     * Passa a vez para o próximo jogador
     */
    public void proximoJogador() {
        jogadorAtualIndex = (jogadorAtualIndex + 1) % jogadores.size();
        System.out.println("Próximo jogador: " + getJogadorAtual().getUsername());
    }

    /**
     * Processa uma jogada
     * @return true se encontrou o tesouro, false caso contrário
     */
    public boolean processarJogada(int linha, int coluna) {
        if (!jogoEmAndamento) {
            return false;
        }

        boolean acertou = (linha == posicaoTesouro.x && coluna == posicaoTesouro.y);

        if (acertou) {
            System.out.println("Tesouro encontrado por " + getJogadorAtual().getUsername() + "!");
            jogoEmAndamento = false;
        } else {
            System.out.println("Jogada errada em [" + linha + "][" + coluna + "]");
            proximoJogador();
        }

        return acertou;
    }

    /**
     * Reinicia o jogo
     */
    public void reiniciarJogo() {
        this.jogadorAtualIndex = 0;
        this.jogoEmAndamento = true;

        // Gera nova posição aleatória do tesouro
        this.posicaoTesouro = new Point(
            (int) (Math.random() * linhas),
            (int) (Math.random() * colunas)
        );

        System.out.println("Jogo reiniciado! Novo tesouro em: [" + posicaoTesouro.x + "][" + posicaoTesouro.y + "]");
    }

    // Getters
    public boolean isJogoEmAndamento() {
        return jogoEmAndamento;
    }

    public Point getPosicaoTesouro() {
        return posicaoTesouro;
    }

    public List<Conexoes> getJogadores() {
        return jogadores;
    }

    public int getLinhas() {
        return linhas;
    }

    public int getColunas() {
        return colunas;
    }
}
