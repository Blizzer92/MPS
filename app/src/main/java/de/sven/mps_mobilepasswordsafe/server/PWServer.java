package de.sven.mps_mobilepasswordsafe.server;

import java.io.IOException;

/**
 * Created by sven on 22.10.15.
 */
public class PWServer extends NanoHTTPD{

    private final static int PORT = 1337;

    public PWServer() throws IOException {
        super(PORT);
        start();
        System.out.println( "\nRunning! Point your browers to http://localhost:1337/ \n" );
    }

    @Override
    public Response serve(IHTTPSession session) {
        String msg = "<html><body><h1>Hello server</h1>\n";
        msg += "<p>We serve " + session.getUri() + " !</p>";
        return newFixedLengthResponse( msg + "</body></html>\n" );
    }
}
