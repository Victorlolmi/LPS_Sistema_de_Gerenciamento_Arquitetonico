/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import model.dao.UsuarioDAO;
import model.entities.Gestor;
import model.entities.Usuario;
import model.entities.Cliente;
import model.entities.Endereco;
import model.exceptions.EnderecoException;
import model.exceptions.UsuarioException; 
import model.valid.ValidadorEndereco;
import model.valid.ValidadorUsuario;     
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
        
        try {
            // 1. Obter dados da View
            String nome = view.getNome();
            String cpf = view.getCpf();
            String email = view.getEmail();
            String senha = view.getSenha();
            boolean isGestor = view.isTipo();
            
            // Dados do Endereço
            String rawCep = view.getCep(); // <--- PEGA O CEP BRUTO
            String cidade = view.getCidade();
            String bairro = view.getBairro();
            String numero = view.getNumero();
            String logradouro = view.getLogradouro();

            // 2. Validações Básicas dos campos de Usuário
            if (nome.isEmpty() || cpf.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                view.exibeMensagem("Todos os campos de cadastro são obrigatórios!");
                return;
            }

            // --- [CORREÇÃO] VALIDAÇÃO E LIMPEZA DO CEP ---
            // 1. Verifica se está vazio (Banco exige NOT NULL)
            if (rawCep == null || rawCep.trim().isEmpty()) {
                view.exibeMensagem("Erro: O CEP é obrigatório.");
                return;
            }

            // 2. Verifica formato (Só números ou com traço)
            if (!rawCep.matches("\\d{8}|\\d{5}-\\d{3}")) {
                view.exibeMensagem("Erro: CEP inválido.\nUse apenas números (Ex: 36000000) ou o formato com traço (Ex: 36000-000).");
                return; 
            }
            // ---------------------------------------------

            // Chamada do Validador de Usuário (Regras de negócio)
            new ValidadorUsuario().validarCadastro(nome, cpf, email, senha, usuarioDAO);

            // Validação simples de CPF (11 dígitos)
            String cpfLimpo = cpf.replaceAll("[^0-9]", ""); 
            if (cpfLimpo.length() != 11) {
                view.exibeMensagem("CPF inválido! Deve conter 11 dígitos.");
                return;
            }
            
            // Verificar duplicidade
            if (usuarioDAO.findByEmailOrCpf(email) != null) {
                view.exibeMensagem("Erro: Email já cadastrado.");
                return;
            }
            if (usuarioDAO.findByEmailOrCpf(cpfLimpo) != null) {
                view.exibeMensagem("Erro: CPF já cadastrado.");
                return;
            }

            // 3. Montar o Endereço (AGORA SEGURO)
            Endereco endereco = new Endereco();
            
            // [IMPORTANTE] Removemos o traço aqui!
            // O banco recebe "36000000" (8 chars), resolvendo o erro 'Data too long'
            endereco.setCep(rawCep.replaceAll("[^0-9]", "")); 
            
            endereco.setCidade(cidade);
            endereco.setBairro(bairro);
            endereco.setNumero(numero);
            endereco.setLogradouro(logradouro);

            // Validador de endereço (se houver regras extras)
            new ValidadorEndereco().validar(endereco);

            // 4. Criptografar a senha
            String senhaComHash = BCrypt.hashpw(senha, BCrypt.gensalt());
            
            // 5. Instanciar o Usuário
            Usuario novoUsuario;

            if (isGestor) {
                novoUsuario = new Gestor();
            } else {
                novoUsuario = new Cliente();
            }

            // Preencher o Usuário
            novoUsuario.setNome(nome);
            novoUsuario.setCpf(cpfLimpo); // Salva CPF limpo
            novoUsuario.setEmail(email);
            novoUsuario.setSenha(senhaComHash);
            
            // [VÍNCULO] Coloca o endereço dentro do usuário
            novoUsuario.setEndereco(endereco);

            // 6. Salvar
            usuarioDAO.salvar(novoUsuario);
            
            view.exibeMensagem("Cadastro realizado com sucesso!");
            navegarParaLogin();

        } catch (UsuarioException e) {
            view.exibeMensagem(e.getMessage());
            
        } catch (EnderecoException e) {
            view.exibeMensagem("Erro no Endereço: " + e.getMessage());
            
        } catch (Exception e) {
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