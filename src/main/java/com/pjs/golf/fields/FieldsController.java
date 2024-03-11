package com.pjs.golf.fields;

import com.pjs.golf.account.entity.Account;
import com.pjs.golf.common.WebCommon;
import com.pjs.golf.common.annotation.CurrentUser;
import com.pjs.golf.common.dto.SearchDto;
import com.pjs.golf.common.exception.NoSuchDataCustomException;
import com.pjs.golf.fields.dto.FieldsDto;
import com.pjs.golf.fields.entity.Fields;
import com.pjs.golf.fields.service.FieldsMapper;
import com.pjs.golf.fields.service.FieldsService;
import com.pjs.golf.game.entity.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/field",  produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class FieldsController {

    private final FieldsService fieldService;
    private final WebCommon webCommon;
    @GetMapping
    public ResponseEntity getFieldList(
            Pageable pageable,
            @RequestParam(required = false) String searchTxt,
            PagedResourcesAssembler<Fields> assembler
    ){
        SearchDto  search = SearchDto.builder().SearchTxt(searchTxt).build();
        Page<Fields> fields = fieldService.getFieldList(search,pageable);
        var pageResources = assembler.toModel(fields, entity ->
                EntityModel.of( FieldsMapper.Instance.toDto(entity))
                        .add(linkTo(FieldsController.class).withRel("query-content"))
                        .add(linkTo(FieldsController.class).withSelfRel())
        );
        pageResources.add(Link.of("/docs/asciidoc/index.html#create-game-api").withRel("profile"));

        return ResponseEntity.ok().body(pageResources);
    }


    /**
     * 필드 등록
     * @return 200 | 400
     * @param fieldsDto FieldDto
     * */

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity creatField(
            @RequestBody FieldsDto fieldsDto,
            Errors errors,
            @CurrentUser Account account){

        if (errors.hasErrors()) {
            return webCommon.badRequest(errors, this.getClass());
        }

        fieldsDto.setRegister(account);
        fieldsDto.setCreateDate(LocalDateTime.now());
        Fields fields = FieldsMapper.Instance.toEntity(fieldsDto);
        try{
            Fields savedFields = fieldService.createField(fields);
            WebMvcLinkBuilder selfLink = linkTo(FieldsController.class).slash(fields.getId());
            EntityModel resource = EntityModel.of(savedFields);
            URI uri = selfLink.toUri();

            resource.add(selfLink.withRel("self"));
            resource.add(selfLink.withRel("update-content"));
            resource.add(Link.of("/docs/asciidoc/api.html#").withRel("profile"));

            return ResponseEntity.created(uri).body(resource);

        }catch (Exception e){
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 필드 상세
     * */
    @GetMapping("/{id}")
    public ResponseEntity viewField(
            @PathVariable int id,
            @CurrentUser Account account){

        try {
            Fields fields = fieldService.getFieldSingle(id);
            WebMvcLinkBuilder selfLink = linkTo(FieldsController.class).slash(fields.getId());
            EntityModel resource = EntityModel.of(fields);
            URI uri = selfLink.toUri();

            resource.add(selfLink.withRel("self"));

            if (fields.getRegister().equals(account)) {
                resource.add(selfLink.withRel("update-content"));
            }

            resource.add(Link.of("/docs/asciidoc/api.html#").withRel("profile"));

            return ResponseEntity.created(uri).body(resource);
        } catch (NoSuchDataCustomException e) {
            return ResponseEntity.notFound().build();
        }catch (Exception e){
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 필드 수정
     * */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity updatetField(
            @RequestBody FieldsDto fieldsDto,
            Errors errors,
            @PathVariable int id,
            @CurrentUser Account account){

        if (errors.hasErrors()) {
            return webCommon.badRequest(errors, this.getClass());
        }

        try {
            Fields query = fieldService.getFieldSingle(id);
            if (query.getRegister().equals(account)){
                return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            }
        }catch (NoSuchDataCustomException e){
            return ResponseEntity.notFound().build();
        }
        fieldsDto.setModifyDate(LocalDateTime.now());
        Fields fields = FieldsMapper.Instance.toEntity(fieldsDto);
        try{
            Fields updatedFields = fieldService.createField(fields);
            WebMvcLinkBuilder selfLink = linkTo(FieldsController.class).slash(fields.getId());
            EntityModel resource = EntityModel.of(updatedFields);
            URI uri = selfLink.toUri();

            resource.add(selfLink.withRel("self"));
            resource.add(selfLink.withRel("update-content"));
            resource.add(Link.of("/docs/asciidoc/api.html#").withRel("profile"));

            return ResponseEntity.created(uri).body(resource);

        }catch (Exception e){
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    /**
     * 필드 삭제
     * */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity deleteField(
            @RequestBody FieldsDto fieldsDto,
            Errors errors,
            @PathVariable int id,
            @CurrentUser Account account){

        if (errors.hasErrors()) {
            return webCommon.badRequest(errors, this.getClass());
        }

        try {
            Fields query = fieldService.getFieldSingle(id);
            if (query.getRegister().equals(account)){
                return new ResponseEntity(HttpStatus.UNAUTHORIZED);
            }
        }catch (NoSuchDataCustomException e){
            return ResponseEntity.notFound().build();
        }
        Fields fields = FieldsMapper.Instance.toEntity(fieldsDto);
        try{
            fieldService.deleteField(fields);
            return ResponseEntity.noContent().build();

        }catch (Exception e){
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
