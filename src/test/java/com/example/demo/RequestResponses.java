package com.example.demo;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;

/**
 * Encapsulates request-mock response pairs for the MockDispatcherSupport class.
 */
public class RequestResponses
{
    /**
     * A container for a request-response pair (with potentially many responses, to be delivered in sequence).
     */
    public static class RequestResponsePair
    {
        private Queue<MockResponse> mockResponses;
        private Predicate<RecordedRequest> requestMatcher;

        /**
         * @param mockResponses  The responses.
         * @param requestMatcher  A predicate that matches a request.
         */
        RequestResponsePair(final Predicate<RecordedRequest> requestMatcher, final MockResponse... mockResponses)
        {
            this.mockResponses = new ConcurrentLinkedQueue<>(List.of(mockResponses));
            this.requestMatcher = requestMatcher;
        }

        public Queue<MockResponse> getMockResponses()
        {
            return mockResponses;
        }

        public Predicate<RecordedRequest> getRequestMatcher()
        {
            return requestMatcher;
        }
    }

    private List<RequestResponsePair> requestResponseList = new ArrayList<>();

    public  List<RequestResponsePair> getRequestResponseList()
    {
        return requestResponseList;
    }

    /**
     * Add a request-response pair.
     * @param mockResponse  The response.
     * @param requestMatcher  A predicate that matches a request.
     */
    public void addResponse(final MockResponse mockResponse, final Predicate<RecordedRequest> requestMatcher)
    {
        requestResponseList.add(new RequestResponsePair(requestMatcher, mockResponse));
    }

    /**
     * Add a request-response pair, with a sequence of responses to use - the responses will be returned in the
     * specified order, with the last response being returned indefinitely (so addResponse(mockResponse, requestMatcher)
     * has the same behaviour as addResponse(requestMatcher, mockResponse)).
     *
     * @param requestMatcher  A predicate that matches a request.
     * @param mockResponses  The responses.
     */
    public void addResponse(final Predicate<RecordedRequest> requestMatcher, final MockResponse... mockResponses)
    {
        requestResponseList.add(new RequestResponsePair(requestMatcher, mockResponses));
    }
}
