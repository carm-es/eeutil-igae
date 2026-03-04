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

import java.nio.charset.StandardCharsets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.gob.aapp.eeutil.eeutilrestigae.exception.EeutilRestException;
import es.gob.aapp.eeutil.eeutilrestigae.utils.FileUtil;
import es.gob.aapp.eeutil.eeutilrestigae.utils.IOUtil;
import ip2.gdoc.core.excepciones.GestorDocException;
import ip2.gdoc.transformacion.transform.tcn.pdf.impl.TransformTcn;

@Service
public class TCNService {

  protected static final Log logger = LogFactory.getLog(TCNService.class);


  private static final String TELCON_PREFIX = "telcon";

  @Autowired
  private FileUtil fileUtil;

  @Autowired
  private IOUtil iOUtil;


  /**
   * Convierte un fichero TCN a PDF
   * 
   * @param entrada
   * @return
   * @throws Exception
   */
  public byte[] convertTCNToPdfBytes(byte[] entrada) throws EeutilRestException {
    try {

      // convertimos de utf-8 a ISO-8859-1
      String newString = new String(entrada, StandardCharsets.ISO_8859_1);

      entrada = newString.getBytes(StandardCharsets.ISO_8859_1);

      // Se genera el pdf a partir del fichero TCN con ayuda de las
      // librerias de IGAE
      TransformTcn tcn = new TransformTcn();

      byte[] salidaTCN =
          Base64.getEncoder().encode(tcn.transformar(Base64.getDecoder().decode(entrada)));

      // Se copia el fichero generado al fichero que devolveremos
      // String fileReturnPath = fileUtil.createFilePath(TELCON_PREFIX,
      // Base64.decodeBase64(salidaTCN));

      return salidaTCN;
    } catch (GestorDocException t) {
      throw new EeutilRestException("Error convirtiendo TCN a PDF " + t.getMessage(), t);
    } catch (Exception t) {
      throw new EeutilRestException("Error convirtiendo TCN a PDF " + t.getMessage(), t);
    }
  }

}
