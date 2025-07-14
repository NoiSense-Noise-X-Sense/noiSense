package com.dosion.noisense.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaController {
  @RequestMapping(value = { "/", "/{path:^(?!api|static|assets|favicon\\.ico)[^\\.]*$}" })
  public String forwardSpa() {
    return "forward:/index.html";
  }
}
