package org.nzbhydra.mapping.newznab.caps;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "limits")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CapsLimits {

    public CapsLimits() {
    }

    public CapsLimits(Integer max, Integer defaultValue) {
        this.max = max;
        this.defaultValue = defaultValue;
    }

    @XmlAttribute
    private Integer max;

    @XmlAttribute(name = "default")
    private Integer defaultValue;


}
