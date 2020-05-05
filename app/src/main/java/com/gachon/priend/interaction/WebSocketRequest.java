package com.gachon.priend.interaction;

import com.gachon.priend.data.IJsonConvertible;
import com.gachon.priend.data.entity.Account;
import com.gachon.priend.data.numeric.Converter;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.json.JSONException;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLContext;

/**
 * A class that represents and execute web socket request
 *
 * @author 유근혁
 * @since May 5th 2020
 */
public final class WebSocketRequest {

    /**
     * A class that represents web socket message
     *
     * @author 유근혁
     * @since May 5th 2020
     */
    public final class Message {

        private boolean isBinary = false;
        private byte[] binaryMessage = null;
        private String textMessage = null;

        private Message(Object message) {
            if (message instanceof byte[]) {
                isBinary = true;
                binaryMessage = (byte[]) message;
            } else {
                isBinary = false;
                textMessage = message.toString();
            }
        }

        /**
         * Get text message as a String
         *
         * @return The text message if exists, null otherwise.
         */
        public String getTextMessageOrNull() {
            return textMessage;
        }

        /**
         * Get binary message as a byte array
         *
         * @return The binary message if exists, null otherwise.
         */
        public byte[] getBinaryMessageOrNull() {
            return binaryMessage;
        }

        /**
         * Get binary message as 16-bit integer
         *
         * @return The 16-bit integer if it is, null otherwise.
         */
        public Short getInt16MessageOrNull() {
            return Converter.byteArrayToInt16OrNull(binaryMessage);
        }

        /**
         * Get binary message as 32-bit integer
         *
         * @return The 32-bit integer if it is, null otherwise.
         */
        public Integer getAsInt32MessageOrNull() {
            return Converter.byteArrayToInt32OrNull(binaryMessage);
        }

        /**
         * Get binary message as 64-bit integer
         *
         * @return The 64-bit integer if it is, null otherwise.
         */
        public Long getAsInt64MessageOrNull() {
            return Converter.byteArrayToInt64OrNull(binaryMessage);
        }

        @Override
        public String toString() {
            if (isBinary) {
                StringBuilder builder = new StringBuilder();
                builder.append('[');
                for (byte bt : binaryMessage) {
                    builder.append(bt);
                    builder.append(' ');
                }
                builder.append(']');

                return builder.toString();
            } else {
                return textMessage;
            }
        }
    }

    /**
     * A listener interface that handles web socket request-response routine
     *
     * @author 유근혁
     * @since May 5th 2020
     */
    public interface RequestAdapter {

        /**
         * Send request parameters
         *
         * @param conn WebSocketRequest instance of which sends request
         */
        void onRequest(WebSocketRequest conn);

        /**
         * Receive a response parameter
         *
         * @param conn WebSocketRequest instance of which receives response
         * @param message The message you send
         * @param paramNumber A sequence number of received message that starts with 0
         */
        void onResponse(WebSocketRequest conn, Message message, int paramNumber);

        /**
         * Clean up
         */
        void onClosed();
    }

    private ExecutorService thread = null;
    private WebSocket webSocket = null;
    private boolean opened = false;

    /**
     * Create an instance with an uri and an event listener
     *
     * @param uri The web socket server uri
     * @param customAdapter The request-response event listener
     */
    public WebSocketRequest(String uri, final RequestAdapter customAdapter) {
        try {
            WebSocketFactory factory = new WebSocketFactory().setConnectionTimeout(5000);
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, null, null);
            webSocket = factory.createSocket(uri);

            webSocket.addListener(new WebSocketAdapter(){

                private int paramNumber = 0;

                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    super.onConnected(websocket, headers);

                    paramNumber = 0;

                    opened = true;
                    if (customAdapter != null) {
                        customAdapter.onRequest(WebSocketRequest.this);
                    }
                }

                @Override
                public void onTextMessage(WebSocket websocket, String text) throws Exception {
                    super.onTextMessage(websocket, text);

                    if (customAdapter != null) {
                        Message msg = new Message(text);
                        customAdapter.onResponse(WebSocketRequest.this, msg, paramNumber++);
                    }
                }

                @Override
                public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
                    super.onBinaryMessage(websocket, binary);

                    if (customAdapter != null) {
                        Message msg = new Message(binary);
                        customAdapter.onResponse(WebSocketRequest.this, msg, paramNumber++);
                    }
                }

                @Override
                public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                    super.onCloseFrame(websocket, frame);

                    if (thread != null) {
                        opened = false;
                        thread.shutdown();

                        if (customAdapter != null) {
                            customAdapter.onClosed();


                        }
                    }
                }
            });

        } catch (NoSuchAlgorithmException | KeyManagementException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Connect server, send a request
     *
     * @param onFailed Callback that handles connection failure
     * @return This instance
     */
    public WebSocketRequest connect(Runnable onFailed) {
        thread = Executors.newSingleThreadExecutor();
        webSocket.connect(thread);

        return this;
    }

    /**
     * Send close frame to the server
     */
    public void close() {
        webSocket.sendClose();
    }

    /**
     * Get if the web socket is opened\
     *
     * @return Weather the web socket is opened or not
     */
    public boolean isOpened() {
        return opened;
    }

    /**
     * Send a text message
     *
     * @param text The text message
     */
    public void send(String text) {
        webSocket.sendText(text);
    }

    /**
     * Send a binary message
     *
     * @param binary Binary message as a byte array
     */
    public void send(byte[] binary) {
        webSocket.sendBinary(binary);
    }

    /**
     * Send a 16-bit integer
     *
     * @param s The 16-bit integer
     */
    public void send(short s) {
        webSocket.sendBinary(Converter.integerToByteArray(s));
    }

    /**
     * Send a 32-bit integer
     *
     * @param i The 32-bit integer
     */
    public void send(int i) {
        webSocket.sendBinary(Converter.integerToByteArray(i));
    }

    /**
     * Send a 64-bit integer
     *
     * @param l The 64-bit integer
     */
    public void send(long l) {
        webSocket.sendBinary(Converter.integerToByteArray(l));
    }

    /**
     * Send JSON-format string as a text message
     *
     * @param jsonConvertible A JSON-convertible object instance
     * @throws JSONException Thrown if something goes wrong when formatting JSON into a string
     */
    public void send(IJsonConvertible jsonConvertible) throws JSONException {
        send(jsonConvertible.toJson().toString(0));
    }

    /**
     * Send ID and authentication token of an account
     *
     * @param account An account to authenticate
     */
    public void sendAuthentication(Account account) {
        send(account.getId());
        send(account.getAuthenticationToken());
    }
}