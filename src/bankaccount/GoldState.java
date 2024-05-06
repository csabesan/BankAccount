package bankaccount;

public class GoldState extends CustomerState {

    @Override
    public void purchaseOnline(double purchaseAmount) {
        if (purchaseAmount >= 50) {
            double fee = 10.0;
            if (context.getBalance() >= purchaseAmount + fee) {
                context.setBalance(context.getBalance() - (purchaseAmount + fee));
                context.updateBalanceLabel();
                context.updateLevel();
            } else {
                context.getInsufficientFundsLabel().setText("Insufficient Funds");
            }
        } else {
            context.getInsufficientFundsLabel().setText("Purchase must be at least $50!");
        }
    }
}
