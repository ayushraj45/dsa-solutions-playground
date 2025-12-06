//Design a Spring Data JPA service method that transfers money between two accounts using pessimistic locking to prevent race conditions. Handle the scenario where multiple simultaneous transfers could cause overdrafts.
@Service
@Transactional
public class AccountService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    public void transferFunds(Long fromAccountId, Long toAccountId, 
                             BigDecimal amount) throws InsufficientFundsException {
        // Always lock in ID order to prevent deadlocks
        Long firstLockId = Math.min(fromAccountId, toAccountId);
        Long secondLockId = Math.max(fromAccountId, toAccountId);
        
        Account firstAccount = accountRepository
            .findByIdWithLock(firstLockId)
            .orElseThrow(() -> new AccountNotFoundException());
            
        Account secondAccount = accountRepository
            .findByIdWithLock(secondLockId)
            .orElseThrow(() -> new AccountNotFoundException());
        
        Account fromAccount = firstLockId.equals(fromAccountId) ? 
            firstAccount : secondAccount;
        Account toAccount = firstLockId.equals(toAccountId) ? 
            firstAccount : secondAccount;
        
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }
        
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));
        
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
    }
}

// Repository method
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.id = :id")
    Optional<Account> findByIdWithLock(@Param("id") Long id);
}
