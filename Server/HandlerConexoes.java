package Server;

import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author J
 */
public class HandlerConexoes extends Thread {

    private Main servidor;
    private Conexoes cliente;

    public HandlerConexoes(Conexoes cliente, Main servidor) {
        this.cliente = cliente;
        this.servidor = servidor;
    }

    @Override
    public void finalize() throws Throwable {
        try {
            encerrar();
        } finally {
            super.finalize();
        }
    }

    //Remove o cliente em questão
    public void encerrar() {
        this.servidor.removerCliente(cliente);
    }

    /**
     * Processa as mensagens recebidas do cliente
     */
    private void processarMensagem(String mensagem) {
        System.out.println("Mensagem de " + cliente.getUsername() + ": " + mensagem);

        // Verifica o tipo de mensagem
        if (mensagem.startsWith("JOGADA:")) {
            // Formato: JOGADA:linha,coluna
            processarJogada(mensagem);
        } else {
            // Tenta processar como número (compatibilidade com código antigo)
            try {
                int tecla = Integer.parseInt(mensagem);
                System.out.println("Tecla recebida de " + cliente.getUsername() + ": " + tecla);
            } catch (NumberFormatException e) {
                System.out.println("Mensagem desconhecida: " + mensagem);
            }
        }
    }

    /**
     * Processa uma jogada recebida do cliente
     */
    private void processarJogada(String mensagem) {
        try {
            // Remove o prefixo "JOGADA:"
            String dados = mensagem.substring(7);
            String[] partes = dados.split(",");

            if (partes.length != 2) {
                System.out.println("Formato de jogada inválido: " + mensagem);
                return;
            }

            int linha = Integer.parseInt(partes[0].trim());
            int coluna = Integer.parseInt(partes[1].trim());

            System.out.println("Jogada de " + cliente.getUsername() + ": [" + linha + "][" + coluna + "]");

            // Envia a jogada para o servidor processar
            servidor.processarJogada(cliente, linha, coluna);

        } catch (Exception e) {
            System.out.println("Erro ao processar jogada: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String message;
        try {
            while (true) {
                // readLine() retorna null quando cliente desconecta
                if (!servidor.isAlive()) {
                    break;
                }
                message = this.cliente.getInput().readLine();

                // Verifica se cliente desconectou
                if (message == null) {
                    System.out.println("Cliente desconectou: " + cliente.getUsername());
                    break;
                }

                // Verifica se mensagem está vazia
                if (message.isEmpty()) {
                    System.out.println("Mensagem vazia recebida");
                    continue; // Pula para próxima iteração
                }

                // Processa mensagem
                processarMensagem(message);
            }
        } catch (IOException e) {
            System.out.println("Erro de I/O com cliente " + cliente.getUsername() + ": " + e.getMessage());
        } finally {
            System.out.println("Encerrando conexão com: " + cliente.getUsername());
            encerrar();
        }
    }

}
