package com.mwasilew.server_app.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "factorization_results")
public class FactorizationResult {
    private Integer number;
    @Column(nullable = false, columnDefinition = "JSON")
    private String factors;

    @Id
    public Integer getNumber() {
        return number;
    }
}
