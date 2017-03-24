package org.neo4j.teamcity;

import org.neo4j.teamcity.domain.TCBuild;
import org.neo4j.teamcity.domain.TCBuilds;
import org.neo4j.teamcity.domain.TCFile;
import org.neo4j.teamcity.domain.TCFiles;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Predicate;

public class TeamCity {

    private void log(String s) {
        if (verbose) {
            System.out.println("  [+] " + s);
        }
    }

    private static final String rootPath = "/httpAuth/app/rest";
    private String server;
    private Client client;
    private WebTarget root;
    private boolean verbose;

    public TeamCity(Client client, String server, boolean verbose) {
        this.server = server;
        this.client = client;
        this.verbose = verbose;
        log("Connecting to TeamCity at " + server);
        this.root = client.target(server + rootPath);
    }

    public TeamCity(Client client, String server) {
        this(client, server, false);
    }

    public void download(String buildType, Predicate<TCBuild> buildFilter, Predicate<TCFile> artifactFilter, Path outDir) throws IOException {
        Artifacts artifacts = artifacts(buildType, buildFilter);
        Optional<TCFile> artifact = artifacts.find(artifactFilter);
        if (artifact.isPresent()) {
            download(artifact.get(), outDir);
        } else {
            System.err.printf("No matching artifact found%n");
        }
    }

    private Artifacts artifacts(String buildType, Predicate<TCBuild> filter) {
        WebTarget buildTarget = build(buildType, filter);
        log(String.format("New Artifacts for build %s", buildTarget.getUri().toString()));
        return new Artifacts(buildTarget, client.target(server));
    }

    private WebTarget build(String buildType, Predicate<TCBuild> filter) {
        WebTarget buildTypeTarget = root.path("buildTypes").path("id:" + buildType).path("builds");
        Response response = buildTypeTarget.request().get();
        if (200 == response.getStatus()) {
            log("Successfully acquired the builds target for buildType: " + buildTypeTarget.getUri().toString());
            TCBuilds tcBuilds = response.readEntity(TCBuilds.class);
            log("Successfully read the builds target entity into a TCBuilds");
            Optional<TCBuild> tcBuild = tcBuilds.getBuilds().stream().filter(filter).findFirst();
            if (tcBuild.isPresent()) {
                String buildPath = tcBuild.get().getHref();
                log("Found matching build at: " + buildPath);
                String id = buildPath.substring(buildPath.lastIndexOf("/")).split(":")[1];
                WebTarget buildTarget = root.path("builds").path("id:" + id);
                return buildTarget;
            } else {
                System.err.println("No matching build found");
                System.exit(1);
            }
        } else {
            throw new RuntimeException(String.format("  [x] %s%n  [x]%s%n  [x]%s%n",
                    String.format("Response code: %d%n", response.getStatus()),
                    String.format("Response entity: %s%n", response.readEntity(String.class)),
                    String.format("Request URI: %s%n", buildTypeTarget.getUri().toString())

            ));
        }
        return null;
    }


    private void download(TCFile artifact, Path outDir) throws IOException {
        log(String.format("Downloading %s%n      from %s%n      to %s%n      size: %d KB%n",
                artifact.getName(), artifact.getContent().getHref(), outDir.toString(), artifact.getSize()/1024));
        File toFile = outDir.resolve(artifact.getName()).toFile();
        WebTarget toDownload = client.target(server + artifact.getContent().getHref());
        Response response = toDownload.request().get();
        try (InputStream is = response.readEntity(InputStream.class); FileOutputStream fos = new FileOutputStream(toFile);) {
            int length;
            byte[] buffer = new byte[1024];
            while ((length = is.read(buffer)) > -1) {
                fos.write(buffer, 0, length);
            }
        }
    }

    public void close() {
        client.close();
    }

}
