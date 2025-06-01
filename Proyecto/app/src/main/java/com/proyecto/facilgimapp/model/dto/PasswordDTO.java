/**
 * Data Transfer Object (DTO) for handling password information.
 * This class is used to encapsulate a password value for transfer between processes.
 * 
 * @author Francisco Santana
 */
package com.proyecto.facilgimapp.model.dto;

public class PasswordDTO {
    private String password;
    public PasswordDTO() {}
    public PasswordDTO(String password) { this.password = password; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
