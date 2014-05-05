package org.scalavlet

import javax.servlet.{ServletException, ServletContext, ServletContainerInitializer}
import java.{util => ju}
import java.lang.reflect.Modifier

import scala.collection.JavaConverters._
import javax.servlet.annotation.HandlesTypes

/**
 * Servlet 3.0 {@link ServletContainerInitializer} designed to support code-based
 * configuration of the servlet container using Spring's {@link WebApplicationInitializer}
 * SPI as opposed to (or possibly in combination with) the traditional
 * {@code web.xml}-based approach.
 *
 * <h2>Mechanism of Operation</h2>
 * This class will be loaded and instantiated and have its {@link #onStartup}
 * method invoked by any Servlet 3.0-compliant container during container startup assuming
 * that the {@code spring-web} module JAR is present on the classpath. This occurs through
 * the JAR Services API {@link ServiceLoader#load(Class)} method detecting the
 * module's {@code META-INF/services/javax.servlet.ServletContainerInitializer}
 * service provider configuration file. See the
 * <a href="http://download.oracle.com/javase/6/docs/technotes/guides/jar/jar.html#Service%20Provider">
 * JAR Services API documentation</a> as well as section <em>8.2.4</em> of the Servlet 3.0
 * Final Draft specification for complete details.
 *
 * (Quoted from Spring documentation)
 *
 * @see org.springframework.web.SpringServletContainerInitializer
 */
@HandlesTypes(Array(classOf[ScalavletBootable]))
class ScalavletContextInitializer extends ServletContainerInitializer {


  override def onStartup (webAppInitializerClasses: ju.Set[Class[_]], ctx: ServletContext): Unit = {
    ctx.log("Scalavlet initialization started !")

    val bootables = if (webAppInitializerClasses != null) {
      webAppInitializerClasses.asScala.map({ waiClass =>
        if (
          !waiClass.isInterface &&
          !Modifier.isAbstract(waiClass.getModifiers) &&
          classOf[ScalavletBootable].isAssignableFrom(waiClass)) {

          try {
            Some(waiClass.newInstance.asInstanceOf[ScalavletBootable])
          }
          catch {
            case ex: Throwable =>
              throw new ServletException("Failed to instantiate ScalavletBootable class", ex)
          }
        } else
          None
      }).filter(_.isDefined).map(_.get)
    } else Set()



    if (bootables.isEmpty) {
      ctx.log("No ScalavletBootable types detected on classpath")

    } else {
      ctx.log("ScalavletBootable detected on classpath: " + bootables)
      bootables.foreach(bootable => bootable.init(ctx))

    }
  }
}