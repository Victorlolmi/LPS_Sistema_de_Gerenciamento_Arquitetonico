/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entities;
import java.time.LocalDateTime;
import javax.persistence.*;

/**
 * @author Viktin
 */
@Entity
@Table(name = "usuarios")
// Estratégia JOINED: Normaliza o banco. Dados comuns ficam aqui, dados específicos nas tabelas filhas (Cliente, etc).
@Inheritance(strategy = InheritanceType.JOINED) 
@DiscriminatorColumn(name = "tipo_usuario")
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String senha;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true) 
    private String cpf;

    // Refatorado para camelCase (Java Standard). O name="" garante o match com o banco.
    @Column(name = "codigo_recuperacao")
    private String codigoRecuperacao;

    @Column(name = "validade_codigo_recuperacao")
    private LocalDateTime validadeCodigoRecuperacao;
    
    // Performance: LAZY evita carregar o endereço se não for necessário.
    // Cascade ALL garante que se o user for deletado, o endereço também será.
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    private Endereco endereco;
    
    public Usuario(){}
    
    // Getters e Setters (Boilerplate limpo)

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }
    
    public String getCodigo_recuperacao() {
        return codigoRecuperacao;
    }

    public void setCodigo_recuperacao(String codigoRecuperacao) {
        this.codigoRecuperacao = codigoRecuperacao;
    }

    public LocalDateTime getValidade_codigo_recuperacao() {
        return validadeCodigoRecuperacao;
    }

    public void setValidade_codigo_recuperacao(LocalDateTime validadeCodigoRecuperacao) {
        this.validadeCodigoRecuperacao = validadeCodigoRecuperacao;
    }
    
    @Override
    public String toString() {
        // Facilita a visualização em componentes visuais (ex: ComboBox)
        return this.getNome(); 
    }
}