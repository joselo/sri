/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sri;

/**
 *
 * @author ccarreno
 */

import es.mityc.firmaJava.libreria.xades.DataToSign;
import es.mityc.firmaJava.libreria.xades.FirmaXML;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
 
public abstract class GenericXMLSignature
{
  private String pathSignature;
  private String passSignature;
   
  public String getPathSignature()
  {
    return this.pathSignature;
  }
   
  public void setPathSignature(String pathSignature)
  {
    this.pathSignature = pathSignature;
  }
   
  public String getPassSignature()
  {
    return this.passSignature;
  }
   
  public void setPassSignature(String passSignature)
  {
    this.passSignature = passSignature;
  }
   
  protected void execute()
    throws CertificateException, IOException
  {
    KeyStore keyStore = getKeyStore();
    if (keyStore == null)
    {
      throw new IOException("No se pudo obtener almacen de firma.");
    }
    String alias = getAlias(keyStore);
     
 
 
    X509Certificate certificate = null;
    try
    {
      certificate = (X509Certificate)keyStore.getCertificate(alias);
      if (certificate == null)
      {
        throw new IOException("No existe ningún certificado para firmar.");
      }
    }
    catch (KeyStoreException e1)
    {
        throw new IOException("Error: " + e1.getMessage());
    }

    PrivateKey privateKey = null;
    KeyStore tmpKs = keyStore;

    try
    {
      privateKey = (PrivateKey)tmpKs.getKey(alias, this.passSignature.toCharArray());
    }
    catch (UnrecoverableKeyException e)
    {
      throw new IOException("No existe clave privada para firmar.");
    }
    catch (KeyStoreException e)
    {
      throw new IOException("No existe clave privada para firmar.");
    }
    catch (NoSuchAlgorithmException e)
    {
      throw new IOException("No existe clave privada para firmar.");
    }

    Provider provider = keyStore.getProvider(); 
    DataToSign dataToSign = createDataToSign();
    FirmaXML firma = new FirmaXML();
    Document docSigned = null;

    try
    {
      Object[] res = firma.signFile(certificate, dataToSign, privateKey, provider);
      docSigned = (Document)res[0];
    }
    catch (Exception ex)
    {
      throw new IOException("Error realizando la firma: " + ex.getMessage());
    }
    String filePath = getPathOut() + File.separatorChar + getSignatureFileName();
     
    saveDocumenteDisk(docSigned, filePath);
  }
   
  protected abstract DataToSign createDataToSign();
   
  protected abstract String getSignatureFileName();
   
  protected abstract String getPathOut();
   
  protected Document getDocument(String resource) throws IOException
  {
    Document doc = null;
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    File file = new File(resource);

    try
    {
      DocumentBuilder db = dbf.newDocumentBuilder();
      doc = db.parse(file);
    }
    catch (ParserConfigurationException|SAXException|IOException|IllegalArgumentException ex)
    {
      throw new IOException("Error al parsear el documento: " + ex.getMessage());
    }
    return doc;
  }
   
  private KeyStore getKeyStore() throws CertificateException, IOException
  {
    KeyStore ks = null;

    try
    {
      ks = KeyStore.getInstance("PKCS12");
      ks.load(new FileInputStream(this.pathSignature), this.passSignature.toCharArray());
    }
    catch (KeyStoreException e)
    {
        throw new IOException("Error: " + e.getMessage());
    }
    catch (NoSuchAlgorithmException e)
    {
        throw new IOException("Error: " + e.getMessage());      
    }
    catch (CertificateException e)
    {
      throw new IOException("Error: " + e.getMessage());
    }
    catch (IOException e)
    {
        throw new IOException("Error: " + e.getMessage());
    }
    return ks;
  }
   
  private static String getAlias(KeyStore keyStore) throws IOException
  {
    String alias = null;

    try
    {
      Enumeration nombres = keyStore.aliases();
      while (nombres.hasMoreElements())
      {
        String tmpAlias = (String)nombres.nextElement();
        if (keyStore.isKeyEntry(tmpAlias)) {
          alias = tmpAlias;
        }
      }
    }
    catch (KeyStoreException e)
    {
      throw new IOException("Error: " + e.getMessage());
    }
    return alias;
  }
   
  public static void saveDocumenteDisk(Document document, String pathXml) throws IOException
  {
    try
    {
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new File(pathXml));
       
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
       
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(source, result);
    }
    catch (TransformerConfigurationException e)
    {
        throw new IOException("Error: " + e.getMessage());
    }
    catch (TransformerException e)
    {
        throw new IOException("Error: " + e.getMessage());
    }
  }
}