package ru.avg.server.controller.web.protocol;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/approval/{companyInn}/meeting/protocol")
@RequiredArgsConstructor
public class ProtocolController {
}
