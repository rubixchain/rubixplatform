package com.rubix.core.Resources;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:1898")
@RestController
public class Version {

    public static String jarVersion=null;

    @RequestMapping(value = "/getVersion", method = RequestMethod.GET,
            produces = {"application/json", "application/xml"})
    public static String getVersion()
    {
        String CURRENT_VERSION =  Version.class.getPackage().getImplementationVersion();
        jarVersion = CURRENT_VERSION;

        return CURRENT_VERSION;
    }
}
