package com.pjs.golf.fields;

import com.pjs.golf.account.entity.Account;
import com.pjs.golf.common.WebCommon;
import com.pjs.golf.common.annotation.CurrentUser;
import com.pjs.golf.fields.dto.FieldsDto;
import com.pjs.golf.fields.service.FieldMemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/memo", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class FieldMemoController {

    private final FieldMemoService fieldMemoService;
    private final WebCommon webCommon;

    @GetMapping("/{id}")
    public ResponseEntity getFieldMeno(
            @PathVariable int fieldId,
            @CurrentUser Account account
    ) {

        List memos = fieldMemoService.getFiledMemos(fieldId, account.getId());

        EntityModel resource = fieldMemoService.getResource(memos);
        return ResponseEntity.ok(resource);
    }


    @PostMapping("/create")
    public ResponseEntity createFieldMemo(
            @RequestBody FieldsDto fieldsDto,
            Errors errors,
            @CurrentUser Account account
            ) {

        if (errors.hasErrors()) {
            return webCommon.badRequest(errors, this.getClass());
        }
        fieldMemoService.createMemo(fieldsDto);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity updateFieldMemo(
            @RequestBody FieldsDto fieldsDto,
            Errors errors,
            @CurrentUser Account account
    ) {

        if (errors.hasErrors()) {
            return webCommon.badRequest(errors, this.getClass());
        }
        fieldMemoService.updateMemo(fieldsDto);
        return new ResponseEntity(HttpStatus.OK);
    }
}
