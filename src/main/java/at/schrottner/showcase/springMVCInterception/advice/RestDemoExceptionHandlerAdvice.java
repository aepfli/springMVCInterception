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

import at.schrottner.showcase.springMVCInterception.dto.ErrorDTO;
import at.schrottner.showcase.springMVCInterception.exception.DemoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestDemoExceptionHandlerAdvice {

    private static final Logger log = LoggerFactory.getLogger(RestDemoExceptionHandlerAdvice.class);

    @ExceptionHandler(DemoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDTO handler(DemoException e) {
        log.info("I am the demoException handler");
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setMessage(e.getMessage());
        return errorDTO;
    }
}
