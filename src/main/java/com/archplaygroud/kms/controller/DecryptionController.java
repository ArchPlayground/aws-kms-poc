package com.archplaygroud.kms.controller;

import com.archplaygroud.kms.common.ControllerError;
import com.archplaygroud.kms.dto.PathDTO;
import com.archplaygroud.kms.service.DecryptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/decrypt")
@Slf4j
@Profile({"decryptor"})
public class DecryptionController {

    @Autowired
    private DecryptionService decryptionService;

    @PostMapping(path = "/{companyId}/filePath")
    public PathDTO generateDecryptedPath(@PathVariable String companyId,
                                       @RequestBody PathDTO pathDTO) {
        return new PathDTO(decryptionService.decryptPath(companyId, pathDTO.getPath(), false));
    }

    @PostMapping(path = "/{companyId}/filePath/cached")
    public PathDTO generateDecryptedPathUsingCachedKey(@PathVariable String companyId,
                                         @RequestBody PathDTO pathDTO) {
        return new PathDTO(decryptionService.decryptPath(companyId, pathDTO.getPath(), true));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody ControllerError handleException(IllegalArgumentException errorDetails) {
        log.error("Illegal Argument exception: {}", errorDetails.getMessage());
        return new ControllerError(errorDetails.getMessage());
    }
}
