package model;

public class Inventory {
    private int itemId;
    private String itemName;
    private int quantity;
    private int supplierId;

    public Inventory(int itemId, String itemName, int quantity, int supplierId) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.supplierId = supplierId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }
}