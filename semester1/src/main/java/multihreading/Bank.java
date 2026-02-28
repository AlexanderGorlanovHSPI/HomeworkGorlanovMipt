package multihreading;

public class Bank {
    public static void sendToAccountDeadlock(BankAccount from, BankAccount to, int amount){
        if (from == null || to == null) {
            throw new IllegalArgumentException("Accounts cannot be null");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        synchronized (from){
            synchronized (to){
                from.writeOff(amount);
                to.add(amount);
            }
        }
    }
    public static void sendToAccount(BankAccount from, BankAccount to, int amount){
        if (from == null || to == null) {
            throw new IllegalArgumentException("Accounts cannot be null");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }


        BankAccount first = from.getId() < to.getId() ? from : to;
        BankAccount second = from.getId() < to.getId() ? to : from;

        synchronized (first){
            synchronized (second){
                if (from.getBalance() >= amount) {
                    from.writeOff(amount);
                    to.add(amount);
                    System.out.println(Thread.currentThread().getName() + " safely transferred " + amount);
                } else {
                    System.out.println(Thread.currentThread().getName() + " insufficient funds");
                }
            }
        }
    }
}
