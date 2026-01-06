package model.valid;

import model.dao.UsuarioDAO;
import model.exceptions.UsuarioException;

public class ValidadorUsuario {
    
    public void validarCadastro(String nome, String cpf, String email, String senha, UsuarioDAO usuarioDAO) throws UsuarioException {
        
        if (nome.isEmpty() || cpf.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            throw new UsuarioException("Todos os campos são obrigatórios!");
        }

        // Sanitiza a máscara para validar apenas os dígitos
        String cpfLimpo = cpf.replaceAll("[.\\-]", "");
        
        if (cpfLimpo.length() != 11) {
            throw new UsuarioException("CPF inválido! Deve conter 11 dígitos.");
        }
        
        if (usuarioDAO.findByEmailOrCpf(email) != null || usuarioDAO.findByEmailOrCpf(cpfLimpo) != null) {
            throw new UsuarioException("Erro: CPF ou Email já cadastrado no sistema.");
        }
    }
}