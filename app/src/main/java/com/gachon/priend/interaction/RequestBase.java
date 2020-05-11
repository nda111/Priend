package com.gachon.priend.interaction;

/**
 * A abstract class that represents request-response routine
 *
 * @param <T> The type of the response
 * @author 유근혁
 * @since May 5th 2020
 */
public abstract class RequestBase<T> {

    /**
     * The address of the web socket server
     */
    public static final String SERVER_ADDRESS = "wss://priend.herokuapp.com";

    /**
     * A callback that handles response
     *
     * @param <T> The type of the response
     * @author 유근혁
     * @since May 5th 2020
     */
    public interface ResponseListener<T> {

        /**
         * Called on routine terminated
         *
         * @param response The response value
         * @param args Additional response arguments which is optional
         */
        void onResponse(final T response, final Object[] args);
    }

    /**
     * Response of this request
     */
    protected T response = null;

    /**
     * Arguments of the response
     */
    protected Object[] args = null;

    /**
     * Get the URI of web socket server
     *
     * @return URI of web socket server
     */
    protected abstract String getUri();

    /**
     * Called to send request arguments
     *
     * @param conn A connection interface
     */
    protected abstract void onRequest(WebSocketRequest conn);

    /**
     * Method that handles response, must set value of {super.response}
     *
     * @param conn A connection interface
     * @param message A response message
     * @param paramNumber A sequence number of received message that starts with 0
     */
    protected abstract void onResponse(WebSocketRequest conn, WebSocketRequest.Message message, int paramNumber);

    /**
     * Clean up
     */
    protected abstract void onClose();

    /**
     * Send this request and call {responseListener.onResponse}
     *
     * @param responseListener A callback that handles response
     */
    public final void request(final ResponseListener<T> responseListener) {
        new WebSocketRequest(getUri(), new WebSocketRequest.RequestAdapter() {

            @Override
            public void onRequest(WebSocketRequest conn) {
                RequestBase.this.onRequest(conn);
            }

            @Override
            public void onResponse(WebSocketRequest conn, WebSocketRequest.Message message, int paramNumber) {
                RequestBase.this.onResponse(conn, message, paramNumber);
            }

            @Override
            public void onClosed() {
                RequestBase.this.onClose();
                responseListener.onResponse(response, args);
            }
        }).connect(null);
    }
}
