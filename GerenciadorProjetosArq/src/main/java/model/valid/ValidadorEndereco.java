package model.valid;

import model.entities.Endereco;
import model.exceptions.EnderecoException;

public class ValidadorEndereco {

    public void validar(Endereco endereco) throws EnderecoException {
        // 1. Verifica se o objeto existe
        if (endereco == null) {
            throw new EnderecoException("Os dados do endereço não foram preenchidos.");
        }

        // 2. Validação do CEP
        if (endereco.getCep() == null || endereco.getCep().replaceAll("\\D", "").length() != 8) {
            throw new EnderecoException("CEP inválido! O CEP deve conter 8 números.");
        }

        // 3. Campos Obrigatórios
        if (endereco.getLogadouro() == null || endereco.getLogadouro().trim().isEmpty()) {
            throw new EnderecoException("O Logradouro (Rua/Av) é obrigatório.");
        }

        if (endereco.getBairro() == null || endereco.getBairro().trim().isEmpty()) {
            throw new EnderecoException("O Bairro é obrigatório.");
        }
        
        if (endereco.getNumero() == null || endereco.getNumero().trim().isEmpty()) {
            throw new EnderecoException("O Número é obrigatório (use S/N se necessário).");
        }
    }
}