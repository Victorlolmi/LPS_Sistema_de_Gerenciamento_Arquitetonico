/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entities;
import javax.persistence.*;
/**
 *
 * @author Viktin
 */
@Entity
@Table(name = "clientes") // Tabela separada para dados específicos do cliente
@DiscriminatorValue("CLIENTE") // O valor que vai entrar automático na coluna 'tipo_usuario'
@PrimaryKeyJoinColumn(name = "id") // Une a tabela clientes com usuarios pelo ID
public class Cliente extends Usuario {

    // Se o cliente tiver campos exclusivos (ex: CNPJ), coloque aqui.
    // Se não, pode deixar a classe assim mesmo, herdando tudo de Usuario.
    
    // Exemplo (opcional):
    // @Column(name = "cnpj")
    // private String cnpj;
    
    public Cliente() {
        super(); // Chama o construtor do Usuario
    }
}