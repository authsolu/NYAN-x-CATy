package cn.wizzer.modules.sys.service;

import cn.wizzer.common.service.core.BaseService;
import cn.wizzer.common.util.StringUtils;
import cn.wizzer.modules.sys.bean.*;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

import java.util.Date;
import java.util.List;

/**
 * Created by Wizzer.cn on 2015/6/30.
 */
@IocBean(args = {"refer:dao"})
public class UnitService extends BaseService<Sys_unit> {
    public UnitService(Dao dao) {
        super(dao);
    }

    public void update(Sys_unit unit) {
        dao().update(unit);
    }

    @Aop(TransAop.READ_COMMITTED)
    public boolean deleteAndChild(String id) {
        String pid = this.fetch(id).getParentId();
        dao().execute(Sqls.create("delete from sys_unit where id = '" + id + "'"));
        dao().execute(Sqls.create("delete from sys_unit where parentId = '" + id + "'"));
        if (!Strings.isEmpty(pid)) {
            int count = count(Cnd.where("parentId", "=", pid));
            if (count < 1) {
                dao().execute(Sqls.create("update sys_unit set has_children=false where id='" + pid + "'"));
            }
        }
        return true;
    }

    @Aop(TransAop.READ_COMMITTED)
    public boolean save(Sys_unit unit, String pid) {
        try {
            String path = "";
            if (!Strings.isEmpty(pid)) {
                Sys_unit pp = this.fetch(pid);
                path = pp.getPath();
            }
            unit.setPath(getSubPath("sys_unit", "path", path));
            unit.setParentId(pid);
            unit.setCreateUser(StringUtils.getUid());
            dao().insert(unit);
            if (!Strings.isEmpty(pid)) {
                dao().execute(Sqls.create("update sys_unit set has_children=true where id='" + pid + "'"));
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Aop(TransAop.READ_COMMITTED)
    public boolean edit(Sys_unit unit, String pid) {
        try {
            if (!Strings.sNull(pid).equals(unit.getParentId())) {
                if (!Strings.isEmpty(unit.getParentId())) {
                    Sys_unit pp = this.fetch(unit.getParentId());
                    unit.setPath(getSubPath("sys_unit", "path", pp.getPath()));
                    unit.setParentId(pp.getId());
                } else {
                    unit.setPath(getSubPath("sys_unit", "path", ""));
                    unit.setParentId("");
                }
            }
            unit.setCreateTime(new Date());
            unit.setCreateUser(StringUtils.getUid());
            dao().update(unit);
            if (!Strings.isEmpty(pid)) {
                int count = count(Cnd.where("parentId", "=", pid));
                if (count < 1) {
                    dao().execute(Sqls.create("update sys_unit set has_children=false where id='" + pid + "'"));
                }
            }
            if (!Strings.isEmpty(unit.getParentId()))
                dao().execute(Sqls.create("update sys_unit set has_children=true where id='" + unit.getParentId() + "'"));
            if (unit.isHasChildren()) {
                updatePath(unit.getId(), unit.getPath());
            }

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 更新子节点path
     *
     * @param id
     * @param path
     */
    public void updatePath(String id, String path) {
        List<Sys_unit> list = this.query(Cnd.where("parentId", "=", id), null);
        for (Sys_unit unit : list) {
            this.update(Chain.make("path", getSubPath("sys_unit", "path", path)), Cnd.where("id", "=", unit.getId()));
            if (unit.isHasChildren()) {
                updatePath(unit.getId(), unit.getPath());
            }
        }
    }
}

