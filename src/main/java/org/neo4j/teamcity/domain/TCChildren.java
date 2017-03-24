package org.neo4j.teamcity.domain;

import javax.xml.bind.annotation.XmlAttribute;

public class TCChildren {

    String href;

    public String getHref() {
        return href;
    }

    @XmlAttribute
    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public String toString() {
        return "TCChildren{" +
                "href='" + href + '\'' +
                '}';
    }

}
