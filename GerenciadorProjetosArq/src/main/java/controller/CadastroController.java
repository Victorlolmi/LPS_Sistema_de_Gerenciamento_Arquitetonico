/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import model.dao.UsuarioDAO;
import model.entities.Gestor;
import model.entities.Usuario;
import model.entities.Cliente;
import model.exceptions.UsuarioException; // Import novo
import model.valid.ValidadorUsuario;     // Import novo
import org.mindrot.jbcrypt.BCrypt;
import view.screens.FrCadastro;
import view.screens.FrLogin;

/**
 *
 * @author Viktin
 */
public class CadastroController {

    private final FrCadastro view;
    private final UsuarioDAO usuarioDAO;

    public CadastroController(FrCadastro view) {
        this.view = view;
        this.usuarioDAO = new UsuarioDAO();
    }

    // Removi o "throws UsuarioException" da assinatura porque vamos tratar aqui mesmo com try/catch
    public void realizarCadastro() { 
        
        try {
            // 1. Obter dados da View
            String nome = view.getNome();
            String cpf = view.getCpf();
            String email = view.getEmail();
            String senha = view.getSenha();
            boolean isGestor = view.isTipo();

            // Chamada do Validador
            new ValidadorUsuario().validarCadastro(nome, cpf, email, senha, usuarioDAO);

            // 2. Validar os dados de entrada (tratamento de exceções de negócio)
            if (nome.isEmpty() || cpf.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                view.exibeMensagem("Todos os campos são obrigatórios!");
                return;
            }

            // Validação simples de CPF (apenas para garantir que não está em branco e tem 11 dígitos)
            String cpfLimpoAntigo = cpf.replaceAll("[.\\-]", "");
            if (cpfLimpoAntigo.length() != 11) {
                view.exibeMensagem("CPF inválido! Deve conter 11 dígitos.");
                return;
            }

            // 3. Verificar se o usuário já existe
            if (usuarioDAO.findByEmailOrCpf(email) != null || usuarioDAO.findByEmailOrCpf(cpfLimpoAntigo) != null) {
                view.exibeMensagem("Erro: CPF ou Email já cadastrado no sistema.");
                return;
            }
            // Precisamos limpar o CPF novamente para salvar no objeto, pois a variável 'cpfLimpo' 
            // estava dentro do bloco comentado ou no validador.
            String cpfParaSalvar = cpf.replaceAll("[.\\-]", "");

            // 4. Criptografar a senha
            String senhaComHash = BCrypt.hashpw(senha, BCrypt.gensalt());
            Usuario novoUsuario;

            if (isGestor) {
                Gestor gestor = new Gestor();
                // Se o Gestor tivesse campos extras, você os definiria aqui
                novoUsuario = gestor; 
                System.out.println("Criando um novo GESTOR."); 
            } else {
                Cliente cliente = new Cliente();
                // Se o Cliente tivesse campos extras, você os definiria aqui
                novoUsuario = cliente; 
                System.out.println("Criando um novo CLIENTE."); 
            }

            // 5. Criar o novo objeto (Preencher dados)
            novoUsuario.setNome(nome);
            novoUsuario.setCpf(cpfParaSalvar);
            novoUsuario.setEmail(email);
            novoUsuario.setSenha(senhaComHash);

            // 6. Salvar no banco de dados
            usuarioDAO.salvar(novoUsuario);
            
            // Se chegou até aqui sem erro, mostra sucesso
            view.exibeMensagem("Cadastro realizado com sucesso!");
            navegarParaLogin();

        } catch (UsuarioException e) {
            // A mensagem (ex: "CPF Inválido") vem de dentro da Exception
            view.exibeMensagem(e.getMessage());
            
        } catch (Exception e) {
            // Banco fora do ar, erro de código, etc
            view.exibeMensagem("Ocorreu um erro inesperado: " + e.getMessage());
            e.printStackTrace(); 
        }
    }

    public void navegarParaLogin() {
        FrLogin telaLogin = new FrLogin();
        telaLogin.setVisible(true);
        view.dispose(); 
    }
}