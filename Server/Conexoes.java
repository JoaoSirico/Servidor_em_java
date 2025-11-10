package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

/**
 *
 * @author J
 */
public class Conexoes {

    private Socket cliente;
    private String idCliente;
    private String username;

    private BufferedReader input;
    private PrintWriter output;

    public Conexoes(Socket cliente) {
        this.cliente = cliente;
        //Gera uma string aleatória para cada cliente -> Tipo um id caso seja necessário
        this.idCliente = UUID.randomUUID().toString();

        try {
            this.input = new BufferedReader(new InputStreamReader(this.cliente.getInputStream()));
            this.output = new PrintWriter(this.cliente.getOutputStream());
        } catch (IOException e) {
            System.out.println("Não foi possível criar o input e output do cliente");
            //matarCliente
        }
    }

    public BufferedReader getInput() {
        return input;
    }

    public void enviarMensagem(String mensagem) {
        if (output != null) {
            output.println(mensagem);
            output.flush();
        }
    }

    public boolean getSocket() {
        return cliente.isClosed();
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    

    /**
     * Fecha a conexão do cliente com o servidor
     */
    public void encerrarConexao() {
        try {
            if (this.cliente != null) {
                this.cliente.close();
            }

            if (this.input != null) {
                input.close();
            }

            if (this.output != null) {
                output.close();
            }
        } catch (IOException e) {
            System.out.println("Erro ao fechar a conexão do servidor-cliente: " + e.getMessage());
        }
    }

    @Override
    public void finalize() throws Throwable{
        try {
            this.encerrarConexao();
        } catch (Exception e) {
        } finally {
            super.finalize();
        }
    }

}
