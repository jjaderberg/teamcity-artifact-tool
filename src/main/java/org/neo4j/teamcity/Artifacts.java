package org.neo4j.teamcity;

import org.neo4j.teamcity.domain.TCFile;
import org.neo4j.teamcity.domain.TCFiles;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.function.Predicate;

public class Artifacts {

    private final static int DEFAULT_SEARCH_DEPTH = 3;

    private WebTarget target;
    private WebTarget server;
    private long lastAccess = Long.MAX_VALUE;
    private TCFiles artifacts = null;

    public Artifacts(WebTarget build, WebTarget server) {
        this.target = build.path("artifacts").path("children");
        this.server = server;
    }

    public Optional<TCFiles> artifacts() {
        if (System.currentTimeMillis() - lastAccess > 60 * 5 || null == artifacts) {
            lastAccess = System.currentTimeMillis();
            Response response = target.request().get();
            if (200 == response.getStatus()) {
                TCFiles files = response.readEntity(TCFiles.class);
                if (null == files) {
                    System.err.printf("      TCFiles is null%n");
                } else {
                    System.out.printf("      Has %d files%n", files.getCount());
                    artifacts = files;
                }
            }
        }
        return Optional.of(artifacts);
    }

    public Optional<TCFile> find(Predicate<TCFile> filter) {
        return find(filter, DEFAULT_SEARCH_DEPTH);
    }

    public Optional<TCFile> find(Predicate<TCFile> filter, int maxDepth) {
        if (artifacts().isPresent()) {
            return artifacts().get().find(filter, maxDepth, server);
        }
        return Optional.empty();
    }

}
