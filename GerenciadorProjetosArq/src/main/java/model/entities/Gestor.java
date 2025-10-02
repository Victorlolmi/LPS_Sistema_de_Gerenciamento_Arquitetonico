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
@Table(name = "gestores")
@DiscriminatorValue("GESTOR")
public class Gestor extends Usuario {
    
}
