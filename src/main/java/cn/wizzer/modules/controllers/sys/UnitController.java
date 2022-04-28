package cn.wizzer.modules.controllers.sys;

import cn.wizzer.common.annotation.SLog;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.modules.models.sys.Sys_unit;
import cn.wizzer.modules.services.sys.UnitService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wizzer on 2016/6/24.
 */
@IocBean
@At("/private/sys/unit")
@Filters({@By(type = PrivateFilter.class)})
public class UnitController {
    private static final Log log = Logs.get();
    @Inject
    UnitService unitService;

    @At("")
    @Ok("beetl:/private/sys/unit/index.html")
    @RequiresPermissions("sys.manager.unit")
    public Object index() {
        return unitService.query(Cnd.where("parentId", "=", "").asc("location").asc("path"));
    }

    @At
    @Ok("beetl:/private/sys/unit/add.html")
    @RequiresPermissions("sys.manager.unit")
    public Object add(@Param("pid") String pid) {
        return Strings.isBlank(pid) ? null : unitService.fetch(pid);
    }

    @At
    @Ok("json")
    @RequiresPermissions("sys.manager.unit")
    @SLog(tag = "新建单位", msg = "单位名称：${args[0].name}")
    public Object addDo(@Param("..") Sys_unit unit, @Param("parentId") String parentId, HttpServletRequest req) {
        try {
            unitService.save(unit, parentId);
            return Result.success("system.success", req);
        } catch (Exception e) {
            return Result.error("system.error", req);
        }
    }

    @At("/child/*")
    @Ok("beetl:/private/sys/unit/child.html")
    @RequiresPermissions("sys.manager.unit")
    public Object child(@Param("id") String id) {
        return unitService.query(Cnd.where("parentId", "=", id).asc("location").asc("path"));
    }

    @At("/detail/*")
    @Ok("beetl:/private/sys/unit/detail.html")
    @RequiresPermissions("sys.manager.unit")
    public Object detail(@Param("id") String id) {
        return unitService.fetch(id);
    }

    @At
    @Ok("json")
    @RequiresPermissions("sys.manager.unit")
    public Object tree(@Param("pid") String pid) {
        List<Sys_unit> list = unitService.query(Cnd.where("parentId", "=", Strings.sBlank(pid)).asc("location").asc("path"));
        List<Map<String, Object>> tree = new ArrayList<>();
        for (Sys_unit unit : list) {
            Map<String, Object> obj = new HashMap<>();
            obj.put("id", unit.getId());
            obj.put("text", unit.getName());
            obj.put("children", unit.isHasChildren());
            tree.add(obj);
        }
        return tree;
    }
}
