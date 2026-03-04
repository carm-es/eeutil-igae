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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.gob.aapp.eeutil.eeutilrestigae.exception.EeutilRestException;
import es.gob.aapp.eeutil.eeutilrestigae.exception.EeutilRestNoLoggerErrorException;
import es.gob.aapp.eeutil.eeutilrestigae.utils.FileUtil;
import es.gob.aapp.eeutil.eeutilrestigae.utils.IOUtil;
import ip2.gdoc.core.excepciones.GestorDocException;
import ip2.gdoc.transformacion.transform.xml.pdf.impl.TransformXml;

@Component
public class XMLService {

  protected static final Log logger = LogFactory.getLog(XMLService.class);


  private static final String XML_PREFIX = "xml";

  @Autowired
  private FileUtil fileUtil;

  @Autowired
  private IOUtil iOUtil;

  /**
   * Convierte un fichero XML a PDF
   * 
   * @param entrada
   * @return
   * @throws Exception
   */
  public byte[] convertXMLToPdfBytes(byte[] entrada)
      throws EeutilRestException, EeutilRestNoLoggerErrorException {

    byte[] salidaXML = null;

    try {
      // Se genera el pdf a partir del fichero XML con ayuda de las
      // librerias de IGAE
      TransformXml xml = new TransformXml();

      salidaXML = Base64.getEncoder().encode(xml.transformar(Base64.getDecoder().decode(entrada)));

      // Se copia el fichero generado al fichero que devolveremos
      // String fileReturnPath = fileUtil.createFilePath(XML_PREFIX,
      // Base64.decodeBase64(salidaTCN));



    } catch (GestorDocException t) {
      // Si el error es que el xml pasado no es un xml aplantillado de igae
      if (t.getMessage().contains("Error al aplicar la hoja de estilos")) {
        throw new EeutilRestNoLoggerErrorException("Error convirtiendo XML a PDF " + t.getMessage(),
            t);
      }
    } catch (Exception t) {
      throw new EeutilRestException("Error convirtiendo XML a PDF " + t.getMessage(), t);
    }

    return salidaXML;
  }



}
