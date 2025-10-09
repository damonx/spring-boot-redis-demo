package com.example.demo;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

/**
 * A portion of an initializer for integration test classes - for the purposes of getting a port for
 * downstream API calls.
 * Extensions of this should override initialize(ConfigurableApplicationContext).
 */
public abstract class ConfigInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>
{
    /**
     * Returns an available TCP port assigned by current operating system.
     * Important note: repeated calls to this will *not* return the same port; there is also
     * the slim chance of a race condition.
     *
     * @return a random currently unused port for testing.
     */
    protected int getTestPort()
    {
        int testPort;
        try {
            try (ServerSocket socket = new ServerSocket(0)) {
                testPort = socket.getLocalPort();
            }
        } catch (final IOException e) {
            throw new IllegalStateException("Could not get server port", e);
        }
        return testPort;
    }

    /**
     * Returns a stream of property keys from a file.
     *
     * @param endpointKeyFile A non-null name of a UTF-8 encoded file located in the classpath, containing the list of endpoint keys for
     *           the mock server.  In this file, each non-comment, non-blank line represents a property key to be returned, apart from
     *           lines starting with '{@code #}' or '{@code //}', which are treated as comments and skipped.
     *
     * @return a Stream of property keys.
     */
    protected Stream<String> getTestPropertyStreamFrom(final String endpointKeyFile)
    {
        try (InputStream stream = this.getClass().getResourceAsStream(endpointKeyFile)) {
            if (stream == null) {
                throw new IllegalArgumentException("Could not access " + endpointKeyFile);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                // Note the whole stream -> list -> stream thing because just returning the first
                // stream does not actually read from the file until the stream is consumed.
                // That will happen in the caller of this method.  But by then, the file is
                // closed (because it's closed when the containing block of this comment is exited),
                // and reading from the closed file doesn't work so well.
                return reader.lines()
                    .filter(StringUtils::isNotBlank)
                    .map(StringUtils::trim)
                    .filter(line -> !StringUtils.startsWithAny(line, "#", "//"))
                    .toList()
                    .stream();
            }
        } catch (final IOException e) {
            throw new IllegalArgumentException("Could not read " + endpointKeyFile);
        }
    }

    /**
     * Builds a {@link TestPropertyValues} instance containing test properties for a mock server.
     * <p>
     * This method assigns a random available port number for the mock server and creates a set of key-value
     * properties for Spring test configuration. It maps the given endpoint keys to a local URL using the
     * generated port number (e.g., {@code http://localhost:12345}). Additionally, it includes the
     * {@code mockserver.test.port} property to explicitly define the selected port for the  mock server.
     *
     * @param endpointKeyFile A non-null name of a UTF-8 encoded file located in the classpath, containing the list of endpoint keys for
     *           the mock server.  In this file, each non-comment, non-blank line represents a property key to be mapped to the mock server URL, and
     *           lines starting with '{@code #}' or '{@code //}' are treated as comments and skipped.
     * @return a {@code TestPropertyValues} object containing the constructed properties.
     */
    protected TestPropertyValues buildTestPropertyValues(final String endpointKeyFile)
    {
        final int randomPort = getTestPort();
        return TestPropertyValues.of(getTestPropertyStreamFrom(endpointKeyFile),
                endpointKey -> TestPropertyValues.Pair.of(endpointKey, "http://localhost:" + randomPort))
            .and("mockserver.test.port=" + randomPort);
    }
}

