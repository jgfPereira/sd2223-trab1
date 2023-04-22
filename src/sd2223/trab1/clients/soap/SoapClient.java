package sd2223.trab1.clients.soap;


import com.sun.xml.ws.client.BindingProviderProperties;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.WebServiceException;
import sd2223.trab1.api.java.Result;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.logging.Logger;

import static sd2223.trab1.api.java.Result.error;
import static sd2223.trab1.api.java.Result.ok;


/**
 * Shared behavior among SOAP clients.
 * <p>
 * Holds endpoint information.
 * <p>
 * Translates soap responses/exceptions to Result<T> for interoperability.
 *
 * @author smduarte
 */
abstract class SoapClient {
    protected static final int READ_TIMEOUT = 5000;
    protected static final int CONNECT_TIMEOUT = 5000;

    protected static final int MAX_RETRIES = 10;
    protected static final int RETRY_SLEEP = 3000;
    protected static final String WSDL = "?wsdl";
    private static final Logger Log = Logger.getLogger(SoapClient.class.getName());
    protected final URI uri;

    public SoapClient(URI uri) {
        this.uri = uri;
    }

    static private Result.ErrorCode getErrorCodeFrom(Exception e) {
        try {
            return Result.ErrorCode.valueOf(e.getMessage());
        } catch (IllegalArgumentException x) {
            return Result.ErrorCode.INTERNAL_ERROR;
        }
    }

    public static URL toURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void setTimeouts(BindingProvider port) {
        port.getRequestContext().put(BindingProviderProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT);
        port.getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, READ_TIMEOUT);
    }

    protected <T> Result<T> reTry(ResultSupplier<Result<T>> func) {
        for (int i = 0; i < MAX_RETRIES; i++)
            try {
                return func.get();
            } catch (WebServiceException x) {
                x.printStackTrace();
                Log.fine("Timeout: " + x.getMessage());
                sleep_ms(RETRY_SLEEP);
            } catch (Exception x) {
                x.printStackTrace();
                return error(Result.ErrorCode.INTERNAL_ERROR);
            }
        return error(Result.ErrorCode.TIMEOUT);
    }

    protected <R> Result<R> toJavaResult(ResultSupplier<R> supplier) {
        try {
            return ok(supplier.get());
        } catch (WebServiceException x) {
            throw x;
        } catch (Exception e) {
            return error(getErrorCodeFrom(e));
        }
    }

    protected <R> Result<R> toJavaResult(VoidSupplier r) {
        try {
            r.run();
            return ok();
        } catch (WebServiceException x) {
            throw x;
        } catch (Exception e) {
            return error(getErrorCodeFrom(e));
        }
    }

    @Override
    public String toString() {
        return uri.toString();
    }

    private void sleep_ms(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
        }
    }

    interface ResultSupplier<T> {
        T get() throws Exception;
    }

    interface VoidSupplier {
        void run() throws Exception;
    }
}
