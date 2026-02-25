/*
 * Copyright (C) 2025, Gobierno de España This program is licensed and may be used, modified and
 * redistributed under the terms of the European Public License (EUPL), either version 1.1 or (at
 * your option) any later version as soon as they are approved by the European Commission. Unless
 * required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and more details. You
 * should have received a copy of the EUPL1.1 license along with this program; if not, you may find
 * it at http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 */

package es.gob.aapp.eeutil.eeutilrestigae.services;

import java.lang.reflect.Field;

import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.ValidatorHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import es.gob.aapp.eeutil.eeutilrestigae.exception.EeutilRestException;
import es.gob.aapp.eeutil.eeutilrestigae.exception.EeutilRestShortException;
import es.gob.aapp.eeutil.eeutilrestigae.utils.SchemaUtils;
import es.gob.aapp.eeutil.eeutilrestigae.utils.xmlsecurity.XMLSeguridadFactoria;

@Component
public class ValidatorSchemaService {

  protected static final Log logger = LogFactory.getLog(ValidatorSchemaService.class);


  @Autowired
  public SchemaUtils schemaUtils;

  private final static String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

  public boolean validarSchema(String version, byte[] byteValidationSchema)
      throws EeutilRestException, EeutilRestShortException {

    try {

      XMLReader parser = null;

      Source[] aSources = null;

      if (version == null || "1.0".equals(version)) {

        aSources = schemaUtils.getSchemasSourcesVersion("1.0");

      } else if ("2.0".equals(version)) {

        aSources = schemaUtils.getSchemasSourcesVersion("2.0");

      }

      SchemaFactory schemaFactory = SchemaFactory.newInstance(W3C_XML_SCHEMA);
      XMLSeguridadFactoria.setPreventAttackExternalSchemaFactoryStatic(schemaFactory);

      Schema schemaGrammar = schemaFactory.newSchema(aSources);

      javax.xml.validation.Validator schemaValidator = schemaGrammar.newValidator();
      ValidatorHandler vHandler = schemaGrammar.newValidatorHandler();

      DefaultHandler validationHandler = new DefaultHandler();
      schemaValidator.setErrorHandler(validationHandler);

      ContentHandler cHandler = validationHandler;
      vHandler.setContentHandler(cHandler);

      parser = XMLReaderFactory.createXMLReader();
      // to be compliant, completely disable DOCTYPE declaration:
      // parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      // or completely disable external entities declarations:
      // parser.setFeature("http://xml.org/sax/features/external-general-entities", false);
      // parser.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
      parser.setContentHandler(vHandler);

      try {
        parser.parse(new InputSource(new java.io.ByteArrayInputStream(byteValidationSchema)));
      } catch (Exception e) {
        // System.err.println("Fallo en la validacion del schema");
        // la validacion es incorrecta y devolvemos una excepcion.

        String message = null;

        if (e.getMessage().length() > 50000L) {
          message = e.getMessage().substring(0, 2000) + "...";

          Field field = Throwable.class.getDeclaredField("detailMessage");
          field.setAccessible(true);
          field.set(e, message);
          field.setAccessible(false);

          throw new EeutilRestShortException(message, e);
        } else {
          message = e.getMessage();
        }

        throw new EeutilRestShortException(message, e);

      }
      logger.info("VALIDACION DE SCHEMA REALIZADA CORRECTAMENTE");
      // System.out.println("Validacion realizada correctamente:");
      // la validacion es correcta y devolvemos un true
      return true;
    } catch (Exception e1) {
      throw new EeutilRestException(e1.getMessage(), e1);
    }

  }

}
