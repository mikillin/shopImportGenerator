package mnsk.beans.export;

import mnsk.App;

/**
 * Author: S.Rogachevsky
 * Date: 30.11.16
 * Time: 8:54
 */
public class ProductImporter {


    public static final String DEFAULT_STATUS_ON = "1";
    public static final String DEFAULT_STATUS_OFF = "0";
    public static final String DEFAULT_USTANOVKA = "Возможна рассрочка";
    public static final String DEFAULT_DOSTAVKA = "2-25 дней";

    private String SKU = "";
    private String name = "";
    private String category = "";
    private String pod_category = "";
    private String gabarity = "";
    private String brand = "";
    private String material = "";
    private String priznak = "";
    private String ustanovka = "";
    private String dostavka = "";
    private String type = "";
    private String kype_razmesch = "";
    private String kype_kolichestvo = "";
    private String softmebel_mehanizm = "";
    private String matracy_razmer = "";
    private String matracy_sostav = "";
    private String opisanie = "";
    private String status = "";
    private String pohozhye = "";
    private String color = "";
    private String specialKeyID = "";
    private String htmlLink="";


    public String getSKU() {
        return SKU;
    }

    public void setSKU(String SKU) {
        this.SKU = SKU;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPod_category() {
        return pod_category;
    }

    public void setPod_category(String pod_category) {
        this.pod_category = pod_category;
    }

    public String getGabarity() {
        return gabarity;
    }

    public void setGabarity(String gabarity) {
        this.gabarity = gabarity;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getPriznak() {
        return priznak;
    }

    public void setPriznak(String priznak) {
        this.priznak = priznak;
    }

    public String getUstanovka() {
        return ustanovka;
    }

    public void setUstanovka(String ustanovka) {
        this.ustanovka = ustanovka;
    }

    public String getDostavka() {
        return dostavka;
    }

    public void setDostavka(String dostavka) {
        this.dostavka = dostavka;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKype_razmesch() {
        return kype_razmesch;
    }

    public void setKype_razmesch(String kype_razmesch) {
        this.kype_razmesch = kype_razmesch;
    }

    public String getKype_kolichestvo() {
        return kype_kolichestvo;
    }

    public void setKype_kolichestvo(String kype_kolichestvo) {
        this.kype_kolichestvo = kype_kolichestvo;
    }

    public String getSoftmebel_mehanizm() {
        return softmebel_mehanizm;
    }

    public void setSoftmebel_mehanizm(String softmebel_mehanizm) {
        this.softmebel_mehanizm = softmebel_mehanizm;
    }

    public String getMatracy_razmer() {
        return matracy_razmer;
    }

    public void setMatracy_razmer(String matracy_razmer) {
        this.matracy_razmer = matracy_razmer;
    }

    public String getMatracy_sostav() {
        return matracy_sostav;
    }

    public void setMatracy_sostav(String matracy_sostav) {
        this.matracy_sostav = matracy_sostav;
    }

    public String getOpisanie() {
        return opisanie;
    }

    public void setOpisanie(String opisanie) {
        this.opisanie = opisanie;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPohozhye() {
        return pohozhye;
    }

    public void setPohozhye(String pohozhye) {
        this.pohozhye = pohozhye;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getHtmlLink() {
        return htmlLink;
    }

    public void setHtmlLink(String htmlLink) {
        this.htmlLink = htmlLink;
    }

    public String getSpecialKeyID() {
        return specialKeyID;
    }

    public void setSpecialKeyID(String specialKeyID) {
        this.specialKeyID = specialKeyID;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer()
                .append(this.getSKU()).append(App.CSV_SEPARATOR)
                .append(this.getSKU()).append(App.CSV_SEPARATOR)
                .append(this.getName()).append(App.CSV_SEPARATOR)
                .append(this.getCategory()).append(App.CSV_SEPARATOR)
                .append(this.getPod_category()).append(App.CSV_SEPARATOR)
                .append(this.getGabarity()).append(App.CSV_SEPARATOR)
                .append(this.getBrand()).append(App.CSV_SEPARATOR)
                .append(this.getMaterial()).append(App.CSV_SEPARATOR)
                .append(this.getPriznak()).append(App.CSV_SEPARATOR)
                .append(this.getUstanovka()).append(App.CSV_SEPARATOR)
                .append(this.getDostavka()).append(App.CSV_SEPARATOR)
                .append(this.getType()).append(App.CSV_SEPARATOR)
                .append(this.getKype_razmesch()).append(App.CSV_SEPARATOR)
                .append(this.getKype_kolichestvo()).append(App.CSV_SEPARATOR)
                .append(this.getSoftmebel_mehanizm()).append(App.CSV_SEPARATOR)
                .append(this.getMatracy_razmer()).append(App.CSV_SEPARATOR)
                .append(this.getMatracy_sostav()).append(App.CSV_SEPARATOR)
                .append(this.getOpisanie()).append(App.CSV_SEPARATOR)
                .append(this.getStatus()).append(App.CSV_SEPARATOR)
                .append(this.getPohozhye()).append(App.CSV_SEPARATOR)
                .append(this.getColor()).append(App.CSV_SEPARATOR)
                .append(this.getHtmlLink()).append(App.CSV_SEPARATOR)
                .append(this.getSpecialKeyID()).append(System.lineSeparator());

        return sb.toString();
    }
}
