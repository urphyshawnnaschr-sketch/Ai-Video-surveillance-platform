package com.yihecode.camera.ai.web;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.yihecode.camera.ai.entity.Account;
import com.yihecode.camera.ai.service.AccountService;
import com.yihecode.camera.ai.utils.*;
import com.yihecode.camera.ai.web.vo.IdVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

/**
* Account Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Api(tags = "Account Management")
@SaCheckLogin
@Controller
@RequestMapping({"/account"})
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Value("${uploadDir}")
    private String uploadDir;

    /**
* Detail
* @param idVo
* @return
*/
    @ApiOperation("Account Detail")
    @RequestMapping({"/detail"})
    @ResponseBody
    public JsonResult<Account> detail(@RequestBody IdVo idVo) {
        Long id = idVo.getId();
        if (id == null) {
            return JsonResultUtils.success(new Account());
        }
        return JsonResultUtils.success(this.accountService.getById(id));
    }

    /**
* Query Data List
* @return
*/
    @ApiOperation(value = "Query Data List")
    @PostMapping({"/listData"})
    @ResponseBody
    public PageResult listData() {
        List<Account> accountList = this.accountService.list();
        if (accountList == null) {
            accountList = new ArrayList<>();
        }
        return PageResultUtils.success(null, accountList);
    }

    /**
* Save Data
* @param account
* @return
*/
    @ApiOperation(value = "Save Data")
    @PostMapping({"/save"})
    @ResponseBody
    public JsonResult save(Account account) {
        if (StrUtil.isBlank(account.getName())) {
            return JsonResultUtils.fail("Please enter member work Name");
        }
        if (StrUtil.isBlank(account.getAccount())) {
            return JsonResultUtils.fail("Please enter Login Account");
        }
        if (account.getId() != null) {
            account.setPassword(null);
        } else if (StrUtil.isBlank(account.getPassword())) {
            return JsonResultUtils.fail("Please enter initial start Password");
        } else {
            account.setPassword(PassUtils.encrypt(account.getPassword()));
            account.setCreatedAt(new Date());
        }
        if (account.getState() == null) {
            return JsonResultUtils.fail("Please select Account Status");
        }
        account.setUpdatedAt(new Date());

        //

if(account.getId() == null) {
Account oldAccount = accountService.getByAccount(account.getAccount());
if(oldAccount!= null) {
return JsonResultUtils.fail("Account Exist, Please select Other Account");
}
this.accountService.save(account);
} else {
Account oldAccount = accountService.getByAccount(account.getAccount());
if(oldAccount!= null &&!oldAccount.getId().equals(account.getId())) {
return JsonResultUtils.fail("Account Exist, Please select Other Account");
}
this.accountService.saveOrUpdate(account);
}
return JsonResultUtils.success();
}

/**
* Delete Data
* @param id
* @return
*/
@ApiOperation(value ="Delete Data")
@ApiImplicitParam(name ="id", value ="id")
@PostMapping({"/delete"})
@ResponseBody
public JsonResult delete(Long id) {
this.accountService.removeById(id);
return JsonResultUtils.success();
}

/**
* Change Password
* @return
*/
@ApiOperation(value ="Change Password")
@ApiImplicitParam(name ="password", value ="Password")
@PostMapping({"/password"})
@ResponseBody
public JsonResult updatePassword(String password) {
if(StrUtil.isBlank(password)) {
return JsonResultUtils.fail("Please enter new Password");
}

Long id = StpUtil.getLoginIdAsLong();
Account account = new Account();
account.setId(id);
account.setPassword(PassUtils.encrypt(password));
this.accountService.saveOrUpdate(account);
return JsonResultUtils.success();
}

/**
* File Download
* @author Abyss
* @date 2023/11/7 15:36
*/
@GetMapping("/download")
public void download(String fileName, HttpServletRequest request, HttpServletResponse response) {
if (StrUtil.isEmpty(fileName)) return;
try {
File file = new File(uploadDir + fileName);
if (file.exists() && file.length() == 0) return;
response.reset();
response.setContentType("application/octet-stream");
response.setContentLengthLong(file.length());
response.addHeader("Access-Control-Allow-Origin","*");
response.addHeader("Content-Disposition","attachment; filename="+ URLEncoder.encode(fileName,"UTF-8"));
InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
FileCopyUtils.copy(inputStream, response.getOutputStream());
} catch (Exception e) {
e.printStackTrace();
}
}

/**
*
* @return
*/
@GetMapping("check/timeout")
public JsonResult<Integer> checkTimeout() {
return JsonResultUtils.success(new Random().nextInt(10000));
}


}