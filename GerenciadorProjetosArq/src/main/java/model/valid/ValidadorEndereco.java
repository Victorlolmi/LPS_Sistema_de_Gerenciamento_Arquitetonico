package model.valid;

import model.entities.Endereco;
import model.exceptions.EnderecoException;

public class ValidadorEndereco {

    public void validar(Endereco endereco) throws EnderecoException {
        // Fail-fast para evitar NPE mais adiante
        if (endereco == null) {
            throw new EnderecoException("Os dados do endereço não foram preenchidos.");
        }

        // Sanitiza input para validar apenas os dígitos (ignora máscara do front)
        if (endereco.getCep() == null || endereco.getCep().replaceAll("\\D", "").length() != 8) {
            throw new EnderecoException("CEP inválido! O CEP deve conter 8 números.");
        }

        if (endereco.getLogradouro() == null || endereco.getLogradouro().trim().isEmpty()) {
            throw new EnderecoException("O Logradouro (Rua/Av) é obrigatório.");
        }

        if (endereco.getBairro() == null || endereco.getBairro().trim().isEmpty()) {
            throw new EnderecoException("O Bairro é obrigatório.");
        }
        
        // Regra de negócio: Bloqueia nulos. Se não houver número, força o uso de "S/N" explícito.
        if (endereco.getNumero() == null || endereco.getNumero().trim().isEmpty()) {
            throw new EnderecoException("O Número é obrigatório (use S/N se necessário).");
        }
    }
}