package mnsk.services;


import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

class SummaryCart{

    @XmlElement(name="SummaryElement")
    private List<String> summaryElementList;

}