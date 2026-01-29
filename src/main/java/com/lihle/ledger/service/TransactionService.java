package com.lihle.ledger.service;

import com.lihle.ledger.domain.TransactionDomain;
import com.lihle.ledger.dto.TransactionDTO;
import com.lihle.ledger.entity.Transaction;
import com.lihle.ledger.mapper.TransactionMapper;
import com.lihle.ledger.repository.TransactionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Flow:
 * 1. Receives DTO from controller
 * 2. Converts DTO → Domain (using Mapper)
 * 3. Executes business logic on Domain
 * 4. Converts Domain → DTO (using Mapper)
 * 5. Saves Entity via Repository
 * 6. Converts Entity → Domain → DTO
 * 7. Return DTO to Controller
 * */

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private TransactionMapper mapper;

    public TransactionDTO createTransaction(TransactionDTO dto) {

        TransactionDomain domain = mapper.toDomain(dto);

        //1. Business logic happens (domain validates itself in constructor)
        //Domain ensures: accountNumber not empty , amount > 0 , type exists.

        //2. Convert Domain to Entity
        Transaction entity = mapper.toEntity(domain);

        //3. Save to database
        Transaction saveEntity = repository.save(entity);

        //4. Convert back: Entity → Domain → DTO
        TransactionDomain savedDomain = mapper.toDomain(saveEntity);
        return mapper.toDTO(savedDomain);
    }

    //Get All Transactions
    public List<TransactionDTO> getAllTransactions() {
        return repository.findAll()
                .stream()
                .map(mapper::toDomain)
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<TransactionDTO> getTransactionById(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain)
                .map(mapper::toDTO);
    }

    /**
     * UPDATE TRANSACTION STATUS
     *
     * This method uses DOMAIN BEHAVIOR to change status
     */

    public boolean updateTransactionStatus(Long id, String statusString) {
        Optional<Transaction> entityOpt = repository.findById(id);
        if (entityOpt.isEmpty()) {
            return false;
        }


        // 1. Convert Entity → Domain
        TransactionDomain domain = mapper.toDomain(entityOpt.get());

        //2. Use domain behavior based on status
        TransactionDomain updatedDomain;
        if("DISPUTED".equalsIgnoreCase(statusString)) {
            updatedDomain = domain.markAsDisputed();  // Business logic in domain!
        } else if ("REVERSED".equalsIgnoreCase(statusString)){
            updatedDomain = domain.reverse();
        } else if ("COMPLETED".equalsIgnoreCase(statusString)){
            updatedDomain = domain.reverse();
        } else {
            throw new IllegalArgumentException("Invalid status:" + statusString);
        }

        //3. Convert Domain → Entity and save
        Transaction updatedEntity = mapper.toEntity(updatedDomain);
        updatedEntity.setId(id); // Ensure ID is preserved
        repository.save(updatedEntity);

        return true;
    }

    /**
     * GET TRANSACTIONS BY ACCOUNT
   */
    public List<TransactionDTO> getTransactionsByAccount(String accountNumber) {
        return repository.findByAccountNumber(accountNumber)
                .stream()
                .map(mapper:: toDomain)
                .map(mapper:: toDTO)
                .collect(Collectors.toList());
    }

    /**
     * GET TRANSACTIONS BY STATUS
     */
    public List<TransactionDTO> getTransactionsByStatus(String status) {
        return repository.findByStatus(status)
                .stream()
                .map(mapper:: toDomain)
                .map(mapper:: toDTO)
                .collect(Collectors.toList());

    }

}