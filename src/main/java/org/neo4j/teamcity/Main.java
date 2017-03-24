/*
 * Copyright (c) 2002-2017 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.teamcity;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.MutuallyExclusiveGroup;
import net.sourceforge.argparse4j.inf.Namespace;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.neo4j.teamcity.domain.TCBuild;
import org.neo4j.teamcity.domain.TCFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

public class Main {

    private static final String DEFAULT_BUILD_NO = "lastSuccessful";
    private static final String TEAMCITY_SERVER_ENV_KEY = "TEAMCITY_SERVER";
    private static final String TEAMCITY_USERNAME_ENV_KEY = "TEAMCITY_USERNAME";
    private static final String TEAMCITY_PASSWORD_ENV_KEY = "TEAMCITY_PASSWORD";

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        Namespace namespace = null;
        try {
            namespace = parseArgs(args);
        } catch (ArgumentParserException e) {
            System.exit(1);
        }

        String buildType = namespace.getString("build_type");
        String buildNo = namespace.getString("build_number");
        String server = server(namespace);
        String username = username(namespace);
        String password = password(namespace);
        boolean verbose = namespace.getBoolean("verbose");
        String outDir = namespace.getString("output_dir");

        Predicate<TCFile> artifactFilter = artifactFilter(namespace);

        Path outPath = Paths.get(outDir);

        Client client = ClientBuilder.newBuilder().sslContext(new SecurityUtil().sslContext()).build();
        HttpAuthenticationFeature authFeature = HttpAuthenticationFeature.basic(username, password);
        client.register(authFeature);

        try {
            TeamCity teamCity = new TeamCity(client, server, verbose);
            teamCity.download(buildType, buildFilter(buildNo), artifactFilter, outPath);
            teamCity.close();
        } catch (IOException ex) {
            client.close();
            throw ex;
        }
    }

    private static Namespace parseArgs(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newArgumentParser("teamcity")
                .defaultHelp(true)
                .description("Download artifacts from TeamCity.");

        MutuallyExclusiveGroup mutuallyExclusiveGroup = parser.addMutuallyExclusiveGroup();

        mutuallyExclusiveGroup.addArgument("-an", "--artifact-name")
                .help("Name of artifact to download");
        mutuallyExclusiveGroup.addArgument("-ap", "--artifact-pattern")
                .help("Pattern for artifact to download");
        mutuallyExclusiveGroup.required(true);

        parser.addArgument("-bt", "--build-type")
                .help("ID of the build type from which to find build")
                .required(true);
        parser.addArgument("-bn", "--build-number")
                .help("Build number for the build from which to get artifact")
                .setDefault(DEFAULT_BUILD_NO);

        parser.addArgument("-s", "--server")
                .help("URI for TeamCity server");
        parser.addArgument("-u", "--username")
                .help("Username for connecting to TeamCity");
        parser.addArgument("-p", "--password")
                .help("Password for connecting to TeamCity");

        parser.addArgument("-o", "--output-dir")
                .help("Directory in which to put downloaded artifacts")
                .required(true);
        parser.addArgument("-v", "--verbose")
                .help("Verbosely print output")
                .action(Arguments.storeTrue())
                .setDefault(false);
        try {
            return parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            throw e;
        }
    }

    private static String server(Namespace namespace) {
        String server = namespace.getString("server");
        if (null == server) {
            server = System.getenv(TEAMCITY_SERVER_ENV_KEY);
            if (null == server) {
                throw new IllegalArgumentException("'--server' argument is mandatory");
            }
        }
        return server;
    }

    private static String username(Namespace namespace) {
        String username = namespace.getString("username");
        if (null == username) {
            username = System.getenv(TEAMCITY_USERNAME_ENV_KEY);
            if (null == username) {
                throw new IllegalArgumentException("Username must be specified.");
            }
        }
        return username;
    }

    private static String password(Namespace namespace) {
        String password = namespace.getString("password");
        if (null == password) {
            password = System.getenv(TEAMCITY_PASSWORD_ENV_KEY);
            if (null == password) {
                throw new IllegalArgumentException("Password must be specified.");
            }
        }
        return password;
    }

    private static Predicate<TCBuild> buildFilter(String buildNo) {
        if (buildNo.equalsIgnoreCase("lastSuccessful")) {
            return b -> b.getStatus().equals("SUCCESS");
        } else {
            return b -> Integer.toString(b.getNumber()).equals(buildNo);
        }
    }

    private static Predicate<TCFile> artifactFilter(Namespace namespace) {
        String artifactName = namespace.getString("artifact_name");
        if (null == artifactName) {
            Pattern artifactPattern = Pattern.compile(namespace.getString("artifact_pattern"));
            return f -> artifactPattern.matcher(f.getName()).find();
        } else {
            return f -> f.getName().equals(artifactName);
        }
    }

}

