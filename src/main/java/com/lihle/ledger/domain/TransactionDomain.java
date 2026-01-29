package com.lihle.ledger.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DOMAIN MODEL - Immutable representation of a Transaction
 * This is the CORE business object. It contains:
 * - Business rules and validation
 * - No JPA annotations (pure business logic)
 */
public final class TransactionDomain {

    private final Long id;
    private final String accountNumber;
    private final BigDecimal amount;
    private final TransactionType transactionType;
    private final String description;
    private final LocalDateTime transactionDate;
    private final TransactionStatus status;
    private final LocalDateTime createdAt;

    // Private constructor - use Builder to create instances
    private TransactionDomain(Builder builder) {
        this.id = builder.id;
        this.accountNumber = builder.accountNumber;
        this.amount = builder.amount;
        this.transactionType = builder.transactionType;
        this.description = builder.description;
        this.transactionDate = builder.transactionDate;
        this.status = builder.status;
        this.createdAt = builder.createdAt;

        // Business validation in constructor
        validate();
    }

    private void validate() {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be empty");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (transactionType == null) {
            throw new IllegalArgumentException("Transaction type is required");
        }
    }

    // BUSINESS BEHAVIOR: Mark transaction as disputed
    public TransactionDomain markAsDisputed() {
        if (this.status == TransactionStatus.REVERSED) {
            throw new IllegalStateException("Cannot dispute a reversed transaction");
        }
        return new Builder()
                .from(this)
                .status(TransactionStatus.DISPUTED)
                .build();
    }

    // BUSINESS BEHAVIOR: Reverse transaction (after dispute approved)
    public TransactionDomain reverse() {
        if (this.status != TransactionStatus.DISPUTED) {
            throw new IllegalStateException("Can only reverse disputed transactions");
        }
        return new Builder()
                .from(this)
                .status(TransactionStatus.REVERSED)
                .build();
    }

    // BUSINESS BEHAVIOR: Complete transaction (dispute rejected)
    public TransactionDomain complete() {
        return new Builder()
                .from(this)
                .status(TransactionStatus.COMPLETED)
                .build();
    }

    // BUSINESS QUERY: Is this transaction disputed?
    public boolean isDisputed() {
        return this.status == TransactionStatus.DISPUTED;
    }

    // BUSINESS QUERY: Can this transaction be disputed?
    public boolean canBeDisputed() {
        return this.status == TransactionStatus.COMPLETED;
    }

    // Getters only
    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Builder Pattern for creating immutable objects
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String accountNumber;
        private BigDecimal amount;
        private TransactionType transactionType;
        private String description;
        private LocalDateTime transactionDate;
        private TransactionStatus status;
        private LocalDateTime createdAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder accountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder transactionType(TransactionType transactionType) {
            this.transactionType = transactionType;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder transactionDate(LocalDateTime transactionDate) {
            this.transactionDate = transactionDate;
            return this;
        }

        public Builder status(TransactionStatus status) {
            this.status = status;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        // Copy from existing domain object
        public Builder from(TransactionDomain transaction) {
            this.id = transaction.id;
            this.accountNumber = transaction.accountNumber;
            this.amount = transaction.amount;
            this.transactionType = transaction.transactionType;
            this.description = transaction.description;
            this.transactionDate = transaction.transactionDate;
            this.status = transaction.status;
            this.createdAt = transaction.createdAt;
            return this;
        }

        public TransactionDomain build() {
            // Set defaults
            if (transactionDate == null) {
                transactionDate = LocalDateTime.now();
            }
            if (status == null) {
                status = TransactionStatus.COMPLETED;
            }
            if (createdAt == null) {
                createdAt = LocalDateTime.now();
            }

            return new TransactionDomain(this);
        }
    }

    // Value Objects (Enums)
    public enum TransactionType {
        DEBIT, CREDIT
    }

    public enum TransactionStatus {
        PENDING, COMPLETED, DISPUTED, REVERSED
    }

    // Equals and HashCode for domain identity
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionDomain that = (TransactionDomain) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TransactionDomain{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", amount=" + amount +
                ", transactionType=" + transactionType +
                ", status=" + status +
                '}';
    }
}