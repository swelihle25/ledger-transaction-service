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


    private TransactionDTO convertToDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        BeanUtils.copyProperties(transaction, dto);
        return dto;
    }
}