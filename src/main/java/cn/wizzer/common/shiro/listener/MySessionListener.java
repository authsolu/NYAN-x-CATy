package cn.wizzer.common.shiro.listener;

import cn.wizzer.common.util.StringUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.nutz.json.Json;

/**
 * Created by Wizzer.cn on 2015/7/15.
 */
public class MySessionListener implements SessionListener {
    @Override
    public void onStart(Session session) {//会话创建时触发
        System.out.println("会话创建：" + session.getId());
    }

    @Override
    public void onExpiration(Session session) {//会话过期时触发
        System.out.println("会话过期UID::"+StringUtils.getUid());
        System.out.println("会话过期：" + session.getId()+"json:"+ Json.toJson(session));
    }

    @Override
    public void onStop(Session session) {//退出/会话过期时触发
        System.out.println("会话停止：" + session.getId());
    }
}
