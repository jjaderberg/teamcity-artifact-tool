package org.neo4j.teamcity.domain;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@XmlRootElement(name="files")
public class TCFiles {

    int count;
    List<TCFile> files;

    public int getCount() {
        return count;
    }

    @XmlAttribute
    public void setCount(int count) {
        this.count = count;
    }

    public List<TCFile> getFiles() {
        return files;
    }

    @XmlElement(name="file")
    public void setFiles(List<TCFile> files) {
        this.files = files;
    }

    public Optional<TCFile> find(Predicate<TCFile> artifactFilter, int searchDepth, WebTarget server) {
        System.out.printf("  <find>  I am searching for artifact and I'm willing to go %d deep%n", searchDepth);
        Iterator<TCFile> iterator = files.stream().filter(artifactFilter).iterator();
        if (iterator.hasNext()) {
            return Optional.of(iterator.next());
        } else if (searchDepth > 0){
            for (Iterator<TCFile> filesIterator = files.stream().filter(f -> f.getSize() == 0).iterator(); filesIterator.hasNext();) {
                TCFile file = filesIterator.next();
                Response response = server.path(file.getChildren().getHref()).request().get();
                if (200 == response.getStatus()) {
                    TCFiles tcFiles = response.readEntity(TCFiles.class);
                    Optional<TCFile> tcFile = tcFiles.find(artifactFilter, searchDepth-1, server);
                    if (tcFile.isPresent()) {
                        return tcFile;
                    }
                }
            }
        }
        return Optional.empty();
    }

}
