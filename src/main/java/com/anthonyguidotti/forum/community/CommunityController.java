package com.anthonyguidotti.forum.community;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CommunityController {

    @GetMapping(value = "/r/{name}")
    public String community(
            @PathVariable String name
    ) {


        return "community";
    }
}
