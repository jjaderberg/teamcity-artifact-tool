package org.neo4j.teamcity.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name="builds")
public class TCBuilds {

    List<TCBuild> builds;

    public List<TCBuild> getBuilds() {
        return builds;
    }

    @XmlElement(name="build")
    public void setBuilds(List<TCBuild> builds) {
        this.builds = builds;
    }
}
