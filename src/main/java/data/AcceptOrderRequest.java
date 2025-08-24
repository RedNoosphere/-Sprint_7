package data;

public class AcceptOrderRequest {
    private int courierId;

    public AcceptOrderRequest() {
    }

    public AcceptOrderRequest(int courierId) {
        this.courierId = courierId;
    }

    public int getCourierId() { return courierId; }
    public void setCourierId(int courierId) { this.courierId = courierId; }
}