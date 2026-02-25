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

package es.gob.aapp.eeutil.eeutilrestigae.configuration;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@Order(value = 2)
public class ResourceUtils implements ApplicationContextAware {

  @Autowired
  private ApplicationContext applicationContext;

  private String pathProperties;

  @PostConstruct
  public void loadPathProperties() {
    this.pathProperties = System.getProperty("eeutil-igae.config.path");
  }

  /***
   * 
   * @return obtiene la ruta de configuracion del modulo.
   */
  public String getPathProperties() {
    return pathProperties;
  }



  public void setResourceLoader(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  /***
   * Para tener disponible la ruta de la carpeta de propiedades.
   * 
   * @return
   */
  public Resource readExternalFolderProperties() {
    Resource file = applicationContext.getResource("file:" + pathProperties);
    return file;
  }


  /***
   * Para tener disponible la ruta de los schemas
   * 
   * @return
   */
  public Resource readExternalFolderSchemas(String version) {
    Resource file =
        applicationContext.getResource("file:" + pathProperties + "/schemas/" + version);
    return file;
  }


  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    // TODO Auto-generated method stub

  }



}
