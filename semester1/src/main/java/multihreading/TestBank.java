package multihreading;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class TestBank {

    @Test
    void testBasicTransfer() {
        BankAccount account1 = new BankAccount(1, 1000);
        BankAccount account2 = new BankAccount(2, 1000);
        Bank bank = new Bank();

        bank.sendToAccount(account1, account2, 500);

        assertEquals(500, account1.getBalance());
        assertEquals(1500, account2.getBalance());
    }

    @Test
    void testNegativeAmount() {
        BankAccount account1 = new BankAccount(1, 1000);
        BankAccount account2 = new BankAccount(2, 1000);
        Bank bank = new Bank();

        assertThrows(IllegalArgumentException.class, () -> {
            bank.sendToAccount(account1, account2, -100);
        });
    }

    @Test
    void testNullAccounts() {
        BankAccount account1 = new BankAccount(1, 1000);
        Bank bank = new Bank();

        assertThrows(IllegalArgumentException.class, () -> {
            bank.sendToAccount(account1, null, 100);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            bank.sendToAccount(null, account1, 100);
        });
    }

    @Test
    void testConcurrentSafeTransfers() throws InterruptedException {
        BankAccount account1 = new BankAccount(1, 10000);
        BankAccount account2 = new BankAccount(2, 10000);
        Bank bank = new Bank();

        int threadCount = 10;
        int transfersPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < transfersPerThread; j++) {
                        bank.sendToAccount(account1, account2, 10);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < transfersPerThread; j++) {
                        bank.sendToAccount(account2, account1, 5);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        int totalBalance = account1.getBalance() + account2.getBalance();
        assertEquals(20000, totalBalance, "Total balance should remain constant");
    }

    @Test
    @Timeout(5)
    void testDeadlockScenario() throws InterruptedException {
        BankAccount account1 = new BankAccount(1, 1000);
        BankAccount account2 = new BankAccount(2, 1000);
        Bank bank = new Bank();

        AtomicBoolean deadlockOccurred = new AtomicBoolean(false);

        Thread thread1 = new Thread(() -> {
            try {
                bank.sendToAccountDeadlock(account1, account2, 100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "Thread-1");

        Thread thread2 = new Thread(() -> {
            try {
                bank.sendToAccountDeadlock(account2, account1, 100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "Thread-2");

        thread1.start();
        thread2.start();

        thread1.join(2000);
        thread2.join(2000);

        if (thread1.isAlive() || thread2.isAlive()) {
            deadlockOccurred.set(true);
            System.out.println("Deadlock detected! Threads are still alive after timeout.");

            thread1.interrupt();
            thread2.interrupt();
        }

        System.out.println("Deadlock test completed. Deadlock occurred: " + deadlockOccurred.get());
    }

    @Test
    void testMultipleAccounts() throws InterruptedException {
        BankAccount[] accounts = new BankAccount[5];
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new BankAccount(i + 1, 1000);
        }
        Bank bank = new Bank();

        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(20);

        for (int i = 0; i < 20; i++) {
            final int fromIndex = i % accounts.length;
            final int toIndex = (i + 1) % accounts.length;
            final int amount = (i % 3 + 1) * 50;

            executor.submit(() -> {
                try {
                    bank.sendToAccount(accounts[fromIndex], accounts[toIndex], amount);
                } catch (Exception e) {
                    System.err.println("Transfer failed: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(10, TimeUnit.SECONDS), "All transfers should complete within timeout");
        executor.shutdown();

        int totalBalance = 0;
        for (BankAccount account : accounts) {
            totalBalance += account.getBalance();
        }
        assertEquals(5000, totalBalance, "Total balance should remain constant");
    }
}