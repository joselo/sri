# Sri

Proyecto Java para la Facturación Electrónica que se usa en Ecuador, 
se basa en las especificaciones definidas en XADES_BES lo que permitiría ser 
usada en otros ambientes que requieran XADES_BES.

## Forma de usar

    XAdESBESSignature.firmar(input_file_path, key_store_path, key_store_password, output_path, out_file_name)

También.

Al compilar el código se genera un archivo sri.jar que permite firmar el documento
de la siguiente forma:

    java -jar sri.jar /path/sample/certificate.p12 cErTiFicAtEPaSsWoRd /path/sample/unsignedFile.xml /path/sample outputFile.xml

## Créditos

Todo el código es crédito de Cristhian Carreño quien expone en el siguiente tutorial como firmar un documento.

    http://webcarreno.org/index.php/tutorials/28-sri-tutorial/24-sri-firma-digital-xades-bes
