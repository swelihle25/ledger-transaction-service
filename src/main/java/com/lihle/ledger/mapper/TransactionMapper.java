package com.lihle.ledger.mapper;

import com.lihle.ledger.domain.TransactionDomain;
import com.lihle.ledger.dto.TransactionDTO;
import com.lihle.ledger.entity.Transaction;
import org.springframework.stereotype.Component;

/**
 * MAPPER - Converts between different representations
 * Layers:
 * Controller ←→ DTO ←→ Domain ←→ Entity ←→ Database
 * This mapper handles:
 * 1. DTO → Domain (API request to business object)
 * 2. Domain → DTO (Business object to API response)
 * 3. Domain → Entity (Business object to database model)
 * 4. Entity → Domain (Database model to business object)
 */
@Component
public class TransactionMapper {

    /**
     * DTO → DOMAIN
     * Used when: Controller receives a request and needs to pass it to Service.
     * @param dto - Data from API request
     * @return Domain object for business logic+
     */
    public TransactionDomain toDomain(TransactionDTO dto) {
        if (dto == null) {
            return null;
        }

        return TransactionDomain.builder()
                .id(dto.getId())
                .accountNumber(dto.getAccountNumber())
                .amount(dto.getAmount())
                .transactionType(mapToTransactionType(dto.getTransactionType()))
                .description(dto.getDescription())
                .transactionDate(dto.getTransactionDate())
                .status(mapToTransactionStatus(dto.getStatus()))
                .createdAt(dto.getCreatedAt())
                .build();
    }

    /**
     * DOMAIN → DTO
     * Used when: Service returns result to Controller
     *
     * @param domain - Business object from service layer
     * @return DTO for API response
     */
    public TransactionDTO toDTO(TransactionDomain domain) {
        if (domain == null) {
            return null;
        }

        TransactionDTO dto = new TransactionDTO();
        dto.setId(domain.getId());
        dto.setAccountNumber(domain.getAccountNumber());
        dto.setAmount(domain.getAmount());
        dto.setTransactionType(domain.getTransactionType().name());
        dto.setDescription(domain.getDescription());
        dto.setTransactionDate(domain.getTransactionDate());
        dto.setStatus(domain.getStatus().name());
        dto.setCreatedAt(domain.getCreatedAt());

        return dto;
    }

    /**
     * ENTITY → DOMAIN
     * Used when: Repository retrieves data from database
     *
     * @param entity - JPA entity from database
     * @return Domain object for business logic
     */
    public TransactionDomain toDomain(Transaction entity) {
        if (entity == null) {
            return null;
        }

        return TransactionDomain.builder()
                .id(entity.getId())
                .accountNumber(entity.getAccountNumber())
                .amount(entity.getAmount())
                .transactionType(mapToTransactionType(entity.getTransactionType()))
                .description(entity.getDescription())
                .transactionDate(entity.getTransactionDate())
                .status(mapToTransactionStatus(entity.getStatus()))
                .createdAt(entity.getCreatedAt())
                .build();
    }

    /**
     * DOMAIN → ENTITY
     * Used when: Service needs to save to database
     *
     * @param domain - Business object
     * @return JPA entity for database persistence
     */
    public Transaction toEntity(TransactionDomain domain) {
        if (domain == null) {
            return null;
        }

        Transaction entity = new Transaction();
        entity.setId(domain.getId());
        entity.setAccountNumber(domain.getAccountNumber());
        entity.setAmount(domain.getAmount());
        entity.setTransactionType(domain.getTransactionType().name());
        entity.setDescription(domain.getDescription());
        entity.setTransactionDate(domain.getTransactionDate());
        entity.setStatus(domain.getStatus().name());
        // createdAt is set by @PrePersist in entity

        return entity;
    }

    // Helper methods to convert enums
    private TransactionDomain.TransactionType mapToTransactionType(String type) {
        if (type == null) {
            return null;
        }
        return TransactionDomain.TransactionType.valueOf(type.toUpperCase());
    }

    private TransactionDomain.TransactionStatus mapToTransactionStatus(String status) {
        if (status == null) {
            return null;
        }
        return TransactionDomain.TransactionStatus.valueOf(status.toUpperCase());
    }
}