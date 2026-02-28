package multihreading;

public class BankAccount {
    private final int id;
    private int balance;

    public BankAccount(int id, int initialBalance){
        this.id = id;
        this.balance = initialBalance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getBalance() {
        return balance;
    }

    public int getId() {
        return id;
    }

    public void add(int sum){
        if (sum < 0){
            throw new IllegalArgumentException("add sum cannot be negative");
        }
        balance += sum;
    }

    public void writeOff(int sum){
        if (sum < 0){
            throw new IllegalArgumentException("writeOff sum cannot be negative");
        }
        balance -= sum;
    }
}
