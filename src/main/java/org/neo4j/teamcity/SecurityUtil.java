package org.neo4j.teamcity;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class SecurityUtil {

    public SSLContext sslContext() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext sslContext = SSLContext.getInstance( "TLS" );
        sslContext.init( new KeyManager[0], new TrustManager[]{new TrustAllTrustManager()}, null );
        return sslContext;
    }

    class TrustAllTrustManager implements X509TrustManager
    {
        public void checkClientTrusted( X509Certificate[] chain, String authType ) throws CertificateException
        {
            throw new CertificateException( "All client connections to this client are forbidden." );
        }

        public void checkServerTrusted( X509Certificate[] chain, String authType ) throws CertificateException
        {
            // all fine, pass through
        }

        public X509Certificate[] getAcceptedIssuers()
        {
            return new X509Certificate[0];
        }
    }

}
