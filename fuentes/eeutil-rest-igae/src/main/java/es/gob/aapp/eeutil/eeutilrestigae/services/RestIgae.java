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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.gob.aapp.eeutil.eeutilrestigae.exception.EeutilRestException;
import es.gob.aapp.eeutil.eeutilrestigae.exception.EeutilRestNoLoggerErrorException;
import es.gob.aapp.eeutil.eeutilrestigae.exception.EeutilRestShortException;
import es.gob.aapp.eeutil.eeutilrestigae.model.DatosSalidaSchemaValidator;
import es.gob.aapp.eeutil.eeutilrestigae.model.MDCWrapper;
import es.gob.aapp.eeutil.eeutilrestigae.model.SchemaEniModel;
import es.gob.aapp.eeutil.eeutilrestigae.utils.FileUtil;
import es.igae.gestordocumentos.visualizar.app.VisorDocumentosApp;

@RestController
// @RequestMapping("eeutil-igae")

public class RestIgae {

  protected static final Log logger = LogFactory.getLog(RestIgae.class);

  // @Autowired
  // private TCNService tcnService;

  @Autowired
  private XMLService xmlService;

  @Autowired
  private HTMLService htmlService;


  @Autowired
  private ValidatorSchemaService validatorSchemaService;

  @PostMapping(path = "api/convertirTCN", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public byte[] convertirTCNPdf(@RequestBody byte[] entrada) throws EeutilRestException {

    File fRutaEntrada = null;
    File fRutaSalida = null;
    try {

      // convertimos de utf-8 a ISO-8859-1
      String newString = new String(entrada, StandardCharsets.ISO_8859_1);

      entrada = newString.getBytes(StandardCharsets.ISO_8859_1);


      String rutaEntrada = FileUtil.createFilePath("entradaTCN", Base64.decodeBase64(entrada));
      String rutaSalida = FileUtil.createFilePath("salidaTCN");
      fRutaEntrada = new File(rutaEntrada);
      String[] arg = new String[3];
      arg[0] = "-f";
      arg[1] = rutaEntrada;
      arg[2] = rutaSalida;

      VisorDocumentosApp.main(arg);
      fRutaSalida = new File(rutaSalida);


      return Base64.encodeBase64(FileUtils.readFileToByteArray(fRutaSalida));


      // return tcnService.convertTCNToPdfBytes(entrada);
    } catch (EeutilRestException | IOException e) {
      logger.error(e.getMessage(), e);
      throw new EeutilRestException(e.getMessage(), e);
    } finally {
      if (fRutaEntrada != null && fRutaEntrada.exists())
        fRutaEntrada.delete();
      if (fRutaSalida != null && fRutaSalida.exists())
        fRutaSalida.delete();
    }
  }

  @PostMapping(path = "api/convertirXML", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public byte[] convertirXMLPdf(@RequestBody byte[] entrada) throws EeutilRestException {
    try {
      return xmlService.convertXMLToPdfBytes(entrada);
    } catch (EeutilRestNoLoggerErrorException e) {
      logger.warn(e.getMessage(), e);
      throw new EeutilRestException(e.getMessage(), e);
    } catch (EeutilRestException e) {
      logger.error(e.getMessage(), e);
      throw new EeutilRestException(e.getMessage(), e);
    }
  }

  @PostMapping(path = "api/convertirPDFFacturae", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public byte[] convertirPDFFacturae(@RequestBody byte[] entrada) throws EeutilRestException {

    return htmlService.convertHTMLtoPDF(entrada);
  }


  @PostMapping(path = "api/validarSchemaENI", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DatosSalidaSchemaValidator> validarSchemaENI(
      @RequestBody SchemaEniModel schemaEniModel) throws EeutilRestException {
    try {
      boolean bValue = validatorSchemaService.validarSchema(schemaEniModel.getVersion(),
          schemaEniModel.getEntrada());


      DatosSalidaSchemaValidator datosSalida = new DatosSalidaSchemaValidator(bValue, null);

      return new ResponseEntity<>(datosSalida, HttpStatus.OK);
    } catch (EeutilRestException | EeutilRestShortException e) {
      MDCWrapper wrapper = schemaEniModel.getMdcWrapper();

      if (wrapper != null) {
        cargarMDCvalidarSchemaENI(schemaEniModel, wrapper);
      }

      logger.error(e.getMessage(), e);
      // throw new EeutilRestException(e.getMessage(), e);

      DatosSalidaSchemaValidator datosSalida =
          new DatosSalidaSchemaValidator(false, e.getMessage());

      return new ResponseEntity<>(datosSalida, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private void cargarMDCvalidarSchemaENI(SchemaEniModel schemaEniModel, MDCWrapper wrapper) {
    MDC.put("idApli", wrapper.getIdApli());
    MDC.put("uUId", wrapper.getuUId());
    MDC.put("ipClient", wrapper.getIpClient());
    MDC.put("clientHost", wrapper.getClientHost());
    MDC.put("clientURI", wrapper.getClientURI());
    MDC.put("contentLengh", wrapper.getContentLengh());
    String longitud = schemaEniModel.getEntrada() == null ? ("0 bytes")
        : (schemaEniModel.getEntrada().length + " bytes");
    String extraParam = "[contenido.bytes]=" + "(" + longitud + ")#########[version]=("
        + ((schemaEniModel.getVersion()) != null ? schemaEniModel.getVersion() : "") + ")";
    MDC.put("ExtraParaM", extraParam);
  }


  public static void main(String args[]) throws IOException {
    byte[] encoded = Files.readAllBytes(Paths.get("D:/Descargas/fichero_pruebas.tcn"));
    // System.out.println(new String(encoded, StandardCharsets.UTF_8));

    String newString = new String(encoded, "ISO-8859-1");

    System.out.println(newString);

  }

}
