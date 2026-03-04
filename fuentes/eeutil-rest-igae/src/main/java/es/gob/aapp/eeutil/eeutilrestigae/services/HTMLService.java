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

import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;

import es.gob.aapp.eeutil.eeutilrestigae.exception.EeutilRestException;

@Component
public class HTMLService {

  protected static final Log logger = LogFactory.getLog(HTMLService.class);


  public byte[] convertHTMLtoPDF(byte[] facturaE_HTML) throws EeutilRestException {
    byte[] salida = null;

    facturaE_HTML = Base64.getDecoder().decode(facturaE_HTML);

    try (ByteArrayOutputStream bos = new ByteArrayOutputStream();) {
      // pasar de html a xhtml
      Document document = Jsoup.parse(new String(facturaE_HTML));
      document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
      String contentXHTML = document.html();

      ITextRenderer renderer = new ITextRenderer();
      renderer.setDocumentFromString(contentXHTML);
      renderer.layout();
      renderer.createPDF(bos);


      // salida= Arrays.copyOf(bos.toByteArray(),bos.toByteArray().length);
      salida = bos.toByteArray();

      salida = Base64.getEncoder().encode(salida);

    } catch (Exception e) {
      throw new EeutilRestException(e.getMessage(), e);
    }


    return salida;

  }
}
