package mnsk.beans;

import mnsk.App;

/**
 * Author: S.Rogachevsky
 * Date: 30.11.16
 * Time: 8:54
 */
public class ImportNode {


    private String sku = "";
    private String name = "";
    private String image = "";
    public String price = "-1";
    public String PRICE_CURRENCY = "BYR";

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPRICE_CURRENCY() {
        return PRICE_CURRENCY;
    }

    public void setPRICE_CURRENCY(String PRICE_CURRENCY) {
        this.PRICE_CURRENCY = PRICE_CURRENCY;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer()
                .append(this.getSku()).append(App.CSV_SEPARATOR)
                .append(this.getName()).append(App.CSV_SEPARATOR)
                .append(this.getImage()).append(App.CSV_SEPARATOR)
                .append(this.price).append(App.CSV_SEPARATOR)
                .append(this.PRICE_CURRENCY).append(System.lineSeparator());
        return sb.toString();
    }
}
