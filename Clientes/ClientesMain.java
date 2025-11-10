package Clientes;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import Clientes.ClientesHandler;
import TelaClientes.Hub;
import logica_jogo.CacaTesouroMapaMultiplayer;
import javax.swing.JOptionPane;

/**
 * Cuida da conexão do cliente, gerencia o que tiver relacionado ao socket
 */
public final class ClientesMain {

    private Socket socket;
    private PrintWriter output;
    private boolean isHost;
    private String username;

    private ClientesHandler handler;
    private Hub telaHub;
    private CacaTesouroMapaMultiplayer telaJogo;

    /**
     *
     * @param host
     * @param porta
     * @param username
     * @param isHost
     *
     * @throws Exception
     */
    public ClientesMain(String host, int porta, String username, boolean isHost) throws Exception {
        //Cria o socket do cliente
        this.socket = new Socket(host, porta);
        //Server para imprimir tudopp o que o Cliente escrever no sistema
        this.output = new PrintWriter(this.socket.getOutputStream(), true);

        //Verifica se o cliente sendo criado é o host do jogo ou não
        this.isHost = isHost;
        this.username = username;

        this.handler = new ClientesHandler(socket, this);
        handler.start();

        //A primeira Mensagem do Cliente é a mensagem conténdo seu própio username, assim o servidor consegue pegar esse nome para uso
        mandarMensagem(username);

        // Se não for host, abre a tela do cliente
        if (!isHost) {
            this.telaHub = new Hub(this);
            this.telaHub.setVisible(true);
        }
    }

    public void mandarMensagem(String mensagem) {
        this.output.println(mensagem);
    }

    public Hub getTelaHub() {
        return this.telaHub;
    }

    public void fecharConexao() {
        try {
            if (this.output != null) {
                this.output.close();
            }
            if (this.socket != null) {
                this.socket.close();
            }
            if (this.handler != null) {
                this.handler.interrupt();
            }
            System.out.println("A sessão foi finalizada");
        } catch (IOException e) {
            System.out.println("Erro ao fecharConexao: " + e.getMessage());
        }
    }

    @Override
    public void finalize() throws Throwable {
        try {
            this.fecharConexao();
        } catch (Exception e) {
            System.out.println("Erro ao finalizar: " + e.getMessage());
        } finally {
            super.finalize();
        }
    }

    /**
     * Inicia a tela do jogo para este cliente
     */
    public void iniciarTelaJogo(int linhas, int colunas) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            telaJogo = new CacaTesouroMapaMultiplayer(linhas, colunas, this);
            telaJogo.setVisible(true);

            // Fecha a tela do hub se estiver aberta
            if (telaHub != null) {
                telaHub.setVisible(false);
            }
        });
    }

    /**
     * Envia uma jogada para o servidor
     */
    public void enviarJogada(int linha, int coluna) {
        String mensagem = String.format("JOGADA:%d,%d", linha, coluna);
        mandarMensagem(mensagem);
    }

    /**
     * Notifica a tela do jogo sobre de quem é a vez
     */
    public void notificarVez(String nomeJogador) {
        if (telaJogo != null) {
            telaJogo.notificarVez(nomeJogador);
        }
    }

    /**
     * Processa uma jogada recebida do servidor
     */
    public void processarJogada(String nomeJogador, int linha, int coluna, boolean acertou) {
        if (telaJogo != null) {
            telaJogo.processarJogada(nomeJogador, linha, coluna, acertou);
        }
    }

    /**
     * Mostra uma mensagem de erro
     */
    public void mostrarErro(String erro) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, erro, "Erro", JOptionPane.ERROR_MESSAGE);
        });
    }

    /**
     * Retorna o username do cliente
     */
    public String getUsername() {
        return username;
    }

}
