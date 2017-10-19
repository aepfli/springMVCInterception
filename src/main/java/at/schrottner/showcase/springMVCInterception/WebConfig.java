/*********************************************************************
 * The Initial Developer of the content of this file is NETCONOMY.
 * All portions of the code written by NETCONOMY are property of
 * NETCONOMY. All Rights Reserved.
 *
 * NETCONOMY Software & Consulting GmbH
 * Hilmgasse 4, A-8010 Graz (Austria)
 * FN 204360 f, Landesgericht fuer ZRS Graz
 * Tel: +43 (316) 815 544
 * Fax: +43 (316) 815544-99
 * www.netconomy.net
 *
 * (c) 2017 by NETCONOMY Software & Consulting GmbH
 *********************************************************************/

package at.schrottner.showcase.springMVCInterception;

import at.schrottner.showcase.springMVCInterception.adapter.DemoInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);

    @Autowired
    DemoInterceptor demoInterceptor;

    /**
     * Java Configuration to add a interceptor bean
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.warn("here i am");
        registry.addInterceptor(demoInterceptor);
    }
}
