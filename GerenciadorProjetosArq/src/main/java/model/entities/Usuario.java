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
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED) //  definimos a estratégia a qual conecta o usuario aos seus resltados pelo id
@DiscriminatorColumn(name = "tipo_usuario") // Coluna que identifica o tipo
public abstract class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//Auto incremento
    private Long id;
    @Column(nullable = false)
    private String nome;
    @Column(nullable = false, unique = true)// campo unico em todo BD e nao pode ser vazio
    private String senha;
    @Column(nullable = false,unique = true)
    private String email;
    @Column(nullable = false, unique = true) 
    private String cpf;
    
    // Ligacao com o JPA
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    private Endereco endereco;
    
    public Usuario(){}
    
    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }
    
    /**
     * @return the nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * @param nome the nome to set
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }


    /**
     * @return the senha
     */
    public String getSenha() {
        return senha;
    }

    /**
     * @param senha the senha to set
     */
    public void setSenha(String senha) {
        this.senha = senha;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the cpf
     */
    public String getCpf() {
        return cpf;
    }

    /**
     * @param cpf the cpf to set
     */
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    /**
     * @return the endereco
     */
    public Endereco getEndereco() {
        return endereco;
    }

    /**
     * @param endereco the endereco to set
     */
    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }
    
    
    
}
