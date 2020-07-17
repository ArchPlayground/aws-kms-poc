package com.archplaygroud.kms.controller;

import com.archplaygroud.kms.dto.PathDTO;
import com.archplaygroud.kms.service.EncryptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/encrypt")
@Slf4j
@Profile({"encryptor"})
public class EncryptionController {

    @Autowired
    private EncryptionService encryptionService;

    @PostMapping(path = "/{companyId}/filePath")
    @ResponseStatus(HttpStatus.CREATED)
    public PathDTO createEncryptedPath(@PathVariable String companyId,
                                   @RequestBody PathDTO pathDTO) {
        return new PathDTO(encryptionService.encryptPath(companyId, pathDTO.getPath()));
    }
}
