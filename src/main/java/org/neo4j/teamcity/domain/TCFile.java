package org.neo4j.teamcity.domain;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="file")
public class TCFile {

    String name;
    int size;
    String modificationTime;
    String href;
    TCContent content;
    TCChildren children;

    public String getName() {
        return name;
    }

    @XmlAttribute
    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    @XmlAttribute
    public void setSize(int size) {
        this.size = size;
    }

    public String getModificationTime() {
        return modificationTime;
    }

    @XmlAttribute
    public void setModificationTime(String modificationTime) {
        this.modificationTime = modificationTime;
    }

    public String getHref() {
        return href;
    }

    @XmlAttribute
    public void setHref(String href) {
        this.href = href;
    }

    public TCContent getContent() {
        return content;
    }

    @XmlElement
    public void setContent(TCContent content) {
        this.content = content;
    }

    public TCChildren getChildren() {
        return children;
    }

    @XmlElement
    public void setChildren(TCChildren children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "TCFile{" +
                "name='" + name + '\'' +
                ", size=" + size +
                ", modificationTime='" + modificationTime + '\'' +
                ", href='" + href + '\'' +
                ", content=" + content +
                ", children=" + children +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TCFile tcFile = (TCFile) o;

        if (size != tcFile.size) return false;
        if (!name.equals(tcFile.name)) return false;
        if (!modificationTime.equals(tcFile.modificationTime)) return false;
        if (!href.equals(tcFile.href)) return false;
        if (content != null ? !content.equals(tcFile.content) : tcFile.content != null) return false;
        return children != null ? children.equals(tcFile.children) : tcFile.children == null;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + size;
        result = 31 * result + modificationTime.hashCode();
        result = 31 * result + href.hashCode();
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (children != null ? children.hashCode() : 0);
        return result;
    }

}
