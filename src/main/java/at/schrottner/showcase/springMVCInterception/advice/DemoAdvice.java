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

package at.schrottner.showcase.springMVCInterception.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class DemoAdvice {

    private static final Logger log = LoggerFactory.getLogger(DemoAdvice.class);

    @ModelAttribute("injectedByAdvice")
    public String getInjectedByAdvice() {
        log.info("I am the advice");
        return "injectedByAdvice";
    }
}
