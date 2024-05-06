package bankaccount;

public abstract class CustomerState {
    protected CustomerController context;

    public void setContext(CustomerController context) {
        this.context = context;
    }

    public abstract void purchaseOnline(double purchaseAmount);
}
