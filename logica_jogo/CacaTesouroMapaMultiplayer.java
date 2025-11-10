package logica_jogo;

import Clientes.ClientesMain;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Versão multiplayer do Caça ao Tesouro
 * Sincroniza jogadas entre todos os jogadores através do servidor
 *
 * @author J
 */
public class CacaTesouroMapaMultiplayer extends JFrame {

    private JButton[][] botoes;
    private int linhas;
    private int colunas;
    private int hgap = 2;
    private int vgap = 2;

    private ClientesMain cliente;
    private String jogadorAtual;
    private boolean minhaVez = false;
    private JLabel labelStatus;

    public CacaTesouroMapaMultiplayer(int linhas, int colunas, ClientesMain cliente) {
        this.linhas = linhas;
        this.colunas = colunas;
        this.cliente = cliente;

        initComponents();
        criarMapa();
    }

    private void initComponents() {
        setTitle("Caça ao Tesouro - Multiplayer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Label de status no topo
        labelStatus = new JLabel("Aguardando início do jogo...", SwingConstants.CENTER);
        labelStatus.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        labelStatus.setPreferredSize(new Dimension(400, 40));
        add(labelStatus, BorderLayout.NORTH);
    }

    public void criarMapa() {
        JPanel painelMapa = new JPanel();
        GridLayout gridLayout = new GridLayout(linhas, colunas, hgap, vgap);
        painelMapa.setLayout(gridLayout);

        botoes = new JButton[linhas][colunas];

        for (int l = 0; l < linhas; l++) {
            for (int c = 0; c < colunas; c++) {
                final int linha = l;
                final int coluna = c;

                botoes[l][c] = new JButton("?");
                botoes[l][c].setPreferredSize(new Dimension(50, 50));
                botoes[l][c].setEnabled(false); // Começa desabilitado

                botoes[l][c].addActionListener(action -> {
                    fazerJogada(linha, coluna);
                });

                painelMapa.add(botoes[l][c]);
            }
        }

        add(painelMapa, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Processa quando o jogador clica em um botão
     */
    private void fazerJogada(int linha, int coluna) {
        if (!minhaVez) {
            JOptionPane.showMessageDialog(this,
                "Não é sua vez de jogar!",
                "Aguarde sua vez",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Envia a jogada para o servidor
        cliente.enviarJogada(linha, coluna);

        // Desabilita os botões enquanto aguarda resposta
        desabilitarBotoes();
    }

    /**
     * Notifica de quem é a vez de jogar
     */
    public void notificarVez(String nomeJogador) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            this.jogadorAtual = nomeJogador;

            if (nomeJogador != null && !nomeJogador.isEmpty()) {
                // Verifica se é a vez deste jogador
                String meuUsername = cliente.getUsername();
                minhaVez = nomeJogador.equals(meuUsername);

                if (minhaVez) {
                    labelStatus.setText("SUA VEZ DE JOGAR!");
                    labelStatus.setForeground(new Color(0, 150, 0)); // Verde escuro
                    habilitarBotoes();
                } else {
                    labelStatus.setText("Vez de: " + nomeJogador + " (Aguarde...)");
                    labelStatus.setForeground(Color.BLUE);
                    desabilitarBotoes();
                }
            }
        });
    }

    /**
     * Processa uma jogada recebida do servidor
     */
    public void processarJogada(String nomeJogador, int linha, int coluna, boolean acertou) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            if (acertou) {
                // Jogador encontrou o tesouro
                botoes[linha][coluna].setText("Tesouro!");
                botoes[linha][coluna].setBackground(Color.YELLOW);
                botoes[linha][coluna].setEnabled(false);

                labelStatus.setText(nomeJogador + " encontrou o tesouro!");
                labelStatus.setForeground(Color.GREEN);

                desabilitarBotoes();
                minhaVez = false;

                // Mostra mensagem de vitória
                int opcao = JOptionPane.showConfirmDialog(this,
                    nomeJogador + " encontrou o tesouro!\n\nDeseja jogar novamente?",
                    "Fim do Jogo!",
                    JOptionPane.YES_NO_OPTION);

                if (opcao == JOptionPane.YES_OPTION) {
                    // Aqui poderia solicitar reinício ao servidor
                    System.out.println("Jogador quer jogar novamente");
                } else {
                    dispose();
                }

            } else {
                // Jogada errada
                botoes[linha][coluna].setText("X");
                botoes[linha][coluna].setBackground(Color.RED);
                botoes[linha][coluna].setEnabled(false);

                labelStatus.setText("Jogada de " + nomeJogador + " - Errou!");
                labelStatus.setForeground(Color.RED);

                // Desabilita botões até receber notificação de vez
                desabilitarBotoes();
                minhaVez = false;
            }
        });
    }

    /**
     * Habilita apenas os botões que ainda não foram jogados
     */
    private void habilitarBotoes() {
        for (int l = 0; l < linhas; l++) {
            for (int c = 0; c < colunas; c++) {
                if (botoes[l][c].getText().equals("?")) {
                    botoes[l][c].setEnabled(true);
                }
            }
        }
    }

    /**
     * Desabilita todos os botões
     */
    private void desabilitarBotoes() {
        for (int l = 0; l < linhas; l++) {
            for (int c = 0; c < colunas; c++) {
                botoes[l][c].setEnabled(false);
            }
        }
    }
}
