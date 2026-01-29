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
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionMapper mapper;

    public TransactionDTO createTransaction(TransactionDTO dto) {

        TransactionDomain domain = mapper.toDomain(dto);

        //1. Business logic happens (domain validates itself in constructor)
        //Domain ensures: accountNumber not empty , amount > 0 , type exists.


        //2. Convert Domain to Entity
        Transaction entity = mapper.toEntity(domain);

        //3. Save to database
        Transaction saveEntity = transactionRepository.save(entity);

        //4. Convert back: Entity → Domain → DTO
        TransactionDomain savedDomain = mapper.toDomain(saveEntity);
        return mapper.toDTO(savedDomain);
    }

//Get All Transactions




        Transaction transaction = new Transaction();
        BeanUtils.copyProperties(dto, transaction);
        Transaction saved = transactionRepository.save(transaction);
        return convertToDTO(saved);
    }

    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<TransactionDTO> getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .map(this::convertToDTO);
    }

    public boolean updateTransactionStatus(Long id, String status) {
        Optional<Transaction> opt = transactionRepository.findById(id);
        if (opt.isPresent()) {
            Transaction transaction = opt.get();
            transaction.setStatus(status);
            transactionRepository.save(transaction);
            return true;
        }
        return false;
    }

    private TransactionDTO convertToDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        BeanUtils.copyProperties(transaction, dto);
        return dto;
    }
}