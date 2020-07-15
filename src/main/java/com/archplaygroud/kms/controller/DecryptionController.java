package com.archplaygroud.kms.controller;

import com.archplaygroud.kms.common.ControllerError;
import com.archplaygroud.kms.dto.PathDTO;
import com.archplaygroud.kms.service.DecryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/decrypt")
public class DecryptionController {

    @Autowired
    private DecryptionService decryptionService;

    @PostMapping(path = "/{companyId}/filePath")
    public PathDTO generateDecryptedPath(@PathVariable String companyId,
                                       @RequestBody PathDTO pathDTO) {
        return new PathDTO(decryptionService.decryptPathWithSingleKey(companyId, pathDTO.getPath()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody ControllerError handleException(IllegalArgumentException errorDetails) {
        return new ControllerError(errorDetails.getMessage());
    }
}
