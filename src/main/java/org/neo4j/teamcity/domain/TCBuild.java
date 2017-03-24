package org.neo4j.teamcity.domain;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="build")
public class TCBuild {

    String id;
    String buildTypeId;
    int number;
    String status;
    String state;
    String href;
    String webUrl;

    public String getId() {
        return id;
    }

    @XmlAttribute
    public void setId(String id) {
        this.id = id;
    }

    public String getBuildTypeId() {
        return buildTypeId;
    }

    @XmlAttribute
    public void setBuildTypeId(String buildTypeId) {
        this.buildTypeId = buildTypeId;
    }

    public int getNumber() {
        return number;
    }

    @XmlAttribute
    public void setNumber(int number) {
        this.number = number;
    }

    public String getStatus() {
        return status;
    }

    @XmlAttribute
    public void setStatus(String status) {
        this.status = status;
    }

    public String getState() {
        return state;
    }

    @XmlAttribute
    public void setState(String state) {
        this.state = state;
    }

    public String getHref() {
        return href;
    }

    @XmlAttribute
    public void setHref(String href) {
        this.href = href;
    }

    public String getWebUrl() {
        return webUrl;
    }

    @XmlAttribute
    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    @Override
    public String toString() {
        return "TCBuild{" +
                "id='" + id + '\'' +
                ", buildTypeId='" + buildTypeId + '\'' +
                ", number=" + number +
                ", status='" + status + '\'' +
                ", state='" + state + '\'' +
                ", href='" + href + '\'' +
                ", webUrl='" + webUrl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TCBuild tcBuild = (TCBuild) o;

        if (number != tcBuild.number) return false;
        if (!id.equals(tcBuild.id)) return false;
        if (!buildTypeId.equals(tcBuild.buildTypeId)) return false;
        if (!status.equals(tcBuild.status)) return false;
        if (!state.equals(tcBuild.state)) return false;
        if (!href.equals(tcBuild.href)) return false;
        return webUrl.equals(tcBuild.webUrl);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + buildTypeId.hashCode();
        result = 31 * result + number;
        result = 31 * result + status.hashCode();
        result = 31 * result + state.hashCode();
        result = 31 * result + href.hashCode();
        result = 31 * result + webUrl.hashCode();
        return result;
    }

}
