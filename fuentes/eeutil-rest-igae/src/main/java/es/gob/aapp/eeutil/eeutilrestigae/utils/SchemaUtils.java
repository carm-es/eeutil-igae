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

package es.gob.aapp.eeutil.eeutilrestigae.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;

import es.gob.aapp.eeutil.eeutilrestigae.configuration.ResourceUtils;

@Configuration
@Order(value = 99)
public class SchemaUtils {

  protected static final Log logger = LogFactory.getLog(SchemaUtils.class);

  @Autowired
  public ResourceUtils resourceUtils;

  private static Source[] aSources10;

  private static Source[] aSources20;



  public Source[] getSchemasSourcesVersion(String version) throws IOException {

    if ("1.0".equals(version)) {
      if (aSources10 == null) {
        // ClassPathResource resource = new ClassPathResource("schemas/" + version + "/");
        Resource resource = resourceUtils.readExternalFolderSchemas(version);

        aSources10 = getSchemasSources(resource.getFile().toPath().toString());
      }
      return aSources10;
    } else if ("2.0".equals(version)) {
      if (aSources20 == null) {

        Resource resource = resourceUtils.readExternalFolderSchemas(version);

        aSources20 = getSchemasSources(resource.getFile().toPath().toString());
      }
      return aSources20;
    } else
      throw new IOException("Fallo al cargar los schemas para la version: " + version);
  }


  private Source[] getSchemasSources(String dir) {

    File f = new File(dir);

    List<File> filesIn = getFilesInFolder(f, ".xsd");

    Source[] schemasSources = new Source[filesIn.size()];
    int i = 0;
    for (File schema : filesIn) {
      schemasSources[i] = new StreamSource(schema);
      i++;
    }

    return schemasSources;
  }

  public static List<File> getFilesInFolder(File f, String extension) {
    List<File> files = new ArrayList<>();

    if (f != null) {
      if (!f.isDirectory() || !f.canRead()) {
        return files;
      }

      File[] filesIn = f.listFiles();

      if (filesIn != null) {
        for (File file : filesIn) {
          if (file.getName().endsWith(extension)) {
            files.add(file);
          }
        }
      }
    }

    return files;
  }

}
