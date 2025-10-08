package com.example.demo;

import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import java.io.IOException;
import java.net.BindException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * A helper class for okhttp3's mock webserver; this acts as a dependency wireable version
 * of the mock webserver (so that we can have a separate server instance for each test, *and*
 * include this as an autowired-dependency.  The former is because otherwise tests can affect
 * each other - the list of received requests can persist from one test to another, and so
 * failed tests can cause a cascade of failures in other tests.  In which case it's very
 * hard to work out which test is the cause.)
 */
public class MockServerBean
{
    private MockWebServer mockServer;
    // This is in milliseconds - how long we wait for the mock webserver to give us a request
    // back before assuming there are no more.
    private static final int TEST_WAIT = 10;
    // Also in milliseconds.
    private static final int SHUTDOWN_WAIT = 500;

    public MockWebServer getServer()
    {
        return mockServer;
    }

    /**
     * Create and start a server on the given port.
     *
     * @param port  The port to use.
     * @throws IOException if we couldn't start the server.
     */
    public void startServer(final int port) throws IOException
    {
        mockServer = new MockWebServer();
        try {
            mockServer.start(port);
        } catch (final BindException e) {
            // The port is apparently still in use, even though the previous mockServer's shutdown()
            // succeeded, and returned.  We wait a bit, and try again.  Note that this is effectively
            // not an infinite loop, as I haven't seen a case where it takes more than five seconds.
            //
            // This happens more when the server running this is under load (or has stupid anti-virus shit
            // interfering).
            try {
                Thread.sleep(Duration.ofMillis(SHUTDOWN_WAIT));
            } catch (final InterruptedException e2) {
                Thread.currentThread().interrupt();
            }
            startServer(port);
        }
    }

    /**
     * Take a request from the mock server.
     *
     * @return the request.
     * @throws InterruptedException if the request didn't happen.
     */
    public RecordedRequest takeRequest() throws InterruptedException
    {
        return mockServer.takeRequest(TEST_WAIT, TimeUnit.MILLISECONDS);
    }

    /**
     * Shut the server down.
     *
     * @throws IOException if we couldn't shut the server down.
     */
    public void stopServer() throws IOException
    {
        mockServer.shutdown();
        mockServer = null;
        // Note that the server can't be used after this point.
    }
}
