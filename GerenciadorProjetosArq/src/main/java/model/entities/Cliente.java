/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entities;
import javax.persistence.*;

/**
 * @author Viktin
 */
@Entity
@Table(name = "clientes")
@DiscriminatorValue("CLIENTE")
@PrimaryKeyJoinColumn(name = "id")
public class Cliente extends Usuario {

    // TODO: Adicionar campos específicos de regra de negócio (Ex: CNPJ, LimiteCredito) se necessário.
    // Atualmente funciona apenas como especialização para a estratégia JOINED do JPA.

    public Cliente() {
        super();
    }
}