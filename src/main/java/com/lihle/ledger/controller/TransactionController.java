package com.lihle.ledger.controller;

import com.lihle.ledger.dto.TransactionDTO;
import com.lihle.ledger.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;



@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
     @Valid
    public ResponseEntity<TransactionDTO> createTransaction( @RequestBody TransactionDTO dto) {
        TransactionDTO created = transactionService.createTransaction(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
        // This tells Spring: "Convert incoming JSON to TransactionDTO
    }

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        List<TransactionDTO> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions); // Spring converts dto to JSON

    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long id) {

        //The result might or might not exist, so it returns an Optional
        Optional<TransactionDTO> optionalTransaction = transactionService.getTransactionById(id);


        if (optionalTransaction.isPresent()) {

            //if transaction was found extract transactionDTO object from optional
            TransactionDTO transaction = optionalTransaction.get();
            return ResponseEntity.ok(transaction);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


//    // understanding reference method
//    @GetMapping("/{id}")
//    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long id) {
//        return transactionService.getTransactionById(id)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        boolean updated = transactionService.updateTransactionStatus(id, status);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}