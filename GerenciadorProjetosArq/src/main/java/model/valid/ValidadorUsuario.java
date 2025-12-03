
package model.valid;

import model.dao.UsuarioDAO;
import model.exceptions.UsuarioException;

public class ValidadorUsuario {
    public void validarCadastro(String nome, String cpf, String email, String senha, UsuarioDAO usuarioDAO) throws UsuarioException {
        
        // Validação 1: Campos Vazios
        if (nome.isEmpty() || cpf.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            throw new UsuarioException("Todos os campos são obrigatórios!");
        }

        // Validação 2: CPF
        String cpfLimpo = cpf.replaceAll("[.\\-]", "");
        if (cpfLimpo.length() != 11) {
            throw new UsuarioException("CPF inválido! Deve conter 11 dígitos.");
        }
        
        // Validação 3: Duplicidade (precisa do DAO aqui ou passar como parametro)
        UsuarioDAO dao = new UsuarioDAO(); 
        if (dao.findByEmailOrCpf(email) != null || dao.findByEmailOrCpf(cpfLimpo) != null) {
            throw new UsuarioException("Erro: CPF ou Email já cadastrado no sistema.");
        }
    }
}
