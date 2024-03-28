package es.boffmedia.teras.objects;

public class ShopTransaction {
    private String uuid;
    private String npcName;
    private String itemName;
    private String operation;
    private int unitPrice;
    private int count;

    public ShopTransaction(String uuid, String npcName, String itemName, String operation, int unitPrice, int count) {
        this.uuid = uuid;
        this.npcName = npcName;
        this.itemName = itemName;
        this.operation = operation;
        this.unitPrice = unitPrice;
        this.count = count;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNpcName() {
        return npcName;
    }

    public String getItemName() {
        return itemName;
    }

    public String getOperation() {
        return operation;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public int getCount() {
        return count;
    }

    public void setNpcName(String npcName) {
        this.npcName = npcName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "ShopTransaction{" +
                "npcName='" + npcName + '\'' +
                ", itemName='" + itemName + '\'' +
                ", operation='" + operation + '\'' +
                ", unitPrice=" + unitPrice +
                ", count=" + count +
                '}';
    }
}
