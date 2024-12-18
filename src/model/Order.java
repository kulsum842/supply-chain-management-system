package model;

import java.time.LocalDate;

public class Order {
    private int orderId;
    private LocalDate orderDate;
    private String customerName;
    private int itemId;         
    private int quantity;       
    private String shipmentStatus; 
    private String paymentStatus;  

    public Order(int orderId, LocalDate orderDate, String customerName, int itemId, int quantity, String shipmentStatus, String paymentStatus) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.customerName = customerName;
        this.itemId = itemId;
        this.quantity = quantity;
        this.shipmentStatus = shipmentStatus;
        this.paymentStatus = paymentStatus;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getShipmentStatus() {
        return shipmentStatus;
    }

    public void setShipmentStatus(String shipmentStatus) {
        this.shipmentStatus = shipmentStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", orderDate=" + orderDate +
                ", customerName='" + customerName + '\'' +
                ", itemId=" + itemId +
                ", quantity=" + quantity +
                ", shipmentStatus='" + shipmentStatus + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}
