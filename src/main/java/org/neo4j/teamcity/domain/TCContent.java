package org.neo4j.teamcity.domain;

import javax.xml.bind.annotation.XmlAttribute;

public class TCContent {

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
        return "TCContent{" +
                "href='" + href + '\'' +
                '}';
    }

}
