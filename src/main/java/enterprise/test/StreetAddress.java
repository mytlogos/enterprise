package enterprise.test;

/**
 *
 */
public class StreetAddress implements Address {
    private int id;
    private int address;

    StreetAddress() {

    }

    public StreetAddress(int id, int address) {
        this.id = id;
        this.address = address;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getAddress() {
        return address;
    }

    @Override
    public void setAddress(int address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "StreetAddress{" +
                "id=" + id +
                ", address=" + address +
                '}';
    }
}
