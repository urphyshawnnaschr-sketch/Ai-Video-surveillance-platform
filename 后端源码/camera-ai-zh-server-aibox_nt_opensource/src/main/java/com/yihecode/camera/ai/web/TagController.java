package com.yihecode.camera.ai.web;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.yihecode.camera.ai.entity.Tag;
import com.yihecode.camera.ai.service.TagService;
import com.yihecode.camera.ai.utils.PageResult;
import com.yihecode.camera.ai.utils.PageResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "Label Management")
@SaCheckLogin
@Controller
@RequestMapping({"/tag"})
public class TagController {

    @Autowired
    private TagService tagService;

    @ApiOperation(value = "Query Data List")
    @PostMapping({"/listData"})
    @ApiImplicitParam(name = "type", value = "Label Type")
    @ResponseBody
    public PageResult<List<Tag>> listData(Integer type) {
        return PageResultUtils.success(null, this.tagService.getByType(type));
    }

}