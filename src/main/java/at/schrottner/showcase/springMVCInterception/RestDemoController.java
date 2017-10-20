/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.schrottner.showcase.springMVCInterception;

import at.schrottner.showcase.springMVCInterception.dto.DemoDTO;
import at.schrottner.showcase.springMVCInterception.exception.DemoException;
import at.schrottner.showcase.springMVCInterception.exception.DirectDemoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/*")
public class RestDemoController {

    @GetMapping("/")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public DemoDTO rest() {
        DemoDTO demoDTO = new DemoDTO();
        demoDTO.setId("id");
        demoDTO.setName("name");
        return demoDTO;
    }

    /**
     * will not be catched by the ResponseBody if you put a @ResponseStatus in before it :D
     */
    @GetMapping("/withNull")
    @ResponseBody
    public DemoDTO restWithNull() {
        DemoDTO demoDTO = null;
        return demoDTO;
    }


    /**
     * this will be handled by a RestControllerAdvice
     */
    @GetMapping("/withException")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public DemoDTO restWithException() throws DemoException {
        throw new DemoException("oh no");
    }

    /**
     * As there is no controllerAdvice this will be directly returned
     */
    @GetMapping("/withDirectException")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public DemoDTO restWithDirectException() throws DirectDemoException {
        throw new DirectDemoException("oh no");
    }
}
