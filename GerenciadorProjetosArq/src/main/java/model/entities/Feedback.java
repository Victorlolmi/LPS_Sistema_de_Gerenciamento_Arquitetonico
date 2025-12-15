/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entities;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
/**
 *
 * @author Viktin
 */
@Entity
public class Feedback implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataRegistro;
    private String tipo; // "Problema", "Elogio", "Diário"
    private String comentario;
    
    // NOVO CAMPO: Identifica quem mandou ("Gestor" ou "Cliente")
    private String autor; 

    @ManyToOne
    @JoinColumn(name = "projeto_id")
    private Projeto projeto;

    public Feedback() {
        this.dataRegistro = LocalDateTime.now();
    }

    // Atualizei o construtor para receber o Autor
    public Feedback(String tipo, String comentario, Projeto projeto, String autor) {
        this.dataRegistro = LocalDateTime.now();
        this.tipo = tipo;
        this.comentario = comentario;
        this.projeto = projeto;
        this.autor = autor;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(LocalDateTime dataRegistro) { this.dataRegistro = dataRegistro; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public Projeto getProjeto() { return projeto; }
    public void setProjeto(Projeto projeto) { this.projeto = projeto; }
    
    // Novo Getter/Setter
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
}
