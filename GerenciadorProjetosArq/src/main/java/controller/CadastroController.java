/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import model.dao.UsuarioDAO;
import model.entities.Gestor;
import model.entities.Usuario;
import model.entities.Cliente; // Usaremos a entidade Cliente como padrão para novos cadastros
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

   
    public void realizarCadastro() {
        // 1. Obter dados da View
        String nome = view.getNome();
        String cpf = view.getCpf();
        String email = view.getEmail();
        String senha = view.getSenha();
        boolean isGestor = view.isTipo();

        // 2. Validar os dados de entrada (tratamento de exceções de negócio)
        if (nome.isEmpty() || cpf.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            view.exibeMensagem("Todos os campos são obrigatórios!");
            return;
        }

        // Validação simples de CPF (apenas para garantir que não está em branco e tem 11 dígitos)
        String cpfLimpo = cpf.replaceAll("[.\\-]", "");
        if (cpfLimpo.length() != 3) {
            view.exibeMensagem("CPF inválido! Deve conter 11 dígitos.");
            return;
        }

        // 3. Verificar se o usuário já existe
        if (usuarioDAO.findByEmailOrCpf(email) != null || usuarioDAO.findByEmailOrCpf(cpfLimpo) != null) {
            view.exibeMensagem("Erro: CPF ou Email já cadastrado no sistema.");
            return;
        }

        // 4. Criptografar a senha
        String senhaComHash = BCrypt.hashpw(senha, BCrypt.gensalt());
        Usuario novoUsuario;
        
        if (isGestor) {
            Gestor gestor = new Gestor();
            // Se o Gestor tivesse campos extras, você os definiria aqui
            // Ex: gestor.setNivelAcesso(5);
            novoUsuario = gestor; // Atribui o objeto Gestor à variável pai
            System.out.println("Criando um novo GESTOR."); // Log para debug
        } else {
            Cliente cliente = new Cliente();
            // Se o Cliente tivesse campos extras, você os definiria aqui
            novoUsuario = cliente; // Atribui o objeto Cliente à variável pai
            System.out.println("Criando um novo CLIENTE."); // Log para debug
        }

        // 5. Criar o novo objeto Cliente
        novoUsuario.setNome(nome);
        novoUsuario.setCpf(cpfLimpo);
        novoUsuario.setEmail(email);
        novoUsuario.setSenha(senhaComHash);

        // 6. Salvar no banco de dados
        try {
            usuarioDAO.salvar(novoUsuario);
            view.exibeMensagem("Cadastro realizado com sucesso!");
            
            // Navega de volta para a tela de login
            navegarParaLogin();

        } catch (Exception e) {
            view.exibeMensagem("Ocorreu um erro ao realizar o cadastro: " + e.getMessage());
            e.printStackTrace(); // Loga o erro no console para debug
        }
    }

    public void navegarParaLogin() {
        FrLogin telaLogin = new FrLogin();
        telaLogin.setVisible(true);
        view.dispose(); // Fecha a tela de cadastro
    }
}
