package pl.ecommerce.project.payload.dto;

import lombok.Data;

@Data
public class ContactRequestDTO {
    private String name;
    private String email;
    private String message;
}
